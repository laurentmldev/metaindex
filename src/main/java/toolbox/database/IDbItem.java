package toolbox.database;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.Date;
import java.util.Map;

import toolbox.utils.IFieldValueMapObject;
import toolbox.utils.IIdentifiable;

/**
 * Contains fields names/values + id/name/thumbnailUrl of an item resulting from a DBSearch
 * Mainly used to handle resulting items of a DB search and forward them towards GUI
 * @author laurentml
 *
 */
public interface IDbItem extends IIdentifiable<String>,IFieldValueMapObject {

	public static final String DB_ID_FIELD_NAME="_id";
	
	public Date getLastModifTimestamp();
	public Integer getLastModifUserId();
	public String getThumbnailUrl();
	/**
	 * key is a field (term) name
	 * @return
	 */
	public Map<String, Object> getData();	
}
