package toolbox.database.elasticsearch;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;

import metaindex.app.control.websockets.users.WsControllerUser.CATALOG_MODIF_TYPE;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.IDbItem;
import toolbox.database.elasticsearch.ElasticSearchConnector;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;
import toolbox.utils.parsers.ACsvParser;

public class ESBulkProcess extends AProcessingTask   {

	private Log log = LogFactory.getLog(Catalog.class);
	
	private static final Integer BULK_FLUSH_TRESHOLD=500;
	private static final Long BULK_FLUSH_SECONDS=1L;
	
	private Semaphore _postingDataLock = new Semaphore(1,true);	
	private Semaphore _stoppingProcessingLock = new Semaphore(1,true);
	
	private Date _timestamp=new Date(0);
	private Boolean _interruptFlag=false;
	
	private class MxESBulkListener implements BulkProcessor.Listener {
		Integer _bulkItemIndex=0;
		ESBulkProcess _process;
		private Semaphore _bulkProcessLock = new Semaphore(1,true);
		
		public MxESBulkListener(ESBulkProcess process) {
			_process=process;
		}
		
		@Override
	    public void beforeBulk(long executionId, BulkRequest request) {
			// ensure sequences of bulks are not paralellized
			try { _bulkProcessLock.acquire(); } catch (InterruptedException e) { e.printStackTrace(); }
			
	    }

	    @Override
	    public void afterBulk(long executionId, BulkRequest request,
	            BulkResponse response) {
	    		    	
	    		List<String> errors = new ArrayList<String>();	    		
	    		for (BulkItemResponse bulkItemResponse : response) {
	    			_bulkItemIndex++;
                	if (bulkItemResponse.isFailed()) {
                        BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                        errors.add("Item ["+_bulkItemIndex+"] : "+failure.toString());                        
                    }                	
                }           
	    		if (errors.size()>0) { 
	    			_process.sendErrorMessageToUser(errors.size()+ " ElasticSearch error(s) occured",errors);	    		
	    		}
	    
	    	
	    	_process.addProcessedNbData(new Long(response.getItems().length));
	    	
	    	getActiveUser().sendGuiProgressMessage(
	    			_process.getId(),
	    			getActiveUser().getText("Items.serverside.bulkprocess.progress", _process.getName()),
	    			AProcessingTask.pourcentage(_process.getProcessedNbData(), _process.getTargetNbData()));
	    	
	    	_bulkProcessLock.release();
	    }

	    @Override
	    public void afterBulk(long executionId, BulkRequest request,
	            Throwable failure) {
	        _process.abort();
	        List<String> errors = new ArrayList<String>();
	        errors.add(failure.getMessage());
	        _process.sendErrorMessageToUser("ElasticSearch exception occured",errors);
	        _bulkProcessLock.release();
	    }
	}
	
	private ElasticSearchConnector _datasource;
	private ICatalog _catalog; 
	private BulkProcessor.Builder _builder; 
	private BulkProcessor _processor;
	private IUserProfileData _activeUser;
	
	// optional parser to be used to decode user string when relevant
	private ACsvParser<IDbItem> _parser=null;	
	public ACsvParser<IDbItem> getParser() { return _parser; }
	public void setParser(ACsvParser<IDbItem> parser) { this._parser = parser; }
	//----
	
	public ESBulkProcess(IUserProfileData u, 
						 String name, 
						 Integer expectedNbActions,
						 ICatalog c,
						 Date timestamp,
						 ElasticSearchConnector ds) throws DataProcessException { 
		super(u,name);
		_catalog=c;
		_datasource=ds;
		_timestamp=timestamp;
		this.setTargetNbData(new Long(expectedNbActions));
		this.addObserver(u);
		_activeUser=u;
		BulkProcessor.Listener listener = new MxESBulkListener(this);
		
		_builder = BulkProcessor.builder(
		        (request, bulkListener) ->		        
		        _datasource.getHighLevelClient()
		        	.bulkAsync(request, RequestOptions.DEFAULT, bulkListener),
		        listener);
		_builder.setBulkActions(BULK_FLUSH_TRESHOLD); 
		_builder.setFlushInterval(TimeValue.timeValueSeconds(BULK_FLUSH_SECONDS)); 
		_builder.setGlobalIndex(_catalog.getName());
		_processor = _builder.build(); 
		
		// commented to keep as an example
		//builder.setBulkSize(new ByteSizeValue(1L, ByteSizeUnit.MB)); 
		//builder.setConcurrentRequests(0); 
		//builder.setFlushInterval(TimeValue.timeValueSeconds(10L)); 
		//builder.setBackoffPolicy(BackoffPolicy
		//        .constantBackoff(TimeValue.timeValueSeconds(1L), 3));
	}
	
	
	public void lock() throws InterruptedException { _postingDataLock.acquire(); }
	public void unlock() { _postingDataLock.release(); }
	
	@Override
	public void sendErrorMessageToUser(String msg, List<String> details) {
		getActiveUser().sendGuiErrorMessage(msg, details);		
	}
	@Override
	public void sendErrorMessageToUser(String msg) {
		getActiveUser().sendGuiErrorMessage(msg);		
	}

	public void postDataToIndexOrUpdate(List<IDbItem> d) throws DataProcessException {
		if (!isRunning()) {
			throw new DataProcessException("Processing not ready, unable to post data before");
		}
		List<IDbItem> itemsToIndex = new ArrayList<IDbItem>();
		List<IDbItem> itemsToUpdate = new ArrayList<IDbItem>();
		for (IDbItem curItem : d) {
			curItem.getData().put(ICatalogTerm.MX_TERM_LASTMODIF_TIMESTAMP,ICatalogTerm.MX_TERM_DATE_FORMAT.format(_timestamp));
			curItem.getData().put(ICatalogTerm.MX_TERM_LASTMODIF_USERID, this.getActiveUser().getId());
			if (curItem.getData().containsKey("_id")) { itemsToUpdate.add(curItem); }
			else { itemsToIndex.add(curItem); }
			
		}
		postDataToIndex(itemsToIndex);
		postDataToUpdate(itemsToUpdate);
	}
	
	/** Create given new document */
	public void postDataToIndex(List<IDbItem> d) throws DataProcessException {
		if (!isRunning()) {
			throw new DataProcessException("Processing not ready, unable to post data before");
		}
		if (_catalog.getNbDocuments()+d.size()>_catalog.getQuotaNbDocs()) {
			_activeUser.sendGuiErrorMessage(_activeUser.getText("Items.serverside.uploadItems.tooManyItemsForQuota"));
			this.stop();
			throw new DataProcessException("Processing leading to quota overpass, aborted");
		}
		
		for (IDbItem itemToIndex : d) {	
			IndexRequest request = new IndexRequest().source(itemToIndex.getData());
			_processor.add(request);
		}
		this.addReceivedNbData(new Long(d.size()));
	}
	
	/** Update document or create it with requested id if it does not exist */
	public void postDataToUpdate(List<IDbItem> d) throws DataProcessException {
		if (!isRunning()) {
			throw new DataProcessException("Processing not ready, unable to post data before");
		}
		List<String> errors = new ArrayList<String>();
		for (IDbItem itemToUpdate : d) {
			if (itemToUpdate.getId().length()==0) {
				errors.add("Unable to delete item '"+itemToUpdate.getName()+"', no id defined");
				continue;
			}
			
			String docId=itemToUpdate.getId();
			itemToUpdate.getData().remove("_id");
			UpdateRequest request = new UpdateRequest().id(docId).doc(itemToUpdate.getData());
			IndexRequest insertIfNotExistRequest = new IndexRequest().id(docId).source(itemToUpdate.getData());
			request.upsert(insertIfNotExistRequest);
			_processor.add(request);
		}
		if (errors.size()>0) { sendErrorMessageToUser("Some data could not be updated",errors); }
		this.addReceivedNbData(new Long(d.size()));
	}
	public void postDataToDelete(List<IDbItem> d) throws DataProcessException {
		if (!isRunning()) {
			throw new DataProcessException("Processing not ready, unable to post data before");
		}
		List<String> errors = new ArrayList<String>();
		for (IDbItem itemToDelete : d) {
			if (itemToDelete.getId().length()==0) {
				errors.add("Unable to delete item '"+itemToDelete.getName()+"', no id defined");
				continue;
			}
			DeleteRequest request = new DeleteRequest().id(itemToDelete.getId());
			_processor.add(request);
		}
		if (errors.size()>0) { sendErrorMessageToUser("Some data could not be deleted",errors); }
		this.addReceivedNbData(new Long(d.size()));
	}

	
	@Override
	public void run()  { 
		
		getActiveUser().sendGuiProgressMessage(
    			getId(),
    			getActiveUser().getText("Items.serverside.bulkprocess.progress", getName()),
    			AProcessingTask.pourcentage(getProcessedNbData(),getTargetNbData()));
		
		while (!this.isAllDataProcessed() && !_interruptFlag) {
			try { 
				// wait for having received all data to be processed 
				Thread.sleep(200);
			} catch (InterruptedException e) { 
				e.printStackTrace(); 
			}
		}

		stop();

	}

	@Override
	public Boolean isTerminated() { return _interruptFlag==true || super.isTerminated(); }	
	
	@Override
	/**
	 * Blocking, wait for currently posted data is finished to be processed
	 */
	public void stop() {
		
		try {
			_stoppingProcessingLock.acquire();
			if (this.isTerminated() && !this.isRunning()) {
				_stoppingProcessingLock.release();
				return; 
			}			
			Boolean success = _processor.awaitClose(30L, TimeUnit.SECONDS);			
			_processor.close();
			ESWriteStmt.waitUntilEsIndexRefreshed(_catalog.getName(),_datasource);
			
			getActiveUser().getCurrentCatalog().loadStatsFromDb();
			
			if (!success) {
				log.error("Unable to await end of ES bulk processor "+getName());
				getActiveUser().sendGuiErrorMessage(getActiveUser().getText("Items.serverside.bulkprocess.failed", getName()));
			} else {
				// notify active user that its processing finished
				if (!this.isTerminated()) {
					getActiveUser().sendGuiErrorMessage(getActiveUser().getText("Items.serverside.bulkprocess.failedAfterNItems", 
							getName(),
							this.getProcessedNbData().toString(),
							this.getTargetNbData().toString()));
				} else {
					getActiveUser().sendGuiSuccessMessage(getActiveUser().getText("Items.serverside.bulkprocess.success", 
							getName(),
							this.getProcessedNbData().toString()));
				}
												
			}
			
			// notify all users that some contents changed
			getActiveUser().notifyCatalogContentsChanged(CATALOG_MODIF_TYPE.DOCS_LIST, this.getProcessedNbData());	
			_interruptFlag=true;
			_stoppingProcessingLock.release();
			joinRunnerThread();

		} catch (InterruptedException | DataProcessException e) {
			e.printStackTrace();
			_processor.close();
			_stoppingProcessingLock.release();
			
		}
		
		getActiveUser().sendGuiProgressMessage(
    			getId(),
    			getActiveUser().getText("Items.serverside.bulkprocess.progress", getName()),
    			AProcessingTask.pourcentage(getProcessedNbData(), getTargetNbData()), false /*processing ended*/);
		
		getActiveUser().removeProccessingTask(this.getId());
	}
	@Override
	public void abort() {
		try {
			_processor.awaitClose(0L, TimeUnit.SECONDS);
			_processor.close();
			ESWriteStmt.waitUntilEsIndexRefreshed(_catalog.getName(),_datasource);
			
			getActiveUser().getCurrentCatalog().loadStatsFromDb();
		} catch (InterruptedException | DataProcessException e) {
			e.printStackTrace();
			_processor.close();			
		}
		
		getActiveUser().sendGuiProgressMessage(
    			getId(),
    			getActiveUser().getText("Items.serverside.bulkprocess.progress", getName()),
    			AProcessingTask.pourcentage(getProcessedNbData(), getTargetNbData()), false /*processing ended*/);
		
		getActiveUser().removeProccessingTask(this.getId());
	}
	

};
