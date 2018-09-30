<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.Generic.SimpleText.Properties.jsp" />
<s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.1_WebLink.Properties.jsp" />
<s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.4_Image.Properties.jsp" />
<s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.5_LongText.Properties.jsp" />
<s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.10_TinyText.Properties.jsp" />

<%-- 


<s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.5_LongText.Properties.jsp" />

--%>

<script>

function insert_metadata_properties(insertspot, m) {
	
	var newMetadataDetails = document.getElementById("_template_metadata_properties_generic_").cloneNode(true);
	newMetadataDetails.id="elementProperties.metadata."+m.metadataId;
	newMetadataDetails.style.display='block';
	insertspot.append(newMetadataDetails);
	
	// name
	var metadataName = new_editable_field(
			/*name*/ "workzone.elementdetails.metadataname."+m.metadataId, 
			/*input name*/ "selectedElement.datasetsMap['"+m.datasetId+"'].metadatasMap['"+m.metadataId+"'].name", 
			/*input style*/ "width:80px", 
			/*input placeholder*/ "<s:text name="workzone.dataset.layout.elementName"/>",
			/*onchange Func*/ function(e) { document.getElementById('edit.metadata.'+m.metadataId+'.details.form').submit(); }
		);
	newMetadataDetails.querySelector("._elementProperties_details_metadata_name_").innerHTML=metadataName.innerHTML;
	metadataName.setValue(m.metadataName,m.readOnly || m.templated);
	
	// comment
	var metadataComment = new_editable_field(
			/*name*/ "workzone.elementdetails.metadatacomment."+m.metadataId, 
			/*input name*/ "selectedElement.datasetsMap['"+m.datasetId+"'].metadatasMap['"+m.metadataId+"'].comment", 
			/*input style*/ "width:80px", 
			/*input placeholder*/ "<s:text name="workzone.dataset.layout.elementComment"/>",
			/*onchange Func*/ function(e) { document.getElementById('edit.metadata.'+m.metadataId+'.details.form').submit(); }
		);
	newMetadataDetails.querySelector("._elementProperties_details_metadata_comment_").innerHTML=metadataComment.innerHTML;
	metadataComment.setValue(m.metadataComment,m.readOnly || m.templated);
	
	// term
	var options = [];	
	<s:iterator value="selectedCommunity.terms" var="curTerm">		
		options.push({ value:<s:property value="#curTerm.termId"/>, text:"<s:property value="#curTerm.vocabulary.termNameTraduction"/> (<s:property value="#curTerm.datatypeName"/>)" });
	</s:iterator>	

	var metadataType = new_enumerated_field(
			/*id*/ "workzone.elementdetails.metadatatype.edit."+m.metadataId, 
			/*input name*/ "selectedElement.datasetsMap['"+m.datasetId+"'].metadatasMap['"+m.metadataId+"'].termId", 
			/* options */ options,
			/*onchange Func*/ function(e) { document.getElementById('edit.metadata.'+m.metadataId+'.details.form').submit(); }			
		);
	newMetadataDetails.querySelector("._elementProperties_details_metadata_type_").innerHTML=metadataType.innerHTML;
	metadataType.setValue(m.termId,m.readOnly || m.templated);
	
	// layout position
	var metadataPosition = new_editable_field(
			/*name*/ "workzone.elementdetails.metadataposition."+m.metadataId, 
			/*input name*/ "selectedElement.datasetsMap['"+m.datasetId+"'].metadatasMap['"+m.metadataId+"'].layoutPosition", 
			/*input style*/ "width:80px", 
			/*input placeholder*/ "<s:text name="workzone.dataset.layout.position"/>",
			/*onchange Func*/ function(e) { document.getElementById('edit.metadata.'+m.metadataId+'.details.form').submit(); }
		);
	newMetadataDetails.querySelector("._elementProperties_details_metadata_layoutPosition_").innerHTML=metadataPosition.innerHTML;
	metadataPosition.setValue(m.layoutPosition,m.readOnly || m.templated);
	
	// layout column
	var metadataColumn = new_editable_field(
			/*name*/ "workzone.elementdetails.metadatacolumn."+m.metadataId, 
			/*input name*/ "selectedElement.datasetsMap['"+m.datasetId+"'].metadatasMap['"+m.metadataId+"'].layoutColumn", 
			/*input style*/ "width:80px", 
			/*input placeholder*/ "<s:text name="workzone.dataset.layout.column"/>",
			/*onchange Func*/ function(e) { document.getElementById('edit.metadata.'+m.metadataId+'.details.form').submit(); }
		);
	newMetadataDetails.querySelector("._elementProperties_details_metadata_layoutColumn_").innerHTML=metadataColumn.innerHTML;
	metadataColumn.setValue(m.layoutColumn,m.readOnly || m.templated);
	
	// display name
	var metadataDisplayName = new_boolean_field(
			/*name*/ "workzone.elementdetails.metadatadisplayname."+m.metadataId, 
			/*input name*/ "selectedElement.datasetsMap['"+m.datasetId+"'].metadatasMap['"+m.metadataId+"'].layoutDoDisplayName", 			
			/*onchange Func*/ function(e) { document.getElementById('edit.metadata.'+m.metadataId+'.details.form').submit(); }
		);
	newMetadataDetails.querySelector("._elementProperties_details_metadata_layoutDoDisplayName_").innerHTML=metadataDisplayName.innerHTML;
	metadataDisplayName.setValue(m.layoutDoDisplayName,m.readOnly || m.templated);
	
	
	// align
	var options = [];	
	options.push({ value:"left", text:"<s:text name="workzone.dataset.layout.align.left"/>" });
	options.push({ value:"center", text:"<s:text name="workzone.dataset.layout.align.center"/>" });
	options.push({ value:"right", text:"<s:text name="workzone.dataset.layout.align.right"/>" });	
	var metadataAlign = new_enumerated_field(
			/*id*/ "workzone.elementdetails.metadataalign.edit."+m.metadataId, 
			/*input name*/ "selectedElement.datasetsMap['"+m.datasetId+"'].metadatasMap['"+m.metadataId+"'].layoutAlign", 
			/*options*/ options,
			/*onchange Func*/ function(e) { document.getElementById('edit.metadata.'+m.metadataId+'.details.form').submit(); }			
		);
	newMetadataDetails.querySelector("._elementProperties_details_metadata_layoutAlign_").innerHTML=metadataAlign.innerHTML;
	metadataAlign.setValue(m.layoutAlign,m.readOnly || m.templated);
	
	// size
	options = [];	
	options.push({ value:"small", text:"<s:text name="workzone.dataset.layout.size.small"/>" });
	options.push({ value:"normal", text:"<s:text name="workzone.dataset.layout.size.normal"/>" });
	options.push({ value:"big", text:"<s:text name="workzone.dataset.layout.size.big"/>" });	
	var metadataSize = new_enumerated_field(
			/*id*/ "workzone.elementdetails.metadatasize.edit."+m.metadataId, 
			/*input name*/ "selectedElement.datasetsMap['"+m.datasetId+"'].metadatasMap['"+m.metadataId+"'].layoutSize", 
			/*options*/ options,
			/*onchange Func*/ function(e) { document.getElementById('edit.metadata.'+m.metadataId+'.details.form').submit(); }			
		);
	newMetadataDetails.querySelector("._elementProperties_details_metadata_layoutSize_").innerHTML=metadataSize.innerHTML;
	metadataSize.setValue(m.layoutSize,m.readOnly || m.templated);
	
	
	// specific part
	var specificDataInsertSpot = newMetadataDetails.querySelector("._specific_data_");	
	if (m.metadataType=='web-link') { insert_metadata_properties_WebLink(specificDataInsertSpot,m); }
	/*
	else if (m.metadataType=='audio') {  }
	else if (m.metadataType=='video') {  }
	*/
	else if (m.metadataType=='image') { insert_metadata_properties_Image(specificDataInsertSpot,m); }
	else if (m.metadataType=='long-text') { insert_metadata_properties_LongText(specificDataInsertSpot,m); }
	/*
	else if (m.metadataType=='number') {  }
	
	else if (m.metadataType=='date') {  }
	else if (m.metadataType=='period') {  }
	else if (m.metadataType=='keywords') {  }
	*/
	else if (m.metadataType=='tiny-text') { insert_metadata_properties_TinyText(specificDataInsertSpot,m);  }
	
	else {		
		specificDataInsertSpot.innerHTML="<span class=\"comment\">Unsupported datatype "+m.metadataType+" for Metadata '"+m.metadataName+"'.</span>";
	}
	
	
}
</script>

<span id="_template_metadata_properties_generic_" style="display:none" >
	<table>
	
					<tr>
						<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.elementName"/></span></td>
						<td><span class="_elementProperties_details_metadata_name_" ></span>							
					</tr>
					<tr>
						<td><span class="fieldtitle"><s:text name="workzone.metadata.layout.elementComment"/></span></td>
						<td><span class="_elementProperties_details_metadata_comment_" ></span>					
					</tr>		
					<tr>										
						<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.type"/></span></td>
						<td><span class="_elementProperties_details_metadata_type_" ></span></td>
					</tr>
					<tr>
						<td><table><tr>
							<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.column"/></span></td>
							<td><span class="_elementProperties_details_metadata_layoutColumn_" ></span></td>
						</tr></table></td>
						<td><table><tr>
							<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.position"/></span></td>
							<td><span class="_elementProperties_details_metadata_layoutPosition_" ></span></td>						
						</tr></table></td>											
					</tr>
					<tr>
						<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.displayName"/></span></td>
						<td><span class="_elementProperties_details_metadata_layoutDoDisplayName_" ></span></td>
					</tr>
					<tr>
						<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.align"/></span></td>
						<td><span class="_elementProperties_details_metadata_layoutAlign_"></span></td>							
					</tr>
					<tr>
						<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.size"/></span></td>
						<td><span class="_elementProperties_details_metadata_layoutSize_"></span></td>						
					</tr>	
	</table>
	<span class="_specific_data_"></span>
</span>
	


	    	
	    			
		
