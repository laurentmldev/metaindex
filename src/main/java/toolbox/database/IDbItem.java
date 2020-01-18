package toolbox.database;

import java.util.Date;
import java.util.Map;
import toolbox.utils.IIdentifiable;

/**
 * Contains fields names/values + id/name/thumbnailUrl of an item resulting from a DBSearch
 * Mainly used to handle resulting items of a DB search and forward them towards GUI
 * @author laurentml
 *
 */
public interface IDbItem extends IIdentifiable<String> {

	public Date getLastModifTimestamp();
	public Integer getLastModifUserId();
	public String getThumbnailUrl();
	/**
	 * key is a field (term) name
	 * @return
	 */
	public Map<String, Object> getData();	
}
