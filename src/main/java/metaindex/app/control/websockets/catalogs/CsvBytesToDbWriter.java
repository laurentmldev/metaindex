package metaindex.app.control.websockets.catalogs;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import toolbox.database.IDbItem;
import toolbox.database.IDbItemsProcessor;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;
import toolbox.utils.IPair;
import toolbox.utils.IStreamProducer;
import toolbox.utils.filetools.ABytesWriter;
import toolbox.utils.filetools.IBytesToDbWriter;
import toolbox.utils.parsers.CsvDbItemsParser;
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;
import toolbox.utils.parsers.IListParser.ParseException;


/**
 * Parse received contents as CSV data and store it into ES DB
 * @author laurentml
 *
 */
public class CsvBytesToDbWriter extends ABytesWriter implements IBytesToDbWriter {
	
	private Log log = LogFactory.getLog(CsvBytesToDbWriter.class);
	
	private CsvDbItemsParser _csvParser;
	private IDbItemsProcessor _itemsBulkProcessor;
	private Integer _totalNbLines=0;
	private String _unfinishedLineFromPreviousChunk="";
	private final String DEFAULT_CSV_SEP=";";
	
	public CsvBytesToDbWriter(String filePath,Long fileBytesSize, AProcessingTask parentProcessingTask) throws DataProcessException {
		super(fileBytesSize,parentProcessingTask);
		
	}

	@Override
	public void init(IDbItemsProcessor itemsBulkProcess,
					 Map<String,PARSING_FIELD_TYPE> parsingTypes,
					 Map<String,String> fieldsMapping,
					 Integer maxStrFieldLength) throws DataProcessException {
		_itemsBulkProcessor = itemsBulkProcess;
		_csvParser = new CsvDbItemsParser();
		_csvParser.setFieldsParsingTypes(parsingTypes);
		_csvParser.setFieldsMapping(fieldsMapping);
		_csvParser.setCsvSeparator(DEFAULT_CSV_SEP);
		_csvParser.setMaxStrFieldLength(maxStrFieldLength);
		
	}
	
	@Override
	public void start() throws DataProcessException {
		_itemsBulkProcessor.start();
	}
	
	@Override
	public String getName() {
		return "CSV-Uploader";
	}

	private Boolean _endWithNewLine(byte[] contents) {
		return contents[contents.length-1]=='\n';
	}
	
	@Override
	protected void handleReceivedContentsSequence(byte[] contents, Integer lastDumpedSequenceNumber)
			throws DataProcessException {
		
		
		ByteArrayInputStream stream = new ByteArrayInputStream(contents);		
		InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
		BufferedReader reader = new BufferedReader(streamReader);
		
		String line;
		Integer nbParsedLines=0;
		List<String> csvRows=new ArrayList<>();
		try {
			while ((line=reader.readLine())!=null) {
				// autodetect separator and columns names from very first (header) line
				if (_totalNbLines==0) { 				
					String colsNames[]=line.split(DEFAULT_CSV_SEP,-1);
					if (line.split(",",-1).length>colsNames.length) {
						colsNames=line.split(",",-1);
						_csvParser.setCsvSeparator(",");
					}
					else if (line.split("\t",-1).length>colsNames.length) {
						colsNames=line.split("\t",-1);
						_csvParser.setCsvSeparator("\t");
					}
					colsNames[0]=colsNames[0].replace("#","");
					_csvParser.setColsNames(colsNames);
					_totalNbLines++;
					continue;
				}
				if (nbParsedLines==0) {
					line=_unfinishedLineFromPreviousChunk+line;
					_unfinishedLineFromPreviousChunk="";
				}
				csvRows.add(line);
				nbParsedLines++;	
				_totalNbLines++;				
			}
			// if last line does not finish with a new line, it might be a single line truncated
			// in that case we wait for new chunk to complete it
			if (!_endWithNewLine(contents) && lastDumpedSequenceNumber!=-1) {
				_unfinishedLineFromPreviousChunk=csvRows.get(csvRows.size()-1);
				csvRows.remove(csvRows.size()-1);
			}
		} 
		catch (IOException e) {
			throw new ParseException("Unable to split CSV line into columns: '"+e.getMessage()+"' at l."+_totalNbLines,e);
		}
		List<IDbItem> parsedItemsToIndex=new ArrayList<>();
		parsedItemsToIndex = _csvParser.parseAll(csvRows); 	
		_itemsBulkProcessor.handle(parsedItemsToIndex); 		
	}

	@Override
	protected void handleStop() throws DataProcessException {
		if (_unfinishedLineFromPreviousChunk.length()>0) {
			String lastLine=_unfinishedLineFromPreviousChunk;
			_unfinishedLineFromPreviousChunk="";
			// use -1 as sequence number to indicate that its the very last one
			handleReceivedContentsSequence(lastLine.getBytes(),-1);		
		}		
	}

	@Override
	public void abort() throws DataProcessException {		
		// TODO: rollback already uploaded elements?
	}
	

	
	}