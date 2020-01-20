package toolbox.database.sql;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import toolbox.database.IDatabaseWriteStmt;
import toolbox.database.IDatasourcedStmt;
import toolbox.exceptions.DataProcessException;

public abstract class SQLWriteStmt<TData> implements IDatabaseWriteStmt<TData>,IDatasourcedStmt<SQLDataSource>
 {

	private SQLDataSource _datasource;
	
	// one list of statements for each data to process
	private List< List<PreparedStatement> > _preparedStatements = new ArrayList<List<PreparedStatement>>();
	
	public SQLWriteStmt(SQLDataSource ds) {		
		_datasource=ds; 
	}
	
	protected abstract List<TData> getDataList();
	protected abstract List<PreparedStatement> prepareStmts() throws DataProcessException;
	protected abstract void populateStatements(TData dataObject, List<PreparedStatement> stmts) throws DataProcessException;
	
	protected void prepareAndPopulateStmts() throws DataProcessException {
		List<TData> dlist = getDataList();
		for (TData curData : dlist) {
			List<PreparedStatement> stmts = prepareStmts();			
			populateStatements(curData,stmts);
			_preparedStatements.add(stmts); 
		}
	}
	
	@Override
	public Boolean execute() throws DataProcessException {
		prepareAndPopulateStmts();
		Connection con = this.getDatasource().getConnection();
		try { 			
			con.setAutoCommit(false);
			Iterator<List<PreparedStatement>> it1 = _preparedStatements.iterator();
			while (it1.hasNext()) {
				List<PreparedStatement> stmts = it1.next();
				Iterator<PreparedStatement> it2 = stmts.iterator();
				while (it2.hasNext()) {
					PreparedStatement stmt = it2.next();
					stmt.executeBatch();				
				}
			}		
			con.commit();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
			if (getDatasource().getConnection()!=null) {
				try { 
					con.rollback();
					con.setAutoCommit(true);
				} 
				catch (SQLException e1) {
					e1.printStackTrace();
					throw new DataProcessException("Unable to revert SQLDb operation : "+e1.getMessage(), e); 
				}
			}
			throw new DataProcessException("Unable to perform SQLDb operation : "+e.getMessage(),e);
		}
		return true;
	}
	
	@Override
	public SQLDataSource getDatasource() {
		return _datasource;
	}
	

		
}
