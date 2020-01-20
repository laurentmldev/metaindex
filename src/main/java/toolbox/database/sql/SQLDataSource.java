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
