package metaindex.data.community;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.ContextLoader;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.IBufferizedData;
import metaindex.data.IBufferizedDataHandle;
import metaindex.data.IGenericMetaindexData;
import metaindex.data.community.Community;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDBAccessFactoryManager;
import metaindex.dbaccess.IDBAccessedData;
import metaindex.dbaccess.IDataAccessAware;
import metaindex.dbaccess.IJsonEncodable;

public  interface ICommunityComplexData extends IGenericMetaindexData {
	
	
	public Integer getCommunityId();
	
	/**
	 * Says if this subdata actually modify its base template
	 * If isTemplated is false, then always return false.
	 */
	public boolean isModifyOverridenTemplate();

	/**
	 * Says if this subdata is based on a template
	 */
	public boolean isTemplated();


}
