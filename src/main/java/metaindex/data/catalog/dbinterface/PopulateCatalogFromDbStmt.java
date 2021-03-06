package metaindex.data.catalog.dbinterface;

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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;

import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLPopulateStmt;
import toolbox.exceptions.DataProcessException;

class PopulateCatalogFromDbStmt extends SQLPopulateStmt<ICatalog>   {

	private Log log = LogFactory.getLog(PopulateCatalogFromDbStmt.class);
	
	public static final String SQL_REQUEST = 
			"select catalogs.catalog_id,catalogs.shortname, "
			+"catalogs.owner_id,catalogs.thumbnailUrl, "							
			+"catalogs.itemNameFields,catalogs.itemThumbnailUrlField,catalogs.urlPrefix,catalogs.perspectiveMatchField, "
			+"catalogs.timeField_term_id,catalogs.lastUpdate "
			+"from catalogs";
	
	List<ICatalog> _data;
	Boolean _onlyIfTimestampChanged=false;
	
	public PopulateCatalogFromDbStmt(List<ICatalog> d, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_data=d;
	}
	public PopulateCatalogFromDbStmt(List<ICatalog> d, SQLDataConnector ds, Boolean onlyIfTimestampChanged) 
																				throws DataProcessException { 
		super(ds);
		_data=d;
		_onlyIfTimestampChanged=onlyIfTimestampChanged;
	}
	public PopulateCatalogFromDbStmt(SQLDataConnector ds) throws DataProcessException { 
		super(ds);
	}
	
	
	@Override
	public ICatalog mapRow(ResultSet rs, int rowNum) throws SQLException {
		ICatalog d;
		String dbKey = rs.getString(2);
		d = _data.stream()
				.filter(p -> p.getName().equals(dbKey))
				.findFirst()
				.orElse(null);
		if (d==null) {
			d = new Catalog();
			_data.add(d);
		}
		
		if (_onlyIfTimestampChanged==true) {
			Timestamp dbDate = rs.getTimestamp(10);
			if (!d.shallBeProcessed(dbDate)) { return d; } 
		}
		d.setId(rs.getInt(1));
		d.setName(rs.getString(2));
		d.setOwnerId(rs.getInt(3));
		d.setThumbnailUrl(rs.getString(4));
		
		List<String> fieldnames = new ArrayList<String>();
		String[] fieldnamesArray = rs.getString(5).split(",");
		for (String f : fieldnamesArray) { fieldnames.add(f); }
		d.setItemNameFields(fieldnames);
		
		d.setItemThumbnailUrlField(rs.getString(6));
		d.setItemsUrlPrefix(rs.getString(7));
		d.setPerspectiveMatchField(rs.getString(8));
		d.setTimeFieldTermId(rs.getInt(9));
		d.setLastUpdate(rs.getTimestamp(10));
		return d;
	}

	@Override
	public String buildSqlQuery()  {				
		String sql = SQL_REQUEST;
		if (_data.size()>0) {
			sql+=" where ";		
			Iterator<ICatalog> it = _data.iterator();
			Boolean firstOne=true;
			while (it.hasNext()) {
				if (!firstOne) { sql+=" or "; }
				sql += "catalogs.shortname='"+it.next().getName()+"'";
				firstOne=false;
			}
		}
		return sql;
	}
					
};
