package metaindex.app.control.websockets.catalogs;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.ArrayList;

import metaindex.app.Globals;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;
import toolbox.utils.filetools.FileBinOutstream;
import toolbox.utils.filetools.FileDescriptor;


public class HandleFileUploadProcess extends AProcessingTask   {


	
	private Log log = LogFactory.getLog(HandleFileUploadProcess.class);
	
	private Boolean _interruptFlag=false;
	private Integer _curFileId=null;
	private List<String> _filesNames=new ArrayList<>();
	private String _uploadPath="";
	private Map<Integer,FileBinOutstream> _fileOutstreams = new HashMap<>();
	private Map<Integer,FileDescriptor> _fileDescriptors = new HashMap<>();
	
	private Semaphore _processorLock = new Semaphore(1,true);
	private Map<Integer, Map<Integer, byte[]> > _pendingFileData = new HashMap<>();
	
	
	public HandleFileUploadProcess(IUserProfileData u, 
						 String taskName, 
						 String uploadPath,
						 List<FileDescriptor> filesToDumpDescr) throws DataProcessException { 
		super(u,taskName);
		setUploadPath(uploadPath);
		this.addObserver(u);		
		
		Long totalBytesSize=0L;
		for (FileDescriptor desc : filesToDumpDescr) {
			_filesNames.add(desc.getName());
			FileBinOutstream outstream=new FileBinOutstream(
												getFullFsPath(desc.getName()),
												desc.getByteSize(),
												this,
												Globals.LOCAL_USERDATA_NORMALIZATION_FORM);
			_fileOutstreams.put(desc.getId(),outstream);
			_fileDescriptors.put(desc.getId(),desc);
			totalBytesSize+=desc.getByteSize();
		}		
		this.setTargetNbData(totalBytesSize);		
		
	}
		
	private String getFullFsPath(String fileName) {
		
		return _uploadPath+"/"+fileName;
	}
	@Override
	public void sendErrorMessageToUser(String msg, List<String> details) {
		getActiveUser().sendGuiErrorMessage(msg, details);		
	}
	@Override
	public void sendErrorMessageToUser(String msg) {
		getActiveUser().sendGuiErrorMessage(msg);		
	}

	public void postFileData(Integer fileId,Integer sequenceNumber, byte[] rawData) throws DataProcessException, InterruptedException {
		
		if (!_fileOutstreams.containsKey(fileId)) {
			throw new DataProcessException("No such file id in current processing");
		}
		_processorLock.acquire();
		try {
			setCurFileId(fileId);
			if (!_pendingFileData.containsKey(fileId)) { _pendingFileData.put(fileId,new HashMap<>()); }
			_pendingFileData.get(fileId).put(sequenceNumber, rawData);
		} catch(Throwable e) {
			e.printStackTrace();
			_processorLock.release();
		}
		_processorLock.release();
		
	}
	
	@Override
	public void addProcessedNbData(Long nbDataProcessed) {
		super.addProcessedNbData(nbDataProcessed);
		String curFileName=_filesNames.get(this.getCurFileId());
		getActiveUser().sendGuiProgressMessage(
    			getId(),
    			curFileName,
    			AProcessingTask.pourcentage(getProcessedNbData(), getTargetNbData()));
	}
	
	@Override
	public void run()  { 
		
		getActiveUser().sendGuiProgressMessage(
    			getId(),
    			getActiveUser().getText("Items.serverside.bulkprocess.progress", getName()),
    			AProcessingTask.pourcentage(getProcessedNbData(),getTargetNbData()));
		
		List<Integer> seqNbsToRemove = new ArrayList<>();
		
		while (!this.isAllDataProcessed() && !_interruptFlag) {
			try { 
				
				// dump pending files sequences
				_processorLock.acquire();
				for (Integer fileId : _pendingFileData.keySet()) {
					Map<Integer,byte[]> curFilePendingData = _pendingFileData.get(fileId);					
					for (Integer pendingSeqNb : curFilePendingData.keySet()) {
						_fileOutstreams.get(fileId).write(pendingSeqNb,curFilePendingData.get(pendingSeqNb));
						seqNbsToRemove.add(pendingSeqNb);
					}
					for (Integer seqNbToRemove : seqNbsToRemove) {
						curFilePendingData.remove(seqNbToRemove);
					}
					seqNbsToRemove.clear();
				}
				_processorLock.release();
				// wait for having received all data to be processed 
				Thread.sleep(500);		
				
			} catch (DataProcessException|InterruptedException e) { 
				e.printStackTrace(); 
				_processorLock.release();
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
		
		String failedFilesStr = "";
		
		for (Integer fileId : _fileDescriptors.keySet()) {
			FileDescriptor fileDesc = _fileDescriptors.get(fileId);
			FileBinOutstream fileDump = _fileOutstreams.get(fileId);
			try {
				fileDump.acquireLock();
				fileDump.stop(); 
				fileDump.releaseLock();
			} catch (DataProcessException | InterruptedException e) {
				if (failedFilesStr.length()>0) { failedFilesStr+=", "; }
				failedFilesStr+=fileDesc.getName();
				log.error("Unable to finalize properly upload of file "+fileDesc.getName());
			}			
		}	
		
		if (failedFilesStr.length()>0) {
			getActiveUser().sendGuiErrorMessage(getActiveUser().getText("Items.serverside.fileupload.failed",
													failedFilesStr));
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
			getActiveUser().getCurrentCatalog().loadStatsFromDb();
		} catch (DataProcessException e) {
			e.printStackTrace();			
		}
		
		getActiveUser().sendGuiProgressMessage(
    			getId(),
    			getActiveUser().getText("Items.serverside.bulkprocess.progress", getName()),
    			AProcessingTask.pourcentage(getProcessedNbData(), getTargetNbData()), false /*processing ended*/);
		
		getActiveUser().removeProccessingTask(this.getId());
	}

	public String getUploadPath() {
		return _uploadPath;
	}

	public void setUploadPath(String uploadPath) {
		this._uploadPath = uploadPath;
	}

	public Integer getCurFileId() {
		return _curFileId;
	}

	public void setCurFileId(Integer curFileId) {
		this._curFileId = curFileId;
	}


};
