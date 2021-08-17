package metaindex.data.term;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import toolbox.exceptions.DataProcessException;
import toolbox.utils.IIdentifiable;

public interface ICatalogTerm extends IIdentifiable<Integer>{
	
	// used to truncate fields value when too long
	// if so, full field can be retrieved through a dedicated api/viewer
	// This value is used in GetItemsFromDbStmt when building items contents
	public static final Integer MX_FIELD_VALUE_MAX_LENGTH = 5000;
	public static final Integer MX_FIELD_VALUE_NO_MAX_LENGTH = -1;
	
	// if changed, update also value in perspective°field_longtext.jsp and error text 'Items.longtext.tooLong'
	public static final Integer MX_FIELD_LONGTEXT_MAX_NBCHARS = 990000;
	
	// /!\ this list shall be coherent with : 
	// - SQL field 'catalog_terms.datatype' enum definition.
	// - webapp/secure/commons/js/mx_helpers.jsp
	public enum TERM_DATATYPE { UNKNOWN,
								TINY_TEXT, 
								LONG_TEXT, 
								DATE, 
								INTEGER, 
								FLOAT, 
								PAGE_URL, 
								IMAGE_URL, 
								AUDIO_URL, 
								VIDEO_URL,
								GEO_POINT,
								LINK
								// update also SQL table enum if this list is changed
								// and webapp/secure/commons/js/mx_helpers.jsp, and webapp/secure/catalogs/details.jsp
							  }
	
	// if changing that, look for occurrences in javascript too,
	// it might be used for adaptive GUI
	public enum RAW_DATATYPE { Tunknown,
								 Ttext, 
								 Tkeyword, 
								 Tdate, 
								 Tinteger, 
								 Tshort, 
								 Tfloat ,
								 Tgeo_point,
								 Tjoin								 
								 };
				

	public static RAW_DATATYPE getRawDatatype(TERM_DATATYPE datatype) {
		if (datatype==TERM_DATATYPE.FLOAT) { return RAW_DATATYPE.Tfloat; }
		if (datatype==TERM_DATATYPE.INTEGER) { return RAW_DATATYPE.Tinteger; }
		if (datatype==TERM_DATATYPE.DATE) { return RAW_DATATYPE.Tdate; }
		if (datatype==TERM_DATATYPE.GEO_POINT) { return RAW_DATATYPE.Tgeo_point; }
		if (datatype==TERM_DATATYPE.LINK) { return RAW_DATATYPE.Ttext; }
		if (datatype==TERM_DATATYPE.LONG_TEXT) { return RAW_DATATYPE.Ttext; }
		return RAW_DATATYPE.Ttext;
	}

	public static TERM_DATATYPE getTermDatatype(RAW_DATATYPE mappingType) {
		switch(mappingType) {
			case Ttext :
			case Tkeyword :
				return TERM_DATATYPE.TINY_TEXT;
			case Tdate :
				return TERM_DATATYPE.DATE;
			case Tinteger :
			case Tshort :
				return TERM_DATATYPE.INTEGER;
			case Tfloat :
				return TERM_DATATYPE.FLOAT;
			case Tgeo_point :			
				return TERM_DATATYPE.GEO_POINT;
			case Tjoin:
				return TERM_DATATYPE.UNKNOWN;
			default:
				return TERM_DATATYPE.TINY_TEXT;
		}
			
	}
	
	public static RAW_DATATYPE getRawDatatype(String typeStr) {
		
		RAW_DATATYPE type = RAW_DATATYPE.Ttext;
		if (typeStr==null) { type = RAW_DATATYPE.Tunknown; }
		else if (typeStr.equals("text")) { type = RAW_DATATYPE.Ttext; }
		else if (typeStr.equals("keyword")) { type = RAW_DATATYPE.Tkeyword; }
		else if (typeStr.equals("integer")) { type = RAW_DATATYPE.Tinteger; }
		else if (typeStr.equals("short")) { type = RAW_DATATYPE.Tshort; }
		else if (typeStr.equals("float")) { type = RAW_DATATYPE.Tfloat; }
		else if (typeStr.equals("geo_point")) { type = RAW_DATATYPE.Tgeo_point; }
		else if (typeStr.equals("date")) { type = RAW_DATATYPE.Tdate; }
		else { type = RAW_DATATYPE.Tunknown; }
		
		return type;
	}
	// ignore terms starting with "mx_" (internal use) in WsMsgCatalogDetails_answer.java
	// if changed, default value of 'timeFieldName' in sql catalogs table shall be updated to 
	public static final String MX_TERM_LASTMODIF_TIMESTAMP="mx_updated_timestamp";
	public static final DateFormat MX_TERM_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static final String MX_TERM_EPOCH_TIMESTAMP="1970-01-01 00:00:00.000";
	
	// ignore terms starting with "mx_" (internal use) in WsMsgCatalogDetails_answer.java
	public static final String MX_TERM_LASTMODIF_USERID="mx_updated_userid";
	public static Boolean isInternalField(String fieldName) {
		return fieldName.equals(MX_TERM_LASTMODIF_TIMESTAMP)
				|| fieldName.equals(MX_TERM_LASTMODIF_USERID);
	}
	
	
	/// Factory method for Terms
	public static ICatalogTerm BuildCatalogTerm(TERM_DATATYPE type) {
		ICatalogTerm t = null;
			t = new CatalogTerm();
			t.setDatatype(type);
			t.setRawDatatype(ICatalogTerm.getRawDatatype(type));
		return t;
	}
	public static ICatalogTerm BuildCatalogTerm(String type) {
		return BuildCatalogTerm(ICatalogTerm.TERM_DATATYPE.valueOf(type));
	}
	public static ICatalogTerm BuildCatalogTerm(RAW_DATATYPE type) {
		return BuildCatalogTerm(getTermDatatype(type));
	}
	public Integer getCatalogId();
	/**
	 * Retrieve the name of the field containing the actual value.
	 * Basically return the name of ElasticSearch field name.
	 * Default behaviour is to use same name for the applicative term and underlying field,
	 * but that might be different if required.
	 * @return name of corresponding raw field
	 */
	public String getRawFieldName();
	public TermVocabularySet getVocabulary(Integer guiLanguageId);
	public Map<String,TermVocabularySet> getVocabularies();
	public TermVocabularySet getVocabulary(String guiLanguageShortName);
	void setVocabulary(String guiLanguageShortName, TermVocabularySet voc);	
	
	public TERM_DATATYPE getDatatype();
	public RAW_DATATYPE getRawDatatype();
	public void setRawDatatype(RAW_DATATYPE rawDatatype);
	public Map<String,Object> getMappingProperties();
	public void setMappingProperties(Map<String,Object> props) throws DataProcessException;
	
	public List<String> getEnumsList();
	public Boolean getIsMultiEnum();
	
	void setName(String name);
	void setId(Integer id);	
	void setCatalogId(Integer id);	
	void setDatatype(TERM_DATATYPE datatype);
	void setEnumsList(List<String> enumsList);
	void setIsMultiEnum(Boolean isMultiEnum);
	
}
