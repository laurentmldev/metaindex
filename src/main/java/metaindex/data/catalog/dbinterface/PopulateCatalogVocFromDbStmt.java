package metaindex.data.catalog.dbinterface;

/**
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.CatalogVocabularySet;
import metaindex.data.catalog.ICatalog;
import metaindex.data.perspective.ICatalogPerspective;
import metaindex.data.userprofile.UserProfileData;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLPopulateStmt;
import toolbox.database.sql.SQLReadStreamStmt;
import toolbox.exceptions.DataProcessException;

class PopulateCatalogVocFromDbStmt extends SQLPopulateStmt<ICatalog>   {

	public static final String SQL_REQUEST = 
			"select catalog_vocabulary_id,"
			+ "catalog_id,guilanguage_id,"
			+ "catalogName,catalogComment,"
			+ "itemTraduction,itemsTraduction,"
			+ "userTraduction,usersTraduction"
			+ " from catalog_vocabulary ";
	
	List<ICatalog> _data;
	public PopulateCatalogVocFromDbStmt(List<ICatalog> d, SQLDataSource ds) throws DataProcessException { 
		super(ds);
		_data=d;
	}
	
	@Override
	public ICatalog mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		ICatalog d;
		Integer catalogId = rs.getInt(2);
		Integer guiLanguageId = rs.getInt(3);
		d = _data.stream()
				.filter(p -> p.getId().equals(catalogId))
				.findFirst()
				.orElse(null);
		
		if (d==null) { throw new SQLException("Catalog '"+catalogId+"' not loaded, unable to load vocabulary found in DB."); }
		
		CatalogVocabularySet v = new CatalogVocabularySet();
		
		v.setCatalogId(d.getId());
		v.setGuiLanguageId(guiLanguageId);
		
		v.setId(rs.getInt(1));
		v.setName(rs.getString(4));
		v.setComment(rs.getString(5));
		v.setItem(rs.getString(6));
		v.setItems(rs.getString(7));
		v.setUser(rs.getString(8));
		v.setUsers(rs.getString(9));
		
		d.setVocabulary(guiLanguageId, v);
		
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
				sql += "catalog_vocabulary.catalog_id='"+it.next().getId()+"'";
				firstOne=false;
			}
		}
		return sql;
	}
					
};
