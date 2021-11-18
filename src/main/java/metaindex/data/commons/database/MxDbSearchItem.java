package metaindex.data.commons.database;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.app.Globals;
import metaindex.data.catalog.Catalog;
import metaindex.data.term.ICatalogTerm;
import toolbox.database.IDbItem;
import toolbox.exceptions.DataProcessException;

/**
 * Fill-in id/name/thumbnail based on catalog configuration
 * @author laurentml
 *
 */
public class MxDbSearchItem implements IDbItem {

	private Log log = LogFactory.getLog(MxDbSearchItem.class);
	
	private String _id;
	private List<String> _nameFields;
	private String _thumbnailUrlField;
	private String _urlPrefix;
	
	private Date _lastModifTimestamp=new Date(0);
	private Integer _lastModifUserId;
	
	private static final String BASIC_TITLE_SEPARATOR=" ";
	private String _nameVal="";
	private String _thumbnailUrlVal="";
	
	private Boolean _isThumbnailFieldMulti=false;
	
	private Map<String,Object> _data = new HashMap<String,Object>();
	
	public MxDbSearchItem(String id, Map<String,Object> data,
						  List<String> nameFields, String thumbnailUrlField, 
						  String urlPrefix,
						  Boolean isThumbnailFieldMulti) throws DataProcessException {		
		_id=id;
		_data=data;
		_nameFields=nameFields;
		_thumbnailUrlField=thumbnailUrlField;
		_urlPrefix=urlPrefix;
		_isThumbnailFieldMulti=isThumbnailFieldMulti;
		
		try {
			String lastUserIdStr=_data.get(ICatalogTerm.MX_TERM_LASTMODIF_USERID).toString();
			_lastModifUserId=Integer.valueOf(lastUserIdStr);
		} catch(Exception e) { 
			_lastModifUserId=0;
			//throw new DataProcessException("Unhandled userId format : "+lastUserIdStr,e); 
		}
		String dateStr=_data.get(ICatalogTerm.MX_TERM_LASTMODIF_TIMESTAMP).toString();	
		
		try {
			setLastModifTimestamp(ICatalogTerm.MX_TERM_DATE_FORMAT.parse(dateStr)); 
		}
		catch(Exception e) { 
			//throw new DataProcessException("Unhandled date format : "+dateStr,e); 
			log.error("Unhandled date format : '"+dateStr+"', setting it to epoch.");	
			e.printStackTrace();
			setLastModifTimestamp(new Date(0));			
		}
		
		_data.remove(ICatalogTerm.MX_TERM_LASTMODIF_TIMESTAMP);
		_data.remove(ICatalogTerm.MX_TERM_LASTMODIF_USERID);
	}
	@Override
	public String getId() {
		return _id;
	}

	public void setId(String id) {
		this._id = id;
	}
	
	private Boolean isOpeningSeparator(String separator) {
		return separator.equals("(") || separator.equals("[") || separator.equals("{");
	}
	private Boolean isClosingSeparator(String separator) {
		return separator.equals(")") || separator.equals("]") || separator.equals("}");
	}
	@Override
	/**
	 * Item name is a string to summarize item contents.
	 * It is built from a list of fields (see 'cards title' field in catalog customization)
	 * to be concatenated to build this name.
	 */
	public String getName() {
		Integer openSeparator=0;
		String separator="";
				
		// user defined a _nameFields string listing the name of the fields
		// to be used to build the item 'name'
		if (_nameVal.length()==0) {
			
			for (String fieldname : _nameFields) {
				Object curFieldVal = _data.get(fieldname);
				// custom separator defined within quotes
				if (fieldname.startsWith("\"") && fieldname.endsWith("\"")) {
					separator = fieldname.replace("\"", "");					
				}
				else if (curFieldVal!=null) {
					// test if separator shall be added
					if (_nameVal.length()>0 && curFieldVal.toString().length()>0 
						|| !separator.equals(BASIC_TITLE_SEPARATOR) && curFieldVal.toString().length()>0) {
						
						if (isOpeningSeparator(separator)) { 
							openSeparator++;
							if (_nameVal.length()>0) { _nameVal+=" "; }							
						}
						else if (isClosingSeparator(separator) && openSeparator>0) {
							openSeparator--; 						
						}						
						_nameVal+=separator;
					}
					// keep a space between contents containing coma-separated data,
					// allowing easier display in GUI, because then it can be cut
					// over several lines.
					_nameVal+=curFieldVal.toString().replace(",", ", ");
					separator=BASIC_TITLE_SEPARATOR;
				}
			}
			if (_nameVal.length()>0 && isClosingSeparator(separator)  && openSeparator>0) { _nameVal+=separator; }
		}
		 
		return _nameVal;
	}

	@Override
	public String getThumbnailUrl() {
		if (_thumbnailUrlVal.length()==0) {
			Object curFieldVal = _data.get(_thumbnailUrlField);
			if (curFieldVal!=null) { _thumbnailUrlVal=curFieldVal.toString(); }					
		}
		// for multi pics, use the first one to get the thumbnail
		if (_isThumbnailFieldMulti && _thumbnailUrlVal.contains(",")) 
		{ 
			String urls[]=_thumbnailUrlVal.split(",");
			if (urls.length>0) { _thumbnailUrlVal=_thumbnailUrlVal.split(",")[0]; }
		}
		if (!_thumbnailUrlVal.startsWith("http")) { return _urlPrefix+"/"+_thumbnailUrlVal; }
		
		return _thumbnailUrlVal;
	}

	@Override
	public Map<String, Object> getData() {
		return _data;
	}

	@Override
	public Date getLastModifTimestamp() {
		return _lastModifTimestamp;
	}
	public void setLastModifTimestamp(Date d) {
		_lastModifTimestamp=d;
	}
	public String getLastModifTimestampStr() {
		String pattern = "yyyy/MM/dd HH:mm:ss";
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(getLastModifTimestamp());		
	}
	
	@Override
	public Integer getLastModifUserId() {
		return _lastModifUserId;
	}
	public void setLastModifUserId(Integer uid) {
		_lastModifUserId=uid;
	}
	public String getLastModifUserNickname() {
		if (Globals.Get().getUsersMgr().getUserById(getLastModifUserId())!=null) {
			return Globals.Get().getUsersMgr().getUserById(getLastModifUserId()).getNickname();
		} else { return getLastModifUserId().toString(); }
	}
	@Override
	public Object getValue(String fieldName) {
		return _data.get(fieldName);
	}
	@Override
	public void setValue(String fieldName, Object value) {
		_data.put(fieldName, value);
		
	}
	@Override
	public Set<String> getFieldsList() {
		return _data.keySet();
	}
	
	
}
