package toolbox.utils.filetools;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import java.util.Map;

import toolbox.database.IDbItem;
import toolbox.database.IDbItemsProcessor;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IStreamProducer;
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;


/**
 * Parse received contents as CSV data and store it into ES DB
 * @author laurentml
 *
 */
public interface IBytesToDbWriter extends IBytesWriter,IStreamProducer<IDbItem> {
	
	public void init(IDbItemsProcessor dbItemsBulkProcess, 
					 Map<String,PARSING_FIELD_TYPE> parsingTypes,
					 Map<String, String> fieldsMapping) throws DataProcessException ;
	
	}