package toolbox.database.elasticsearch;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.app.Globals;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.DbSearchResult;
import toolbox.database.IDbItem;
import toolbox.database.IDbSearchResult.SORTING_ORDER;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;
import toolbox.utils.AStreamHandler;
import toolbox.utils.IPair;
import toolbox.utils.IStreamHandler;

public class ESDownloadProcess extends AProcessingTask   {
	
	private Log log = LogFactory.getLog(Catalog.class);
	
	private Semaphore _postingDataLock = new Semaphore(1,true);
	private Semaphore _stoppingProcessingLock = new Semaphore(1,true);
	private ICatalog _catalog; 
	private String _targetName;

	private Long _fromIndex=0L;
	private String _query="";
	private List<String> _preFilters=new ArrayList<>();
	private List< IPair<String,SORTING_ORDER> > _sortByFieldName=new ArrayList<>();
	
	private AStreamHandler<IDbItem> _streamHandler=null;
	
	private Boolean _dataGenerated = false;
	 
	public ESDownloadProcess(IUserProfileData u, 
						 String name, 
						 String targeFileName,
						 AStreamHandler<IDbItem> streamHandler,
						 Long maxNbItems,
						 ICatalog c,
						 Long fromIndex,
						 String query,
						 List<String> preFilters,
						 List< IPair<String,SORTING_ORDER> > sortingOrder) throws DataProcessException { 
		super(u,name);
		_catalog=c;
		this.setTargetName(targeFileName);
		this.setTargetNbData(new Long(maxNbItems));
		_streamHandler=streamHandler;
		this.setFromIndex(fromIndex);
		this.setQuery(query);
		this.setPreFilters(preFilters);
		this.setSortByFieldName(sortingOrder);
		
		// progress messages will be sent directly by the stream processing task
		// rather by this by the DB-extract task
		//this.addObserver(u);		
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
		
		try { 
			
			_streamHandler.start();

			class StreamForwarder implements IStreamHandler<DbSearchResult> {
				@Override public void handle(List<DbSearchResult> loadedDocsResults) throws DataProcessException {
					if (loadedDocsResults.size()>1) {
						log.warn("only one dbresult expected for thus usecase, got "+loadedDocsResults.size());						
					}
					DbSearchResult loadedResult = loadedDocsResults.get(0);
					
					// seems to return null value when 0 hits
		    		Long totalHits = loadedResult.getTotalHits();
		    		if (totalHits==null) { 
		    			log.warn("No Hits for given data extraction request");			    			
		    		}
		    		_streamHandler.setTargetNbData(totalHits);
		    		setTargetNbData(totalHits);
		    		List<IDbItem> items = loadedResult.getItems();
		    		addReceivedNbData(new Long(items.size()));
		    		_streamHandler.handle(items);			    		
		    		addProcessedNbData(new Long(items.size()));						
			}}
			
			Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
					.getLoadDocsStreamFromDbStmt(this.getCatalog(),
					    				this.getFromIndex(), 
					    				this.getTargetNbData(),
					    				this.getQuery(),
					    				this.getPreFilters(),							    				
					    				this.getSortByFieldName(),
					    				-1 /*no length limit on fields values*/).execute(new StreamForwarder());
			
			
    		
    		
		} catch (DataProcessException e) { 
			e.printStackTrace(); 				
		}
		
		stop();

	}


	@Override
	/**
	 * Blocking, wait for currently posted data is finished to be processed
	 */
	public void stop() {
		
		try {
			
			while (!this.isAllDataProcessed()) {
				Thread.sleep(200);
			}
			_stoppingProcessingLock.acquire();
			if (_streamHandler!=null) { _streamHandler.stop(); }
			
			if (this.isTerminated() && !this.isRunning()) { 
				_stoppingProcessingLock.release();
				return; 
			}			
			
			Boolean success=(_streamHandler!=null && _streamHandler.isAllDataProcessed());
			if (!success) {
				log.error("Unable to await end of download processor "+getName());
				getActiveUser().sendGuiErrorMessage(getActiveUser().getText("Items.serverside.csvdownload.failed", getName()));
			} else {
				if (_dataGenerated==false) {
					// notify active user that its processing finished
					getActiveUser().sendGuiSuccessMessage(getActiveUser().getText("Items.serverside.csvdownload.success", 
																				getName(),
																				this.getProcessedNbData().toString()));
					_dataGenerated=true;
				}
			}
			
			_stoppingProcessingLock.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
			_stoppingProcessingLock.release();
			
		}
		
		getActiveUser().removeProccessingTask(this.getId());
	}
	@Override
	public void abort() {
		_streamHandler.abort();			
		
		getActiveUser().removeProccessingTask(this.getId());
	}


	public ICatalog getCatalog() {
		return _catalog;
	}


	public void setCatalog(ICatalog catalog) {
		this._catalog = catalog;
	}

	
	public Long getFromIndex() {
		return _fromIndex;
	}


	public void setFromIndex(Long _fromIndex) {
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
	
	public String getTargetName() {
		return _targetName;
	}

	public void setTargetName(String targeFileName) {
		this._targetName = targeFileName;
	}

	public Boolean isDataGenerated() { return _dataGenerated; }

};
