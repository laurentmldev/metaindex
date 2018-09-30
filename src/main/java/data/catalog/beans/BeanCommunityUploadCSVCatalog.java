package metaindex.data.catalog.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;

import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.catalog.TemplatesElementsCatalog;
import metaindex.data.community.ICommunity;
import metaindex.data.community.beans.BeanCommunity;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadataFunctions.InapropriateStringForTypeException;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.MetadataHandle;
import metaindex.data.metadata.beans.BeanElementAddMetadataProcess;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IGenericEncodable.POPULATE_POLICY;

import java.io.File;
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths; 
 
public class BeanCommunityUploadCSVCatalog extends BeanCommunity {
 
	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanCommunityUpdateCatalogProcess.class);
	
	private File formFile;
	private String formFileContentType;
    private String formFileName;
    private POPULATE_POLICY policy;
	private Integer formBaseElementId=0;
	private String csvMappingStr="";
	private String csvTransformRegexesStr="";
    
    private Integer catalogId = 0;
	   	
    public static final String CSV_MAPPING_ID_ELEMENT_NAME = "___ELEMENT_NAME___";
    public static final String CSV_MAPPING_ID_ELEMENT_COMMENT = "___ELEMENT_COMMENT___";
    
    private String stripStr(String str) {
    	return str.replaceAll("^#?\b*", "").replaceAll("\b*$", "");
    }
    
    private String unescapeRegex(String str) {
    	return str.replaceAll("__SLASH__", "/")
    				.replaceAll("__COMA__", ",")
    				.replaceAll("__SEMICOLUMN__", ";");
    }
    
    
    // It is possible to reference contents of CSV fields in transform regex : $(<csv_field_name>)
    private String applyCsvColsRefs(String str,Map<String,Integer> csvColsList, String[] csvValues) 
    														throws UnknownReferencedCSVFieldException {
    	
    	Pattern pattern = Pattern.compile("field\\[([^\b\\]]+)\\]");
    	
    	    StringBuffer output = new StringBuffer();
    	    Matcher matcher = pattern.matcher(str);
    	    while (matcher.find()) {
    	    	String csvColName=matcher.group(1);
    	    	try {
    	    		Integer csvIdx=csvColsList.get(csvColName);
    	    		String rep =csvValues[csvIdx];
	    	        matcher.appendReplacement(output, rep);
    	    	} catch (Exception e) { 
    	    		throw new UnknownReferencedCSVFieldException(csvColName);
    	    	}
    	
    	    }
    	    matcher.appendTail(output);
    	    return output.toString();

    }
    
    private class UnknownReferencedCSVFieldException extends Exception {
		private static final long serialVersionUID = 1L;
		public UnknownReferencedCSVFieldException(String csvFieldName) {
    		super("Given field '"+csvFieldName+"' is not found in CSV file.");
    	}
    }
    private String applyTranformRegex(String str, List<String[]> regexes, Map<String,Integer> csvColsList, String[] csvValues) 
    																					throws UnknownReferencedCSVFieldException {
    	if (regexes==null) { return str; } 
    	 
    	Iterator<String[]> regexesIt=regexes.iterator();
		while (regexesIt.hasNext()) {
			String[] curRegexes=regexesIt.next();
			String matchRegex=unescapeRegex(curRegexes[0]);
			String transformRegex= applyCsvColsRefs(unescapeRegex(curRegexes[1]),csvColsList,csvValues);
			str=str.replaceAll(matchRegex, transformRegex);
		}
		
		return str;
    }
	@Override 
  	public String execute() throws Exception {
		
		if (this.hasErrors()) {
			log.error("Some errors detected in multi-part file upload pre-processing");
			return BeanProcessResult.BeanProcess_ERROR.toString();
		}
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
		List<IElement> elementsToCreate = null;
		ICommunity c = null;
		try {
			
			// Map between an Element metadataId (or name or comment) and corresponding CSV column 
			Map<String,Integer> csvMappingTable = new HashMap<String,Integer>();
			Map<String,List<String[]>> regexesMetadataTable = new HashMap<String,List<String[]>>();
			IElementHandle baseEl = this.getSelectedCommunity().getElement(this.getBaseElementId());

			// Preparing CSV csvColumns / Metaindex mapping
			
			// the split function ignores tailing empty columns of CSV, which
			// results as an error during processing. So, we remember
			// the highest index referenced so that, if ignore, we can artificially
			// add trailing empty columns when parsing contents
			Integer maxCsvParamIdx=0;
			String[] mappingTableStr=this.getCsvMappingStr().split(";");
			for (int i=0;i<mappingTableStr.length;i++){
				String curCSVMappingStr=stripStr(mappingTableStr[i]);
				String[] mapping = curCSVMappingStr.split("=");				
				String elementParamId = mapping[0];
				// ignore unset metadata
				if (mapping.length<2) { continue; } 
				Integer csvParamIdx = new Integer(mapping[1]);	
				if (csvParamIdx>maxCsvParamIdx) { maxCsvParamIdx=csvParamIdx; }
				csvMappingTable.put(elementParamId, csvParamIdx);				
			}
			c = CommunitiesAccessor.getCommunity(this.getSelectedCommunity().getCommunityId());
			if (!this.getSelectedCommunity().isIdentified()) 
			{
				addActionMessage("Sorry, no current community selected for creating new element from CSV file.");
				return BeanProcessResult.BeanProcess_ERROR.toString(); 
			}
			
			// Parse transformation regexes defined by user
			String regexesStr = this.getCsvTransformRegexes();
			String[] regexesPerMetadataTbl = regexesStr.split(";");

			// for each metadata, retrieve potential regexes for adaptation of strings coming from CSV
			for (int i=0;i<regexesPerMetadataTbl.length;i++) {	
				
				String regexId=regexesPerMetadataTbl[i].split("=")[0];
				if (regexesPerMetadataTbl[i].split("=").length==1) { continue; }
				String[] regexesTbl=regexesPerMetadataTbl[i].split("=")[1].split(",");
				List<String[]> regexesDef = new ArrayList<String[]>();
				
				// for current metadata, retirve list of regexes to apply successively
				for (int j=0;j<regexesTbl.length;j++) {
					String[] curRegexes = new String[2]; //0:match regex, 1:transform regex
					String curRegexesStr=regexesTbl[j];
					
					// regex String is '/match regex/transform regex/'
					// if transform regex is empty, then split array might miss the last (empty) entry
					String[] curRegexesArray=curRegexesStr.split("/");
					curRegexes[0]="";
					curRegexes[1]="";
					if (curRegexesArray.length>1) { curRegexes[0]=curRegexesArray[1]; }
					if (curRegexesArray.length>2) { curRegexes[1]=curRegexesArray[2]; }
					
					regexesDef.add(curRegexes);
				}
				
				regexesMetadataTable.put(regexId, regexesDef);
			}
			
			// processing lines
			log.error("File Path : "+getFormFile().getCanonicalPath());
			List<String> lines= Files.readAllLines(Paths.get(getFormFile().getCanonicalPath()));
			elementsToCreate = new ArrayList<IElement>();
						
			// Create imported elements
			Iterator<String> it = lines.iterator();			
			Integer counter = 0;
			Map<String,Integer> csvColsList=new HashMap<String,Integer>();
			// for each CSV line
			while (it.hasNext()) {
				String curLine = it.next();
				counter++;
				
				if (counter==1) {
					String[] csvColsListArray = curLine.split(";");
					for (int i=0;i<csvColsListArray.length;i++) {
						String curFieldName=csvColsListArray[i];
						curFieldName=curFieldName.replaceAll("^\\s*#?\\s*","");
						curFieldName=curFieldName.replaceAll("\\s*$","");
						csvColsList.put(curFieldName, i);
					}
					continue;
				}
				// ignore commented lines
				if (curLine.startsWith("#")) { continue; }
				
				// build a CSV columns array from proper size
				// cannot increase dynamically size of the array (or yes?)
				// so for now convert it as a List object  ...
				List<String> csvColumns = new ArrayList<String>();
				String csvParsedColumns[]= curLine.split(";");
				for (int i=0;i<csvParsedColumns.length;i++) { csvColumns.add(csvParsedColumns[i]); }
				// ... and add missing empty fields at the end if needed
				while (csvColumns.size()<csvColsList.size()) { 
					csvColumns.add(""); 
				}
				csvParsedColumns=csvColumns.toArray(csvParsedColumns);
				// ------- Start Populating Element ------- 
				IElement newElement = new Element(c);	
				newElement.setElementId(CommunitiesAccessor.getNewElementId());
				Integer nameCSVIndex = csvMappingTable.get(CSV_MAPPING_ID_ELEMENT_NAME);
				if (nameCSVIndex==null) { newElement.setName(baseEl.getName() + " " + counter); }
				else {					
					newElement.setName(	applyTranformRegex(	csvColumns.get(nameCSVIndex),
															regexesMetadataTable.get(CSV_MAPPING_ID_ELEMENT_NAME),
															csvColsList,csvParsedColumns)); 
				}
				Integer commentCSVIndex = csvMappingTable.get(CSV_MAPPING_ID_ELEMENT_COMMENT);
				if (commentCSVIndex==null) { newElement.setComment(baseEl.getComment()); }
				else { 
					newElement.setComment( applyTranformRegex(	csvColumns.get(commentCSVIndex),
																regexesMetadataTable.get(CSV_MAPPING_ID_ELEMENT_COMMENT),
																csvColsList,csvParsedColumns)); 
				}
				newElement.setCommunityId(this.getSelectedCommunity().getCommunityId());
				newElement.setTemplateRefElementId(baseEl.getElementId());
				elementsToCreate.add(newElement);
				c.addElement(this.getLoggedUserProfile(), newElement);
				
				// 2- Create metadata (and Datasets when required)				
				Iterator<String> itParamsKeys = csvMappingTable.keySet().iterator();
				while(itParamsKeys.hasNext()) {
					String curKey = itParamsKeys.next();
					Integer curCsvIdx=csvMappingTable.get(curKey);
					// if cur CSV mapping corresponds to a metadata and not to element name or comment
					if (!curKey.equals(CSV_MAPPING_ID_ELEMENT_NAME)
							&& !curKey.equals(CSV_MAPPING_ID_ELEMENT_COMMENT))
					{
						Integer metadataId = new Integer(curKey);
						IMetadataHandle templateMetadata = baseEl.getMetadata(metadataId);
						
						IMetadata newMetadata = newElement.addMetadata(this.getLoggedUserProfile(),
																		templateMetadata.getDatasetId(), 
																		templateMetadata.getName(), 
																		templateMetadata.getComment(), 
																		null, null, templateMetadata.getTermId());		
						
						newMetadata.setValueFromStr(applyTranformRegex(	csvColumns.get(curCsvIdx),
																		regexesMetadataTable.get(metadataId.toString()),
																		csvColsList,csvParsedColumns));  						
					}					
				} 
			}
			this.getSelectedCommunity().createFullElements(this.getLoggedUserProfile(), elementsToCreate);	
			
			
		} catch (Exception e) {
			log.error("Unable to use uploaded CSV file for populating catalog "+getCatalogId()+": "+e.getMessage());
			e.printStackTrace();
			this.addActionError(e.getMessage());
			// Remove from community elements which have been added but could not 
			// be stored into DB
			Iterator<IElement> it = elementsToCreate.iterator();
			while (it.hasNext()) {
				IElement el = it.next();
				c.removeElement(this.getLoggedUserProfile(), el);
			}
			status =  BeanProcessResult.BeanProcess_ERROR;
		}
		
		return status.toString();
	}
	 
	public Integer getCatalogId() { return catalogId; }
	public void setCatalogId(Integer catId) { catalogId=catId; }
 	public File getFormFile() { return formFile; }
	public void setFormFile(File myFile) { this.formFile = myFile; }
	public String getFormFileContentType() { return formFileContentType; }
	public void setFormFileContentType(String myFileContentType) { this.formFileContentType = myFileContentType; }
	public String getFormFileFileName() { return formFileName; }
	public void setFormFileFileName(String myFileName) { this.formFileName = myFileName; }
	public POPULATE_POLICY getPopulatePolicy() { return policy; }
	public void setPopulatePolicy(POPULATE_POLICY myPolicy) { policy = myPolicy; }
	public Integer getBaseElementId() { return formBaseElementId; }
	public void setBaseElementId(Integer val) { formBaseElementId=val; }
	public String getCsvMappingStr() { return csvMappingStr; }
	public void setCsvMappingStr(String val) { csvMappingStr=val; }	
	public String getCsvTransformRegexes() { return csvTransformRegexesStr; }
	public void setCsvTransformRegexes(String val) { csvTransformRegexesStr=val; }	
	
	
}
