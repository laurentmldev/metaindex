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
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import metaindex.data.catalog.UserItemContents;


/**
 * Convert CSV lines into decoded fields. Need to be overridden in order
 * to build expected resulting specific object as a result.
 * @author laurentml
 *
 */
public class SpreadsheetDbItemsParser extends ADbItemsParser<Row> {
	
	private Log log = LogFactory.getLog(SpreadsheetDbItemsParser.class);

	

	@Override
	protected IDbItem buildObjectFromFieldsMap(Map<String, Object> fieldsMap) {
		UserItemContents result = new UserItemContents();
		result.setData(fieldsMap);
		return result;
	}
	
	@Override
	public IDbItem parse(Row str) throws ParseException {
		Map<String, Object> parsed = toMap(str);
		if (parsed!=null) { return buildObjectFromFieldsMap(parsed); } 		
		return null;
	}
	
	@Override 
	protected Boolean shallBeParsed(Row entry) {
		return true;
	}
	
	/**
	 * Extract a single line from csv file and populate a corresponding map with only fields requested by user
	 */
	@Override
	protected Map<String, Object> toMap(Row spreadsheetRow) throws ParseException {
		
		// ignore empty or comment line
		if (!shallBeParsed(spreadsheetRow)) { return null; }	
				
		Map<String, Object> result= new HashMap<String,Object>();
		int colidx=0;		
		
		
		for (String colName : getColsNames()) {
			colidx++;
			String curColContents="";
			Cell curCellContents=spreadsheetRow.getCell(colidx-1);
			if (curCellContents!=null) {
				switch(curCellContents.getCellType()) {
		            case BLANK:                
		            case STRING:
		                curColContents=curCellContents.getStringCellValue();
		                break;
		            case NUMERIC:
		            	curColContents=new Double(curCellContents.getNumericCellValue()).toString();
		            	break;
		            default:
		                throw new ParseException("Unhandled Spreadsheet datatype '"+curCellContents.getCellType()+"' ");
		        }
			}
			
			
			// ignore empty fields. When used fields need some mandatory data,
			// this avoid useless error to be raised.
			// Example : when type is elasticsearch geo-point : it cannot have empty value 
			if (curColContents.length()==0) { continue; }
			
			// clear existing cell contents if forced by user
			if (curColContents.equals(MX_CLEAR_CELL_STR)) {
				curColContents="";
			}
			
			// restore new lines
			curColContents=curColContents.replaceAll(MX_CR_ESCAPE_STR, "\n");
			
			String dbFieldName = this.getFieldsMapping().get(colName);			
			PARSING_FIELD_TYPE fieldType = this.getFieldsParsingTypes().get(dbFieldName);
			extractItemStrData(result,dbFieldName,curColContents,fieldType);

		}
		
		return result;
	}

	
	
}
