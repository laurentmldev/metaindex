package metaindex.data.community;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.IBufferizedData;
import metaindex.data.IBufferizedDataHandle;
import metaindex.data.IMultiLanguageData;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.accessors.AMetaindexAccessor;

/**
 * Store data of a community term
 * @author laurent
 *
 */
public interface ICommunityTermHandle  extends ICommunityTermData, ICommunityAccessing,IBufferizedDataHandle {
					
	public TermVocabularySet getVocabulary();
	
}
