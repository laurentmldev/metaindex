package toolbox.utils.parsers;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.HashMap;
import java.util.Map;

import toolbox.database.IDbItem;
import toolbox.utils.IPair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.catalog.UserItemContents;


/**
 * Convert CSV lines into decoded fields. Need to be overridden in order
 * to build expected resulting specific object as a result.
 * @author laurentml
 *
 */
public class CsvDbItemsParser extends ADbItemsParser<String> {
	
	private Log log = LogFactory.getLog(CsvDbItemsParser.class);
	
	
	private static final String MX_SEP_ESCAPE_STR="__SEP__";	
	private static final String MX_QUOTE_ESCAPE_STR="__QUOTE__";
	/*
	private static final String MX_ANTISLASH_ESCAPE_STR="__ANTISLASH__";		
	private static final String MX_GT_ESCAPE_STR="__GT__";
	private static final String MX_LT_ESCAPE_STR="__LT__";
	private static final String MX_DOLLARS_ESCAPE_STR="__DOLLARS__";
	*/
	
	private String _csvSeparator=";";
	private String _commentsMarker="#";
	private char _stringIdentifier='"';

	@Override
	protected IDbItem buildObjectFromFieldsMap(Map<String, Object> fieldsMap) {
		UserItemContents result = new UserItemContents();
		result.setData(fieldsMap);
		return result;
	}
	
	public String getCsvSeparator() { return _csvSeparator; }
	public void setCsvSeparator(String sep) { _csvSeparator=sep; }
	
	@Override
	public IDbItem parse(String str) throws ParseException {
		Map<String, Object> parsed = toMap(str);
		if (parsed!=null) { return buildObjectFromFieldsMap(parsed); } 		
		return null;
	}
	
	@Override 
	protected Boolean shallBeParsed(String entry) {
		return !(entry.startsWith(_commentsMarker) || entry.length()==0);
	}
	
	/**
	 * Extract a single line from csv file and populate a corresponding map with only fields requested by user
	 */
	@Override
	protected Map<String, Object> toMap(String csvLine) throws ParseException {
		
		// ignore empty or comment line
		if (!shallBeParsed(csvLine)) { return null; }	
		
		// clean input line from separators within quotes
		csvLine=csvLine.replace("\\\"",MX_QUOTE_ESCAPE_STR);
		/*
		csvLine=csvLine.replaceAll("<",MX_LT_ESCAPE_STR);
		csvLine=csvLine.replaceAll(">",MX_GT_ESCAPE_STR);
		csvLine=csvLine.replaceAll("\\$",MX_DOLLARS_ESCAPE_STR);
		csvLine=csvLine.replaceAll("\\\\",MX_ANTISLASH_ESCAPE_STR);	
		*/
		//String quotedCsvLine=java.util.regex.Matcher.quoteReplacement(csvLine);		
		Matcher m = Pattern.compile(_stringIdentifier+"[^"+_stringIdentifier+"]+"+_stringIdentifier).matcher(csvLine);
		// for each text section within quotes, replace separators by temporary marker
		// prior to split columns

		while(m.find()) {
			String groupStr=m.group();
			String escaped_col = groupStr.replace(this.getCsvSeparator(), MX_SEP_ESCAPE_STR);			
			csvLine=csvLine.replace(groupStr,escaped_col);
		}
		
		String cols[] = csvLine.split(getCsvSeparator(),-1);
		if (cols.length!=getColsNames().length) { 
			throw new ParseException("expected "+getFieldsParsingTypes().size()+" columns, found "+cols.length+" : "+csvLine); 
		}
		
		Map<String, Object> result= new HashMap<String,Object>();
		int colidx=0;		
		for (String csvColName : getColsNames()) {
			colidx++;
			
			String dbFieldName = this.getFieldsMapping().get(csvColName);
			// colName is present in FieldsMapping if and only if
			// user selected it as "to be imported" so if it is not present we ignore it
			if (dbFieldName==null) { continue; }
			
			String curColContents=cols[colidx-1];
			
			// ignore empty fields. When used fields need some mandatory data,
			// this avoid useless error to be raised.
			// Example : when type is elasticsearch geo-point : it cannot have empty value 
			if (curColContents.length()==0) { continue; }
			
			// clear existing cell contents if forced by user
			for (String clearCellMarker : MX_CLEAR_CELL_STR) {
				if (curColContents.equals(clearCellMarker)) {
					curColContents="";
				}
			}
						
			// restore new lines
			for (String crMarker : MX_CR_ESCAPE_STR) {
				curColContents=curColContents.replace(crMarker, "\n");
			}			
			curColContents=curColContents.replace(MX_QUOTE_ESCAPE_STR, "\"");
			curColContents=curColContents.replace(MX_SEP_ESCAPE_STR, this.getCsvSeparator());
			/*
			curColContents=curColContents.replaceAll(MX_GT_ESCAPE_STR, ">");
			curColContents=curColContents.replaceAll(MX_LT_ESCAPE_STR, "<");
			curColContents=curColContents.replaceAll(MX_DOLLARS_ESCAPE_STR, "\\$");
			curColContents=curColContents.replaceAll(MX_ANTISLASH_ESCAPE_STR, "\\\\");
			*/
			
			PARSING_FIELD_TYPE fieldType = this.getFieldsParsingTypes().get(dbFieldName);
			extractItemStrData(result,dbFieldName,curColContents,fieldType);
		
		}
		
		return result;
	}

	
	
}