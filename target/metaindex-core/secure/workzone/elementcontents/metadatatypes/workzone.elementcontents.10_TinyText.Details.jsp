<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<table>
<s:include value="/secure/workzone/elementcontents/metadatatypes/workzone.elementcontents.Generic.Details.jsp" >
	<s:param name="curDataset" value="#curDataset" />
	<s:param name="curMetadata" value="#curMetadata" />
</s:include>
			<tr>
						<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.displayName"/></span></td>
						<td>
							<input 	type="hidden" id="edit.metadata.<s:property value="#curMetadata.metadataId"/>.details.form.layoutDoDisplayName"
									name="selectedElement.datasetsMap['<s:property value="#curDataset.datasetId" />'].metadatasMap['<s:property value="#curMetadata.metadataId" />'].layoutDoDisplayName"
									value="<s:property value="#curMetadata.layoutDoDisplayName"/>"></input>
							<input type="checkbox" <c:if test="${curMetadata.layoutDoDisplayName == 'true' }" >checked</c:if> 	
									<c:if test="${curMetadata.readOnly or curMetadata.templated}" >disabled="true"</c:if>							 
								onchange="	document.getElementById('edit.metadata.<s:property value="#curMetadata.metadataId"/>.details.form.layoutDoDisplayName').value=this.checked;
											document.getElementById('edit.metadata.<s:property value="#curMetadata.metadataId"/>.details.form').submit();" /></td>
				</tr>

					<tr>
						<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.align"/></span></td>
						<td>
							<c:if test="${curMetadata.readOnly or curMetadata.templated}" >
									<c:if test="${curMetadata.layoutAlign=='left'}"><s:text name="workzone.dataset.layout.align.left"/></c:if>
									<c:if test="${curMetadata.layoutAlign=='center'}"><s:text name="workzone.dataset.layout.align.center"/></c:if>
									<c:if test="${curMetadata.layoutAlign=='right'}"><s:text name="workzone.dataset.layout.align.right"/></c:if>
							</c:if>
							<c:if test="${not curMetadata.readOnly and not curMetadata.templated}" >
								<a href="#" id="workzone.elementdetails.metadataalign.readonly.<s:property value="#curMetadata.metadataId" />"									
											onclick="event.stopPropagation();							
												document.getElementById('workzone.elementdetails.metadataalign.edit.<s:property value="#curMetadata.metadataId" />').style.display='block';
												document.getElementById('workzone.elementdetails.metadataalign.readonly.<s:property value="#curMetadata.metadataId" />').style.display='none';"
											class="modifyable" 							
									>
									<c:if test="${curMetadata.layoutAlign=='left'}"><s:text name="workzone.dataset.layout.align.left"/></c:if>
									<c:if test="${curMetadata.layoutAlign=='center'}"><s:text name="workzone.dataset.layout.align.center"/></c:if>
									<c:if test="${curMetadata.layoutAlign=='right'}"><s:text name="workzone.dataset.layout.align.right"/></c:if>
										
								</a>	
								<select name="selectedElement.datasetsMap['<s:property value="#curDataset.datasetId" />'].metadatasMap['<s:property value="#curMetadata.metadataId" />'].layoutAlign"
										id="workzone.elementdetails.metadataalign.edit.<s:property value="#curMetadata.metadataId" />" 
										onchange="document.getElementById('edit.metadata.<s:property value="#curMetadata.metadataId"/>.details.form').submit();" 
										style="width:10%;display:none" >
									<option value="left" <c:if test="${curMetadata.layoutAlign=='left'}">selected</c:if> ><s:text name="workzone.dataset.layout.align.left"/></option>
									<option value="center" <c:if test="${curMetadata.layoutAlign=='center'}">selected</c:if> ><s:text name="workzone.dataset.layout.align.center"/></option>
									<option value="right" <c:if test="${curMetadata.layoutAlign=='right'}">selected</c:if> ><s:text name="workzone.dataset.layout.align.right"/></option>
								</select>
							</c:if>
						</td>
					</tr>
					<tr>
						<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.size"/></span></td>
						<td>
							<c:if test="${curMetadata.readOnly or curMetadata.templated}" >
									<c:if test="${curMetadata.layoutSize=='small'}"><s:text name="workzone.dataset.layout.size.small"/></c:if>
									<c:if test="${curMetadata.layoutSize=='normal'}"><s:text name="workzone.dataset.layout.size.normal"/></c:if>
									<c:if test="${curMetadata.layoutSize=='big'}"><s:text name="workzone.dataset.layout.size.big"/></c:if>
							</c:if>
							<c:if test="${not curMetadata.readOnly and not curMetadata.templated}" >
								<a href="#" id="workzone.elementdetails.metadatasize.readonly.<s:property value="#curMetadata.metadataId" />"									
											onclick="event.stopPropagation();							
												document.getElementById('workzone.elementdetails.metadatasize.edit.<s:property value="#curMetadata.metadataId" />').style.display='block';
												document.getElementById('workzone.elementdetails.metadatasize.readonly.<s:property value="#curMetadata.metadataId" />').style.display='none';"
											class="modifyable" 							
									>
									<c:if test="${curMetadata.layoutSize=='small'}"><s:text name="workzone.dataset.layout.size.small"/></c:if>
									<c:if test="${curMetadata.layoutSize=='normal'}"><s:text name="workzone.dataset.layout.size.normal"/></c:if>
									<c:if test="${curMetadata.layoutSize=='big'}"><s:text name="workzone.dataset.layout.size.big"/></c:if>										
								</a>	
								<select name="selectedElement.datasetsMap['<s:property value="#curDataset.datasetId" />'].metadatasMap['<s:property value="#curMetadata.metadataId" />'].layoutSize"
										id="workzone.elementdetails.metadatasize.edit.<s:property value="#curMetadata.metadataId" />"
										style="display:none"
										onchange="document.getElementById('edit.metadata.<s:property value="#curMetadata.metadataId"/>.details.form').submit();" >
									<option value="small" <c:if test="${curMetadata.layoutSize=='small'}">selected</c:if> ><s:text name="workzone.dataset.layout.size.small"/></option>
									<option value="normal" <c:if test="${curMetadata.layoutSize=='normal'}">selected</c:if> ><s:text name="workzone.dataset.layout.size.normal"/></option>
									<option value="big" <c:if test="${curMetadata.layoutSize=='big'}">selected</c:if> ><s:text name="workzone.dataset.layout.size.big"/></option>
								</select>
							</c:if>
						</td>
					</tr>	
					<tr>
						<td><span class="fieldtitle"><s:text name="workzone.metadata.layout.fontWeight"/></span></td>
						<td>
							<c:if test="${curMetadata.readOnly or curMetadata.templated}" >
									<c:if test="${curMetadata.asTinyText.fontWeight=='Bold'}"><span style="font-weight:bold;"><s:text name="workzone.metadata.layout.fontWeight.bold"/></span></c:if>
									<c:if test="${curMetadata.asTinyText.fontWeight!='Bold' and curMetadata.asTinyText.fontWeight!='Italic'}"><s:text name="workzone.metadata.layout.fontWeight.normal"/></c:if>
									<c:if test="${curMetadata.asTinyText.fontWeight=='Italic'}"><span style="font-style:italic;"><s:text name="workzone.metadata.layout.fontWeight.italic"/></span></c:if>		
							</c:if>
							<c:if test="${not curMetadata.readOnly and not curMetadata.templated}" >
								<a href="#" id="workzone.elementdetails.fontweight.readonly.<s:property value="#curMetadata.metadataId" />"									
											onclick="event.stopPropagation();							
												document.getElementById('workzone.elementdetails.fontweight.edit.<s:property value="#curMetadata.metadataId" />').style.display='block';
												document.getElementById('workzone.elementdetails.fontweight.readonly.<s:property value="#curMetadata.metadataId" />').style.display='none';"
											class="modifyable" 							
									>
									<c:if test="${curMetadata.asTinyText.fontWeight=='Bold'}"><span style="font-weight:bold;"><s:text name="workzone.metadata.layout.fontWeight.bold"/></span></c:if>
									<c:if test="${curMetadata.asTinyText.fontWeight!='Bold' and curMetadata.asTinyText.fontWeight!='Italic'}"><s:text name="workzone.metadata.layout.fontWeight.normal"/></c:if>
									<c:if test="${curMetadata.asTinyText.fontWeight=='Italic'}"><span style="font-style:italic;"><s:text name="workzone.metadata.layout.fontWeight.italic"/></span></c:if>		
								</a>	
								<select name="selectedElement.datasetsMap['<s:property value="#curDataset.datasetId" />'].metadatasMap['<s:property value="#curMetadata.metadataId" />'].asTinyText.fontWeight"
										id="workzone.elementdetails.fontweight.edit.<s:property value="#curMetadata.metadataId" />"
										style="display:none"
										onchange="document.getElementById('edit.metadata.<s:property value="#curMetadata.metadataId"/>.details.form').submit();" >
									<option value="Bold" <c:if test="${curMetadata.asTinyText.fontWeight=='Bold'}">selected</c:if> ><s:text name="workzone.metadata.layout.fontWeight.bold"/></option>
									<option value="Normal" <c:if test="${curMetadata.asTinyText.fontWeight!='Bold' and curMetadata.asTinyText.fontWeight!='Italic'}">selected</c:if> ><s:text name="workzone.metadata.layout.fontWeight.normal"/></option>
									<option value="Italic" <c:if test="${curMetadata.asTinyText.fontWeight=='Italic'}">selected</c:if> ><s:text name="workzone.metadata.layout.fontWeight.italic"/></option>
								</select>
							</c:if>
						</td>
				</tr>	

</table>						
		
