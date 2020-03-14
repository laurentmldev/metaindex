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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.commons.globals.Globals;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.users.WsControllerUser.CATALOG_MODIF_TYPE;
import toolbox.database.DbSearchResult;
import toolbox.database.IDbItem;
import toolbox.database.IDbSearchResult.SORTING_ORDER;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;
import toolbox.utils.IPair;
import toolbox.utils.IStreamHandler;
import toolbox.utils.parsers.CsvDumper;

public class ESDownloadCsvProcess extends AProcessingTask   {
	
	private Log log = LogFactory.getLog(Catalog.class);
	
	private Date _timestamp=new Date(0);
	
	private Semaphore _postingDataLock = new Semaphore(1,true);
	private Semaphore _stoppingProcessingLock = new Semaphore(1,true);
	private ICatalog _catalog; 
	private String _targeFileName;

	private Long _fromIndex=0L;
	private String _query="";
	private List<String> _csvColsList=new ArrayList<>();
	private List<String> _preFilters=new ArrayList<>();
	private List< IPair<String,SORTING_ORDER> > _sortByFieldName=new ArrayList<>();
	
	private CsvDumper<IDbItem> _csvDump=null;
	
	private Boolean _csvDataGenerated = false;
	 
	public ESDownloadCsvProcess(IUserProfileData u, 
						 String name, 
						 String targeFileName,
						 List<String> csvColsList,
						 Long maxNbItems,
						 ICatalog c,
						 Long fromIndex,
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
		
		// progres messages will be sent by the CSV-Dump task
		// rather by than by the DB-extract task
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
			
			_csvDump=new CsvDumper<IDbItem>(this.getActiveUser(),
					this.getName()+":CsvGenerator",
					this.getTargetNbData(),
					this.getCsvColsList(),
					_timestamp,
					this.getTargeFileName());
			_csvDump.start();

			class CsvDumpForwarder implements IStreamHandler<DbSearchResult> {
				@Override public void handle(List<DbSearchResult> loadedDocsResults) throws InterruptedException {
					if (loadedDocsResults.size()>1) {
						log.warn("only one dbresult expected for thus usecase, got "+loadedDocsResults.size());						
					}
					DbSearchResult loadedResult = loadedDocsResults.get(0);
					
					// seems to return null value when 0 hits
		    		Long totalHits = loadedResult.getTotalHits();
		    		if (totalHits==null) { 
		    			log.error("No Hits for given CSV-dump request");			    			
		    		}
		    		_csvDump.setTargetNbData(totalHits);
		    		setTargetNbData(totalHits);
		    		List<IDbItem> items = loadedResult.getItems();
		    		addReceivedNbData(new Long(items.size()));
		    		_csvDump.postDataToDump(items);			    		
		    		addProcessedNbData(new Long(items.size()));						
			}}
			
			Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
					.getLoadDocsStreamFromDbStmt(this.getCatalog(),
					    				this.getFromIndex(), 
					    				this.getTargetNbData(),
					    				this.getQuery(),
					    				this.getPreFilters(),							    				
					    				this.getSortByFieldName()).execute(new CsvDumpForwarder());
			
			
    		
    		
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
				_csvDataGenerated=true;
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
		_csvDump.abort();			
		
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

	public Boolean isCsvDataGenerated() { return _csvDataGenerated; }

};
