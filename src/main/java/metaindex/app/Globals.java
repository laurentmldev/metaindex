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
import java.text.Normalizer;
import java.text.Normalizer.Form;
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
import metaindex.data.commons.globals.plans.IPlansManager;
import metaindex.data.commons.globals.plans.PlansManager;
import metaindex.app.control.catalogdrive.ICatalogsDrive;
import metaindex.app.control.catalogdrive.SftpCatalogsDrive;
import metaindex.app.periodic.fs.MxTmpFolderMonitor;
import metaindex.app.periodic.monitoring.UsersQuotasChecker;
import metaindex.app.periodic.statistics.MxStatisticsManager;
import metaindex.data.catalog.CatalogsManager;
import metaindex.data.catalog.ICatalogsManager;
import metaindex.data.userprofile.IUsersManager;
import metaindex.data.userprofile.UsersManager;
import toolbox.database.elasticsearch.ElasticSearchConnector;
import toolbox.database.kibana.KibanaConnector;
import toolbox.database.kibana.KibanaConnector.KIBANA_HTTP_METHOD;
import toolbox.database.sql.SQLDataConnector;
import toolbox.exceptions.DataAccessException;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.mailing.DummyMailSender;
import toolbox.utils.mailing.GoogleMailSender;
import toolbox.utils.mailing.IEmailSender;
import toolbox.utils.statistics.IStatisticsManager;

@Configuration
public class Globals {

	private static final String MX_PROPERTIES_FILE = "metaindex.properties";
	public static final Integer AUTOREFRESH_PERIOD_SEC=5;
	private static final String MX_EMAIL_SUBJECT_PREFIX= "[MetaindeX]";
	public static final String LOCAL_USERDATA_PATH_SUFFIX="/userdata/catalogs";
	
	// files stored in catalogs drive must be normalized in order to ensure their are
	// always accessible via corresponding UTF-8 URI
	// This normalization is done when storing a new file, either via Web upload
	// of via SFTP upload.
	public static final Form LOCAL_USERDATA_NORMALIZATION_FORM=Normalizer.Form.NFC;
	
	Properties _mx_config = new Properties();
	static Map<String, String> env = System.getenv();
	
	private static Globals _singleton=new Globals();
	public static Globals Get() { return _singleton; }
	
	private static IStatisticsManager _mxStats= new MxStatisticsManager();
	private static MxTmpFolderMonitor _mxTmpFolderCleaner= new MxTmpFolderMonitor();
	private static UsersQuotasChecker _mxUsersQuotaChecker= new UsersQuotasChecker();
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
	
	public enum APPLICATION_STATUS { STOPPED, RUNNING, FAILURE };
	private APPLICATION_STATUS _applicationStatus = APPLICATION_STATUS.STOPPED;	
	
	private SQLDataConnector _sqlConnector;
	private ElasticSearchConnector _esConnector;
	private KibanaConnector _kibanaConnector;
	private IGuiLanguagesManager _guiLanguagesManager = new GuiLanguagesManager();
	private IGuiThemesManager _guiThemesManager = new GuiThemesManager();
	private IPlansManager _plansManager = new PlansManager();
	private IUsersManager _usersManager = new UsersManager();
	private ICatalogsManager _catalogsManager = new CatalogsManager();
	private IMxDbManager _dbManager; 
	
	private Map<String,PropertiesConfiguration> _propertiesMap = new ConcurrentHashMap<String,PropertiesConfiguration>();
	
	private IEmailSender _mailSender = new GoogleMailSender();
	private ICatalogsDrive _catalogsDrive = null;
	
	// -------------
	
	private final String MX_EMAIL_HEADER="<html>    \n" + 
			"<header>\n" + 
			"\n" + 
			"    <style>\n" + 
			"        .scale-color-white:hover {\n" + 
			"            color: white;\n" + 
			"        }\n" + 
			"        .scale, .scale-color-white, .scale-color-blue, .scale-color-green, .scale-bgcolor-white, .mx-help-icon {\n" + 
			"            transition: all 0.3s ease-in-out;\n" + 
			"        }\n" + 
			"        .app-title {\n" + 
			"            color: #929292;\n" + 
			"            text-shadow: 1px 2px 3px #555;\n" + 
			"            font-size: 5vw;\n" + 
			"            font-weight: bold;\n" + 
			"            text-align: center;\n" + 
			"            letter-spacing: 0.08rem;\n" + 
			"            font-family: \"Helvetica Neue\", Arial, sans-serif, \"Apple Color Emoji\", \"Segoe UI Emoji\", \"Segoe UI Symbol\", \"Noto Color Emoji\";\n" + 
			"        }\n" + 
			"        .app-title2 {\n" + 
			"            color: #a5c8d8;\n" + 
			"        }\n" + 
			"    </style>\n" + 
			"</header>\n" + 
			"<body>\n";
	
	private final String MX_EMAIL_FOOTER="<br/>\n" + 
			"<div class=\"app-title\" style=\"font-size:4vw;padding:0;margin:0;width:auto;\">\n" + 
			"    <span class=\"scale-color-white\">M</span><span class=\"app-title2 scale-color-white\" style=\"color:white;\">etainde</span><span class=\"scale-color-white\">X</span>            		            			\n" + 
			"</div>\n" + 
			"</body>\n" + 
			"</html>";
	
	public void sendEmail(String recipientEmail,String ccEmail,String cciEmail, String subject, String msg) throws DataProcessException {
		
		if (msg.length()==0 || subject.length()==0) {
			throw new DataProcessException("Cowardly refusing to send empty email (or with empty subject) to '"+recipientEmail+"'");
		}
		
		try {					
			_mailSender.send(GetMxProperty("mx.mailer.user"), 
						 GetMxProperty("mx.mailer.password"), 
						 recipientEmail, 
						 ccEmail,cciEmail,
						 MX_EMAIL_SUBJECT_PREFIX+" "+subject, 
						 MX_EMAIL_HEADER+msg+MX_EMAIL_FOOTER);
		
		} catch (MessagingException e) {
			throw new DataProcessException("Unable to send email '"+subject+"' to '"+recipientEmail+"' : "+e.getMessage(),e);
		}
	}
	public void sendEmail(String recipientEmail, String subject, String msg) throws DataProcessException {
		sendEmail(recipientEmail,"","",subject,msg);
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
	public String getUserdataFsPathCatalogs() { 
		return getUserdataFsPath()+"/catalogs";
	}
	public String getUserdataFsPathUsers() { 
		return getUserdataFsPath()+"/users";
	}
	public String getWebappsTmpFsPath() {
		String path=getWebappsFsPath()+"/"+Globals.GetMxProperty("mx.appname")+"/secure/mxtmp/";		
		File directory = new File(path);
        if (! directory.exists()){ directory.mkdir(); }        
        return path;
    }
	
	public String getHttpBaseUrl() {
		String baseurl= Globals.GetMxProperty("mx.protocol")+"://"
				+Globals.GetMxProperty("mx.host");
		String portStr=Globals.GetMxProperty("mx.port");
		if (portStr.length()>0) { baseurl+=":"+portStr; }
		return baseurl;
					
	}
	public String getWebAppBaseUrl() {
		return getHttpBaseUrl()+"/"+Globals.GetMxProperty("mx.appname");					
	}
	public String getWebAppsTmpUrl() { 
		String url = getWebAppBaseUrl()+"/secure/mxtmp/";
		return url;
	}
	
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
				log.info("loading Languages ... ");
				_guiLanguagesManager.loadFromDb();
				log.info("loading GUI themes ... ");
				_guiThemesManager.loadFromDb();
				log.info("loading Plans ... ");
				_plansManager.loadFromDb();
				log.info("loading Catalogs ... ");
				Globals.Get().getCatalogsMgr().loadFromDb();
				log.info("loading Users ... ");
				Globals.Get().getUsersMgr().loadFromDb();
								
			} catch (Exception e) {
				throw new DataProcessException("Application initialization failed.",e);
			}
			log.info("starting statistics manager ... ");
			_mxStats.start();
			
			log.info("starting tmp files cleaner ... ");
			_mxTmpFolderCleaner.start();
			
			log.info("starting quotas checker ... ");
			_mxUsersQuotaChecker.start();
			
			log.info("MetaindeX connections init done");
			
			
			if (isDevMode()) {
				_mailSender=new DummyMailSender();
			}
		}
				
		log.info("Starting MetaindeX SFTP server ... ");
		_catalogsDrive=new SftpCatalogsDrive(Integer.valueOf(
						Globals.GetMxProperty("mx.drive.sftp.port")));
		_catalogsDrive.start();	
		
		// send an email to ensure this service is functional
		this.sendEmail(Globals.GetMxProperty("mx.mailer.admin_recipient"), "Server Started !", 
					"Dear and beloved admin,<br/><br/>"
					+"Your MetaindeX server just started on '"+Globals.GetMxProperty("mx.host")+"'"
					+" (devMode="+isDevMode()+") and if you can read this email "
					+"then its a good sign that everything is working fine.<br/><br/>"
					+"See you soon ;)<br/>");
							
		
	}
	
	public void stop() throws DataProcessException {
		
		
			log.info("MetaindeX connections closing ... ");
			
			_esConnector.close();
			_kibanaConnector.close();
			_sqlConnector.close();
			_mxStats.stop();
			_mxTmpFolderCleaner.stop();
			_mxUsersQuotaChecker.stop();
			log.info("MetaindeX connections closed");
		
	}
	public IGuiLanguagesManager getGuiLanguagesMgr() { return _guiLanguagesManager; }
	public IGuiThemesManager getGuiThemesMgr() { return _guiThemesManager; }
	public IPlansManager getPlansMgr() { return _plansManager; }
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
	public void setApplicationStatus(APPLICATION_STATUS applicationStatus) {
		_applicationStatus = applicationStatus;
	}

	public Boolean isDevMode() {
		return new Boolean(GetMxProperty("mx.devmode"));
	}
	public String getDetailsStr() {

		return   "\n	###### MetaindeX v"+Globals.GetMxProperty("mx.version")+"  ######"+"\n"
				+"- mx.devmode="+isDevMode()+"\n"
				+"- mx.host="+Globals.GetMxProperty("mx.host")+"\n"
				+"- mx.protocol="+Globals.GetMxProperty("mx.protocol")+"\n"
				+"- mx.port="+Globals.GetMxProperty("mx.port")+"\n"
				+"- mx.appname="+Globals.GetMxProperty("mx.appname")+"\n"
				+"- mx.userdata.path="+Globals.GetMxProperty("mx.userdata.path")+"\n"
				+"- tmp files path="+Globals.Get().getWebappsTmpFsPath()+"\n"
				+"- ------------------------ DRIVES CONF ------------------------"+"\n"
				+"- mx.drive.sftp.port="+Globals.GetMxProperty("mx.drive.sftp.port")+"\n"
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
				+"- ------------------------ Statistics ------------------------"+"\n"
				+"- mx.statistics.update_period_sec="+Globals.GetMxProperty("mx.statistics.update_period_sec")+"\n"
				
				+System.getProperties()
			;
		
		
	}

}
