package metaindex.dbaccess.accessors;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.userprofile.GuiThemeData;
import metaindex.dbaccess.*;
import metaindex.dbaccess.IDataAccessAware.DataAccessConnectException;



public abstract class AGuiThemesAccessor extends AJdbcDataAccessor {
	
	public AGuiThemesAccessor(ADataAccessFactory accessorsFactory, DataSource dataSource, 
			PlatformTransactionManager txManager) throws DataAccessConnectException {
		super(accessorsFactory,dataSource,txManager);
	}

	public abstract List<GuiThemeData> getGuiThemesList();
}
