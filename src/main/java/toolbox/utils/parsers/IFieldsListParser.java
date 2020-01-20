package toolbox.utils.parsers;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;

import toolbox.utils.IPair;
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;

public interface IFieldsListParser<TFrom,TTo> extends IListParser<TFrom,TTo> {
	
	public enum PARSING_FIELD_TYPE { NUMBER, TEXT };

	public void setFieldsDescriptions(List<IPair<String,PARSING_FIELD_TYPE> > fieldsDescr);
	
	public List<IPair<String,PARSING_FIELD_TYPE> > getFieldsDescriptions();
	
}
