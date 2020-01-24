package metaindex.data.commons.globals;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ContextLoader;

import org.springframework.context.annotation.Configuration;

import metaindex.data.commons.database.IMxDbManager;
import metaindex.data.commons.database.MxDbManager;
import metaindex.data.commons.globals.guilanguage.GuiLanguagesManager;
import metaindex.data.commons.globals.guilanguage.IGuiLanguagesManager;
import metaindex.data.commons.globals.guitheme.GuiThemesManager;
import metaindex.data.commons.globals.guitheme.IGuiThemesManager;
import metaindex.data.catalog.CatalogsManager;
import metaindex.data.catalog.ICatalogsManager;
import metaindex.data.userprofile.IUsersManager;
import metaindex.data.userprofile.UsersManager;
import toolbox.database.elasticsearch.ESDataSource;
import toolbox.database.sql.SQLDataSource;
import toolbox.exceptions.DataAccessException;
import toolbox.exceptions.DataProcessException;

@Configuration
public class Globals {

	Properties _mx_config = new Properties();
	static Map<String, String> env = System.getenv();
	
	private static Globals _singleton=new Globals();
	public static Globals Get() { return _singleton; }
	
	/**
	 * Prop value from config files.
	 * Value might be overriden from env
	 * @param propname
	 * @return
	 */
	public static String GetMxProperty(String propname) {
		if (env.containsKey(propname)) { 
			String propFromEnv = env.get(propname);
			if (propFromEnv.length()>0) { return propFromEnv; }
		}
		
		return _singleton._mx_config.getProperty(propname); 
	}
	private Globals() {
		
		try {
			_mx_config.load(getClass().getClassLoader().getResourceAsStream("metaindex.properties"));
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	// ------------------------
	
	// TODO move it into Metaindex 
	public enum MX_PROCESSING_TYPE { UNKNOWN_PROCESSING,
								CATALOG_UPLOAD_PROCESSING,
								CATALOG_UPLOAD_DB_UPLOADING};
	
	private Log log = LogFactory.getLog(Globals.class);
	private final String DB_DATASOURCE_SQL = "dataSource";
	
	public enum APPLICATION_STATUS { STOPPED, RUNNING, FAILURE, MAINTENANCE };
	private APPLICATION_STATUS _applicationStatus = APPLICATION_STATUS.STOPPED;	
	
	private SQLDataSource _sqlDs;
	private ESDataSource _esDs;
	private IGuiLanguagesManager _guiLanguagesManager = new GuiLanguagesManager();
	private IGuiThemesManager _guiThemesManager = new GuiThemesManager();	
	private IUsersManager _usersManager = new UsersManager();
	private ICatalogsManager _catalogsManager = new CatalogsManager();
	private IMxDbManager _dbManager; 
	
	private Map<String,PropertiesConfiguration> _propertiesMap = new ConcurrentHashMap<String,PropertiesConfiguration>();
	
	public String getWebappsFsPath() { return System.getProperty("catalina.base")+"/webapps"; }
	
	public String getAppBaseUrl() {
		return Globals.GetMxProperty("mx.protocol")+"://"
				+Globals.GetMxProperty("mx.host")+":"+Globals.GetMxProperty("mx.port")
				+"/";		
	}
	public void init() throws DataProcessException {
		
		log.info("###### MetaindeX v"+Globals.GetMxProperty("mx.version")+" ######");
		log.info("###### mx.="+Globals.GetMxProperty("mx."));
		log.info("###### mx.host="+Globals.GetMxProperty("mx.host"));
		log.info("###### mx.protocol="+Globals.GetMxProperty("mx.protocol"));
		log.info("###### mx.port="+Globals.GetMxProperty("mx.port"));
		log.info("###### mx.appname="+Globals.GetMxProperty("mx.appname"));
		log.info("###### mx.ftp.port.range_low="+Globals.GetMxProperty("mx.ftp.port.range_low"));
		log.info("###### mx.ftp.port.range_high="+Globals.GetMxProperty("mx.ftp.port.range_high"));
		log.info("###### mx.ftp.passive.range_low="+Globals.GetMxProperty("mx.ftp.passive.range_low"));
		log.info("###### mx.ftp.passive.range_high="+Globals.GetMxProperty("mx.ftp.passive.range_high"));
		log.info("###### ------------------------ KIBANA ------------------------");
		log.info("###### mx.kibana.host="+Globals.GetMxProperty("mx.kibana.host"));
		log.info("###### mx.kibana.protocol="+Globals.GetMxProperty("mx.kibana.protocol"));
		log.info("###### mx.kibana.port="+Globals.GetMxProperty("mx.kibana.port"));
		log.info("###### mx.kibana.urlparams="+Globals.GetMxProperty("mx.kibana.urlparams"));
		log.info("###### ------------------------ MySQL ------------------------");
		log.info("###### mx.sql.host="+Globals.GetMxProperty("mx.sql.host"));
		log.info("###### mx.sql.port="+Globals.GetMxProperty("mx.sql.port"));
		log.info("###### mx.sql.dbname="+Globals.GetMxProperty("mx.sql.dbname"));
		log.info("###### mx.sql.user="+Globals.GetMxProperty("mx.sql.user"));
		log.info("###### ------------------------ ElastisSearch ------------------------");
		log.info("###### mx.elk.host="+Globals.GetMxProperty("mx.elk.host"));
		log.info("###### mx.elk.protocol="+Globals.GetMxProperty("mx.elk.protocol"));
		log.info("###### mx.elk.port1="+Globals.GetMxProperty("mx.elk.port1"));
		log.info("###### mx.elk.port2="+Globals.GetMxProperty("mx.elk.port2"));
		log.info("###### ---------------------------------------------------------");
						
		if (_sqlDs==null) {
			
			_esDs = new ESDataSource(GetMxProperty("mx.elk.host"),
									 Integer.valueOf(GetMxProperty("mx.elk.port1")),
									 Integer.valueOf(GetMxProperty("mx.elk.port2")),
									 GetMxProperty("mx.elk.proto"));
			/*
			MysqlDataSource mysqlDS = new MysqlDataSource();
			String sqlUrl="jdbc:mysql://"
							+this.getConfig().getProperty("mx.sql.host")
							+":"+this.getConfig().getProperty("mx.sql.port")
							+"/"+this.getConfig().getProperty("mx.sql.dbname")
							+"?useUnicode=true&amp;useLegacyDatetimeCode=false&amp;serverTimezone=UTC";
			mysqlDS.setURL(sqlUrl);
			mysqlDS.setUser(this.getConfig().getProperty("mx.sql.user"));
			mysqlDS.setPassword(this.getConfig().getProperty("mx.sql.password"));
			*/
			// using same SQL datasource then for users authentication
			DataSource ds = (DataSource)ContextLoader.getCurrentWebApplicationContext().getBean(DB_DATASOURCE_SQL);
			
			_sqlDs = new SQLDataSource(ds);
			_dbManager = new MxDbManager(_sqlDs,_esDs);
			try {			
				// load all languages and themes data at init
				_guiLanguagesManager.loadFromDb();
				_guiThemesManager.loadFromDb();	
				Globals.Get().getCatalogsMgr().loadFromDb();
				Globals.Get().getUsersMgr().loadFromDb();
								
			} catch (Exception e) {
				throw new DataProcessException("Application initialization failed.",e);
			}
		}
	}
	public IGuiLanguagesManager getGuiLanguagesMgr() { return _guiLanguagesManager; }
	public IGuiThemesManager getGuiThemesMgr() { return _guiThemesManager; }
	public IUsersManager getUsersMgr() { return _usersManager; }
	public ICatalogsManager getCatalogsMgr() { return _catalogsManager; }
	public IMxDbManager getDatabasesMgr() { return _dbManager; }
	
	/**
	 * Get properties corresponding to given name. Properties are loaded from file in resources folder,
	 * named <propsName>.properties 
	 * @param propsName the name of properties to be retrieved (without '.properties' suffix)
	 * @return corresponding properties object
	 * @throws DataAccessException if no such properties could be loaded from resources folder
	 */
	public PropertiesConfiguration getProperties(String propsName) throws DataAccessException {
		// if not already loaded, get it from file
		if (_propertiesMap.get(propsName)==null) {
			Configurations configs = new Configurations();			
			try {
				String fileName = Globals.class.getClassLoader()
								.getResource(propsName+".properties").getFile();
				PropertiesConfiguration curProperties = configs.properties(new File(fileName));
				_propertiesMap.put(propsName, curProperties);
			} catch (ConfigurationException cex) { throw new DataAccessException(cex); }		
		}
		
		return _propertiesMap.get(propsName);
	}
	public APPLICATION_STATUS getApplicationStatus() {
		return _applicationStatus;
	}
	public void setApplicationStatus(APPLICATION_STATUS _applicationStatus) {
		Globals.Get()._applicationStatus = _applicationStatus;
	}

}
