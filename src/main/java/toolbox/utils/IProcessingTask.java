package toolbox.utils;

import toolbox.patterns.observer.*;
import toolbox.utils.parsers.IListParser;

import java.util.List;

import metaindex.data.userprofile.IUserProfileData;

public interface IProcessingTask extends IObservable<IObserver<IProcessingTask> >, IIdentifiable<Integer>, IRunnable {

	
	public void start();
	public void stop();
	public void abort();
	Boolean isTerminated();	
	
	String getProcessingType();
	void setProcessingType(String processingType);

	Integer getTargetNbData();
	void setTargetNbData(Integer targetNbData);
	Integer getReceivedNbData();
	void addReceivedNbData(Integer nbDataAdded);
	Integer getProcessedNbData();
	void addProcessedNbData(Integer nbDataProcessed);

	
	IUserProfileData getActiveUser();
	void sendErrorMessageToUser(String msg, List<String> details);
	void sendErrorMessageToUser(String msg);
	Boolean isAllDataReceived();
	Boolean isAllDataProcessed();
	
}
