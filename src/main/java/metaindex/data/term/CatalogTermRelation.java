package metaindex.data.term;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.app.Globals;
import metaindex.app.control.websockets.items.WsControllerItem;
import metaindex.data.catalog.ICatalog;
import toolbox.exceptions.DataProcessException;

public class CatalogTermRelation extends CatalogTerm {

	static private Log log = LogFactory.getLog(WsControllerItem.class);
	
	
	// shall be built by BuildCatalogTerm factory method only
	// in order to ensure that proper dynamic class is instanciated
	protected CatalogTermRelation() {
		this.setDatatype(TERM_DATATYPE.RELATION);
		this.setRawDatatype(RAW_DATATYPE.Tjoin);
	}
	
	@Override
	// use special 'relations' field, which must be unique per index in ElasticSearch
	public String getRawFieldName() { return ICatalogTerm.MX_FIELD_RELATIONS; }
	
	@Override
	public void setDatatype(TERM_DATATYPE datatype) {
		assert(datatype==TERM_DATATYPE.RELATION);
		super.setDatatype(TERM_DATATYPE.RELATION);
	}
	
	@Override
	public Boolean getIsMultiEnum() {
		return false;
	}
	@Override
	public void setIsMultiEnum(Boolean isMultiEnum) { assert(isMultiEnum==false); }

	@Override
	/** 
	 * For Relation terms, enums list contains parent relation name and child name.
	 * We ensure here that defined parent/child relation names match an existing
	 * relation defined in ElasticSearch. 
	 */
	public void setEnumsList(List<String> enumsList) {
		// ignore empty list
		if (enumsList.size()==0 || enumsList.size()==1 && enumsList.get(0).length()==0) { return; }
		
		if (enumsList.size()!=2) {
			log.error("While setting enums of relation term '"+this.getName()
					+"' : given enums list contains "+enumsList.size()+" elements while 2 are expected");
			
			return;
		}
		
		// if mapping property defined locally we check it
		// this case is used when we create a new Term, no specific consistency check here :
		//   1- we set term mapping properties
		//   2- we set enums list
		if (this.getMappingProperties().get("relations")!=null) {
			super.setEnumsList(enumsList);
			return;
		}
		
		// otherwise we check consistency with relations defined in the Catalog
		// this case is used when loading terms from DB
		//   1- we load catalog field (loaded as a term) 'mx_relations' from ElasticSearch DB, it contains the "catalog mapping properties"
		//   2- we load applicative term contents from SQL DB and ensure enumsList is consistent with 
		//	    existing relation names in the 'mx_relations' ES field
		
		String parentRoleName=enumsList.get(0);
		String childRoleName=enumsList.get(1);
		
		ICatalog c = Globals.Get().getCatalogsMgr().getCatalog(this.getCatalogId());
		Map<String,String> catalogRelationsMappingDef = c.getTermsRelationsDefinitions();
		
		if (catalogRelationsMappingDef==null) {
			log.error("While setting enums of relation term '"+this.getName()
					+"' : no relations mapping definition available in catalog "+c.getName());
			return;
		}
		if (!catalogRelationsMappingDef.containsKey(parentRoleName)) {
			String definedRelationsStr="";
			for (String parentName : catalogRelationsMappingDef.keySet()) { 
				definedRelationsStr+=parentName+":"+catalogRelationsMappingDef.get(parentName)+" "; 
			}
			log.error("While setting enums of relation term '"+this.getName()
					+"' : no such parent '"+parentRoleName
					+"' in relations definition of catalog "+c.getName()+": "+definedRelationsStr);
			return;
		}
		if (!catalogRelationsMappingDef.get(parentRoleName).equals(childRoleName)) {
			log.error("While setting enums of relation term '"+this.getName()
					+"' : no such child '"+childRoleName+"' associated to parent name '"+parentRoleName
					+"' in relations definition of catalog "+c.getName()
					+", existing child relation is '"+catalogRelationsMappingDef.get(parentRoleName)+"'");
			return;
		}
		super.setEnumsList(enumsList);
		

	}
	
	@Override
	public void setMappingProperties(Map<String,Object> props) throws DataProcessException {
		
		if (!props.containsKey("relations")) {
			throw new DataProcessException("missing 'relations' definition property (name of parent and child relation links), "
										+"unable to set mapping properties of relation field "+this.getName());
		}
		
		super.setMappingProperties(props);		
					
	}
	
	public void setParentChildRolesNames(String parentRoleName, String childRoleName) {
		List<String> enums = new ArrayList<>();
		enums.add(parentRoleName);
		enums.add(childRoleName);
		setEnumsList(enums);
	}
	
	public String getParentRoleName() {
		if (this.getEnumsList().size()!=2) { return ""; }
		return this.getEnumsList().get(0);
	}

	public String getChildRoleName() {
		if (this.getEnumsList().size()!=2) { return ""; }
		return this.getEnumsList().get(1);
	}

	
}
