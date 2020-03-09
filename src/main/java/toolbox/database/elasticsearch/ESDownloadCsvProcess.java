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
import metaindex.data.term.ICatalogTerm;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.users.WsControllerUser.CATALOG_MODIF_TYPE;
import toolbox.database.IDbItem;
import toolbox.database.elasticsearch.ESDataSource;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;
import toolbox.utils.parsers.ACsvParser;

public class ESDownloadCsvProcess extends AProcessingTask   {

	private Log log = LogFactory.getLog(Catalog.class);
	
	private Date _timestamp=new Date(0);
	
	private Semaphore _postingDataLock = new Semaphore(1,true);
	private Semaphore _stoppingProcessingLock = new Semaphore(1,true);
	private ESDataSource _datasource;
	private ICatalog _catalog; 
	
	public ESDownloadCsvProcess(IUserProfileData u, 
						 String name, 
						 Integer expectedNbActions,
						 ICatalog c,
						 Date timestamp,
						 ESDataSource ds) throws DataProcessException { 
		super(u,name);
		_catalog=c;
		_datasource=ds;
		_timestamp=timestamp;
		this.setTargetNbData(expectedNbActions);
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
				// wait for having received all data to be processed 
				Thread.sleep(200);
			} catch (InterruptedException e) { 
				e.printStackTrace(); 
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
			if (this.isTerminated() && !this.isRunning()) { return; }			
			
			getActiveUser().getCurrentCatalog().loadStatsFromDb();
			
			Boolean success=false;// tmp
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
