package metaindex.data.catalog.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLReadStreamStmt;
import toolbox.exceptions.DataProcessException;

class GetCatalogIdFromDrivePortStmt extends SQLReadStreamStmt<Integer>   {

	private Log log = LogFactory.getLog(GetCatalogIdFromDrivePortStmt.class);
	
	public static final String SQL_REQUEST = 
			"select DISTINCT catalogs.catalog_id "
			+" from catalogs ";
	
	Integer _drivePort=null;
	
	
	public GetCatalogIdFromDrivePortStmt(Integer port, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_drivePort=port;
	}
	
	
	@Override
	public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
		Integer catalogId = rs.getInt(1);
		return catalogId; 		
	}

	@Override
	public String buildSqlQuery()  {				
		String sql = SQL_REQUEST+" where drivePort="+_drivePort;		
		return sql;
	}
					
};
