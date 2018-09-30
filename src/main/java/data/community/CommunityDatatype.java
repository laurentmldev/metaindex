package metaindex.data.community;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.accessors.AMetaindexAccessor;

/**
 * Store data of a community term
 * @author laurent
 *
 */
public class CommunityDatatype  {
	
	private Log log = LogFactory.getLog(CommunityDatatype.class);
	
	private int datatypeId;
	private String datatypeName="";
	
	public CommunityDatatype(int datatypeId, String datatypeName)  {
			this.setDatatypeId(datatypeId);
			this.setDatatypeName(datatypeName);
	}

	/**
	 * @return the datatypeId
	 */
	public int getDatatypeId() {
		return datatypeId;
	}

	/**
	 * @param datatypeId the datatypeId to set
	 */
	public void setDatatypeId(int datatypeId) {
		this.datatypeId = datatypeId;
	}

	/**
	 * @return the datatypeName
	 */
	public String getDatatypeName() {
		return datatypeName;
	}

	/**
	 * @param datatypeName the datatypeName to set
	 */
	public void setDatatypeName(String datatypeName) {
		this.datatypeName = datatypeName;
	}



}
