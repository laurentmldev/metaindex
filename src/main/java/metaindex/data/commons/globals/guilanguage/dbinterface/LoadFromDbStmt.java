package metaindex.data.commons.globals.guilanguage.dbinterface;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import metaindex.data.commons.globals.guilanguage.GuiLanguage;
import metaindex.data.commons.globals.guilanguage.IGuiLanguage;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLReadStmt;
import toolbox.exceptions.DataProcessException;

class LoadFromDbStmt extends SQLReadStmt<IGuiLanguage>   {

	public static final String SQL_REQUEST = 
			"select guilanguages.guilanguage_id,guilanguages.name,"
			+"guilanguages.shortname from guilanguages";	

	private List<IGuiLanguage> _data;
	
	public LoadFromDbStmt(List<IGuiLanguage> data,SQLDataSource ds) throws DataProcessException { 
		super(ds);
		_data=data;
	}

	public LoadFromDbStmt(SQLDataSource ds) throws DataProcessException { 
		super(ds);
	}

	@Override
	public IGuiLanguage mapRow(ResultSet rs, int rowNum) throws SQLException {		
		IGuiLanguage d;
		Integer dbKey=rs.getInt(1);
		if (_data==null) { d = new GuiLanguage(); }
		else {
			d = _data.stream()
					.filter(l -> l.getId().equals(dbKey))
					.findFirst()
					.orElse(null);
		}
		d.setId(rs.getInt(1));
		d.setName(rs.getString(2));
		d.setShortName(rs.getString(3));
		return d;
	}

	@Override
	public String buildSqlQuery() { return SQL_REQUEST; }
					
};
