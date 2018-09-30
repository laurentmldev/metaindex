package metaindex.dbaccess.impl.metaindexdb;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.userprofile.GuiLanguageData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.accessors.AGuiLanguagesAccessor;


public class DBGuiLanguagesAccessor extends AGuiLanguagesAccessor {
	
//-------- DB Management InnerClasses ----------
	
	private class GuiLanguagesExtractor implements ResultSetExtractor<GuiLanguageData> {

		 public GuiLanguageData extractData(ResultSet resultSet) throws SQLException, DataAccessException {
		  
		  GuiLanguageData languageRow = new GuiLanguageData();
		  
		  languageRow.setId(resultSet.getInt(1));
		  languageRow.setName(unescape(resultSet.getString(2)));
		  languageRow.setShortName(unescape(resultSet.getString(3)));
		  
		  return languageRow;
		 }
		}
		
	private class GuiLanguagesMapper implements RowMapper<GuiLanguageData> {
	
	 public GuiLanguageData mapRow(ResultSet resultSet, int line) throws SQLException 
	 {
	  GuiLanguagesExtractor languagesExtractor = new GuiLanguagesExtractor();
	  return languagesExtractor.extractData(resultSet);
	 }
	
	}
//-------- End of DB Management InnerClasses ----------
	
	private Log log = LogFactory.getLog(DBGuiLanguagesAccessor.class);
	
	static final String SQL_GET_ALL_FROM_GUILANGUAGES = "select * from guilanguages";
	
	private List<GuiLanguageData> guiLanguages  = new ArrayList<GuiLanguageData>();

	public DBGuiLanguagesAccessor(ADataAccessFactory accessorsFactory,DataSource dataSource,PlatformTransactionManager txManager) 
										throws DataAccessErrorException,DataAccessConnectException
	{
		super(accessorsFactory,dataSource,txManager);
		populate();
		
	}
	
	private void populate() throws DataAccessErrorException
	{
		String sql = SQL_GET_ALL_FROM_GUILANGUAGES;
		try {
			guiLanguages = querySqlData(sql, new GuiLanguagesMapper());
		} catch (Exception e)
		{
			log.error("DB Error while creating retrieving available GUI Languages : "+e.getMessage());
			throw new DataAccessErrorException(e);
		}
	}
	
	public List<GuiLanguageData> getGuiLanguagesList()
	{
		return this.guiLanguages;
	}
}
