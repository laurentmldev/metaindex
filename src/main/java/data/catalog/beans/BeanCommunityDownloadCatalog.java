package metaindex.data.catalog.beans;

import java.util.ArrayList;
import java.util.Iterator;
 
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.community.beans.BeanCommunity;
import metaindex.data.element.IElement;
import metaindex.data.management.CommunitiesAccessor;

 
public class BeanCommunityDownloadCatalog extends BeanCommunity {
 
	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanCommunityUpdateCatalogProcess.class);
	
	private String normalizeFileName(String fileName) {
		return fileName.replace(' ', '_');		
	}
	@Override 
  	public String execute() throws Exception {
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
		
		HttpServletResponse response = ServletActionContext.getResponse();
		
		try {
			response.setContentType("text/json");
			String fileName = normalizeFileName(this.getSelectedCommunity().getIdName()+"_"
							+this.getSelectedCatalog().getCatalogId()+".omc");
			response.setHeader("Content-disposition", "attachment;filename="+fileName);
	 
			response.getOutputStream().print(getFileStrContents());
			
			response.getOutputStream().flush();
 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return status.toString();
	}
	
	public String getFileStrContents() {
		String result = "";
		String json = this.getSelectedCatalog().encode().toString();
		ArrayList<String> rows = new ArrayList<String>();
		rows.add(json);
			 
		Iterator<String> iter = rows.iterator();
		while (iter.hasNext()) {
			result += (String) iter.next();			
		}
		
		return result;
	}
	 
}
