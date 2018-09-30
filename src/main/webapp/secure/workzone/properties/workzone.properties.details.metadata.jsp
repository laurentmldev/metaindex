<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.Generic.Properties.jsp" />

<script>



function elementProperties_addDetails_Metadata(m) {
	
	var subdataNode = document.getElementById("elementdetails_subdata");
	
	var newMetadataDetails = document.getElementById("_template_details_metadata_").cloneNode(true);
	newMetadataDetails.id="edit.metadata."+m.metadataId+".details";
	newMetadataDetails.style.display='block';
	subdataNode.append(newMetadataDetails);
	
	// override-template form
	var overrideTemplateForm = newMetadataDetails.querySelector("._override_template_form_");
	overrideTemplateForm.id="edit.metadata."+m.metadataId+".overridetemplate.form";
	newMetadataDetails.querySelector("._override_template_form_name_").value=m.metadataName;
	newMetadataDetails.querySelector("._override_template_form_comment_").value=m.metadataComment;
	newMetadataDetails.querySelector("._override_template_form_datasetId_").value=m.datasetId;
	newMetadataDetails.querySelector("._override_template_form_termId_").value=m.termId;
	newMetadataDetails.querySelector("._override_template_form_column_").value=m.layoutColumn;
	newMetadataDetails.querySelector("._override_template_form_position_").value=m.layoutPosition;
	
	// details form
	var detailsForm = newMetadataDetails.querySelector("._details_form_");
	detailsForm.id="edit.metadata."+m.metadataId+".details.form";
	
	var layout = newMetadataDetails.querySelector("._layout_");
	layout.id+=m.metadataId;
	
	if (m.templated) {
		if (!m.modifyOverridenTemplate) {
			var modifyTemplateLink = newMetadataDetails.querySelector("._text_not_modify_template_");
			modifyTemplateLink.style.display='block';
			modifyTemplateLink.onclick=function(e) { document.getElementById('edit.metadata.'+m.metadataId+'.overridetemplate.form').submit(); }
		} else {
			newMetadataDetails.querySelector("._text_modify_template_").style.display='block';
		}
	}
	
	var insertspot = newMetadataDetails.querySelector("._metadata_details_insertspot_");
	insertspot.id="elementProperties.metadata.insertspot."+m.metadataId;
	insert_metadata_properties(insertspot,m);
	
}

</script>

<span id="_template_details_metadata_" style="display:none"> 
           
              
     		   <form class="_override_template_form_" action="<c:url value="/addMetadataProcess" />" method="post" >
               	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
               	<input type="hidden"  class="_override_template_form_name_" name="formMetadataName" />
               	<input type="hidden"  class="_override_template_form_comment_" name="formMetadataComment" />
               	<input type="hidden"  class="_override_template_form_datasetId_" name="formDatasetId" />
               	<input type="hidden"  class="_override_template_form_termId_" name="formTermId" />
               	<input type="hidden"  class="_override_template_form_column_" name="formColumn" />
               	<input type="hidden"  class="_override_template_form_position_" name="formPosition" />
              </form>
              <form class="_details_form_" action="<c:url value="/updateMetadataProcess" />" method="post" >
                    <input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div id="layout.metadata." class="_layout_" style="display:none;">
                        
                    	<!-- if templated -->    
                       	<center>
                        	<!--  if not curMetadata.modifyOverridenTemplate -->	                        	
                        	<span class="_text_not_modify_template_ clickable modifyable" style="display:none" ><s:text name="element.details.overrideTemplateValues" /></span>
                        	<!--  if curMetadata.modifyOverridenTemplate -->
                        	<span class="comment _text_modify_template_" style="display:none"><s:text name="element.details.overridingTemplateValues" /></span>                        
                        </center>
                        
                        <span class="_metadata_details_insertspot_"></span>
                               
                    </div>
	          </form>
                
        
</span>
 	

