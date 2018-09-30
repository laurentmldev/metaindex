package metaindex.data.dataset.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.opensymphony.xwork2.Preparable;

import metaindex.data.beans.AMetaindexBean;
import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.dataset.Dataset;
import metaindex.data.element.IElement;
import metaindex.data.element.beans.BeanElementUpdateElementDataProcess;
import metaindex.data.metadata.Metadata;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

/// We just re-export in DB all data of the Bean
public class BeanElementUpdateDatasetProcess extends BeanElementUpdateElementDataProcess  {

	private static final long serialVersionUID = -3096098703226452138L;
	
}
