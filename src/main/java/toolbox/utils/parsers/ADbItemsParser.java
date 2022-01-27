package toolbox.utils.parsers;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import toolbox.database.IDbItem;
import toolbox.utils.IPair;
import toolbox.utils.parsers.IListParser.ParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Convert CSV lines into decoded fields. Need to be overridden in order
 * to build expected resulting specific object as a result.
 * @author laurentml
 *
 */
public abstract class ADbItemsParser<TFrom> implements IFieldsListParser<TFrom,IDbItem> {
	
	private Log log = LogFactory.getLog(ADbItemsParser.class);
	
	public static final String[] MX_CR_ESCAPE_STR = { "&cr&","__CR__" };
	public static final String[] MX_CLEAR_CELL_STR= { "&empty&","__empty__" };
	public static final String TRUNCATED_STR_TERMINATOR = "...[TRUNCATED]";
	
	// truncate str fields if longer than that
	// (if value is -1, then never truncate)
	private Integer _maxStrFieldLength=-1;
	
	private Integer _nbLinesParsed=0;
	// parsing type by db field name (only for fields chosen expected to be actually parsed)
	private Map<String,PARSING_FIELD_TYPE> _fieldsParsingTypes = new HashMap<>();
	// db field name by csvColName (only for fields chosen expected to be actually parsed)
	private Map<String,String> _fieldsMapping = new HashMap<>();
	// exhaustive list of columns where chosen fields shall be extracted from
	private String _csvColsNames[];
	
	/// To be overridden by specialization
	protected abstract IDbItem buildObjectFromFieldsMap(Map<String, Object> fieldsMap);
	
	// keep list of items with truncated contents (because some fields having too long contents)
	private List<String> _parsedItemsWithTruncatedContents = new ArrayList<String>();
	private Semaphore _parseWarningsLock = new Semaphore(1,true);
	
	@Override
	public String[] getColsNames() { return _csvColsNames; }
	@Override
	public void setColsNames(String[] csvColsNames) { _csvColsNames=csvColsNames; }
	
	@Override
	public Map<String,PARSING_FIELD_TYPE> getFieldsParsingTypes() { return _fieldsParsingTypes; }
	
	@Override
	public void setFieldsParsingTypes(Map<String,PARSING_FIELD_TYPE> fieldsTypesDescr) {
		_fieldsParsingTypes=fieldsTypesDescr;
	}
	

	@Override
	public Map<String, String> getFieldsMapping() {
		return _fieldsMapping;
	}

	@Override
	public void setFieldsMapping(Map<String, String> fieldsNamesDescr) {
		_fieldsMapping=fieldsNamesDescr;		
	}
	
	protected abstract Boolean shallBeParsed(TFrom entry);
	
	@Override
	public List<IDbItem> parseAll(List<TFrom> listInput) throws ParseException {		
		
		List<String> parseErrors = new ArrayList<String>();
		
		List<IDbItem> result = new ArrayList<IDbItem>();
		for (TFrom str : listInput) {
			_nbLinesParsed++;
			// ignore comments and empty lines
			if (!shallBeParsed(str)) { continue; } 
			
			try { 
				IDbItem parsed = parse(str);
				if (parsed!=null) { result.add(parsed); }
			} catch (ParseException e) {
				parseErrors.add("l."+_nbLinesParsed+" : "+e.getMessage());
			}
		}
		
		if (parseErrors.size()>0) { 
			throw new ParseException(parseErrors.size()+" parse errors in received contents",parseErrors); 
		}
		return result;
	
	}	
	
	protected Object parseStrField(String itemId,String entryStrContents) throws ParseException, InterruptedException {
		
		// remove wrapping quotes if any
		if (entryStrContents.startsWith("\"") && entryStrContents.endsWith("\"")) {
			entryStrContents=entryStrContents.substring(1, entryStrContents.length()-1);
		}
					
		// truncate string if too long
		if (entryStrContents.length()>getMaxStrFieldLength()) {
			entryStrContents=entryStrContents.substring(0,
					getMaxStrFieldLength()-TRUNCATED_STR_TERMINATOR.length())+TRUNCATED_STR_TERMINATOR;
			_parseWarningsLock.acquire();
			_parsedItemsWithTruncatedContents.add(itemId);
			_parseWarningsLock.release();
		}
		return entryStrContents; 
	}
	
	protected Object parseNumberFieldContents(String entryStrContents) throws ParseException { 
		entryStrContents=entryStrContents.replace(",","."); // handle Excel text-export format
		try { return Integer.parseUnsignedInt(entryStrContents); }
		catch (Exception e1) { try { return Integer.parseInt(entryStrContents); }
		catch (Exception e2) { try { return Double.parseDouble(entryStrContents); }
		catch (Exception e3) { try { return Double.valueOf(entryStrContents).longValue(); }
		catch (Exception e4) { throw new ParseException("Expected number, got '"+entryStrContents+"'"); }}}}
	}
		
	
	@Override
	public IDbItem parse(TFrom entry) throws ParseException {
		Map<String, Object> parsed = toMap(entry);
		if (parsed!=null && parsed.keySet().size()>0) { return buildObjectFromFieldsMap(parsed); } 		
		return null;
	}
	
	/**
	 * Extract a single line from input contents file and populate a corresponding map with only fields requested by user
	 * @param fieldName the name of the field (in database)
	 */
	protected abstract Map<String, Object> toMap(TFrom csvLine) throws ParseException;
	
	protected void extractItemStrData(	Map<String, Object> parsedData,
										String dbFieldName,
										String fieldStrValue, 
										PARSING_FIELD_TYPE fieldType) 
													throws ParseException {
		
		String itemId="";
		if (parsedData.containsKey(IDbItem.DB_ID_FIELD_NAME)) {
			itemId=(String)parsedData.get(IDbItem.DB_ID_FIELD_NAME);
		}
		try {
			switch (fieldType) {
				case TEXT:
				case DATE: // date is parsed as a string (typically dd/mm/yyyy)					
					parsedData.put(dbFieldName, parseStrField(itemId,fieldStrValue));
					break;
				case NUMBER:
					parsedData.put(dbFieldName, parseNumberFieldContents(fieldStrValue));
					break;
				default:
					throw new ParseException("Unhandled field type "
							+fieldType.toString()+" fieldName="+dbFieldName+"/"+dbFieldName+" value='"+fieldStrValue+"'");						
			}
		} catch (ParseException e) {
			throw new ParseException("Contents not compatible with type '"+fieldType.toString()
						+"' of field '"+dbFieldName+"' (input field '"+dbFieldName
						+"') : "	+e.getMessage()+" : "+fieldStrValue);
		} catch (InterruptedException e2) {
			throw new ParseException("Parsing failed, sorry :"+e2.getMessage());
		}
		
	}

	public Integer getMaxStrFieldLength() {
		return _maxStrFieldLength;
	}

	public void setMaxStrFieldLength(Integer maxStrFieldLength) {
		this._maxStrFieldLength = maxStrFieldLength;
	}
	// get current warnings list and empty it
	public List<String> retrieveTruncatedItemIds()  {
		
		try {
			_parseWarningsLock.acquire();
			List<String> rst = _parsedItemsWithTruncatedContents;
			_parsedItemsWithTruncatedContents=new ArrayList<>();
			_parseWarningsLock.release();
			return rst;
		} catch (InterruptedException e2) {
			log.error("Exception while parsing contents: "+e2.getMessage());
			_parseWarningsLock.release();
			return null;
		}
	}
	
	
}
