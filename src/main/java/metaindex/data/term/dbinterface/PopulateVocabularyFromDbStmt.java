package metaindex.data.term.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.app.Globals;
import metaindex.data.catalog.CatalogsManager;
import metaindex.data.catalog.ICatalog;
import metaindex.data.term.CatalogTerm;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.TERM_DATATYPE;
import metaindex.data.term.TermVocabularySet;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLPopulateStmt;
import toolbox.database.sql.SQLReadStreamStmt;
import toolbox.exceptions.DataProcessException;

/**
 * Load terms from SQL DB and update catalogs terms list
 * @author laurentml
 *
 */
class PopulateVocabularyFromDbStmt extends SQLPopulateStmt<ICatalogTerm>   {
	
	private Log log = LogFactory.getLog(PopulateVocabularyFromDbStmt.class);

	public static final String SQL_REQUEST = 
			"select catalog_terms_vocabulary_id,catalog_term_id,guilanguage_id,termTraduction "							
			+"from catalog_terms_vocabulary";

	private Collection<ICatalogTerm> _data;	
	
	public PopulateVocabularyFromDbStmt(Collection<ICatalogTerm> d, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_data=d;
		assert(d.size()>0);
	}
	

	@Override
	public ICatalogTerm mapRow(ResultSet rs, int rowNum) throws SQLException {
		ICatalogTerm d;
		Integer dbKey = rs.getInt(2);
		String termName = rs.getString(4);
		if (_data==null) {
			log.error("Trying to load vocabulary of a term not (yet) loaded : "+termName);
			return null;
		}
	 
		d = _data.stream()
			.filter(p -> p.getId().equals(dbKey))
			.findFirst()
			.orElse(null);
			
		TermVocabularySet voc = new TermVocabularySet();
		voc.setGuiLanguageId(rs.getInt(3));
		voc.setId(rs.getInt(2));
		voc.setName(termName);		
		d.setVocabulary(Globals.Get().getGuiLanguagesMgr().getGuiLanguage(voc.getGuiLanguageId()).getShortname(), voc);
		
		return d;
	}

	@Override
	public String buildSqlQuery()  {				
		String sql = SQL_REQUEST;
		
		sql+=" where ("; 
		// no data to be listed, so we force for an empty result
		if (_data.size()==0) { sql+="catalog_term_id='-1'"; }
		else {
			Boolean firstOne=true;				
			firstOne=true;
			for (ICatalogTerm t : _data) {
				if (!firstOne) { sql+=" or "; }
				sql += "catalog_term_id='"+t.getId()+"'";
				firstOne=false;
			}
		}
		sql+=")";
					
		return sql;
	}
					
};
