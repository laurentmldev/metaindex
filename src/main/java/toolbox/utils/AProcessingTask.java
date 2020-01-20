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

import toolbox.patterns.observer.*;
import metaindex.data.userprofile.IUserProfileData;

public abstract class AProcessingTask implements IProcessingTask {
	
	public static Float pourcentage(Integer current, Integer target) {
		if (target.equals(0)) { return 0F; }
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
	
	private String _processingId="";
	
	private Integer _targetNbData=0;	
	private Integer _receivedNbData=0;
	private Integer _processedNbData=0;
	
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
	public String getProcessingType() { return _processingId; }
	
	@Override
	public void setProcessingType(String processingId) { this._processingId=processingId; }
	
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
	public Integer getTargetNbData() {
		return _targetNbData;
	}
	@Override
	public void setTargetNbData(Integer targetNbData) {
		this._targetNbData = targetNbData;
	}
	@Override
	public Integer getReceivedNbData() {
		return _receivedNbData;
	}
	@Override
	public void addReceivedNbData(Integer nbDataAdded) {
		_receivedNbData+=nbDataAdded;
	}
	@Override
	public Integer getProcessedNbData() {
		return _processedNbData;
	}
	@Override
	public void addProcessedNbData(Integer nbDataProcessed) {
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
	
	@Override
	public Boolean isRunning() { return _runnerThread.isAlive(); }
}
