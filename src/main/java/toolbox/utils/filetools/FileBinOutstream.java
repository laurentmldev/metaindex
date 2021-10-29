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
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import toolbox.database.IDbItemsProcessor;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;
import toolbox.utils.ILockable;
import toolbox.utils.IPair;
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;

/**
 * Dump received contents into target (bin) file, preserving order depending on given sequence number
 * @author laurentml
 *
 */
public class FileBinOutstream extends AFileOutstream {
	
	private Log log = LogFactory.getLog(FileBinOutstream.class);
	
	private String _filePath="";	
	private BufferedOutputStream _outstream = null;
	private String _tmpFilePath;
	private Form _normalizeForm = null;
	
		
	public FileBinOutstream(String filePath,Long fileBytesSize, AProcessingTask parentProcessingTask, Form normalizeForm) throws DataProcessException {
		super(fileBytesSize,parentProcessingTask);
		_normalizeForm=normalizeForm;
		
		String normalizedFilePath=filePath;
		if (_normalizeForm!=null) {
			normalizedFilePath= Normalizer.normalize(filePath, _normalizeForm);
		}
		 
		setFilePath(normalizedFilePath);
		setTmpFilePath(normalizedFilePath+".tmp"+parentProcessingTask.getId());

		try {
			_outstream=new BufferedOutputStream(new FileOutputStream(getTmpFilePath()));
		} catch (IOException ex) {
			throw new DataProcessException("unable to open outstream to file "
								+getTmpFilePath()+" : "+ex.getMessage());
		}
	}
	
	protected void handleReceivedContentsSequence(byte[] contents,Integer lastDumpedSequenceNumber) throws IOException {
		_outstream.write(contents);
	}
	
	public void abort() throws DataProcessException {
		File tmpTargetFile = new File(getTmpFilePath());
		if (tmpTargetFile.exists()) {
			if (!tmpTargetFile.delete()) {
				log.error("unable to delete local userdata content : "+tmpTargetFile);
			}
		}	
	}
	
	protected void handleStop() throws DataProcessException {
		File tmpTargetFile = new File(getTmpFilePath());
		
		try { _outstream.flush(); }
		catch (IOException e) {
			if (tmpTargetFile.exists()) {
				if (!tmpTargetFile.delete()) {
					log.error("unable to delete local userdata content : "+tmpTargetFile);
				}
			}
			throw new DataProcessException("Unable to write final contents into target file, sorry");
			
		}
		
		// when written last bytes, finalize generated file
		if (!getNbBytesDumped().equals(getFileTargetBytesSize())) {
			
			if (tmpTargetFile.exists()) {
				if (!tmpTargetFile.delete()) {
					log.error("unable to delete local userdata content : "+tmpTargetFile);
				}
			}		
			
			throw new DataProcessException("Closed outstream to file "+getFilePath()+" while all contents were not received yet");	
		}
		else {
			File finalizedTargetFile = new File(getFilePath());
			boolean success = tmpTargetFile.renameTo(finalizedTargetFile);
			if (!success) {
				if (!tmpTargetFile.delete()) {
					log.error("unable to delete local userdata content : "+tmpTargetFile);
				}
				throw new DataProcessException("unable to finalize file "
						+getFilePath()+", sorry operation aborted");
				
			}			
		}
	}

	public String getFilePath() {
		return _filePath;
	}

	public String getName() { return getFilePath(); }
	
	public void setFilePath(String filePath) {
		this._filePath = filePath;
	}

	public String getTmpFilePath() {
		return _tmpFilePath;
	}

	public void setTmpFilePath(String tmpFilePath) {
		this._tmpFilePath = tmpFilePath;
	}


	
	}