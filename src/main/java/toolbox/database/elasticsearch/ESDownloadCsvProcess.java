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

import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.commons.globals.Globals;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.users.WsControllerUser.CATALOG_MODIF_TYPE;
import toolbox.database.DbSearchResult;
import toolbox.database.IDbItem;
import toolbox.database.IDbSearchResult.SORTING_ORDER;
import toolbox.database.elasticsearch.ESDataSource;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;
import toolbox.utils.IPair;
import toolbox.utils.parsers.ACsvParser;
import toolbox.utils.parsers.CsvDumper;

public class ESDownloadCsvProcess extends AProcessingTask   {

	private static final Integer RETRIEVALS_SIZE = 1000;
	
	private Log log = LogFactory.getLog(Catalog.class);
	
	private Date _timestamp=new Date(0);
	
	private Semaphore _postingDataLock = new Semaphore(1,true);
	private Semaphore _stoppingProcessingLock = new Semaphore(1,true);
	private ICatalog _catalog; 
	private String _targeFileName;

	private Integer _fromIndex=0;
	private String _query="";
	private List<String> _csvColsList=new ArrayList<>();
	private List<String> _preFilters=new ArrayList<>();
	private List< IPair<String,SORTING_ORDER> > _sortByFieldName=new ArrayList<>();
	
	private CsvDumper<IDbItem> _csvDump=null;
	
	public ESDownloadCsvProcess(IUserProfileData u, 
						 String name, 
						 String targeFileName,
						 List<String> csvColsList,
						 Integer maxNbItems,
						 ICatalog c,
						 Integer fromIndex,
						 String query,
						 List<String> preFilters,
						 List< IPair<String,SORTING_ORDER> > sortingOrder,
						 Date timestamp) throws DataProcessException { 
		super(u,name);
		_catalog=c;
		_timestamp=timestamp;
		this.setTargeFileName(targeFileName);
		this.setTargetNbData(new Long(maxNbItems));
		this.setCsvColsList(csvColsList);
		this.setFromIndex(fromIndex);
		this.setQuery(query);
		this.setPreFilters(preFilters);
		this.setSortByFieldName(sortingOrder);
		
		this.addObserver(u);
		
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

	@Override
	public void run()  { 
		
		getActiveUser().sendGuiProgressMessage(
    			getId(),
    			getActiveUser().getText("Items.serverside.csvdownload.progress", getName()),
    			AProcessingTask.pourcentage(getProcessedNbData(),getTargetNbData()));
		
		while (!this.isAllDataProcessed()) {
			try { 
				
				List<DbSearchResult> results = new ArrayList<>();
				log.error("#########@ TODO");
				/*
				Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
						.getLoadFromDbStmt(this.getCatalog(),
						    				this.getFromIndex(), 
						    				RETRIEVALS_SIZE,
						    				this.getQuery(),
						    				this.getPreFilters(),							    				
						    				this.getSortByFieldName()).execute();
				*/
				// seems to return null value when 0 hits
	    		Long totalHits = results.get(0).getTotalHits();
	    		if (totalHits==null) { 
	    			log.error("No Hits for given CSV-dump request");
	    			break;
	    		}
	    		this.setTargetNbData(totalHits);
	    		List<IDbItem> items = results.get(0).getItems();
	    		this.addReceivedNbData(new Long(items.size()));
	    		this.setFromIndex(this.getFromIndex()+items.size());

	    		if (_csvDump==null) { 
	    			_csvDump=new CsvDumper<IDbItem>(this.getActiveUser(),
	    								this.getName()+":CsvGenerator",
	    								this.getTargetNbData(),
	    								this.getCsvColsList(),
	    								_timestamp,
	    								this.getTargeFileName());
	    			_csvDump.start();
	    		}	    		
	    		_csvDump.postDataToDump(items);
	    		
	    		this.addProcessedNbData(new Long(items.size()));
	    		
	    		
			} catch (DataProcessException | InterruptedException e) { 
				e.printStackTrace(); 
				break;
			}
		}
		
		stop();

	}


	@Override
	/**
	 * Blocking, wait for currently posted data is finished to be processed
	 */
	public void stop() {
		
		try {
			_stoppingProcessingLock.acquire();
			if (_csvDump!=null) { _csvDump.stop(); }
			
			if (this.isTerminated() && !this.isRunning()) { return; }			
			
			Boolean success=(_csvDump!=null && _csvDump.isAllDataProcessed());
			if (!success) {
				log.error("Unable to await end of CSV download processor "+getName());
				getActiveUser().sendGuiErrorMessage(getActiveUser().getText("Items.serverside.csvdownload.failed", getName()));
			} else {
				// notify active user that its processing finished
				getActiveUser().sendGuiSuccessMessage(getActiveUser().getText("Items.serverside.csvdownload.success", 
																				getName(),
																				this.getProcessedNbData().toString()));								
			}
			
			// notify all users that some contents changed
			getActiveUser().notifyCatalogContentsChanged(CATALOG_MODIF_TYPE.DOCS_LIST, this.getProcessedNbData());			
			
			_stoppingProcessingLock.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
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
		_csvDump.abort();			
		
		getActiveUser().sendGuiProgressMessage(
    			getId(),
    			getActiveUser().getText("Items.serverside.bulkprocess.progress", getName()),
    			AProcessingTask.pourcentage(getProcessedNbData(), getTargetNbData()), false /*processing ended*/);
		
		getActiveUser().removeProccessingTask(this.getId());
	}


	public ICatalog getCatalog() {
		return _catalog;
	}


	public void setCatalog(ICatalog catalog) {
		this._catalog = catalog;
	}

	
	public Integer getFromIndex() {
		return _fromIndex;
	}


	public void setFromIndex(Integer _fromIndex) {
		this._fromIndex = _fromIndex;
	}


	public String getQuery() {
		return _query;
	}


	public void setQuery(String _query) {
		this._query = _query;
	}


	public List<String> getPreFilters() {
		return _preFilters;
	}


	public void setPreFilters(List<String> _preFilters) {
		this._preFilters = _preFilters;
	}


	public List< IPair<String,SORTING_ORDER> > getSortByFieldName() {
		return _sortByFieldName;
	}


	public void setSortByFieldName(List< IPair<String,SORTING_ORDER> > _sortByFieldName) {
		this._sortByFieldName = _sortByFieldName;
	}
	
	public String getTargeFileName() {
		return _targeFileName;
	}

	public void setTargeFileName(String targeFileName) {
		this._targeFileName = targeFileName;
	}


	public List<String> getCsvColsList() {
		return _csvColsList;
	}


	public void setCsvColsList(List<String> _csvColsList) {
		this._csvColsList = _csvColsList;
	}

	

};
