package metaindex.data.catalog.dbinterface;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.UserProfileData;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLReadStmt;
import toolbox.exceptions.DataProcessException;

class LoadContentsFromDbStmt extends SQLReadStmt<ICatalog>   {

	public static final String SQL_REQUEST = 
			"select catalogs.catalog_id,catalogs.shortname, "
			+"catalogs.creator_id,catalogs.thumbnailUrl, "							
			+"catalogs.itemNameFields,catalogs.itemThumbnailUrlField,catalogs.urlPrefix,catalogs.perspectiveMatchField, "
			+"catalogs.quotaNbDocs,catalogs.quotaFtpDiscSpaceBytes "
			+"from catalogs";
	
	List<ICatalog> _data;
	public LoadContentsFromDbStmt(List<ICatalog> d, SQLDataSource ds) throws DataProcessException { 
		super(ds);
		_data=d;
	}
	public LoadContentsFromDbStmt(SQLDataSource ds) throws DataProcessException { 
		super(ds);
	}
	@Override
	public ICatalog mapRow(ResultSet rs, int rowNum) throws SQLException {
		ICatalog d;
		String dbKey = rs.getString(2);
		if (_data==null) { d = new Catalog(); }
		else { 
			d = _data.stream()
				.filter(p -> p.getName().equals(dbKey))
				.findFirst()
				.orElse(null);
		}
		d.setId(rs.getInt(1));
		d.setName(rs.getString(2));
		d.setCreatorId(rs.getInt(3));
		d.setThumbnailUrl(rs.getString(4));
		
		List<String> fieldnames = new ArrayList<String>();
		String[] fieldnamesArray = rs.getString(5).split(",");
		for (String f : fieldnamesArray) { fieldnames.add(f); }
		d.setItemNameFields(fieldnames);
		
		d.setItemThumbnailUrlField(rs.getString(6));
		d.setItemsUrlPrefix(rs.getString(7));
		d.setPerspectiveMatchField(rs.getString(8));
		d.setQuotaNbDocs(rs.getLong(9));
		d.setQuotaFtpDiscSpaceBytes(rs.getInt(10));
		return d;
	}

	@Override
	public String buildSqlQuery()  {				
		String sql = SQL_REQUEST;
		if (_data!=null) {
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
