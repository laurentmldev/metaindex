<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<fieldset  >
<legend >

	<span class="fieldsetTitle" ><s:property value="selectedCommunity.vocabulary.capElementTraduction" /></span>
	
</legend>

	<form 	id="edit.element.<s:property value="selectedCatalog.selectedElement.elementId"/>.details.form" 
			action="<c:url value="/updateElementDataProcess" />" method="post" >
		<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
		
		<c:if test="${not selectedElement.readOnly}" >
			<div style="width:100%;display:flex;align-items: center;justify-content:center;" >
					<a  href="#" id="workzone.elementdetails.icon.editModeOn" 
		 				title="<s:text name="workzone.icon.createData" />" 
		 				class="bigicon icon_modify" 
		 				onmouseover="event.stopPropagation();"
		 				onclick="switchToEditMode(true);
		 					document.getElementById('workzone.elementdetails.icon.editModeOn').style.display='none';
		 					document.getElementById('workzone.elementdetails.icon.editModeOff').style.display='block';">
					</a>
	 				
					<a  href="#" id="workzone.elementdetails.icon.editModeOff" style="display:none;"
		 				title="<s:text name="workzone.icon.createData" />" 
		 				class="bigicon icon_modify_active" 
		 				onmouseover="event.stopPropagation();"
		 				onclick="switchToEditMode(false);
								document.getElementById('workzone.elementdetails.icon.editModeOn').style.display='block';
								document.getElementById('workzone.elementdetails.icon.editModeOff').style.display='none';">
					</a>
			</div>
			</c:if>
		<table style="padding:5px">
			
		<tr><td>
		<span class="fieldtitle"  >ID</span></td>
		<td><span id="selectedElement.elementId" ><s:property value="selectedElement.elementId"/></span>
		
			</td></tr>
			<tr>
					<td><span class="fieldtitle"><s:text name="workzone.element.layout.elementName"/></span></td>
					<td>						
						<s:include value="/secure/subparts/global.editablefield.jsp" >
							<s:param name="id" value="'workzone.elementdetails.elementname.'+selectedCatalog.selectedElement.elementId" />
							<s:param name="text" value="selectedCatalog.selectedElement.name" />
							<s:param name="inputname" value="'selectedElement.name'" />	
							<s:param name="inputvalue" value="selectedCatalog.selectedElement.name" />	
							<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
							<s:param name="inputonchange" value="'document.getElementById(\\'edit.element.'+selectedCatalog.selectedElement.elementId+'.details.form\\').submit();'" />				    											    						
							<s:param name="inputstyle" value="width:80px" />
							<s:param name="readOnly" value="selectedElement.readOnly" />												
						</s:include>						
					</td>
				</tr>
				<tr>
					<td><span class="fieldtitle"><s:text name="workzone.element.layout.elementComment"/></span></td>
					<td>						
						<s:include value="/secure/subparts/global.editablefield.jsp" >
							<s:param name="id" value="'workzone.elementdetails.elementcomment.'+selectedCatalog.selectedElement.elementId" />
							<s:param name="text" value="selectedCatalog.selectedElement.comment" />
							<s:param name="inputname" value="'selectedElement.comment'" />	
							<s:param name="inputvalue" value="selectedCatalog.selectedElement.comment" />	
							<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
							<s:param name="inputonchange" value="'document.getElementById(\\'edit.element.'+selectedCatalog.selectedElement.elementId+'.details.form\\').submit();'" />				    											    						
							<s:param name="inputstyle" value="width:80px" />
							<s:param name="readOnly" value="selectedElement.readOnly" />												
						</s:include>						
					</td>
				</tr>
				<c:if test="${not selectedElement.templated}" >	
				<tr>
					<td><span class="fieldtitle"><s:text name="workzone.element.layout.istemplate"/></span></td>
					<td>						
						<s:include value="/secure/subparts/global.editablebooleanfield.jsp" >
							<s:param name="id" value="'workzone.elementdetails.istemplate.'+selectedCatalog.selectedElement.elementId" />
							<s:param name="text" value="selectedElement.template" />
							<s:param name="inputname" value="'selectedElement.template'" />	
							<s:param name="inputvalue" value="selectedCatalog.selectedElement.template" />	
							<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
							<s:param name="inputonchange" value="'document.getElementById(\\'edit.element.'+selectedCatalog.selectedElement.elementId+'.details.form\\').submit();'" />				    											    						
							<s:param name="inputstyle" value="width:80px" />	
							<s:param name="readOnly" value="selectedElement.readOnly" />											
						</s:include>						
					</td>
				</tr>
				</c:if>
				<c:if test="${selectedElement.template}" >
					<tr>
						<td><span class="fieldtitle"><s:text name="workzone.element.nbreferencingelements"/></span></td>
						<td><span><s:property value="selectedElement.nbReferencingElements"/></span></td>
					</tr>
				</c:if>
				<c:if test="${not selectedElement.template}" >
					<tr>
						<td><span class="fieldtitle"><s:text name="workzone.element.layout.templateRefElementId"/></span></td>
						<td>						
							<s:include value="/secure/subparts/global.editableTemplateRefElementIdField.jsp" >
								<s:param name="id" value="'workzone.elementdetails.templateRefElementId.'+selectedCatalog.selectedElement.elementId" />
								<s:param name="text" value="selectedCatalog.selectedElement.templateRefElementName" />
								<s:param name="inputname" value="'selectedElement.templateRefElementIdStr'" />	
								<s:param name="inputvalue" value="selectedCatalog.selectedElement.templateRefElementId" />	
								<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
								<s:param name="inputonchange" value="'document.getElementById(\\'edit.element.'+selectedCatalog.selectedElement.elementId+'.details.form\\').submit();'" />				    											    						
								<s:param name="inputstyle" value="width:80px" />	
								<s:param name="readOnly" value="selectedElement.readOnly" />											
							</s:include>						
						</td>
					</tr>	
				</c:if>			
 		</table> 
 		</form>
 			
 	<fieldset id="elementdetails_structure" style="display:none;padding:4px;border:none;" class="menushadowcard" >
 	
 		<center><div class="negative" style="padding:2px;"><s:text name="workzone.elementdetails.structure" /></div></center>
 		
	<s:iterator value="selectedCatalog.selectedElement.datasets" var="curDataset">	
	
	<ul class="menutree">
		<li class="menutree"  >
			<input type="checkbox" id="checkBox.dataset.<s:property value="#curDataset.datasetId" />" checked>	
			<c:if test="${curDataset.nbMetadata gt 0}" ><label for="checkBox.dataset.<s:property value="#curDataset.datasetId" />" ></label></c:if> 
			
				<span id="selector.dataset.<s:property value="#curDataset.datasetId" />" 
					class=" treechoice_dataset"
					onclick="if (editModeActive) { switchSelected_dataset_<s:property value="#curDataset.datasetId" />(); }"
					onmouseover="if (selected_dataset_<s:property value="#curDataset.datasetId" />==false) { 
											document.getElementById('selector.dataset.<s:property value="#curDataset.datasetId" />').classList.add('editModeDatasetMouseOverMenu');
											document.getElementById('content.dataset.<s:property value="#curDataset.datasetId" />').classList.add('editModeDatasetMouseOver<c:if test="${curDataset.readOnly}" >RO</c:if>'); 
								 }"
					onmouseout="if (selected_dataset_<s:property value="#curDataset.datasetId" />==false) { 
									document.getElementById('selector.dataset.<s:property value="#curDataset.datasetId" />').classList.remove('editModeDatasetMouseOverMenu');
									document.getElementById('content.dataset.<s:property value="#curDataset.datasetId" />').classList.remove('editModeDatasetMouseOver<c:if test="${curDataset.readOnly}" >RO</c:if>');
								 }" >
							<s:property value="#curDataset.name" /></span>
			
			<c:if test="${curDataset.nbMetadata gt 0}" >
				<ul class="menutree">										
					<s:iterator value="#curDataset.metadata" var="curMetadata">	

							<li class="menutree" >
								<span id="selector.metadata.<s:property value="#curMetadata.metadataId" />" 
									class="treechoice_metadata"
									onclick="if (editModeActive) { switchSelected_metadata_<s:property value="#curMetadata.metadataId" />(); }"
									onmouseover="if (selected_metadata_<s:property value="#curMetadata.metadataId" />==false) { 
													document.getElementById('selector.metadata.<s:property value="#curMetadata.metadataId" />').classList.add('editModeMetadataMouseOverMenu');
													document.getElementById('content.metadata.<s:property value="#curMetadata.metadataId" />').classList.add('editModeMetadataMouseOver<c:if test="${curMetadata.readOnly}" >RO</c:if>');
												 }"
									onmouseout="if (selected_metadata_<s:property value="#curMetadata.metadataId" />==false) { 
													document.getElementById('selector.metadata.<s:property value="#curMetadata.metadataId" />').classList.remove('editModeMetadataMouseOverMenu');
													document.getElementById('content.metadata.<s:property value="#curMetadata.metadataId" />').classList.remove('editModeMetadataMouseOver<c:if test="${curMetadata.readOnly}" >RO</c:if>');
												 }"
												 <%-- drag and drop mangament --%> 
												draggable="true"
												ondragstart="handleDragStartMetadata(document.getElementById('selector.metadata.<s:property value="#curMetadata.metadataId" />'),event);"
												ondragend="handleDragEndMetadata(document.getElementById('selector.metadata.<s:property value="#curMetadata.metadataId" />'),event);" 
												>
									<s:property value="#curMetadata.name" />
								</span>
							</li>							
							
							
					</s:iterator>
				</ul>
			</c:if>
			</li>
	</ul>
	</s:iterator>
	
</fieldset>
	
</fieldset>

 	
<fieldset id="elementdetails_subdata" style="display:none;">
	<legend><span id="elementdetails_subdata.legendTitle" class="fieldsetTitle" ></span></legend>

		
<%-- Dataset details  --%>
	<s:iterator value="selectedCatalog.selectedElement.datasets" var="curDataset">	
	 <form id="edit.dataset.<s:property value="#curDataset.datasetId"/>.details.form" action="<c:url value="/updateDatasetProcess" />" method="post" >
		<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>			
		<div id="layout.dataset.<s:property value="#curDataset.datasetId" />" style="display:none;">
		<c:if test="${curMetadata.templated}"><center><span class="comment"><s:text name="element.details.fromTemplate"></s:text></span></center></c:if>
			<table>
				<tr>
					<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.elementName"/></span></td>
					<td>						
						<s:include value="/secure/subparts/global.editablefield.jsp" >
							<s:param name="id" value="'workzone.elementdetails.datasetname.'+#curDataset.datasetId" />
							<s:param name="text" value="#curDataset.name" />
							<s:param name="inputname" value="'selectedElement.datasetsMap[\\''+#curDataset.datasetId+'\\'].name'" />	
							<s:param name="inputvalue" value="#curDataset.name" />	
							<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
							<s:param name="inputonchange" value="'document.getElementById(\\'edit.dataset.'+#curDataset.datasetId+'.details.form\\').submit();'" />				    											    						
							<s:param name="inputstyle" value="width:80px" />
							<s:param name="readOnly" value="#curDataset.readOnly or #curDataset.templated" />												
						</s:include>						
					</td>
					
				</tr>
								
				<tr>
					<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.elementComment"/></span></td>
					<td>						
						<s:include value="/secure/subparts/global.editablefield.jsp" >
							<s:param name="id" value="'workzone.elementdetails.datasetcomment.'+#curDataset.datasetId" />
							<s:param name="text" value="#curDataset.comment" />
							<s:param name="inputname" value="'selectedElement.datasetsMap[\\''+#curDataset.datasetId+'\\'].comment'" />	
							<s:param name="inputvalue" value="#curDataset.comment" />	
							<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
							<s:param name="inputonchange" value="'document.getElementById(\\'edit.dataset.'+#curDataset.datasetId+'.details.form\\').submit();'" />                                                                                                                                                      
                            <s:param name="inputstyle" value="width:80px" />
                            <s:param name="readOnly" value="#curDataset.readOnly" />                                                                   
                       </s:include>
                                        </td>
                </tr>
	            <tr>
                        <td><span class="fieldtitle"><s:text name="workzone.dataset.layout.position"/></span></td>
                        <td><s:property value="#curDataset.layoutPosition" /></td>
                </tr>
                <tr>
                        <td><span class="fieldtitle"><s:text name="workzone.dataset.layout.nbColumns"/></span></td>
                        <td>
                                <s:include value="/secure/subparts/global.editablefield.jsp" >
                                        <s:param name="id" value="'workzone.elementdetails.nbColumns.'+#curDataset.datasetId" />
                                        <s:param name="text" value="#curDataset.layoutNbColumns" />
                                        <s:param name="inputname" value="'selectedElement.datasetsMap[\\''+#curDataset.datasetId+'\\'].layoutNbColumns'" />
                                        <s:param name="inputvalue" value="#curDataset.layoutNbColumns" />
                                        <s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>
                                        <s:param name="inputonchange" value="'document.getElementById(\\'edit.dataset.'+#curDataset.datasetId+'.details.form\\').submit();'" />                                                                                                                                                      
                                        <s:param name="inputstyle" value="'width:30px'" />    
                                        <s:param name="readOnly" value="#curDataset.readOnly or #curDataset.templated" />                                                             
                                </s:include>

                        </td>
                </tr>
                <tr>
                        <td><span class="fieldtitle"><s:text name="workzone.dataset.layout.displayFrame"/></span></td>
                        <td>
                        <input  type="hidden" id="edit.dataset.<s:property value="#curDataset.datasetId"/>.details.form.layoutDoDisplayName"
                                                        name="selectedElement.datasetsMap['<s:property value="#curDataset.datasetId" />'].layoutDoDisplayName"
                                                        value="<s:property value="#curDataset.layoutDoDisplayName"/>" />
                        <input type="checkbox" <c:if test="${curDataset.layoutDoDisplayName == 'true' }" >checked</c:if> 
                        				<c:if test="${curDataset.readOnly or curDataset.templated}" >disabled='true'</c:if>
                                        onchange="document.getElementById('edit.dataset.<s:property value="#curDataset.datasetId"/>.details.form.layoutDoDisplayName').value=this.checked;
                                                                        document.getElementById('edit.dataset.<s:property value="#curDataset.datasetId"/>.details.form').submit();"/></td>
                </tr>
        </table>
                </div>
                </form>
	<%-- Metadata details  --%>
                <s:iterator value="#curDataset.metadata" var="curMetadata">
                		<form id="edit.metadata.<s:property value="#curMetadata.metadataId"/>.overridetemplate.form" action="<c:url value="/addMetadataProcess" />" method="post" >
                        	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        	<input type="hidden"  name="formMetadataName" value="<s:property value="#curMetadata.name" />"/>
                        	<input type="hidden"  name="formMetadataComment" value="<s:property value="#curMetadata.comment" />"/>
                        	<input type="hidden"  name="formDatasetId" value="<s:property value="#curDataset.datasetId" />"/>
                        	<input type="hidden"  name="formTermId" value="<s:property value="#curMetadata.termId" />"/>
                        	<input type="hidden"  name="formColumn" value="<s:property value="#curMetadata.layoutColumn" />"/>
                        	<input type="hidden"  name="formPosition" value="<s:property value="#curMetadata.layoutPosition" />"/>
                        </form>
                        <form id="edit.metadata.<s:property value="#curMetadata.metadataId"/>.details.form" action="<c:url value="/updateMetadataProcess" />" method="post" >
                        <input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <div id="layout.metadata.<s:property value="#curMetadata.metadataId" />" style="display:none;">
                        <c:if test="${curMetadata.templated}">
                        	<center>
	                        <c:if test="${not curMetadata.modifyOverridenTemplate}">	                        	
	                        	<span class="clickable modifyable"
	                        		onclick="document.getElementById('edit.metadata.<s:property value="#curMetadata.metadataId"/>.overridetemplate.form').submit();">
	                        			<s:text name="element.details.overrideTemplateValues" />
                       			</span>
	                        </c:if>
	                        <c:if test="${curMetadata.modifyOverridenTemplate}">
	                        	<span class="comment"><s:text name="element.details.overridingTemplateValues" /></span>
	                        </c:if>
	                        </center>
                        </c:if>
                        <c:choose>
                                <c:when test="${curMetadata.datatypeId=='1'}" >
                                        <s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.1_WebLink.Details.jsp" >
                                                                        <s:param name="curDataset" value="#curDataset" />
                                                                        <s:param name="curMetadata" value="#curMetadata" />
                                        </s:include>
                                </c:when>
                                <c:when test="${curMetadata.datatypeId=='4'}" >                                
                                        <s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.4_Image.Details.jsp" >
                                                                        <s:param name="curDataset" value="#curDataset" />
                                                                        <s:param name="curMetadata" value="#curMetadata" />
                                        </s:include>
                                </c:when>
                                <c:when test="${curMetadata.datatypeId=='5'}" >
                                        <s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.5_LongText.Details.jsp" >
                                                                        <s:param name="curDataset" value="#curDataset" />
                                                                        <s:param name="curMetadata" value="#curMetadata" />
                                        </s:include>
                                </c:when>
                                <c:when test="${curMetadata.datatypeId=='6'}" >
                                        <s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.6_Number.Details.jsp" >
                                                                        <s:param name="curDataset" value="#curDataset" />
                                                                        <s:param name="curMetadata" value="#curMetadata" />
                                        </s:include>
                                </c:when>
                                <c:when test="${curMetadata.datatypeId=='10'}" >
                                        <s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.10_TinyText.Details.jsp" >
                                                                        <s:param name="curDataset" value="#curDataset" />
                                                                        <s:param name="curMetadata" value="#curMetadata" />
                                        </s:include>
                                </c:when>
                        </c:choose>
                               
                        </div>
                        </form>
                </s:iterator>
        </s:iterator>

</fieldset>   
