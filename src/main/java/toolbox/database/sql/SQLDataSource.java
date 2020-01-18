package toolbox.database.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import toolbox.database.IDataSource;
import toolbox.exceptions.DataProcessException;

public class SQLDataSource implements IDataSource {
	
	DataSource _sqlDs;
	public SQLDataSource(DataSource ds) { _sqlDs=ds; }
	
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
		// nothing to do explicitly		
	}
	
}
