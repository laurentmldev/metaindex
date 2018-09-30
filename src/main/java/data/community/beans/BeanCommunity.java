package metaindex.data.community.beans;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.transaction.PlatformTransactionManager;

import com.opensymphony.xwork2.Preparable;

import metaindex.data.IMultiLanguageData;
import metaindex.data.beans.AMetaindexBean;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.CatalogHandle;
import metaindex.data.catalog.ICatalogContents;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.community.CommunityDatatype;
import metaindex.data.community.CommunityTerm;
import metaindex.data.community.CommunityTermHandle;
import metaindex.data.community.CommunityVocabularySet;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;
import metaindex.data.community.ICommunityTermData;
import metaindex.data.community.ICommunityTermHandle;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IGenericEncodable.UnableToPopulateException;
import metaindex.websockets.AMetaindexWSServer;


public class BeanCommunity extends AMetaindexBean implements Preparable  {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanCommunity.class);
	private String idName = "";
		
	@Override
  	public String execute() throws Exception {
				
  		// load full community data TODO: if not already done
  		this.getSelectedCommunity().update();
  		return BeanProcessResult.BeanProcess_SUCCESS.toString();
	}
 
	public String getIdName() { return idName; }
	public void setIdName(String ic) { this.idName = ic; }

}
