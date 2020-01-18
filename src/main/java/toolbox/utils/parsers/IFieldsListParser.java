package toolbox.utils.parsers;

import java.util.List;

import toolbox.utils.IPair;
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;

public interface IFieldsListParser<TFrom,TTo> extends IListParser<TFrom,TTo> {
	
	public enum PARSING_FIELD_TYPE { NUMBER, TEXT };

	public void setFieldsDescriptions(List<IPair<String,PARSING_FIELD_TYPE> > fieldsDescr);
	
	public List<IPair<String,PARSING_FIELD_TYPE> > getFieldsDescriptions();
	
}
