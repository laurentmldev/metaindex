package metaindex.data.metadata;

import metaindex.dbaccess.IDBAccessedData.BeanDataException;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;


/**
 * Bean storing Metadata characteristic information
 * @author laurent
 *
 */
public interface IMetadataContents{
  	
	
	public String getName();
  	public Integer getMetadataId();
	public void setMetadataId(Integer metadataId);
	public void setName(String name) throws DataAccessConstraintException;
	public String getComment();
	public void setComment(String comment) throws DataAccessConstraintException;
	public Integer getTermId();
	public void setTermId(Integer termId);
	public Integer getDatasetId();
	public void setDatasetId(Integer datasetId);
	public Integer getLayoutColumn();
	public void setLayoutColumn(Integer layoutColumn);
	public boolean isLayoutDoDisplayName();
	public void setLayoutDoDisplayName(Boolean layoutDoDisplayName);
	public String getLayoutAlign();
	public void setLayoutAlign(String layoutAlign);
	public String getLayoutSize();
	public void setLayoutSize(String layoutSize);
	public String getString1();
	public void setString1(String string1) throws DataAccessConstraintException;
	public String getString2();
	public void setString2(String string2) throws DataAccessConstraintException;
	public String getString3();
	public void setString3(String string3) throws DataAccessConstraintException;
	public String getString4();
	public void setString4(String string4) throws DataAccessConstraintException;
	public String getLongString();
	public void setLongString(String longString) throws DataAccessConstraintException;
	public Double getValueNumber1();
	public void setValueNumber1(Double valueNumber1);
	public Double getValueNumber2();
	public void setValueNumber2(Double valueNumber2);
	public Double getValueNumber3();
	public void setValueNumber3(Double valueNumber3);
	public Double getValueNumber4();
	public void setValueNumber4(Double valueNumber4);
	public int getLayoutPosition();
	public void setLayoutPosition(Integer layoutPosition);

}
