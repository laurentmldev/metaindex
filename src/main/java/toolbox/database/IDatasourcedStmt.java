package toolbox.database;


public interface IDatasourcedStmt<TDatasource extends IDataSource> {
				
	public TDatasource getDatasource();
}
