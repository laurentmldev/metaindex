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
public class CatalogChatMsg implements ICatalogChatMsg {
	
	private String _authorName="";
	private String _authorId="";
	private Date _timestamp;
	private String _text="";
		
	public String getAuthorName() {
		return _authorName;
	}
	public void setAuthorName(String _authorName) {
		this._authorName = _authorName;
	}
	public String getAuthorId() {
		return _authorId;
	}
	public void setAuthorId(String _authorId) {
		this._authorId = _authorId;
	}
	public Date getTimestamp() {
		return _timestamp;
	}
	public void setTimestamp(Date _timestamp) {
		this._timestamp = _timestamp;
	}
	public String getText() {
		return _text;
	}
	public void setText(String _text) {
		this._text = _text;
	}
	
	
}
