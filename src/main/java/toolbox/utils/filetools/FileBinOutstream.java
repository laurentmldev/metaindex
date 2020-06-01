package toolbox.utils.filetools;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;
import toolbox.utils.ILockable;

/**
 * Dump received contents into target (bin) file, preserving order depending on given sequence number
 * @author laurentml
 *
 */
public class FileBinOutstream implements ILockable {
	
	private Log log = LogFactory.getLog(FileBinOutstream.class);
	
	private String _filePath="";
	private Long _fileTargetBytesSize=0L;

	private Map<Integer,byte[] > _bufferedContentsBySequenceNumber = new HashMap<>();
	private Integer _lastDumpedSequenceNumber=0;
	private BufferedOutputStream _outstream = null;
	private Semaphore _dumperLock = new Semaphore(1,true);
	private Long _nbBytesDumped=0L;
	private AProcessingTask _processingTask=null;
	private String _tmpFilePath;
	
	private Boolean _stopDone = false;
	
	
	public FileBinOutstream(String filePath,Long fileBytesSize, AProcessingTask proc) throws DataProcessException {
		setFilePath(filePath);
		setTmpFilePath(filePath+".tmp"+proc.getId());
		setFileTargetBytesSize(fileBytesSize);

		try {
			_outstream=new BufferedOutputStream(new FileOutputStream(getTmpFilePath()));
			_processingTask=proc;
		} catch (IOException ex) {
			throw new DataProcessException("unable to open outstream to file "
								+getTmpFilePath()+" : "+ex.getMessage());
		}
	}
	

	@Override
	public void acquireLock() throws InterruptedException { _dumperLock.acquire(); }

	@Override
	public void releaseLock() { _dumperLock.release(); }
	
	public void write(Integer sequenceNumber, byte[] contents) throws DataProcessException {
		
		try {
			_dumperLock.acquire();
			_processingTask.addReceivedNbData(new Long(contents.length));
			
			if (sequenceNumber.equals(_lastDumpedSequenceNumber+1)) {
				_outstream.write(contents);
				_lastDumpedSequenceNumber++;	
				_nbBytesDumped+=new Long(contents.length);				
				_processingTask.addProcessedNbData(new Long(contents.length));
				//log.error("### processed data : "+_processingTask.getProcessedNbData()+" (+ "+contents.length+")");
				
				// check if some next data has been received before and then buffered
				Integer nextSequenceNumber=_lastDumpedSequenceNumber+1;
				if (_bufferedContentsBySequenceNumber.containsKey(nextSequenceNumber)) {
					byte[] nextContents=_bufferedContentsBySequenceNumber.get(nextSequenceNumber);					
					_bufferedContentsBySequenceNumber.remove(nextSequenceNumber);
					_dumperLock.release();
					write(nextSequenceNumber,nextContents);					
				} else {
					_dumperLock.release();
				}
				
			} else {				
				_bufferedContentsBySequenceNumber.put(sequenceNumber,contents);
				_dumperLock.release();
			}	
			
			if (_nbBytesDumped.equals(this.getFileTargetBytesSize())) { 
				_dumperLock.acquire();
				if (_stopDone==false) {stop(); }
				_dumperLock.release();
			}
			
		}
		catch (IOException|InterruptedException ex) {
			try {_outstream.close();}
			catch(Throwable t) {
				log.error("While handling exception, unable to close outstream for file "
						+getTmpFilePath()+" : "+t.getMessage());
			}
			_dumperLock.release();			
			throw new DataProcessException("unable to write into outstream for file "
											+getTmpFilePath()+" : "+ex.getMessage());
		}
			
	}
	public void stop() throws DataProcessException {
		
		if (_stopDone==true) { return; }
		//log.error("### written "+_nbBytesDumped+" bytes, buffer contains "+_bufferedContentsBySequenceNumber.size()+" unprocessed data");
		try {
			_stopDone=true;
			_outstream.flush();
			_outstream.close();
			
		} catch (IOException e) {	
			_dumperLock.release();
			File userdataFile = new File(getTmpFilePath());
			if (userdataFile.exists()) {
				if (!userdataFile.delete()) {
					log.error("unable to delete local userdata content : "+userdataFile);
				}
			}
			throw new DataProcessException("Unable to close outstream to file "+getFilePath());
		}
		
		// when written last bytes, finalize generated file
		if (!_nbBytesDumped.equals(getFileTargetBytesSize())) {
			File userdataFile = new File(getTmpFilePath());
			if (userdataFile.exists()) {
				if (!userdataFile.delete()) {
					log.error("unable to delete local userdata content : "+userdataFile);
				}
			}		
			_dumperLock.release();
			throw new DataProcessException("Closed outstream to file "+getFilePath()+" while all contents were not received yet");	
		}
		
		// rename file to definitive name
		File tmpUserdataFile = new File(getTmpFilePath());
		File userdataFile = new File(getFilePath());
		if (userdataFile.exists()) {
			if (!userdataFile.delete()) {
				log.error("unable to delete local userdata content : "+userdataFile);
			}
		}	
		tmpUserdataFile.renameTo(userdataFile);
	}

	public String getFilePath() {
		return _filePath;
	}

	public void setFilePath(String filePath) {
		this._filePath = filePath;
	}

	public Long getFileTargetBytesSize() {
		return _fileTargetBytesSize;
	}

	public void setFileTargetBytesSize(Long fileTargetBytesSize) {
		this._fileTargetBytesSize = fileTargetBytesSize;
	}

	public String getTmpFilePath() {
		return _tmpFilePath;
	}

	public void setTmpFilePath(String tmpFilePath) {
		this._tmpFilePath = tmpFilePath;
	}

	
	}