package metaindex.data.commons.globals.guilanguage;

import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.IDatabaseInterface;

public class GuiLanguage implements IGuiLanguage {
	
	private Log log = LogFactory.getLog(GuiLanguage.class);
	
	
	private Integer _id;
	private String _name;
	private String _shortName;
	
	@Override
	public Integer getId() { return _id; }
	@Override
	public void setId(Integer id) { _id=id; }

	@Override
	public String getName() { return _name; }
	@Override
	public void setName(String name) { _name=name; }
	
	@Override
	public String getShortname() { return _shortName; }
	@Override
	public void setShortName(String shortName) { _shortName=shortName; }
	
	
	
	
}
