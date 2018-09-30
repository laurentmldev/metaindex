package metaindex.test;

import java.util.HashMap;


import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.management.CommunitiesAccessor;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@RunWith(Suite.class)
@SuiteClasses({	
	
				TestUserProfile.class,				
				TestCommunity.class,
				
				/// TestCatalog should be run before TestElement data, because it changes list of defined elements
				TestCatalog.class,
				/// TestElement should be run before TestMetadata because of TestMetadata.testCreateMetadataFromTemplateWithImplicitNewDataset
				/// which invalidate current implementation of TestElementData.testTemplateElement
				TestElementData.class,
				TestDataset.class,
				TestMetadata.class,				
				TestCommunityTerms.class,		
				// Be careful not to set a breakpoint within the WS handshake, ...
				TestWebSockets.class,
				
				})
public class TestSuiteMetaindex {

	private static Log log = LogFactory.getLog(TestSuiteMetaindex.class);
	private static final String SQL_SCRIPT_POPULATE_TEST_DB = "sql/dbsetup.sql";
	private static final String SQL_SCRIPT_CLEAN_TEST_DB = "sql/dbclean.sql";
	public static HashMap<String,Object> testData = new HashMap<String,Object>();
    
	private static void cleanTestDB(DataSource ds) throws Exception {
		log.error("		... cleanTestDB");
		Resource resource = new ClassPathResource(SQL_SCRIPT_CLEAN_TEST_DB);
	    ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
	    databasePopulator.populate(ds.getConnection());		 
	}

	private static void populateTestDB(DataSource ds) throws Exception {
		log.error("		... populateTestDB");
		Resource resource = new ClassPathResource(SQL_SCRIPT_POPULATE_TEST_DB);
	    ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
	    databasePopulator.populate(ds.getConnection());
	}
	

    @BeforeClass
    public static void setUp() throws Exception {
    	log.error("Preparing Test Database :");
    	
    	//-- Global Datasource data
    	testData.put("global.datasource.url", "jdbc:mysql://localhost:8889/metaindexvalid?useUnicode=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
    	testData.put("global.datasource.username", "metaindexwebuser");
    	testData.put("global.datasource.password", "metaindexwebuserpwd");
    	
    	//-- User Profile Test Data
    	testData.put("userprofile.username", "testuser");
    	testData.put("userprofile.username2", "testuser2");
    	testData.put("userprofile.email", "testuser@metaindex.org");
    	testData.put("userprofile.password", "testuserpwd");
    	testData.put("userprofile.guiLanguageId", new Integer(1));
    	testData.put("userprofile.guiThemeId", new Integer(1));
    	testData.put("userprofile.acceptConditions", "on");
    	
    	//-- Community Test Data
    	testData.put("community.name", "Test Community");
    	testData.put("community.groupname", "Workers");
    	testData.put("community.en.name", "Test Community");
    	testData.put("community.en.name", "Test Community");

    	//-- Terms Test Data
    	testData.put("term.idName", "Testing Term");
    	testData.put("term.existingIdName", "Title");
    	testData.put("term.newTypeId", "5"); // 5=Number 

    	//-- Creating fake servlet context
    	/*
    	MockServletContext sc = new MockServletContext("classpath:");
        ServletContextListener listener = new ContextLoaderListener(wac);
        ServletContextEvent event = new ServletContextEvent(sc);
        listener.contextInitialized(event);
        */
    	//-- creating Test Application Context
    	DataSource testDataSource = new DriverManagerDataSource(
    							(String)testData.get("global.datasource.url"),
    							(String)testData.get("global.datasource.username"),
    							(String)testData.get("global.datasource.password"));
    	
	    try {
	    	cleanTestDB(testDataSource);
	    	populateTestDB(testDataSource);
	    	log.error("		... setGlobalDataSource");
	    	AGenericMetaindexData.setGlobalDataSource(testDataSource);
	    } catch (Exception e)
	    { 
	    	log.error(e.getMessage());
	    	throw e;
	    }
    }

    @AfterClass
    public static void tearDown() {
    	log.info("Tearing down MetaIndex Test Env");
    	CommunitiesAccessor.reset();

    }
}
