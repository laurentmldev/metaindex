<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>



	    	    		<fieldset  id="content.dataset.<s:property value="#curDataset.datasetId"/>" 
	    	    			style="margin-top:20px;font-size:1em;"	  
	    	    			class="datasetcontent<c:if test="${curDataset.readOnly}" >RO</c:if> <c:if test="${curDataset.layoutDoDisplayName == false}" >hiddenfieldset</c:if>"
	    			
	    	    			onclick="if (editModeActive) { event.stopPropagation(); switchSelected_dataset_<s:property value="#curDataset.datasetId" />(); }"
	    	    			onmouseover="if (editModeActive) { 
	    	    							if (selected_dataset_<s:property value="#curDataset.datasetId" />==false) { 
	    	    								document.getElementById('selector.dataset.<s:property value="#curDataset.datasetId" />').classList.add('editModeDatasetMouseOverMenu');
	    	    								document.getElementById('content.dataset.<s:property value="#curDataset.datasetId" />').classList.add('editModeDatasetMouseOver<c:if test="${curDataset.readOnly}" >RO</c:if>');
	    	    						 	} 
	    	    						 	document.getElementById('workzone.elementcontents.icon.delete.dataset.<s:property value="#curDataset.datasetId"/>').style.display='block';
		    	    						 <c:if test="${curDataset.layoutDoDisplayName == false}" >
		    	    							document.getElementById('content.dataset.<s:property value="#curDataset.datasetId"/>').classList.remove('hiddenfieldset');
		    	    						</c:if>
		    	    					}"
   						    onmouseout="if (editModeActive) { 
   						    				if (selected_dataset_<s:property value="#curDataset.datasetId" />==false) { 
   						    					document.getElementById('selector.dataset.<s:property value="#curDataset.datasetId" />').classList.remove('editModeDatasetMouseOverMenu');
   						    					document.getElementById('content.dataset.<s:property value="#curDataset.datasetId" />').classList.remove('editModeDatasetMouseOver<c:if test="${curDataset.readOnly}" >RO</c:if>');
   						    			 	}
	   						    			 document.getElementById('workzone.elementcontents.icon.delete.dataset.<s:property value="#curDataset.datasetId"/>').style.display='none';
	   						    			 <c:if test="${curDataset.layoutDoDisplayName == false}" >
		    	    							document.getElementById('content.dataset.<s:property value="#curDataset.datasetId"/>').classList.add('hiddenfieldset');
		    	    						</c:if>
		    	    					}"
 						    draggable="false"
							ondragstart="event.stopPropagation();if (editModeActive && !<s:property value="#curDataset.readOnly"/>) { handleDragStartDataset(<s:property value="#curDataset.datasetId" />,document.getElementById('content.dataset.<s:property value="#curDataset.datasetId"/>'),event); }"
							ondragend="event.stopPropagation();handleDragEndDataset(document.getElementById('content.dataset.<s:property value="#curDataset.datasetId"/>'),event);"
	
	    	    		>
	    	    				<legend >
	    	    					<table><tr>
	    	    					<td><span class="fieldsetTitle" title="<s:property value="#curDataset.comment"/>" ><s:property value="#curDataset.name"/></span></td>
	    	    					<td>
	    	    						<c:if test="${not curDataset.readOnly}" >
	    	    							<a  href="#"  id="workzone.elementcontents.icon.delete.dataset.<s:property value="#curDataset.datasetId"/>"
									 				title="<s:text name="workzone.icon.deleteDataset" />" class="smallicon icon_deleteElement" style="display:none;"  
									 				onclick="	event.stopPropagation();
									 					document.getElementById('workzone.deleteDataset.form.formDatasetId').value='<s:property value="#curDataset.datasetId"/>';
						 							
						 							document.getElementById('workzone.deleteDataset.form.datasetName').innerHTML='<s:property value="name"/>';
						 							document.getElementById('workzone.deleteDataset.form.datasetComment').innerHTML='<s:property value="comment"/>';									 							
						 							document.getElementById('workzone.deleteDataset.form.modal').style.display='table';">
									 		</a>
								 		</c:if>

	    	    					</td>	
									</tr></table>							 													
	    	    				</legend>
	    	    		
	    	    		<c:if test="${curDataset.nbMetadata eq 0}">
			    			<h3><span class="negative"><s:text name="workzone.empty" ></s:text></span></h3>
			    		</c:if>
	    	    	<table style="width:100%;"><tr>
	    	    		<%--For each column of the dataset--%> 
			    	   <s:iterator value="#curDataset.columnsMetadata" var="columnsData" status="itStatus"  > 			    	    		
			    	   	 <td >
<center>
			    	   	 	<c:if test="${curDataset.readOnly}">&nbsp;&nbsp;&nbsp;</c:if>
			    	   		<c:if test="${!curDataset.readOnly}">										    	  
			    	   	 		<s:include value="/secure/subparts/workzone.elementcontents.dropzone.metadata.jsp" >
			    								<s:param name="dropDatasetId" value="#curDataset.datasetId" />
			    								<s:param name="dropColumn" value="#itStatus.count" />
			    								<s:param name="dropPosition" value="1" />			    											    								
   								</s:include>
							</c:if>
			        		<s:iterator value="#columnsData" var="curMetadata" >				        		 
	   						  	<s:include value="/secure/subparts/workzone.elementcontents.metadata.jsp" >
			    								<s:param name="curDataset" value="#curDataset" />
			    								<s:param name="curMetadata" value="#curMetadata" />			    											    											    						
   								</s:include>
				        		<center>	
				        		<c:if test="${!curDataset.readOnly}">		        				
									<s:include value="/secure/subparts/workzone.elementcontents.dropzone.metadata.jsp" >
					    								<s:param name="dropDatasetId" value="#curDataset.datasetId" />
					    								<s:param name="dropColumn" value="#curMetadata.layoutColumn" />
					    								<s:param name="dropPosition" value="#curMetadata.layoutPosition + 1" />			    											    								
		   							</s:include>	        		
		   						</c:if>	
		   						<c:if test="${curDataset.readOnly}">&nbsp;</c:if>					        		
				        		</center>
			        	  	</s:iterator><%-- For each metadata of current column --%>
			        	  	 </center>
			        	</td>  
			        	
			        	</s:iterator><%-- For each column --%>
			        	
			        	</tr></table>
		       		
		       		</fieldset>
		       		
		       			       			    			       
			 
			    
