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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

import metaindex.data.catalog.UserItemContents;


/**
 * Convert CSV lines into decoded fields. Need to be overridden in order
 * to build expected resulting specific object as a result.
 * @author laurentml
 *
 */
public class OdfSpreadsheetDbItemsParser extends ADbItemsParser<OdfTableRow>  implements ISpreadsheetDbItemsParser {
	
	private Log log = LogFactory.getLog(ExcelSpreadsheetDbItemsParser.class);

	

	@Override
	protected IDbItem buildObjectFromFieldsMap(Map<String, Object> fieldsMap) {
		UserItemContents result = new UserItemContents();
		result.setData(fieldsMap);
		return result;
	}
	
	@Override
	public IDbItem parse(OdfTableRow str) throws ParseException {
		Map<String, Object> parsed = toMap(str);
		if (parsed!=null) { return buildObjectFromFieldsMap(parsed); } 		
		return null;
	}
	
	@Override 
	protected Boolean shallBeParsed(OdfTableRow entry) {
		return true;
	}
	/*
	final static public LocalDate EXCEL_EPOCH_REF = LocalDate.of(1899,Month.DECEMBER,30);
	private LocalDate excelDate2javaDate(Integer excelDate) {
		return EXCEL_EPOCH_REF.plusDays(excelDate);
	}
	*/
	/**
	 * Extract a single line from csv file and populate a corresponding map with only fields requested by user
	 */
	@Override
	protected Map<String, Object> toMap(OdfTableRow spreadsheetRow) throws ParseException {
		
		// ignore empty or comment line
		if (!shallBeParsed(spreadsheetRow)) { return null; }	
				
		Map<String, Object> result= new HashMap<String,Object>();
		int colidx=0;		
		
		
		for (String colName : getColsNames()) {
			colidx++;
			OdfTableCell curCellContents=spreadsheetRow.getCellByIndex(colidx-1);
			String curColStrContents=curCellContents.getStringValue();
			
			//"boolean", "currency", "date", "float", "percentage", "string" or "time"
			/*
			String cellType = curCellContents.getValueType();
			if (cellType==null) { cellType="string"; } 
			if (cellType.equals("boolean")) { curColStrContents=curCellContents.getBooleanValue().toString(); }
			else if (cellType.equals("currency")) { curColStrContents=curCellContents.getCurrencyValue().toString(); }
			else if (cellType.equals("date")) { curColStrContents=curCellContents.getDateValue().toString(); }
			else if (cellType.equals("float")) { curColStrContents=curCellContents.getDoubleValue().toString(); }
			else if (cellType.equals("percentage")) { curColStrContents=curCellContents.getPercentageValue().toString(); }
			else if (cellType.equals("string")) { curColStrContents=curCellContents.getStringValue().toString(); }
			else if (cellType.equals("time")) { curColStrContents=curCellContents.getTimeValue().toString(); }
			else {
				throw new ParseException("Unhandled ODF Spreadsheet datatype '"+cellType+"' ");
			}
			*/
		

			
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
				extractItemStrData(result,dbFieldName,curColStrContents,fieldType);
			}

		}
		
		return result;
	}

	
	
}
