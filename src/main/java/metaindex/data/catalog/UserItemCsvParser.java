package metaindex.data.catalog;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.Map;

import toolbox.database.IDbItem;
import toolbox.utils.parsers.ACsvParser;

public class UserItemCsvParser extends ACsvParser<IDbItem> {

	@Override
	protected IDbItem buildObjectFromFieldsMap(Map<String, Object> fieldsMap) {
		UserItemContents result = new UserItemContents();
		result.setData(fieldsMap);
		return result;
	}
	

}
