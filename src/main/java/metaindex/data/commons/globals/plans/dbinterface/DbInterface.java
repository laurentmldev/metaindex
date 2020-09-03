package metaindex.data.commons.globals.plans.dbinterface;

import java.util.List;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import metaindex.data.commons.globals.plans.IPlan;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLDatabaseInterface;
import toolbox.database.sql.SQLPopulateStmt;
import toolbox.database.sql.SQLReadStreamStmt;
import toolbox.exceptions.DataProcessException;

public class DbInterface extends SQLDatabaseInterface<IPlan> {

	public DbInterface(SQLDataConnector ds) {
		super(ds);
	}
	
	
	
	public SQLPopulateStmt<IPlan> getLoadNewPlansFromDbStmt(List<IPlan> existingPlans) throws DataProcessException {
		Boolean newOnesOnly=true;
		Boolean onlyIfTimestampChanged=false;
		return new PopulatePlanFromDbStmt(existingPlans, onlyIfTimestampChanged,newOnesOnly, getDataConnector());
	}
	
	public SQLPopulateStmt<IPlan> getLoadFromDbStmt(List<IPlan> plans) throws DataProcessException {
		return new PopulatePlanFromDbStmt(plans, getDataConnector());
	}
	
	public SQLPopulateStmt<IPlan> getLoadFromDbStmt(List<IPlan> plans, Boolean onlyIfTimestampChanged) throws DataProcessException {
		return new PopulatePlanFromDbStmt(plans, onlyIfTimestampChanged, getDataConnector());
	}
	public SQLPopulateStmt<IPlan> getLoadFromDbStmt() throws DataProcessException {
		return new PopulatePlanFromDbStmt(getDataConnector());
	}
}
