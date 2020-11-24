package metaindex.data.catalog.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import metaindex.data.catalog.ICatalogCustomParams;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

class CreateOrUpdateCatalogIntoSqlDbStmt extends SQLWriteStmt<ICatalogCustomParams>   {

	IUserProfileData _activeUser;
	List<ICatalogCustomParams> _data = new ArrayList<ICatalogCustomParams>();
	public CreateOrUpdateCatalogIntoSqlDbStmt(IUserProfileData activeUser, 
										List<ICatalogCustomParams> catalogsCustomParams, 
										SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_activeUser=activeUser;
		_data.addAll(catalogsCustomParams);		
	}
	
				
	@Override
	protected List<ICatalogCustomParams> getDataList() { return _data; }
	
	@Override
	protected List<PreparedStatement> prepareStmts() throws DataProcessException {
		
		List<PreparedStatement> result = new ArrayList<PreparedStatement>();
		
		try {
			result.add(this.getDataConnector().getConnection().prepareStatement(
					"insert into catalogs (shortname,owner_id,thumbnailUrl,itemNameFields,"
							+"itemThumbnailUrlField,urlPrefix,perspectiveMatchField,drivePort,timeField_term_id)"
							+" values (?,?,?,?,?,?,?,?,?)"
					+"ON DUPLICATE KEY UPDATE "
							+"thumbnailUrl=?,itemNameFields=?,itemThumbnailUrlField=?,urlPrefix=?,perspectiveMatchField=?,drivePort=?,timeField_term_id=?"));
			
		} catch (SQLException e) { throw new DataProcessException(e); }
		
		return result;
	}
	@Override
	protected void populateStatements(ICatalogCustomParams dataObject, List<PreparedStatement> stmts) throws DataProcessException {
		
		PreparedStatement stmt = stmts.get(0);
		try {
			stmt.setString(1, dataObject.getName());
			stmt.setInt(2, _activeUser.getId());
			String itemNameFields = "";
			for (String curField : dataObject.getItemNameFields()) {
				if (itemNameFields.length()>0) { itemNameFields+=","; }
				itemNameFields+=curField;
			}
			stmt.setString(3, dataObject.getThumbnailUrl());
			stmt.setString(4, itemNameFields);
			stmt.setString(5, dataObject.getItemThumbnailUrlField());
			stmt.setString(6, dataObject.getItemsUrlPrefix());
			stmt.setString(7, dataObject.getPerspectiveMatchField());
			stmt.setInt(8, dataObject.getDrivePort());
			
			if (dataObject.getTimeFieldTermId()==null || dataObject.getTimeFieldTermId().equals(0)) { stmt.setNull(9, 0 /*sql type for int TBC*/); }
			else { stmt.setInt(9, dataObject.getTimeFieldTermId()); }
			
			stmt.setString(10, dataObject.getThumbnailUrl());
			stmt.setString(11, itemNameFields);
			stmt.setString(12, dataObject.getItemThumbnailUrlField());
			stmt.setString(13, dataObject.getItemsUrlPrefix());
			stmt.setString(14, dataObject.getPerspectiveMatchField());	
			stmt.setInt(15, dataObject.getDrivePort());
			
			if (dataObject.getTimeFieldTermId()==null || dataObject.getTimeFieldTermId().equals(0)) { stmt.setNull(16, 0 /*sql type for int TBC*/); }
			else { stmt.setInt(16, dataObject.getTimeFieldTermId()); }
			
			stmt.addBatch();
		} catch (SQLException e) { throw new DataProcessException(e); }		
	}
						
};
