package toolbox.utils.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import toolbox.utils.BasicPair;
import toolbox.utils.IPair;
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;

/**
 * Convert CSV lines into decoded fields. Need to be overridden in order
 * to build expected resulting specific object as a result.
 * @author laurentml
 *
 */
public abstract class ACsvParser<TTo> implements IFieldsListParser<String,TTo> {
	
	Integer _nbEntriesParsed=0;
	List<IPair<String,PARSING_FIELD_TYPE> > _columnsDef = new ArrayList<IPair<String,PARSING_FIELD_TYPE> >();
	
	/// To be overridden by specialization
	protected abstract TTo buildObjectFromFieldsMap(Map<String, Object> fieldsMap);
	
	public String getCsvSeparator() { return ";"; }
	
	@Override
	public List<IPair<String,PARSING_FIELD_TYPE> > getFieldsDescriptions() { return _columnsDef; }
	
	@Override
	public void setFieldsDescriptions(List<IPair<String,PARSING_FIELD_TYPE> > fieldsDescr) {
		_columnsDef=fieldsDescr;
	}
	
	@Override
	public TTo parse(String str) throws ParseException {
		Map<String, Object> parsed = toMap(str);
		if (parsed!=null) { return buildObjectFromFieldsMap(parsed); } 		
		return null;
	}
	
	@Override
	public List<TTo> parseAll(List<String> listStr) throws ParseException {		
		
		List<String> parseErrors = new ArrayList<String>();
		
		List<TTo> result = new ArrayList<TTo>();
		for (String str : listStr) {
			_nbEntriesParsed++;
			try { 
				TTo parsed = parse(str);
				if (parsed!=null) { result.add(parsed); }
			} catch (ParseException e) {
				parseErrors.add("l."+_nbEntriesParsed+" : "+e.getMessage());
			}
		}

		if (parseErrors.size()>0) { throw new ParseException(parseErrors); }
		return result;
	
	}	
	
	private Object parseStrField(String csvContents) throws ParseException { 
		return csvContents; 
	}
	
	private Object parseNumberFieldContents(String csvContents) throws ParseException { 
		try { return Integer.parseUnsignedInt(csvContents); }
		catch (Exception e1) { try { return Integer.parseInt(csvContents); }
		catch (Exception e2) { try { return Double.parseDouble(csvContents); }
		catch (Exception e3) { throw new ParseException("Expected number, got '"+csvContents+"'"); }}}
	}
	
	private Map<String, Object> toMap(String d) throws ParseException {
		// ignore empty line
		if (d.startsWith("#") || d.length()==0) { return null; }
		
		// -1 prevents to remove empty columns
		String cols[] = d.split(getCsvSeparator(),-1);
		if (cols.length!=_columnsDef.size()) { 
			throw new ParseException("expected "+_columnsDef.size()+" columns, found "+cols.length+" : "+d); 
		}
		
		Map<String, Object> result= new HashMap<String,Object>();
		int colidx=0;
		for (IPair<String,PARSING_FIELD_TYPE> coldef : _columnsDef) {
			colidx++;
			String curColContents=cols[colidx-1];
			
			// ignore empty fields. When used fields need some mandatory data,
			// this avoid useless error to be raised.
			// Example : when type is elasticsearch geo-point : it cannot have empty value 
			if (curColContents.length()==0) { continue; }
			
			String fieldName = coldef.getFirst();
			PARSING_FIELD_TYPE fieldType = coldef.getSecond();
			
			try {
				switch (fieldType) {
					case TEXT:
						result.put(fieldName, parseStrField(curColContents));
						break;
					case NUMBER:
						result.put(fieldName, parseNumberFieldContents(curColContents));
						break;
					default:
						throw new ParseException("Unhandled field type "+coldef.getFirst());						
				}
			} catch (ParseException e) {
				throw new ParseException("Contents not compatible with type '"+coldef.getSecond().toString()
							+"' of field '"+coldef.getFirst()
							+"' : "	+e.getMessage()+" : "+d);
			}

		}
		
		return result;
	}

	
	
}
