package metaindex.app.control.websockets.catalogs;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import toolbox.database.IDbItem;
import toolbox.database.IDbItemsProcessor;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;
import toolbox.utils.IPair;
import toolbox.utils.filetools.ADbOutstream;
import toolbox.utils.parsers.IListParser.ParseException;
import toolbox.utils.parsers.SpreadsheetDbItemsParser;
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;


/**
 * Parse received contents as CSV data and store it into ES DB
 * @author laurentml
 *
 */
public class SpreadsheetDbOutstream extends ADbOutstream {
	
	public enum CALC_TYPE {UNKNOWN,XLS,XLSX,OOXML};
	private Log log = LogFactory.getLog(SpreadsheetDbOutstream.class);
	
	private static final Integer NBENTRIES_TO_POST_TRESHOLD=500;
	
	private Integer _sheetNumber=0;
	private CALC_TYPE _calcType=CALC_TYPE.UNKNOWN;
	
	private SpreadsheetDbItemsParser _itemsParser;	
	private IDbItemsProcessor _itemsBulkProcessor;
	
	private PipedInputStream _in;
	private PipedOutputStream _out;
	
	public SpreadsheetDbOutstream(CALC_TYPE calcType, Long fileBytesSize, AProcessingTask parentProcessingTask,Integer sheetNumber) 
			throws DataProcessException {		
		super(fileBytesSize,parentProcessingTask);
		_calcType=calcType;
		_sheetNumber=sheetNumber;
		
	}

	@Override
	public void init(IDbItemsProcessor itemsBulkProcess,
					 List<IPair<String,PARSING_FIELD_TYPE>> parsingTypes,
					 Map<String,String> fieldsMapping) throws DataProcessException {
		_itemsBulkProcessor = itemsBulkProcess;
		_itemsParser = new SpreadsheetDbItemsParser();
		_itemsParser.setCsvColsTypes(parsingTypes);
		_itemsParser.setChosenFieldsMapping(fieldsMapping);
		
		try {
			_in = new PipedInputStream(_out);
			_out = new PipedOutputStream();
		} catch (IOException e) {
			throw new DataProcessException("Unable to redirect Spreadsheet bytes stream to Excel parser: "+e.getMessage(),e);
		}		
	}
	@Override
	public void start() throws DataProcessException {
		_itemsBulkProcessor.start();
	}
	

	@Override
	protected void handleStop() throws DataProcessException {
		
		Workbook workbook;
		try {
			switch (_calcType) {
				case XLS:
					workbook = new HSSFWorkbook(_in);
					break;
				case XLSX:
					workbook = new XSSFWorkbook(_in);
					break;
				default:
					throw new DataProcessException("Unsupported calc. format: "+_calcType.toString());
			}
		} catch (IOException e) {
			throw new DataProcessException("Unable to open workbook from received spreadsheet bytes: "+e.getMessage(),e);
		}
		
		List<IDbItem> entries = new ArrayList<>();
		
		Sheet sheet = workbook.getSheetAt(_sheetNumber);
		Integer rowNb=0;
		try {
			// parse rows into IDbItems to be injected into DB
			// flush them every n entries
			for (Row row : sheet) {				
				rowNb++;
				// skip first row containing names
				// mapping between spreadsheet names and DB contents
				// has been provided already, so no need to parse this row
				if (rowNb==1) { continue; }
				IDbItem newEntry;
				newEntry = _itemsParser.parse(row);				
				entries.add(newEntry);		
				if (entries.size()==NBENTRIES_TO_POST_TRESHOLD) {
					_itemsBulkProcessor.postDataToIndexOrUpdate(entries);
					entries=new ArrayList<>();
				}
			}
			
			// flush remaining items
			if (entries.size()>0) {
				_itemsBulkProcessor.postDataToIndexOrUpdate(entries);
			}
			
			// closing workbook
			workbook.close();
			_out.close();
			_in.close();
			
		} catch (ParseException e) {
			try { 
				workbook.close();
				_out.close();
				_in.close();
			}
			catch (IOException e2) {
				throw new DataProcessException("Unable to close workbook: "+e.getMessage(),e);
			}
			throw new DataProcessException("Unable to post spreadsheet contents into database: "+e.getMessage(),e);
		} catch (IOException e) {
			throw new DataProcessException("Unable to close workbook: "+e.getMessage(),e);
		}
		
		
	}

	@Override
	public void abort() throws DataProcessException {		
		try { 
			_out.close();
			_in.close();
		}
		catch (IOException e) {
			throw new DataProcessException("Unable to close workbook: "+e.getMessage(),e);
		}
	}

	@Override
	public String getName() {
		return "Spreadsheet-Items-Importer";
	}


	@Override
	protected void handleReceivedContentsSequence(byte[] contents, Integer lastDumpedSequenceNumber)
			throws IOException {
		
		_out.write(contents);		
	}

	

	
	}