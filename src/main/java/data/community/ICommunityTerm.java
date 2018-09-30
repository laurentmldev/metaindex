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
public interface ICommunityTerm extends ICommunityAccessing,ICommunityTermData,ICommunitySubdata {
				
	public TermVocabularySet getVocabulary(int guiLanguageId);

}
