package metaindex.data.commons.globals.guitheme.dbinterface;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import metaindex.data.commons.globals.guitheme.GuiTheme;
import metaindex.data.commons.globals.guitheme.IGuiTheme;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLReadStmt;
import toolbox.exceptions.DataProcessException;

class LoadFromDbStmt extends SQLReadStmt<IGuiTheme>   {

	public static final String SQL_REQUEST = 
			"select guithemes.guitheme_id,guithemes.name,"
			+"guithemes.shortname from guithemes";	

	private List<IGuiTheme> _data;
	
	public LoadFromDbStmt(List<IGuiTheme> data,SQLDataSource ds) throws DataProcessException { 
		super(ds);
		_data=data;
	}

	public LoadFromDbStmt(SQLDataSource ds) throws DataProcessException { 
		super(ds);
	}

	@Override
	public IGuiTheme mapRow(ResultSet rs, int rowNum) throws SQLException {		
		IGuiTheme d;
		Integer dbKey=rs.getInt(1);
		if (_data==null) { d = new GuiTheme(); }
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
