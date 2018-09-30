package metaindex.data.userprofile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.community.Community;
import metaindex.data.community.ICommunitySubdata;
import metaindex.dbaccess.ADataAccessFactory;

/**
 * Java object container for GuiLanguage DB table.
 * Exactly same fields than GuiLanguage, which inherits from this class 
 * @author Laurent ML
 */
public class GuiThemeData extends AGenericMetaindexData<GuiThemeData> 
{

	public GuiThemeData() {
		super(ADataAccessFactory.getDataAccessImplFactory(
				ADataAccessFactory.DATA_ACCESS_IMPL_DB_METAINDEX));		
	}
	private Log log = LogFactory.getLog(GuiThemeData.class);
	private Integer id;
	private String name;
	private String shortname;
	
	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getShortName() { return shortname; }
	public void setShortName(String shortname) { this.shortname = shortname; }
	@Override
	public GuiThemeData clone() throws CloneNotSupportedException {
		log.error("Clone not implemented for GuiThemeData object.");
		return null;
	}
	@Override
	public void checkDataDBCompliance() throws DataAccessConstraintException {
		// nothing for now		
	}
	@Override
	public boolean isIdentified() {
		return true;
	}
	@Override
	public boolean isReadOnly() {
		return false;
	}
	@Override
	public void setReadOnly(boolean isReadOnly) {
		// nothing here		
	}
	

}
