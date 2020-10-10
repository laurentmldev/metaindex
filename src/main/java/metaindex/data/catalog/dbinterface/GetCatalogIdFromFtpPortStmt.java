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

class GetCatalogIdFromFtpPortStmt extends SQLReadStreamStmt<Integer>   {

	private Log log = LogFactory.getLog(GetCatalogIdFromFtpPortStmt.class);
	
	public static final String SQL_REQUEST = 
			"select DISTINCT catalogs.catalog_id "
			+" from catalogs ";
	
	Integer _ftpPort=null;
	
	
	public GetCatalogIdFromFtpPortStmt(Integer ftpPort, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_ftpPort=ftpPort;
	}
	
	
	@Override
	public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
		Integer catalogId = rs.getInt(1);
		return catalogId; 		
	}

	@Override
	public String buildSqlQuery()  {				
		String sql = SQL_REQUEST+" where ftpPort="+_ftpPort;		
		return sql;
	}
					
};
