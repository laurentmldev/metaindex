package metaindex.data;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADBAccessedData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IJsonEncodable;

public abstract class AGenericMetaindexData<T> extends ADBAccessedData implements IGenericMetaindexData {
	
	private Log log = LogFactory.getLog(AGenericMetaindexData.class);
	
	

	public AGenericMetaindexData(ADataAccessFactory dataAccessor)  {
		super(dataAccessor);
	}

	/* Clone
	@Override
	public abstract T clone() throws CloneNotSupportedException;
	*/
}

