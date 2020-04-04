package metaindex.data.commons.globals.guitheme.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import metaindex.data.commons.globals.guitheme.GuiTheme;
import metaindex.data.commons.globals.guitheme.IGuiTheme;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLReadStreamStmt;
import toolbox.exceptions.DataProcessException;

class LoadFromDbStmt extends SQLReadStreamStmt<IGuiTheme>   {

	public static final String SQL_REQUEST = 
			"select guithemes.guitheme_id,guithemes.name,"
			+"guithemes.shortname from guithemes";	

	public LoadFromDbStmt(SQLDataConnector ds) throws DataProcessException { 
		super(ds);
	}

	@Override
	public IGuiTheme mapRow(ResultSet rs, int rowNum) throws SQLException {		
		IGuiTheme d;
		d = new GuiTheme();
		d.setId(rs.getInt(1));
		d.setName(rs.getString(2));
		d.setShortName(rs.getString(3));
		return d;
	}

	@Override
	public String buildSqlQuery() { return SQL_REQUEST; }
					
};
