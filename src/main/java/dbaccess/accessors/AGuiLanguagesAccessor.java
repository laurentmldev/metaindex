package metaindex.dbaccess.accessors;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.userprofile.GuiLanguageData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.AJdbcDataAccessor;
import metaindex.dbaccess.IDataAccessAware.DataAccessConnectException;

public abstract class AGuiLanguagesAccessor extends AJdbcDataAccessor {

	public AGuiLanguagesAccessor(ADataAccessFactory accessorsFactory, DataSource dataSource, 
			PlatformTransactionManager txManager) throws DataAccessConnectException {
		super(accessorsFactory,dataSource,txManager);
	}
	
	public abstract List<GuiLanguageData> getGuiLanguagesList();
}
