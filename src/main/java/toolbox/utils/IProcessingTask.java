package toolbox.utils;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.exceptions.DataProcessException;
import toolbox.patterns.observer.*;

import java.util.List;

// TODO : remove dep to metaindex classes
import metaindex.data.userprofile.IUserProfileData;

public interface IProcessingTask extends IObservable<IObserver<IProcessingTask> >, IIdentifiable<Integer>, IRunnable {

	
	public void start() throws DataProcessException;
	public void stop() throws DataProcessException;
	public void abort();
	Boolean isTerminated();	
	
	String getProcessingType();
	void setProcessingType(String processingType);

	Long getTargetNbData();
	void setTargetNbData(Long targetNbData);
	Long getReceivedNbData();
	void addReceivedNbData(Long nbDataAdded);
	Long getProcessedNbData();
	void addProcessedNbData(Long nbDataProcessed);

	
	IUserProfileData getActiveUser();
	void sendErrorMessageToUser(String msg, List<String> details);
	void sendErrorMessageToUser(String msg);
	Boolean isAllDataReceived();
	Boolean isAllDataProcessed();
	
}
