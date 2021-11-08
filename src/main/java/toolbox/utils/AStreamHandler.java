package toolbox.utils;

import java.io.IOException;

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
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;
import toolbox.utils.IStreamHandler;

public abstract class AStreamHandler<T> extends AProcessingTask implements IStreamHandler<T>   {

	private Log log = LogFactory.getLog(Catalog.class);
	
	
	// send progress message every n pourcents of progress
	private static final Integer PROGRESS_MSG_POURCENT_STEP=1;
	
	private Semaphore _postingDataLock = new Semaphore(1,true);	
	private Semaphore _stoppingProcessingLock = new Semaphore(1,true);
	
	private String _targetFileName="";
	private String _progressMessageName="";
	private List<T> _bufferizedDataToHandle = new ArrayList<>();	
	
	private Boolean finalized=false;
	
	public AStreamHandler(IUserProfileData u, 
						 String name, 
						 Long expectedNbActions,
						 Date timestamp,
						 String targetFileName,
						 String progressMessageName) throws DataProcessException { 
		super(u,name);
		this.setTargetNbData(expectedNbActions);
		this.setTargetFileName(targetFileName);
		this.setProgressMessageName(progressMessageName);
		this.addObserver(u);
		
	}
	
	@Override
	public void handle(List<T> o) throws DataProcessException {
		try {
			_postingDataLock.acquire();
			_bufferizedDataToHandle.addAll(o);
			addReceivedNbData(new Long(o.size()));
			//log.error("### added "+o.size()+" data to dump -> "+_bufferizedDataToHandle.size()+" in processing queue ");
			_postingDataLock.release();
			Thread.sleep(10);
		 } catch (InterruptedException e) {
			throw new DataProcessException("unable to handle given elements: "+e.getMessage(),e); 
		 }
	}
	
	public void lock() throws InterruptedException { _postingDataLock.acquire(); }
	public void unlock() { _postingDataLock.release(); }
	
	public abstract void beforeFirst();
	public abstract void handle(T o);
	public abstract void flush() throws IOException;
	public abstract void afterLast() throws IOException;
	
	@Override
	public void run()  { 
		
		getActiveUser().sendGuiProgressMessage(
    			getId(),
    			getActiveUser().getText(this.getProgressMessageName(), getName()),
    			AProcessingTask.pourcentage(getProcessedNbData(),getTargetNbData()));
		
		
		while (!this.isAllDataProcessed()) {
			try { 
				// wait for having received all data to be processed 
				Thread.sleep(200);
				
				if (this.getProcessedNbData()==0) { beforeFirst(); }
				
				_postingDataLock.acquire();
				//if (_bufferizedDataToHandle.size()>0) { log.error("#### dumping "+_bufferizedDataToHandle.size()+" lines"); }
				for (T d : _bufferizedDataToHandle) {
					handle(d);
					addProcessedNbData(1L);

					if (this.getProcessedNbData()/this.getTargetNbData()%PROGRESS_MSG_POURCENT_STEP <= 0.01) {
						getActiveUser().sendGuiProgressMessage(
			    			getId(),
			    			getActiveUser().getText(this.getProgressMessageName(), getName()),
			    			AProcessingTask.pourcentage(getProcessedNbData(), getTargetNbData()), true /*processing continuing*/);
					}
					
				}				
				_bufferizedDataToHandle.clear();
				_postingDataLock.release();
				
				flush();
				
			} catch (InterruptedException|IOException e) { 
				e.printStackTrace(); 
				this.abort();
				return;
			}
		}
		
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
			if (finalized==false) {
				afterLast();
				
				getActiveUser().sendGuiProgressMessage(
		    			getId(),
		    			getActiveUser().getText("Items.serverside.gencsvprocess.progress", getName()),
		    			AProcessingTask.pourcentage(getProcessedNbData(), getTargetNbData()), false /*processing ended*/);
			
				getActiveUser().removeProccessingTask(this.getId());
				
				finalized=true;
			}

			_stoppingProcessingLock.release();
		} catch (InterruptedException|IOException e) {
			e.printStackTrace();
			_stoppingProcessingLock.release();	
			this.abort();
			return;
		}
		
		
	}
	@Override
	public void abort() {
		
		getActiveUser().sendGuiProgressMessage(
    			getId(),
    			getActiveUser().getText("Items.serverside.gencsvprocess.progress", getName()),
    			AProcessingTask.pourcentage(getProcessedNbData(), getTargetNbData()), false /*processing ended*/);
		
		getActiveUser().removeProccessingTask(this.getId());
	}
	

	public String getTargetFileName() {
		return _targetFileName;
	}
	public void setTargetFileName(String targetFileName) {
		this._targetFileName = targetFileName;
	}
	
	@Override
	public void sendErrorMessageToUser(String msg, List<String> details) {
		getActiveUser().sendGuiErrorMessage(msg, details);		
	}
	@Override
	public void sendErrorMessageToUser(String msg) {
		getActiveUser().sendGuiErrorMessage(msg);		
	}


	public String getProgressMessageName() {
		return _progressMessageName;
	}

	public void setProgressMessageName(String msg) {
		this._progressMessageName = msg;
	}
	
};
