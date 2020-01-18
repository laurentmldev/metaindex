package metaindex.data.catalog;

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
