<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>



					<tr>						
						<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.elementName"/></span></td>
						<td>
							<s:include value="/secure/subparts/global.editablefield.jsp" >
								<s:param name="id" value="'workzone.elementdetails.metadataname.'+#curMetadata.metadataId" />
								<s:param name="text" value="#curMetadata.name" />
								<s:param name="inputname" value="'selectedElement.datasetsMap[\\''+#curDataset.datasetId+'\\'].metadatasMap[\\''+#curMetadata.metadataId+'\\'].name'" />	
								<s:param name="inputvalue" value="#curMetadata.name" />	
								<s:param name="inputplaceholder" value="" /><%--workzone.dataset.layout.elementName--%>		
								<s:param name="inputonchange" value="'document.getElementById(\\'edit.metadata.'+#curMetadata.metadataId+'.details.form\\').submit();'" />				    											    						
								<s:param name="inputstyle" value="'width:80px'" />			
								<s:param name="readOnly" value="#curMetadata.readOnly && ! #curMetadata.templated" />									
							</s:include>
						</td>
					</tr>
				<tr>
					<td><span class="fieldtitle"><s:text name="workzone.metadata.layout.elementComment"/></span></td>
					<td>						
						<s:include value="/secure/subparts/global.editablefield.jsp" >
							<s:param name="id" value="'workzone.elementdetails.metadatacomment.'+#curMetadata.metadataId" />
							<s:param name="text" value="#curMetadata.comment" />
							<s:param name="inputname" value="'selectedElement.datasetsMap[\\''+#curDataset.datasetId+'\\'].metadatasMap[\\''+#curMetadata.metadataId+'\\'].comment'" />	
							<s:param name="inputvalue" value="#curMetadata.comment" />	
							<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
							<s:param name="inputonchange" value="'document.getElementById(\\'edit.metadata.'+#curMetadata.metadataId+'.details.form\\').submit();'" />				    											    						
							<s:param name="inputstyle" value="width:80px" />
							<s:param name="readOnly" value="#curMetadata.readOnly" />	
						</s:include>						
					</td>
				</tr>
					<tr>
						<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.type"/></span></td>
						<td>	
							<c:if test="${curMetadata.readOnly or curMetadata.templated}" >
								<s:property value="#curMetadata.term.vocabulary.termNameTraduction"/> (<s:property value="#curMetadata.term.datatypeName"/>)
							</c:if>
							<c:if test="${not curMetadata.readOnly and not curMetadata.templated}" >
								<a href="#" id="workzone.elementdetails.metadatatype.readonly.<s:property value="#curMetadata.metadataId" />"									
											onclick="event.stopPropagation();							
												document.getElementById('workzone.elementdetails.metadatatype.edit.<s:property value="#curMetadata.metadataId" />').style.display='block';
												document.getElementById('workzone.elementdetails.metadatatype.readonly.<s:property value="#curMetadata.metadataId" />').style.display='none';"
											class="modifyable" 							
									>
									<s:property value="#curMetadata.term.vocabulary.termNameTraduction"/> (<s:property value="#curMetadata.term.datatypeName"/>)	
								</a>					
								<select name="selectedElement.datasetsMap['<s:property value="#curDataset.datasetId" />'].metadatasMap['<s:property value="#curMetadata.metadataId" />'].termId"
										id="workzone.elementdetails.metadatatype.edit.<s:property value="#curMetadata.metadataId" />"
										style="display:none"
										onchange="document.getElementById('edit.metadata.<s:property value="#curMetadata.metadataId"/>.details.form').submit();" >
									<s:iterator value="selectedCommunity.terms" var="curTerm">
		 								<option value="<s:property value="#curTerm.termId"/>"  <c:if test="${curMetadata.termId==curTerm.termId}">selected</c:if> >
		 									<s:property value="#curTerm.vocabulary.termNameTraduction"/> (<s:property value="#curTerm.datatypeName"/>)</option>
		 							</s:iterator>								
								</select>
							</c:if>
						</td>
					</tr>
					<tr>
						<td><table><tr>
						<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.position"/></span></td>
						<td><s:property value="#curMetadata.layoutPosition" /></td>						
						</tr></table></td>
						<td><table><tr>
						<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.column"/></span></td>
						<td><s:property value="#curMetadata.layoutColumn" /></td>
						</tr></table></td>					
					</tr>
						
	
		
	


	    	
	    			
		
