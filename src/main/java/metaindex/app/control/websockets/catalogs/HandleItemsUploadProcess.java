package metaindex.app.control.websockets.catalogs;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.IDbItemsProcessor;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;
import toolbox.utils.IPair;
import toolbox.utils.filetools.IBytesToDbWriter;
import toolbox.utils.filetools.FileDescriptor;
import toolbox.utils.filetools.IBytesWriter;
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;
import metaindex.app.control.websockets.catalogs.ExcelSpreadsheetBytesToDbWriter.CALC_TYPE;


import java.util.List;
import java.util.Map;


/**
 * 
 * Parse and store received CSV data into ES database
 * @author laurentml
 *
 */
public class HandleItemsUploadProcess extends HandleFileUploadProcess   {

	private IBytesToDbWriter _dbItemsOutstream;
	
	public static Boolean isCsvFile(String fileName) {
		return fileName.endsWith(".csv");
	}
	
	@Override
	public IBytesWriter getNewOutStream(String path, 
			 Long byteSize, 
			 AProcessingTask parentProcessingTask,
			 java.text.Normalizer.Form fileNameNormalizationForm) throws DataProcessException {
		
		if (isCsvFile(path)) {
			_dbItemsOutstream=new CsvBytesToDbWriter(path,byteSize,parentProcessingTask);
		} else if (path.endsWith(".xls")) {
			_dbItemsOutstream=new ExcelSpreadsheetBytesToDbWriter(CALC_TYPE.XLS,byteSize,parentProcessingTask,0);
		} else if (path.endsWith(".xlsx") ) {
			_dbItemsOutstream=new ExcelSpreadsheetBytesToDbWriter(CALC_TYPE.XLSX,byteSize,parentProcessingTask,0);
		} else if (path.endsWith(".ods")) {
			_dbItemsOutstream=new OdfSpreadsheetBytesToDbWriter(CALC_TYPE.ODF,byteSize,parentProcessingTask,0);
		} else {
			throw new DataProcessException("Unknown file format to import items ("+path+"). Allowed extensions are csv|xls|xlsx|ods");
		}
		return _dbItemsOutstream;		
	}
	
	public HandleItemsUploadProcess(IUserProfileData u,String taskName,
						List<FileDescriptor> filesToProcess) throws DataProcessException {		
		super(u,taskName,"items-db-uploader",filesToProcess);		
	}
	
	public void init(IDbItemsProcessor dbItemsBulkProcess,
			Map<String,PARSING_FIELD_TYPE> parsingTypes,
			Map<String,String> fieldsMapping) throws DataProcessException {
		
		_dbItemsOutstream.init(dbItemsBulkProcess,parsingTypes,fieldsMapping);
	}
	
	@Override
	public void start() throws DataProcessException {
		_dbItemsOutstream.start();
		super.start();
	}
	
	@Override
	public void stop() throws DataProcessException{
		super.stop();
		_dbItemsOutstream.stop();
	}


};
