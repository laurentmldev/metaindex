package metaindex.data.commons.globals.plans.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import java.sql.ResultSet;
import java.sql.SQLException;

import metaindex.data.commons.globals.plans.Plan;
import metaindex.data.commons.globals.plans.IPlan;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLReadStreamStmt;
import toolbox.exceptions.DataProcessException;

class LoadFromDbStmt extends SQLReadStreamStmt<IPlan>   {

	public static final String SQL_REQUEST = 
			"select plan_id,name,quotaCreatedCatalogs,quotaNbDocsPerCatalog,quotaFtpDiscSpaceBytesPerCatalog,yearlyCostEuros"
			+" from plans";	

	public LoadFromDbStmt(SQLDataConnector ds) throws DataProcessException { 
		super(ds);
	}

	@Override
	public IPlan mapRow(ResultSet rs, int rowNum) throws SQLException {		
		IPlan d;
		d = new Plan();
		d.setId(rs.getInt(1));
		d.setName(rs.getString(2));
		d.setQuotaCatalogsCreated(rs.getInt(3));
		d.setQuotaNbDocsPerCatalog(rs.getInt(4));
		d.setQuotaDiscBytesPerCatalog(rs.getLong(5));
		d.setYearlyCostEuros(rs.getFloat(6));
		return d;
	}

	@Override
	public String buildSqlQuery() { return SQL_REQUEST; }
					
};
