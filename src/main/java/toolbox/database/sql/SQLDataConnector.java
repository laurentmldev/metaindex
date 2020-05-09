package toolbox.database.sql;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.userprofile.UserProfileData;
import toolbox.database.IDataConnector;
import toolbox.exceptions.DataProcessException;

public class SQLDataConnector implements IDataConnector {
	
	private Log log = LogFactory.getLog(SQLDataConnector.class);
	
	DataSource _sqlDs;
	public SQLDataConnector(DataSource ds) { _sqlDs=ds; }
	
	public DataSource getSqlDataSource() { return _sqlDs; }
	
	public Connection getConnection() { 
		try {
			return _sqlDs.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} 
	}

	@Override
	public void close() {
		try {
			getConnection().close();
		} catch (SQLException e) {
			log.error("unable to close SQL datasource : "+e.getMessage());
		}
	}
	
}
