package metaindex.data.community;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.IBufferizedData;
import metaindex.data.IMultiLanguageData;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.accessors.AMetaindexAccessor;

/**
 * Store data of a community term
 * @author laurent
 *
 */
public interface ICommunityTermData extends IMultiLanguageData<TermVocabularySet>  {
				
	public void setCommunityId(int communityId);
	public String getIdName();
	public void setIdName(String newIdCode);
	public int getTermId();
	public void setTermId(int termId);
	public int getDatatypeId();
	public void setDatatypeId(int dataTypeId);
	public boolean isEnum();
	public void setEnum(boolean isEnum);
	public String getDatatypeName();
	

}
