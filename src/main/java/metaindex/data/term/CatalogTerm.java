package metaindex.data.term;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.catalog.ICatalog;
import metaindex.data.commons.globals.Globals;
import metaindex.websockets.items.WsControllerItem;
import toolbox.exceptions.DataProcessException;

public class CatalogTerm implements ICatalogTerm {

	static private Log log = LogFactory.getLog(WsControllerItem.class);
	
	private String _name="";
	private Integer _id=0;
	private Integer _catalog_id=0;
	private Map<String,TermVocabularySet> _vocabularySets = new ConcurrentHashMap<>();
	private TERM_DATATYPE _datatype= TERM_DATATYPE.TINY_TEXT;
	private List<String> _enumsList=new ArrayList<String>();
	private Boolean _isMultiEnum=false;
	private Map<String,Object> _mappingProperties = new HashMap<>();
	private RAW_DATATYPE _rawDatatype;
	
	// shall be built by BuildCatalogTerm factory method only
	// in order to ensure that proper dynamic class is instanciated
	protected CatalogTerm() {}

	@Override
	public String getName() {
		return _name;
	}
	@Override
	public void setName(String name) { _name=name; }

	@Override
	// by default same name for the term and underlying raw field in ElasticSearch.
	public String getRawFieldName() { return getName(); }
	@Override
	public Integer getId() {
		return _id;
	}
	@Override
	public void setId(Integer id) { _id=id; }

	@Override
	public Map<String,TermVocabularySet> getVocabularies() {
		return _vocabularySets;
	}
	@Override 
	public TermVocabularySet getVocabulary(String guiLanguageShortName) {
		return _vocabularySets.get(guiLanguageShortName);
	}
	@Override
	public TermVocabularySet getVocabulary(Integer guiLanguageId) {
		String shortName = Globals.Get().getGuiLanguagesMgr().getGuiLanguage(guiLanguageId).getShortname();
		return getVocabulary(shortName);
	}
	@Override
	public void setVocabulary(String guiLanguageShortName,TermVocabularySet voc) { 
		_vocabularySets.put(guiLanguageShortName, voc); 
	}

	@Override
	public TERM_DATATYPE getDatatype() {
		return _datatype;
	}
	@Override
	public void setDatatype(TERM_DATATYPE datatype) { _datatype=datatype; }
	
	@Override
	public List<String> getEnumsList() {
		return _enumsList;
	}
	@Override
	public void setEnumsList(List<String> enumsList) { _enumsList=enumsList; }
	
	@Override
	public Boolean getIsMultiEnum() {
		return _isMultiEnum;
	}
	@Override
	public void setIsMultiEnum(Boolean isMultiEnum) { _isMultiEnum=isMultiEnum; }
	@Override
	public Integer getCatalogId() {
		return _catalog_id;
	}
	@Override
	public void setCatalogId(Integer catalog_id) {
		_catalog_id=catalog_id;		
	}

	@Override
	public Map<String,Object> getMappingProperties() {
		return _mappingProperties;
	}

	@Override
	public void setMappingProperties(Map<String,Object> props) throws DataProcessException {
		this._mappingProperties = props;
	}
	@Override
	public RAW_DATATYPE getRawDatatype() {
		return _rawDatatype;
	}
	@Override
	public void setRawDatatype(RAW_DATATYPE rawDatatype) {
		this._rawDatatype = rawDatatype;
	}

}
