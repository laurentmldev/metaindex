package metaindex.app;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.mail.MessagingException;
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
import metaindex.app.periodic.fs.MxTmpFolderMonitor;
import metaindex.app.periodic.statistics.MxStatisticsManager;
import metaindex.data.catalog.CatalogsManager;
import metaindex.data.catalog.ICatalogsManager;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.IUserProfileData.USER_ROLE;
import metaindex.data.userprofile.IUsersManager;
import metaindex.data.userprofile.UserProfileData;
import metaindex.data.userprofile.UsersManager;
import toolbox.database.elasticsearch.ElasticSearchConnector;
import toolbox.database.kibana.KibanaConnector;
import toolbox.database.kibana.KibanaConnector.KIBANA_PRIVILEGE;
import toolbox.database.kibana.KibanaConnector.KIBANA_SPACE_FEATURE;
import toolbox.database.sql.SQLDataConnector;
import toolbox.exceptions.DataAccessException;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.mailing.GoogleMailSender;
import toolbox.utils.mailing.IEmailSender;
import toolbox.utils.statistics.IStatisticsManager;

@Configuration
public class Globals {

	private static final String MX_PROPERTIES_FILE = "metaindex.properties";
	public static final Integer AUTOREFRESH_PERIOD_SEC=5;
	private static final String MX_EMAIL_SUBJECT_PREFIX= "[MetaindeX]";
	public static final String LOCAL_USERDATA_PATH_SUFFIX="/metaindex/userdata/";
	
	Properties _mx_config = new Properties();
	static Map<String, String> env = System.getenv();
	
	private static Globals _singleton=new Globals();
	public static Globals Get() { return _singleton; }
	
	private static IStatisticsManager _mxStats= new MxStatisticsManager();
	private static MxTmpFolderMonitor _mxTmpFolderCleaner= new MxTmpFolderMonitor();
	private static String _contextPath="";
	
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
	
	public static IStatisticsManager GetStatsMgr() { return _mxStats; }
	
	private Globals() {
		
		try {
			_mx_config.load(getClass().getClassLoader().getResourceAsStream(MX_PROPERTIES_FILE));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	// ------------------------
	
	public enum MX_PROCESSING_TYPE { UNKNOWN_PROCESSING,
								CATALOG_UPLOAD_PROCESSING,
								CATALOG_UPLOAD_DB_UPLOADING};
	
	private Log log = LogFactory.getLog(Globals.class);
	private final String DB_DATASOURCE_SQL = "dataSource";
	
	public enum APPLICATION_STATUS { STOPPED, RUNNING, FAILURE, MAINTENANCE };
	private APPLICATION_STATUS _applicationStatus = APPLICATION_STATUS.STOPPED;	
	
	private SQLDataConnector _sqlConnector;
	private ElasticSearchConnector _esConnector;
	private KibanaConnector _kibanaConnector;
	private IGuiLanguagesManager _guiLanguagesManager = new GuiLanguagesManager();
	private IGuiThemesManager _guiThemesManager = new GuiThemesManager();	
	private IUsersManager _usersManager = new UsersManager();
	private ICatalogsManager _catalogsManager = new CatalogsManager();
	private IMxDbManager _dbManager; 
	
	private Map<String,PropertiesConfiguration> _propertiesMap = new ConcurrentHashMap<String,PropertiesConfiguration>();
	
	private IEmailSender _mailSender = new GoogleMailSender();
	
	// -------------
	
	public void sendEmail(String recipientEmail, String subject, String msg) throws DataProcessException {
		
		if (msg.length()==0 || subject.length()==0) {
			throw new DataProcessException("Cowardly refusing to send empty email (or with empty subject) to '"+recipientEmail+"'");
		}
		
		try {			
			_mailSender.send(GetMxProperty("mx.mailer.user"), 
							 GetMxProperty("mx.mailer.password"), 
							 recipientEmail, 
							 MX_EMAIL_SUBJECT_PREFIX+" "+subject, 
							 msg);
			
		} catch (MessagingException e) {
			throw new DataProcessException("Unable to send email '"+subject+"' to '"+recipientEmail+"' : "+e.getMessage(),e);
		}
	}
	public void setWebappsFsPath(String contextPath) { 
		_contextPath=contextPath; 
	}
	public String getWebappsFsPath() { 
		return _contextPath;
	}
	public String getUserdataFsPath() { 
		return GetMxProperty("mx.userdata.path");
	}
	public String getWebappsTmpFsPath() {
		String path=getWebappsFsPath()+"/mxtmp/";		
		File directory = new File(path);
        if (! directory.exists()){ directory.mkdir(); }        
        return path;
    }
	
	public String getAppBaseUrl() {
		return Globals.GetMxProperty("mx.protocol")+"://"
				+Globals.GetMxProperty("mx.host")+":"+Globals.GetMxProperty("mx.port")
				+"/";		
	}
	public String getWebappsTmpUrl() { return getAppBaseUrl()+"/mxtmp/"; }
	
	/**
	 * This is the main application initialisation function, called once at startup (i.e. at connection of first user)
	 * @throws DataProcessException
	 */
	public void init() throws DataProcessException {
		
		if (_sqlConnector==null) {
			log.info("MetaindeX connections init start ... ");
			
			_esConnector = new ElasticSearchConnector(GetMxProperty("mx.elk.host"),
									 Integer.valueOf(GetMxProperty("mx.elk.port1")),
									 Integer.valueOf(GetMxProperty("mx.elk.port2")),
									 GetMxProperty("mx.elk.protocol"));
			
			_kibanaConnector = new KibanaConnector(GetMxProperty("mx.kibana.api.host"),
												   Integer.valueOf(GetMxProperty("mx.kibana.api.port")),
												   GetMxProperty("mx.kibana.api.protocol")
												   );
			
			// using same SQL datasource then for users authentication
			DataSource ds = (DataSource)ContextLoader.getCurrentWebApplicationContext().getBean(DB_DATASOURCE_SQL);
			
			_sqlConnector = new SQLDataConnector(ds);
			_dbManager = new MxDbManager(_sqlConnector,_esConnector,_kibanaConnector);
			try {			
				// load all languages and themes data at init
				_guiLanguagesManager.loadFromDb();
				_guiThemesManager.loadFromDb();	
				Globals.Get().getCatalogsMgr().loadFromDb();
				Globals.Get().getUsersMgr().loadFromDb();
								
			} catch (Exception e) {
				throw new DataProcessException("Application initialization failed.",e);
			}
			
			// starting statistics manager 
			_mxStats.start();
			// starting tmp-files cleaner
			_mxTmpFolderCleaner.start();
			
			log.info("MetaindeX connections init done");
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
		String mxPropStatus=GetMxProperty("mx.status");
		if(		   (_applicationStatus==APPLICATION_STATUS.RUNNING
				|| _applicationStatus==APPLICATION_STATUS.STOPPED)
				&& mxPropStatus.equals("MAINTENANCE")) {
			_applicationStatus=APPLICATION_STATUS.MAINTENANCE;
		}
		return _applicationStatus;
	}
	public void setApplicationStatus(APPLICATION_STATUS applicationStatus) {
		_applicationStatus = applicationStatus;
	}

	public String getDetailsStr() {

		return   "\n	###### MetaindeX v"+Globals.GetMxProperty("mx.version")+" ######"+"\n"
				+"- mx.status="+Globals.GetMxProperty("mx.status")+"\n\n"
				+"- mx.host="+Globals.GetMxProperty("mx.host")+"\n"
				+"- mx.protocol="+Globals.GetMxProperty("mx.protocol")+"\n"
				+"- mx.port="+Globals.GetMxProperty("mx.port")+"\n"
				+"- mx.appname="+Globals.GetMxProperty("mx.appname")+"\n"
				+"- mx.userdata.path="+Globals.GetMxProperty("mx.userdata.path")+"\n"
				+"- mx.logs.path="+Globals.GetMxProperty("mx.logs.path")+"\n"
				+"- ------------------------ FTP CONF ------------------------"+"\n"
				+"- mx.ftp.port.range_low="+Globals.GetMxProperty("mx.ftp.port.range_low")+"\n"
				+"- mx.ftp.port.range_high="+Globals.GetMxProperty("mx.ftp.port.range_high")+"\n"
				+"- mx.ftp.passive.range_low="+Globals.GetMxProperty("mx.ftp.passive.range_low")+"\n"
				+"- mx.ftp.passive.range_high="+Globals.GetMxProperty("mx.ftp.passive.range_high")+"\n"	
				+"- ------------------------ KIBANA USER ACCESS ------------------------"+"\n"
				+"- mx.kibana.host="+Globals.GetMxProperty("mx.kibana.host")+"\n"
				+"- mx.kibana.protocol="+Globals.GetMxProperty("mx.kibana.protocol")+"\n"
				+"- mx.kibana.port="+Globals.GetMxProperty("mx.kibana.port")+"\n"
				+"- mx.kibana.urlparams="+Globals.GetMxProperty("mx.kibana.urlparams")+"\n"
				+"- ------------------------ KIBANA REST API ------------------------"+"\n"
				+"- mx.kibana.api.hos="+Globals.GetMxProperty("mx.kibana.api.host")+"\n"
				+"- mx.kibana.api.protocol="+Globals.GetMxProperty("mx.kibana.api.protocol")+"\n"
				+"- mx.kibana.api.port="+Globals.GetMxProperty("mx.kibana.api.port")+"\n"				
				+"- ------------------------ ElastisSearch REST API ------------------------"+"\n"
				+"- mx.elk.host="+Globals.GetMxProperty("mx.elk.host")+"\n"
				+"- mx.elk.protocol="+Globals.GetMxProperty("mx.elk.protocol")+"\n"
				+"- mx.elk.port1="+Globals.GetMxProperty("mx.elk.port1")+"\n"
				+"- mx.elk.port2="+Globals.GetMxProperty("mx.elk.port2")+"\n"
				+"- ------------------------ MySQL API ------------------------"+"\n"
				+"- mx.sql.host="+Globals.GetMxProperty("mx.sql.host")+"\n"
				+"- mx.sql.port="+Globals.GetMxProperty("mx.sql.port")+"\n"
				+"- mx.sql.dbname="+Globals.GetMxProperty("mx.sql.dbname")+"\n"
				+"- mx.sql.user="+Globals.GetMxProperty("mx.sql.user")+"\n"
				
			;
	}

}
