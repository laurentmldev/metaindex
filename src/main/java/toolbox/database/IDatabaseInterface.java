package toolbox.database;

public interface IDatabaseInterface<TDataSource extends IDataSource> {
	public TDataSource getDatasource();

}
