package toolbox.utils.parsers;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;
import java.util.Map;

import toolbox.utils.IPair;
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;


public interface IFieldsListParser<TFrom,TTo> extends IListParser<TFrom,TTo> {
	
	public enum PARSING_FIELD_TYPE { NUMBER, TEXT };

	// parsing type by db field name (only for fields chosen expected to be actually parsed)
	public Map<String,PARSING_FIELD_TYPE> getFieldsParsingTypes();
	public void setFieldsParsingTypes(Map<String,PARSING_FIELD_TYPE> fieldsDescr);
	
	// db field name by csvColName (only for fields chosen to be actually parsed)
	public Map<String,String> getFieldsMapping();
	public void setFieldsMapping(Map<String,String> fieldsDescr);
	
	// exhaustive list of columns where chosen fields shall be extracted from
	public String[] getColsNames();
	public void setColsNames(String[] cosNames);
	
	
}
