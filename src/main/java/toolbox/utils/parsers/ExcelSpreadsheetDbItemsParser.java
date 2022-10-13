package toolbox.utils.parsers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;

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
public class ExcelSpreadsheetDbItemsParser extends ADbItemsParser<Row> implements ISpreadsheetDbItemsParser {
	
	private Log log = LogFactory.getLog(ExcelSpreadsheetDbItemsParser.class);

	private DateFormat userDateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
	
	
	final static public LocalDate EXCEL_EPOCH_REF = LocalDate.of(1899,Month.DECEMBER,30);
	private LocalDate excelDate2javaDate(Integer excelDate) {
		return EXCEL_EPOCH_REF.plusDays(excelDate);
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
			String curColStrContents="";
			Double curColNbContents=null;
			Date curColDateContents=null;
			Cell curCellContents=spreadsheetRow.getCell(colidx-1);
			if (curCellContents!=null) {
				switch(curCellContents.getCellType()) {
		            case BLANK:                
		            case STRING:
		                curColStrContents=curCellContents.getStringCellValue();
		                break;
		            case NUMERIC:
		            	curColNbContents=new Double(curCellContents.getNumericCellValue());
		            	curColDateContents=curCellContents.getDateCellValue();
		            	curColStrContents=curColNbContents.toString();
		            	break;
		            default:
		                throw new ParseException("Unhandled Spreadsheet datatype '"+curCellContents.getCellType()+"' ");
		        }
			}
			
			
			// ignore empty fields. When used fields need some mandatory data,
			// this avoid useless error to be raised.
			// Example : when type is elasticsearch geo-point : it cannot have empty value 
			if (curColStrContents.length()==0) { continue; }
			
			// clear existing cell contents if forced by user
			for (String clearCellMarker : MX_CLEAR_CELL_STR) {
				if (curColStrContents.equals(clearCellMarker)) {
					curColStrContents="";
				}
			}
			
			// restore new lines
			for (String crMarker : MX_CR_ESCAPE_STR) {
				curColStrContents=curColStrContents.replace(crMarker, "\n");
			}
			String dbFieldName = this.getFieldsMapping().get(colName);		
			// colName is present in FieldsMapping if and only if
			// user selected it as "to be imported" so if it is not present we ignore it
			if (dbFieldName!=null) {
				PARSING_FIELD_TYPE fieldType = this.getFieldsParsingTypes().get(dbFieldName);
				
				// Excel dates are based on a specific time origin, and need to be shifted properly.
				if (fieldType==PARSING_FIELD_TYPE.DATE) {
					if (curColDateContents!=null) { curColStrContents=userDateFormat.format(curColDateContents); }
					else if (curColNbContents!=null) { curColStrContents=excelDate2javaDate(curColNbContents.intValue()).toString(); }					
				}
				extractItemStrData(result,dbFieldName,curColStrContents,fieldType);
			}

		}
		
		return result;
	}

	
	
}
