package metaindex.data.community;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.ContextLoader;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.AMultiLanguageMetaindexData;
import metaindex.data.community.Community;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDBAccessedData;
import metaindex.dbaccess.IDataAccessAware;

public abstract class AMultiLanguageCommunitySubdata <T,SpecificVocabularySet extends AGenericVocabularySet> 
												extends  AMultiLanguageMetaindexData<T,SpecificVocabularySet> {
	
	private Log log = LogFactory.getLog(AMultiLanguageCommunitySubdata.class);
	
	private ICommunity myCommunity; 
	private int communityId;
	
	public AMultiLanguageCommunitySubdata(ICommunity myCommunity, ADataAccessFactory dataAccessor)  {
			super(dataAccessor);
			this.setCommunityData(myCommunity);
		
	}
	
	
	/**
	 * @return the communityData
	 */
	public ICommunity getCommunityData() {
		return myCommunity;
	}
	/**
	 * @param communityData the communityData to set
	 */
	private void setCommunityData(ICommunity communityData) {
		this.myCommunity = communityData;
	}
	

	/**
	 * @return the communityId
	 */
	public Integer getCommunityId() {
		return communityId;
	}

	/**
	 * @param communityId the communityId to set
	 */
	public void setCommunityId(int communityId) {
		if (communityId!=this.getCommunityData().getCommunityId()) {
			log.warn("Inconsistent communityId / communityData");
		}
		this.communityId = communityId;
	}
	
	
}

