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

import toolbox.utils.IPair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.app.control.websockets.items.WsControllerItemsCsvFileUpload;


/**
 * Convert CSV lines into decoded fields. Need to be overridden in order
 * to build expected resulting specific object as a result.
 * @author laurentml
 *
 */
public abstract class ACsvParser<TTo> implements IFieldsListParser<String,TTo> {
	
	private Log log = LogFactory.getLog(ACsvParser.class);
	
	private static final String MX_SEP_ESCAPE_STR="__MX_ESCAPED_SEPARATOR__";
	public static final String MX_CR_ESCAPE_STR="__MX_CR__";
	private static final String MX_CLEAR_CELL_STR="__empty__";
	private String _csvSeparator=";";
	private String _commentsMarker="#";
	private String _stringIdentifier="\"";
	private Integer _nbLinesParsed=0;
	private List<IPair<String,PARSING_FIELD_TYPE> > _csvColumnsTypes = new ArrayList<>();
	private Map<String,String> _chosenFieldsMapping = new HashMap<>();
	
	/// To be overridden by specialization
	protected abstract TTo buildObjectFromFieldsMap(Map<String, Object> fieldsMap);
	
	public String getCsvSeparator() { return _csvSeparator; }
	public void setCsvSeparator(String sep) { _csvSeparator=sep; }
	
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
	
	@Override
	public List<TTo> parseAll(List<String> listStr) throws ParseException {		
		
		List<String> parseErrors = new ArrayList<String>();
		
		List<TTo> result = new ArrayList<TTo>();
		for (String str : listStr) {
			_nbLinesParsed++;
			// ignore comments and empty lines
			if (str.startsWith(_commentsMarker) || str.length()==0) { continue; } 
			try { 
				TTo parsed = parse(str);
				if (parsed!=null) { result.add(parsed); }
			} catch (ParseException e) {
				parseErrors.add("l."+_nbLinesParsed+" : "+e.getMessage());
			}
		}

		if (parseErrors.size()>0) { throw new ParseException(parseErrors); }
		return result;
	
	}	
	
	private Object parseStrField(String csvContents) throws ParseException {
		
		// remove wrapping quotes if any
		if (csvContents.startsWith("\"") && csvContents.endsWith("\"")) {
			return csvContents.substring(1, csvContents.length()-1);
		}
					
		return csvContents; 
	}
	
	private Object parseNumberFieldContents(String csvContents) throws ParseException { 
		csvContents=csvContents.replace(",","."); // handle bloody Excel text-export format
		try { return Integer.parseUnsignedInt(csvContents); }
		catch (Exception e1) { try { return Integer.parseInt(csvContents); }
		catch (Exception e2) { try { return Double.parseDouble(csvContents); }
		catch (Exception e3) { try { return Double.valueOf(csvContents).longValue(); }
		catch (Exception e4) { throw new ParseException("Expected number, got '"+csvContents+"'"); }}}}
	}
		
	
	@Override
	public TTo parse(String str) throws ParseException {
		Map<String, Object> parsed = toMap(str);
		if (parsed!=null) { return buildObjectFromFieldsMap(parsed); } 		
		return null;
	}
	
	/**
	 * Extract a single line from csv file and populate a corresponding map with only fields requested by user
	 */
	private Map<String, Object> toMap(String csvLine) throws ParseException {
		
		// ignore empty or comment line
		if (csvLine.startsWith("#") || csvLine.length()==0) { return null; }
		
		// clean input line from separators within quotes
		Matcher m = Pattern.compile(_stringIdentifier+"[^"+_stringIdentifier+"]+"+_stringIdentifier).matcher(csvLine);
		while(m.find()) {
			String escaped_col = m.group().replace(this.getCsvSeparator(), MX_SEP_ESCAPE_STR);
			csvLine=csvLine.replaceAll(Pattern.quote(m.group()),escaped_col);
		}		
		
		String cols[] = csvLine.split(getCsvSeparator(),-1);
		if (cols.length!=getCsvColsTypes().size()) { 
			throw new ParseException("expected "+getCsvColsTypes().size()+" columns, found "+cols.length+" : "+csvLine); 
		}
		
		Map<String, Object> result= new HashMap<String,Object>();
		int colidx=0;
		for (IPair<String,PARSING_FIELD_TYPE> coldef : getCsvColsTypes()) {
			colidx++;
			String curColContents=cols[colidx-1];
			
			
			
			// ignore empty fields. When used fields need some mandatory data,
			// this avoid useless error to be raised.
			// Example : when type is elasticsearch geo-point : it cannot have empty value 
			if (curColContents.length()==0) { continue; }
			
			// clear existing cell contents if forced by user
			if (curColContents.equals(MX_CLEAR_CELL_STR)) {
				curColContents="";
			}
			
			// restore escaped separator
			curColContents=curColContents.replaceAll(MX_SEP_ESCAPE_STR, this.getCsvSeparator());
			
			// restore new lines
			curColContents=curColContents.replaceAll(MX_CR_ESCAPE_STR, "\n");
			
			String csvFieldName = coldef.getFirst();
			
			PARSING_FIELD_TYPE fieldType = coldef.getSecond();
			if (this.getChosenFieldsMapping().size()>0 && !this.getChosenFieldsMapping().containsKey(csvFieldName)) {
				continue;
			}
			String mappedFieldName=csvFieldName;
			if (this.getChosenFieldsMapping().size()>0) { mappedFieldName=this.getChosenFieldsMapping().get(csvFieldName); }
			
			try {
				switch (fieldType) {
					case TEXT:
						result.put(mappedFieldName, parseStrField(curColContents));
						break;
					case NUMBER:
						result.put(mappedFieldName, parseNumberFieldContents(curColContents));
						break;
					default:
						throw new ParseException("Unhandled field type "+coldef.getFirst());						
				}
			} catch (ParseException e) {
				throw new ParseException("Contents not compatible with type '"+coldef.getSecond().toString()
							+"' of field '"+mappedFieldName+"' (CSV column '"+coldef.getFirst()
							+"') : "	+e.getMessage()+" : "+csvLine);
			}

		}
		
		return result;
	}

	
	
}
