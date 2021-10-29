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
	
	public static final String MX_CR_ESCAPE_STR="&cr&";
	public static final String MX_CLEAR_CELL_STR="&empty&";
	
	private Integer _nbLinesParsed=0;
	private List<IPair<String,PARSING_FIELD_TYPE> > _csvColumnsTypes = new ArrayList<>();
	private Map<String,String> _chosenFieldsMapping = new HashMap<>();
	
	/// To be overridden by specialization
	protected abstract IDbItem buildObjectFromFieldsMap(Map<String, Object> fieldsMap);
	
	@Override
	public List<IPair<String,PARSING_FIELD_TYPE> > getCsvColsTypes() { return _csvColumnsTypes; }
	
	@Override
	public void setCsvColsTypes(List<IPair<String,PARSING_FIELD_TYPE> > csvColsTypesDescr) {
		_csvColumnsTypes=csvColsTypesDescr;
	}
	

	@Override
	public Map<String, String> getChosenFieldsMapping() {
		return _chosenFieldsMapping;
	}

	@Override
	public void setChosenFieldsMapping(Map<String, String> fieldsNamesDescr) {
		_chosenFieldsMapping=fieldsNamesDescr;		
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
			throw new ParseException(parseErrors); 
		}
		return result;
	
	}	
	
	protected Object parseStrField(String entryStrContents) throws ParseException {
		
		// remove wrapping quotes if any
		if (entryStrContents.startsWith("\"") && entryStrContents.endsWith("\"")) {
			return entryStrContents.substring(1, entryStrContents.length()-1);
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
	 */
	protected abstract Map<String, Object> toMap(TFrom csvLine) throws ParseException;
	
	protected void extractItemStrData(	Map<String, Object> parsedData,
										String fieldName,
										String fieldStrValue, 
										PARSING_FIELD_TYPE fieldType) 
		throws ParseException {
		if (this.getChosenFieldsMapping().size()>0 && !this.getChosenFieldsMapping().containsKey(fieldName)) {
			return;
		}
		String mappedFieldName=fieldName;
		if (this.getChosenFieldsMapping().size()>0) { mappedFieldName=this.getChosenFieldsMapping().get(fieldName); }
		
		try {
			switch (fieldType) {
				case TEXT:
					parsedData.put(mappedFieldName, parseStrField(fieldStrValue));
					break;
				case NUMBER:
					parsedData.put(mappedFieldName, parseNumberFieldContents(fieldStrValue));
					break;
				default:
					throw new ParseException("Unhandled field type "
							+fieldType.toString()+" fieldName="+fieldName+"/"+mappedFieldName+" value='"+fieldStrValue+"'");						
			}
		} catch (ParseException e) {
			throw new ParseException("Contents not compatible with type '"+fieldType.toString()
						+"' of field '"+mappedFieldName+"' (input field '"+fieldName
						+"') : "	+e.getMessage()+" : "+fieldStrValue);
		}
		
	}
	
}
