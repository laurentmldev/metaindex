<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

		   		 
			  <div id="content.metadata.<s:property value="#curMetadata.metadataId"/>" 
			   	  title="<s:property value="#curMetadata.comment"/>" 
			  	  onclick="event.stopPropagation(); if (editModeActive) { switchSelected_metadata_<s:property value="#curMetadata.metadataId" />(); }"
				  onmouseover="if (editModeActive) { 
				  					if (selected_metadata_<s:property value="#curMetadata.metadataId" />==false) { 
					  					document.getElementById('selector.metadata.<s:property value="#curMetadata.metadataId" />').classList.add('editModeMetadataMouseOverMenu');
					  					document.getElementById('content.metadata.<s:property value="#curMetadata.metadataId" />').classList.add('editModeMetadataMouseOver<c:if test="${curMetadata.readOnly}" >RO</c:if>');
					  					document.getElementById('content.dataset.<s:property value="#curDataset.datasetId" />').classList.remove('editModeDatasetMouseOver<c:if test="${curDataset.readOnly}" >RO</c:if>');
				  					}
				  					<c:if test="${not curMetadata.readOnly}" >
				  						document.getElementById('workzone.elementcontents.icon.delete.metadata.<s:property value="#curMetadata.metadataId"/>').style.display='block';
			  						</c:if>
			  						
			  						
		  					 }"
		  					 
				  onmouseout="if (editModeActive) {
				  					if (selected_metadata_<s:property value="#curMetadata.metadataId" />==false) { 
					  					document.getElementById('selector.metadata.<s:property value="#curMetadata.metadataId" />').classList.remove('editModeMetadataMouseOverMenu');
					  					document.getElementById('content.metadata.<s:property value="#curMetadata.metadataId" />').classList.remove('editModeMetadataMouseOver<c:if test="${curMetadata.readOnly}" >RO</c:if>');
					  					if (selected_dataset_<s:property value="#curDataset.datasetId" />==false) { document.getElementById('content.dataset.<s:property value="#curDataset.datasetId" />').classList.add('editModeDatasetMouseOver<c:if test="${curDataset.readOnly}" >RO</c:if>'); }
				  					}
				  					<c:if test="${not curMetadata.readOnly}" >		
				  						document.getElementById('workzone.elementcontents.icon.delete.metadata.<s:property value="#curMetadata.metadataId"/>').style.display='none';
				  					</c:if>				  					
		  					 }"
		  					 
					draggable="false"
					ondragstart="event.stopPropagation(); if (editModeActive && !<s:property value="#curMetadata.readOnly"/>) { handleDragStartMetadata(<s:property value="#curMetadata.metadataId" />,document.getElementById('content.metadata.<s:property value="#curMetadata.metadataId" />'),event); }"
					ondragend="event.stopPropagation();handleDragEndMetadata(document.getElementById('content.metadata.<s:property value="#curMetadata.metadataId" />'),event);"
					
				  	class="<c:if test="${not curMetadata.readOnly}" >metadatacontent</c:if> <c:if test="${curMetadata.readOnly}" >metadatacontentRO</c:if>  metadataalign<s:property value="#curMetadata.layoutAlign"/>" 
			>
			
			<c:if test="${not curMetadata.readOnly}" >
				<a  href="#"  id="workzone.elementcontents.icon.delete.metadata.<s:property value="#curMetadata.metadataId"/>"
		 				title="<s:text name="workzone.icon.deleteMetadata" />" class="smallicon icon_deleteElement" 
		 				style="display:none;"  
		 				onclick="	
		 					event.stopPropagation();
		 					document.getElementById('workzone.deleteMetadata.form.formMetadataId').value='<s:property value="#curMetadata.metadataId"/>';							
							document.getElementById('workzone.deleteMetadata.form.id').innerHTML='<s:property value="#curMetadata.metadataId"/>';
							document.getElementById('workzone.deleteMetadata.form.datasetName').innerHTML='<s:property value="name"/>';
							document.getElementById('workzone.deleteMetadata.form.datasetComment').innerHTML='<s:property value="comment"/>';									 							
							document.getElementById('workzone.deleteMetadata.form.modal').style.display='table';">
		 		</a>
			</c:if>
			  	<form id="edit.metadata.<s:property value="#curMetadata.metadataId"/>.form" action="<c:url value="/updateMetadataProcess" />" method="post" >
			  	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
					      		
			  	<span class="metadata<s:property value="#curMetadata.layoutSize"/>size" >
		 
		    		<c:choose>
		     		<c:when test="${curMetadata.datatypeId eq 1}"><%-- Web Page --%>
		     			<span id="readonly.metadata.<s:property value="#curMetadata.metadataId"/>"	class="metadaText" >
							
		     				<s:include value="/secure/workzone/elementcontents/metadatatypes/workzone.elementcontents.1_WebLink.jsp" >
								<s:param name="curDataset" value="#curDataset" />
								<s:param name="curMetadata" value="#curMetadata" />
							</s:include>
						</span>
						<span id="edit.metadata.<s:property value="#curMetadata.metadataId"/>"
								style="display:none"
								class="matadataTextEdit" >
									<s:include value="/secure/workzone/elementcontents/metadatatypes/workzone.elementcontents.1_WebLink.Edit.jsp" >
												<s:param name="curDataset" value="#curDataset" />
												<s:param name="curMetadata" value="#curMetadata" />
											</s:include>
						</span>
		     		</c:when>
		     		<c:when test="${curMetadata.datatypeId eq 2}"><%-- Audio --%>
		     			<span class="comment"><s:property value="#curMetadata.name"/> : Audio type is not supported yet</span>
		     		</c:when>
		     		<c:when test="${curMetadata.datatypeId eq 3}"><%-- Video --%>
		     			<span class="comment"><s:property value="#curMetadata.name"/> : Video type is not supported yet</span>
		     		</c:when>
		     		<c:when test="${curMetadata.datatypeId eq 4}"><%-- Image --%>
			     			<span id="readonly.metadata.<s:property value="#curMetadata.metadataId"/>"	class="metadaText" >
								<s:include value="/secure/workzone/elementcontents/metadatatypes/workzone.elementcontents.4_Image.jsp">
												<s:param name="curDataset" value="#curDataset" />
												<s:param name="curMetadata" value="#curMetadata" />
											</s:include>
							</span>
					      	<span  id="edit.metadata.<s:property value="#curMetadata.metadataId"/>" 
								style="display:none"
								class="matadataTextEdit" >
									<s:include value="/secure/workzone/elementcontents/metadatatypes/workzone.elementcontents.4_Image.Edit.jsp" >
												<s:param name="curDataset" value="#curDataset" />
												<s:param name="curMetadata" value="#curMetadata" />
											</s:include>
							</span>
		     		</c:when>
		     		<c:when test="${curMetadata.datatypeId eq 5}"><%-- LongText --%>
		     			<span id="readonly.metadata.<s:property value="#curMetadata.metadataId"/>"	class="metadaText" >
							<s:include value="/secure/workzone/elementcontents/metadatatypes/workzone.elementcontents.5_LongText.jsp" >
											<s:param name="curDataset" value="#curDataset" />
											<s:param name="curMetadata" value="#curMetadata" />
										</s:include>
						</span>
				      	<span  id="edit.metadata.<s:property value="#curMetadata.metadataId"/>" 
							style="display:none"
							class="matadataTextEdit" >
								<s:include value="/secure/workzone/elementcontents/metadatatypes/workzone.elementcontents.5_LongText.Edit.jsp" >
											<s:param name="curDataset" value="#curDataset" />
											<s:param name="curMetadata" value="#curMetadata" />
										</s:include>
							</span>
		     		</c:when>
		     		<c:when test="${curMetadata.datatypeId eq 6}"><%-- Number --%>
		     			<span id="readonly.metadata.<s:property value="#curMetadata.metadataId"/>"	class="metadaText" >
							<s:include value="/secure/workzone/elementcontents/metadatatypes/workzone.elementcontents.6_Number.jsp" >
											<s:param name="curDataset" value="#curDataset" />
											<s:param name="curMetadata" value="#curMetadata" />
										</s:include>
						</span>
				      	<span  id="edit.metadata.<s:property value="#curMetadata.metadataId"/>" 
							style="display:none"
							class="matadataTextEdit" >
								<s:include value="/secure/workzone/elementcontents/metadatatypes/workzone.elementcontents.6_Number.Edit.jsp" >
											<s:param name="curDataset" value="#curDataset" />
											<s:param name="curMetadata" value="#curMetadata" />
										</s:include>
							</span>
		     		</c:when>
		     		<c:when test="${curMetadata.datatypeId eq 7}"><%-- Date --%>
		     			<span class="comment"><s:property value="#curMetadata.name"/> : Date type is not supported yet</span>
		     		</c:when>
		     		<c:when test="${curMetadata.datatypeId eq 8}"><%-- Period --%>
		     			<span class="comment"><s:property value="#curMetadata.name"/> : Period type is not supported yet</span>
		     		</c:when>
		     		<c:when test="${curMetadata.datatypeId eq 9}"><%-- Keywords --%>
		     			<span class="comment"><s:property value="#curMetadata.name"/> : Keywords type is not supported yet</span>
		     		</c:when>
		     		<c:when test="${curMetadata.datatypeId eq 10}"><%-- Tiny Text --%>
		     			<span id="readonly.metadata.<s:property value="#curMetadata.metadataId"/>"	class="metadaText" >
							<s:include value="/secure/workzone/elementcontents/metadatatypes/workzone.elementcontents.10_TinyText.jsp" >
											<s:param name="curDataset" value="#curDataset" />
											<s:param name="curMetadata" value="#curMetadata" />
										</s:include>
						</span>
			      		<span  id="edit.metadata.<s:property value="#curMetadata.metadataId"/>" 
							style="display:none"
							class="matadataTextEdit" >
								<s:include value="/secure/workzone/elementcontents/metadatatypes/workzone.elementcontents.10_TinyText.Edit.jsp" >
											<s:param name="curDataset" value="#curDataset" />
											<s:param name="curMetadata" value="#curMetadata" />
								</s:include>
						</span>
		     		</c:when>
		     		<c:otherwise>
		     			<span class="comment">Unknown type <s:property value="#curMetadata.datatypeId"/> for Metadata '<s:property value="#curMetadata.name"/>'.</span>
		     		</c:otherwise>	
		    		</c:choose>	
		   		</span>
		   	  </form>
		   	</div>
		  		
			    
