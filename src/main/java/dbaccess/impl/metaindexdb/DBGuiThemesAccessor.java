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

import metaindex.data.userprofile.GuiThemeData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.accessors.AGuiThemesAccessor;



public class DBGuiThemesAccessor extends AGuiThemesAccessor {
	
//-------- DB Management InnerClasses ----------
	
	private class GuiThemesExtractor implements ResultSetExtractor<GuiThemeData> {

		 public GuiThemeData extractData(ResultSet resultSet) throws SQLException, DataAccessException {
		  
			 GuiThemeData languageRow = new GuiThemeData();
		  
		  languageRow.setId(resultSet.getInt(1));
		  languageRow.setName(unescape(resultSet.getString(2)));
		  languageRow.setShortName(unescape(resultSet.getString(3)));
		  		  
		  return languageRow;
		 }
		}
		
	private class GuiThemesMapper implements RowMapper<GuiThemeData> {
	
	 public GuiThemeData mapRow(ResultSet resultSet, int line) throws SQLException 
	 {
	  GuiThemesExtractor languagesExtractor = new GuiThemesExtractor();
	  return languagesExtractor.extractData(resultSet);
	 }
	
	}
//-------- End of DB Management InnerClasses ----------
	
	private Log log = LogFactory.getLog(DBGuiThemesAccessor.class);

	static final String SQL_GET_ALL_FROM_GuiThemes = "select guitheme_id,name,shortname from guithemes";
	
	private List<GuiThemeData> guithemes  = new ArrayList<GuiThemeData>();

	public DBGuiThemesAccessor(ADataAccessFactory accessorsFactory, DataSource datasource,PlatformTransactionManager txManager) throws DataAccessErrorException,DataAccessConnectException
	{
		super(accessorsFactory,datasource, txManager);
		populate();
		
	}
	
	private void populate() throws DataAccessErrorException
	{
		String sql = SQL_GET_ALL_FROM_GuiThemes;
		
		try {
			guithemes = querySqlData(sql, new GuiThemesMapper());
		} catch (Exception e)
		{
			log.error("DB Error while creating retrieving available GUI Languages : "+e.getMessage());
			throw new DataAccessErrorException(e);
		}	
	}
	
	public List<GuiThemeData> getGuiThemesList()
	{
		return this.guithemes;
	}
}
