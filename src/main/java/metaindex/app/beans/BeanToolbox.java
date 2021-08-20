package metaindex.app.beans;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import metaindex.app.Globals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

    
/**
 * Bean for welcome page
 * @author Laurent ML
 */
public class BeanToolbox extends AMetaindexBean {  
  
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(BeanToolbox.class);
	private static final String DESC_FILE_NAME="description.xml";
	public class ToolDesc {
		private String name;
		private String version;
		private Map<String,String> description = new HashMap<>();
		private String dependencies;
		
		private List<String> filesUrl=new ArrayList<>();
		private List<String> filesName=new ArrayList<>();
		
		private String unescapeHtml(String txt) {
			return txt.replaceAll("&lt;","<").replaceAll("&gt;",">");
		}
		public String getName() { return unescapeHtml(name); }
		public void setName(String name) { this.name = name; }
		public Map<String,String> getDescription() {
			return description;
		}
		public void setDescription(Map<String,String> description) {
			this.description = description;
		}
		public String getDependencies() {
			return unescapeHtml(dependencies);
		}
		public void setDependencies(String dependencies) {
			this.dependencies = dependencies;
		}
		public String getVersion() {
			return unescapeHtml(version);
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public List<String> getFilesUrl() {
			return filesUrl;
		}
		public void setFilesUrl(List<String> filesUrl) {
			this.filesUrl = filesUrl;
		}
		public List<String> getFilesName() {
			return filesName;
		}
		public void setFilesName(List<String> filesName) {
			this.filesName = filesName;
		}
	}
	private List<ToolDesc> toolsDesc = new ArrayList<>();
	
	
	private void _buildFilesList(Path rootPath, ToolDesc toolDesc, String basePathStr) throws IOException {

		List<Path> subFolders = new ArrayList<>();
		Files.list(rootPath)
	        .forEach(toolFilePath -> {
	        	String fileUrl=toolFilePath.toString().replace(Globals.Get().getWebappsFsPath()+"metaindex",
	        													Globals.Get().getWebAppBaseUrl());
	        	String fileName=toolFilePath.toString().replace(basePathStr+"/", "");
	        	
	        	if (new File(toolFilePath.toString()).isDirectory()) { subFolders.add(toolFilePath); }
	        	else if (!fileName.equals(DESC_FILE_NAME)) { 
		        		toolDesc.getFilesUrl().add(fileUrl);
		        		toolDesc.getFilesName().add(fileName);
		        	}	        	
	        });
		
		for (Path subFolder : subFolders) {
			try {
				_buildFilesList(subFolder,toolDesc,basePathStr);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
  	public void prepare() throws Exception {
		
		super.prepare();
		
		toolsDesc.clear();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		String toolsFolder= Globals.Get().getMarketplaceFsPath();
		Path toolsFolderObj = new File(toolsFolder).toPath();
		Files.list(toolsFolderObj)
	        .forEach(path -> {

	        	String descXmlFile=path+"/"+DESC_FILE_NAME;
	        	DocumentBuilder db;
				try {
					db = dbf.newDocumentBuilder();
					Document doc = db.parse(new File(descXmlFile));
					doc.getDocumentElement().normalize();
					
					Element root = (Element) doc.getDocumentElement();
					ToolDesc curToolDesc = new ToolDesc();
					curToolDesc.setName(root.getAttributes().getNamedItem("name").getTextContent());
					curToolDesc.setVersion(root.getAttributes().getNamedItem("version").getTextContent());
					curToolDesc.setDescription(new HashMap<String,String>());
					NodeList descriptions = root.getElementsByTagName("description");
					for (int i=0; i<descriptions.getLength();i++) {
						Node curNode = descriptions.item(i);
						String language = curNode.getAttributes().getNamedItem("language").getTextContent();
						String text = curNode.getTextContent();
						curToolDesc.getDescription().put(language,text);
					}					
					curToolDesc.setDependencies(root.getElementsByTagName("dependencies").item(0).getTextContent());
					
					_buildFilesList(path,curToolDesc,path.toString());
					
					
					toolsDesc.add(curToolDesc);
				} catch (ParserConfigurationException | SAXException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	        });
		
	}
	@Override
  	public String execute() throws Exception {
		super.execute();
		return "BeanProcess_SUCCESS";
	}
	
	public List<ToolDesc> getToolsDesc() {
		return toolsDesc;
	}
}  
