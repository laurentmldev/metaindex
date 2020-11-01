package metaindex.data.catalog;

import java.util.Date;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

/**
 * Bean storing catalog data
 * @author laurent
 *
 */
public interface ICatalogChatMsg {
	String getAuthorName();
	String getAuthorId();
	Date getTimestamp();
	String getText();	
}
