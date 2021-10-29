package toolbox.utils.filetools;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;

/**
 * Parse received contents as CSV data and store it into ES DB
 * @author laurentml
 *
 */
public abstract class AFileOutstream implements IFileOutstream {
	
	
	private Log log = LogFactory.getLog(AFileOutstream.class);
	
	private Long _fileTargetBytesSize=0L;
	private Map<Integer,byte[] > _bufferedContentsBySequenceNumber = new HashMap<>();	
	private Integer _lastDumpedSequenceNumber=0;
	private Semaphore _dumperLock = new Semaphore(1,true);
	private Long _nbBytesDumped=0L;
	private AProcessingTask _parentProcessingTask=null;
	private Boolean _stopDone = false;
	
	public AFileOutstream(Long fileBytesSize, AProcessingTask parentProcessingTask) {
		_fileTargetBytesSize=fileBytesSize;
		_parentProcessingTask=parentProcessingTask;		
	}

	@Override
	public void acquireLock() throws InterruptedException { _dumperLock.acquire(); }

	@Override
	public void releaseLock() { _dumperLock.release(); }
	
	/** Receive byte contents in proper order */
	protected abstract void handleReceivedContentsSequence(byte[] contents,Integer lastDumpedSequenceNumber) throws IOException;
	
	protected abstract void handleStop() throws DataProcessException;
	
	@Override
	public void write(Integer sequenceNumber, byte[] contents) throws DataProcessException {
		
		try {
			_dumperLock.acquire();
			Long contentsLength=new Long(contents.length);
			_parentProcessingTask.addReceivedNbData(contentsLength);
			
			if (sequenceNumber.equals(_lastDumpedSequenceNumber+1)) {
				handleReceivedContentsSequence(contents,_lastDumpedSequenceNumber);
				
				_lastDumpedSequenceNumber++;	
				_nbBytesDumped+=contentsLength;				
				_parentProcessingTask.addProcessedNbData(contentsLength);
				//log.error("### processed data : "+_parentProcessingTask.getProcessedNbData()+" (+ "+contents.length+")");
				
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
			
			_dumperLock.release();			
			throw new DataProcessException("unable to write into outstream '"+getName()+"'"
											+" : "+ex.getMessage());
		}
			
	}	

	@Override
	public void start() throws DataProcessException {
		// Nothing special to do		
	}
	
	@Override
	public void stop() throws DataProcessException {
		
		if (_stopDone==true) { return; }
		//log.error("### written "+_nbBytesDumped+" bytes, buffer contains "+_bufferedContentsBySequenceNumber.size()+" unprocessed data");
		
		_stopDone=true;
		
		try { handleStop(); }
		catch (DataProcessException e) {
			_dumperLock.release();
			throw e;
		}
		
		
		
	}

	@Override
	public Long getFileTargetBytesSize() {
		return _fileTargetBytesSize;
	}
	
	@Override
	public void setFileTargetBytesSize(Long fileTargetBytesSize) {
		this._fileTargetBytesSize = fileTargetBytesSize;
	}

	public Long getNbBytesDumped() {
		return _nbBytesDumped;
	}

	public void setNbBytesDumped(Long _nbBytesDumped) {
		this._nbBytesDumped = _nbBytesDumped;
	}

	
	}