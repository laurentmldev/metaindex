package metaindex.app.control.websockets.catalogs;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;
import toolbox.utils.IProcessingTask;
import toolbox.utils.filetools.IBytesWriter;


public interface IHandleFileUploadProcess extends IProcessingTask   {

	public IBytesWriter getNewOutStream(String path, 
			 Long byteSize, 
			 AProcessingTask parentProcessingTask,
			 java.text.Normalizer.Form fileNameNormalizationForm) throws DataProcessException;
	
	
	public void postFileData(Integer fileId,Integer sequenceNumber, byte[] rawData)
			throws DataProcessException, InterruptedException;
	

};
