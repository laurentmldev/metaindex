package metaindex.data.term.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import metaindex.data.catalog.ICatalog;
import metaindex.data.term.CatalogTerm;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.TERM_DATATYPE;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLPopulateStmt;
import toolbox.database.sql.SQLReadStreamStmt;
import toolbox.exceptions.DataProcessException;

/**
 * Load terms from SQL DB and update catalogs terms list
 * @author laurentml
 *
 */
class PopulateTermsFromDbStmt extends SQLPopulateStmt<ICatalogTerm>   {

	public static final String SQL_REQUEST = 
			"select catalog_term_id,catalog_id,datatype,name,enumsList,isMultiEnum "							
			+"from catalog_terms";
	private List<ICatalog> _catalogs;
	private List<ICatalogTerm> _data = new ArrayList<>();
	
	public PopulateTermsFromDbStmt(List<ICatalog> c, List<ICatalogTerm> d, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_catalogs=c;
		_data=d;
	}
	public PopulateTermsFromDbStmt(List<ICatalog> c, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_catalogs=c;
		
	}

	@Override
	public void execute() throws DataProcessException {
		
		super.execute();
		
		// update catalogs terms lists
		for (ICatalog c :  _catalogs) {
			List<ICatalogTerm> commTerms = _data.stream()
					.filter(t -> t.getCatalogId().equals(c.getId())).collect(Collectors.toList());
			c.updateTermsApplicativeInfo(commTerms);			
		}
		
	}
	@Override
	public ICatalogTerm mapRow(ResultSet rs, int rowNum) throws SQLException {
		ICatalogTerm d;
		Integer dbKey = rs.getInt(1);	
		d = _data.stream()
				.filter(p -> p.getId().equals(dbKey))
				.findFirst()
				.orElse(null);
		if (d==null) {
			String typeStr = rs.getString(3);
			d = ICatalogTerm.BuildCatalogTerm(typeStr); 
			_data.add(d);
		}
		
		d.setId(rs.getInt(1));
		d.setCatalogId(rs.getInt(2));
		d.setDatatype(TERM_DATATYPE.valueOf(rs.getString(3)));
		d.setName(rs.getString(4));
		String enumsStr=rs.getString(5);
		d.setEnumsList(Arrays.asList(enumsStr.split(",")));
		d.setIsMultiEnum(rs.getBoolean(6));
		
		return d;
	}

	@Override
	public String buildSqlQuery()  {				
		String sql = SQL_REQUEST;
		sql+=" where (";
		Boolean firstOne=true;
		for (ICatalog c : _catalogs) {
			if (!firstOne) { sql+=" or "; }
			sql+="catalog_id="+c.getId();	
			firstOne=false;
		}
		sql+=")";
		if (_data.size()>0) {
			sql+=" and (";		
			Iterator<ICatalogTerm> it = _data.iterator();
			firstOne=true;
			while (it.hasNext()) {
				if (!firstOne) { sql+=" or "; }
				sql += "catalog_term_id='"+it.next().getId()+"'";
				firstOne=false;
			}
			sql+=")";
		}
		return sql;
	}
					
};
