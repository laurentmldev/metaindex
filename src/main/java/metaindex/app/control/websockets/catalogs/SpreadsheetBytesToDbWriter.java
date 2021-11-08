package metaindex.app.control.websockets.catalogs;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import toolbox.database.IDbItem;
import toolbox.database.IDbItemsProcessor;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;
import toolbox.utils.IPair;
import toolbox.utils.IStreamProducer;
import toolbox.utils.filetools.ABytesWriter;
import toolbox.utils.filetools.IBytesToDbWriter;
import toolbox.utils.parsers.IListParser.ParseException;
import toolbox.utils.parsers.SpreadsheetDbItemsParser;
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;


/**
 * Parse received contents as CSV data and store it into ES DB
 * @author laurentml
 *
 */
public class SpreadsheetBytesToDbWriter  extends ABytesWriter  implements IBytesToDbWriter,IStreamProducer<IDbItem> {
	
	public enum CALC_TYPE {UNKNOWN,XLS,XLSX,OOXML};
	private Log log = LogFactory.getLog(SpreadsheetBytesToDbWriter.class);
	
	private static final Integer NBENTRIES_TO_POST_TRESHOLD=500;
	
	private Integer _sheetNumber=0;
	private CALC_TYPE _calcType=CALC_TYPE.UNKNOWN;
	
	private SpreadsheetDbItemsParser _itemsParser;	
	private IDbItemsProcessor _itemsBulkProcessor;
	
	private ByteArrayOutputStream _out;
	
	private Workbook _workbook;
	
	public SpreadsheetBytesToDbWriter(CALC_TYPE calcType, Long fileBytesSize, AProcessingTask parentProcessingTask,Integer sheetNumber) 
			throws DataProcessException {		
		super(fileBytesSize,parentProcessingTask);
		_calcType=calcType;
		_sheetNumber=sheetNumber;
		
	}

	
	private void readSpreadSheet(byte[] dataBytes) throws DataProcessException {
		
		ByteArrayInputStream inputStream = new ByteArrayInputStream(dataBytes);

		try {			
			switch (_calcType) {
				case XLS:
					_workbook = new HSSFWorkbook(inputStream);
					break;
				case XLSX:
					_workbook = new XSSFWorkbook(inputStream);
					break;
				default:
					throw new DataProcessException("Unsupported calc. format: "+_calcType.toString());
			}
		} catch (IOException e) {
			throw new DataProcessException("Unable to open _workbook from received spreadsheet bytes: "+e.getMessage(),e);
		}

		List<IDbItem> entries = new ArrayList<>();
		
		Sheet sheet = _workbook.getSheetAt(_sheetNumber);
		Integer rowNb=0;
		try {
			// parse rows into IDbItems to be injected into DB
			// flush them every n entries
			for (Row row : sheet) {				
				rowNb++;
				
				// extracting cols names from first row 
				if (rowNb==1) {					
					Iterator<Cell> cellIt = row.cellIterator();
					String[] colsNames=new String[row.getLastCellNum()];	
					Integer colIdx=0;
					while (cellIt.hasNext()) {
						Cell curCell = cellIt.next();
						colsNames[colIdx]=curCell.getStringCellValue();
						colIdx++;
					}
					_itemsParser.setColsNames(colsNames);
					continue;
				}
				
				IDbItem newEntry;
				newEntry = _itemsParser.parse(row);				
				entries.add(newEntry);		
				if (entries.size()==NBENTRIES_TO_POST_TRESHOLD) {
					_itemsBulkProcessor.handle(entries);
					entries=new ArrayList<>();
				}
			}
			
			// flush remaining items
			if (entries.size()>0) {
				_itemsBulkProcessor.handle(entries);
			}
			
			// closing _workbook
			_workbook.close();
			_out.close();

			
		} catch (ParseException e) {
			try { 
				_workbook.close();
				_out.close();
			}
			catch (IOException e2) {
				throw new DataProcessException("Unable to close _workbook: "+e.getMessage(),e);
			}
			throw new DataProcessException("Unable to post spreadsheet contents into database: "+e.getMessage(),e);
		} catch (IOException e) {
			throw new DataProcessException("Unable to close _workbook: "+e.getMessage(),e);
		}
	}
	
	@Override
	public void init(IDbItemsProcessor itemsBulkProcess,
					 Map<String,PARSING_FIELD_TYPE> parsingTypes,
					 Map<String,String> fieldsMapping) throws DataProcessException {
		_itemsBulkProcessor = itemsBulkProcess;
		_itemsParser = new SpreadsheetDbItemsParser();
		_itemsParser.setFieldsParsingTypes(parsingTypes);
		_itemsParser.setFieldsMapping(fieldsMapping);
		
		_out = new ByteArrayOutputStream();
				
	}
	@Override
	public void start() throws DataProcessException {
		_itemsBulkProcessor.start();
	}
	

	@Override
	protected void handleStop() throws DataProcessException {
		try {
			readSpreadSheet(_out.toByteArray()); 
			_out.close();
			_workbook.close();
			
		} catch (IOException e) {
			throw new DataProcessException("Unable to close workbook: "+e.getMessage(),e);
		}
		
	
	}

	@Override
	public void abort() throws DataProcessException {		
		try { 
			_out.close();
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
			throws DataProcessException {
		_out.write(contents,0,contents.length);		
	}

	

	
	}