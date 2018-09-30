package metaindex.data.element.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.transaction.PlatformTransactionManager;

import com.opensymphony.xwork2.Preparable;

import metaindex.data.beans.AMetaindexBean;
import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogContents;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;
import metaindex.data.community.beans.BeanCommunity;
import metaindex.data.dataset.Dataset;
import metaindex.data.dataset.DatasetHandle;
import metaindex.data.dataset.IDataset;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.element.Element;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.Metadata;
import metaindex.data.metadata.MetadataHandle;
import metaindex.data.metadata.specialized.Metadata_Image;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.dbaccess.IGenericEncodable.UnableToPopulateException;
import metaindex.dbaccess.accessors.AMetadataAccessor;


public class BeanElement extends AMetaindexBean implements Preparable  {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanElement.class);
	  	
	@Override
  	public String execute() throws Exception {
  				
	// load full element data if not already done
	if (this.getSelectedElement().isIdentified()) {// && !this.getSelectedElement().getRefData().isLoaded()) {
		this.getSelectedElement().update();
	}
	
		return BeanProcessResult.BeanProcess_SUCCESS.toString();
	}
  


	@Override
	public IElement clone() throws CloneNotSupportedException {
		log.error("Clone method not available for Bean object");
		return null;
	}


}
