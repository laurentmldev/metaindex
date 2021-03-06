package toolbox.utils;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import toolbox.patterns.observer.*;
import toolbox.utils.filetools.FileBinOutstream;
import metaindex.data.userprofile.IUserProfileData;

public abstract class AProcessingTask implements IProcessingTask {
	
	private Log log = LogFactory.getLog(AProcessingTask.class);
	
	public static Float pourcentage(Long current, Long target) {
		if (target.equals(0L)) { return 0F; }
    	return (current.floatValue() / target.floatValue()) * 100.0F;    	
	}
	
	private class RunnerThread extends Thread {		
		IProcessingTask _processingToBeRun;
		public RunnerThread(String name, IProcessingTask p) { 
			super(name);
			_processingToBeRun=p;
		}
		@Override
		public void run() {
			_processingToBeRun.run();
			//log.error("### id="+this.getId()+" - run");
		}
	}
	
	private static Integer processingTasksCounter=0;
	private static final Integer PROCESSING_TASK_ID_START=1000;
	
	synchronized public static Integer getNewProcessingTaskId() {
		return PROCESSING_TASK_ID_START+processingTasksCounter++;
	}
	
	private RunnerThread _runnerThread; 
	private Integer _processingTaskId=0;
	private IUserProfileData _activeUser = null;
	
	private List<IObserver<IProcessingTask> > _observers = new ArrayList<IObserver<IProcessingTask> >();
	
	private String _processingType="";
	
	private Long _targetNbData=0L;	
	private Long _receivedNbData=0L;
	private Long _processedNbData=0L;
	
	public AProcessingTask(IUserProfileData activeUser,String name) {
		this._activeUser=activeUser;		
		_processingTaskId=getNewProcessingTaskId();
		_runnerThread = new RunnerThread(name,this);
		activeUser.addProcessingTask(this);		
	}
	
	@Override
	public IUserProfileData getActiveUser() { return _activeUser; }
	
	@Override
	public void notifyObservers() throws InterruptedException {
		Iterator<IObserver<IProcessingTask> > it = _observers.iterator();
		while (it.hasNext()) { it.next().notifyChange(this); }
	}	
	
	@Override
	public void addObserver(IObserver<IProcessingTask> newObserver) { _observers.add(newObserver); }
	
	@Override
	public void removeObserver(IObserver<IProcessingTask> oldObserver) { _observers.remove(oldObserver); }

	@Override
	public String getProcessingType() { return _processingType; }
	
	@Override
	public void setProcessingType(String processingId) { this._processingType=processingId; }
	
	@Override
	public String getName() {
		return _runnerThread.getName();
	}

	@Override
	public Boolean isAllDataReceived() { 
		return getTargetNbData().equals(getReceivedNbData()); 
	}
	
	@Override
	public Boolean isAllDataProcessed() { 
		return getTargetNbData().equals(getProcessedNbData()); 
	}

	@Override
	public Long getTargetNbData() {
		return _targetNbData;
	}
	@Override
	public void setTargetNbData(Long targetNbData) {
		this._targetNbData = targetNbData;
	}
	@Override
	public Long getReceivedNbData() {
		return _receivedNbData;
	}
	@Override
	public void addReceivedNbData(Long nbDataAdded) {
		_receivedNbData+=nbDataAdded;
	}
	@Override
	public Long getProcessedNbData() {
		return _processedNbData;
	}
	@Override
	public void addProcessedNbData(Long nbDataProcessed) {
		_processedNbData+=nbDataProcessed;
	}
	
	@Override
	public Integer getId() {
		return _processingTaskId;
	}
	
	@Override
	public Boolean isTerminated() { return this.getProcessedNbData().equals(this.getTargetNbData()); }	
	
	@Override
	public void start() { _runnerThread.start(); }
	
	public void joinRunnerThread() throws InterruptedException {
		// avoid deadlock if a thread tries to join itself
		if (Thread.currentThread()!=_runnerThread){ _runnerThread.join(); } 
	}
	
	@Override
	public Boolean isRunning() { return _runnerThread.isAlive(); }
}
