package metaindex.data.commons.globals.plans.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import metaindex.data.commons.globals.plans.Plan;
import metaindex.data.userprofile.IUserProfileData.CATEGORY;
import metaindex.data.commons.globals.plans.IPlan;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLPopulateStmt;
import toolbox.exceptions.DataProcessException;

class PopulatePlanFromDbStmt extends SQLPopulateStmt<IPlan>   {

	private List<IPlan> _data;
	private Boolean _onlyIfTimestampChanged=false;
	private Boolean _newOnesOnly=false;
	
	public static final String SQL_REQUEST = 
			"select plan_id,name,availableForPurchase,"
						+"quotaCreatedCatalogs,quotaNbDocsPerCatalog,quotaDriveMBytesPerCatalog,"
						+"yearlyCostEuros,category,lastUpdate"
			+" from plans";	

	public PopulatePlanFromDbStmt(List<IPlan> d, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_data=d;
	}
	public PopulatePlanFromDbStmt(List<IPlan> d, Boolean onlyIfTimestampChanged, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_data=d;
		_onlyIfTimestampChanged=onlyIfTimestampChanged;
	}
	public PopulatePlanFromDbStmt(List<IPlan> d, 
					Boolean onlyIfTimestampChanged,
					Boolean newOnesOnly,
					SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_data=d;
		_onlyIfTimestampChanged=onlyIfTimestampChanged;
		_newOnesOnly=newOnesOnly;
	}
	public PopulatePlanFromDbStmt(SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_data=new ArrayList<>();
	}

	@Override
	public IPlan mapRow(ResultSet rs, int rowNum) throws SQLException {	
		
		IPlan d;
		Integer dbKey = rs.getInt(1);
		d = _data.stream()
				.filter(p -> p.getId().equals(dbKey))
				.findFirst()
				.orElse(null);
		
		if (d==null) { 
			d =new Plan();
			_data.add(d);
		}

		Timestamp dbDate = rs.getTimestamp(9);
		if (_onlyIfTimestampChanged==true) {
			if (!d.shallBeProcessed(dbDate)) { return d; } 
		}
		
		d.setId(rs.getInt(1));
		d.setName(rs.getString(2));
		d.setAvailableForPurchase(rs.getBoolean(3));
		d.setQuotaCatalogsCreated(rs.getInt(4));
		d.setQuotaNbDocsPerCatalog(rs.getLong(5));
		d.setQuotaDriveMBytesPerCatalog(rs.getLong(6));
		d.setYearlyCostEuros(rs.getFloat(7));
		d.setCategory(CATEGORY.valueOf(rs.getString(8)));
		d.setLastUpdate(dbDate);
		return d;
	}

	@Override
	public String buildSqlQuery() { 
		String sql = SQL_REQUEST;
		
		if (_data.size()>0) {
			String comp="=";
			if (_newOnesOnly==true) { comp="!="; }
			sql+=" where ";
			String condSql="";
			for (IPlan p : _data) {
				if (condSql.length()>0) { condSql+=" and "; }
				condSql +=" plan_id"+comp+p.getId();
			}
			sql+=condSql;
		}
		return sql;
	}
					
};
