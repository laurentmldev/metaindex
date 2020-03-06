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


public interface IFieldsListParser<TFrom,TTo> extends IListParser<TFrom,TTo> {
	
	public enum PARSING_FIELD_TYPE { NUMBER, TEXT };

	// get the data type to give to each CSV column
	public List<IPair<String,PARSING_FIELD_TYPE> > getCsvColsTypes();
	public void setCsvColsTypes(List<IPair<String,PARSING_FIELD_TYPE> > fieldsDescr);
	
	// get the list of CSV columns to actually keep and final name to associate with
	// if empty, retrieve all columns, keeping original CSV names  
	public Map<String,String> getChosenFieldsMapping();
	public void setChosenFieldsMapping(Map<String,String> fieldsDescr);
	
	
}
