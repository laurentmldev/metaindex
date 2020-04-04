package metaindex.data.userprofile.dbinterface;

import toolbox.database.elasticsearch.ESDatabaseInterface;
import toolbox.database.elasticsearch.ESWriteStmt;
import toolbox.database.elasticsearch.ElasticSearchConnector;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLDatabaseInterface;
import toolbox.database.sql.SQLPopulateStmt;
import toolbox.database.sql.SQLReadStreamStmt;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

import java.util.ArrayList;
import java.util.List;

import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;

public class ESDbInterface  extends ESDatabaseInterface<IUserProfileData> 
{
	
	public ESDbInterface(ElasticSearchConnector ds) { super(ds); }
	
	public ESWriteStmt<IUserProfileData> getCreateOrUpdateUserStmt(IUserProfileData user, List<String> roles) throws DataProcessException {		
		return new CreateOrUpdateESUserStmt(user,this.getDataConnector(),roles);
	}
	
	
}
