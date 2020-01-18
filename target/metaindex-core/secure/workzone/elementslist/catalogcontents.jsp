<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

	


<center>


<!-- Current catalog elements list container -->
 <fieldset id="workzone.catalogcontents" class="menushadowcard" style="padding-bottom:20px"
 
 	onclick="clearMultiSelection();" >
 
 		<legend>
 		<table><tr>
 		<td>
 				<a  href="#" id="workzone.catalogcontents.icon.createNewElement" 
 				title="<s:text name="workzone.icon.createElement" /> <s:property value="selectedCommunity.vocabulary.elementTraduction" />" 
 				class="icon icon_createElement" 
 				onclick="event.stopPropagation();document.getElementById('workzone.createElement.form.modal').style.display='table';"></a>	
 				
		</td>
   		<td>			
   				<!-- Maximize -->
   				<a  href="#" id="workzone.catalogcontents.icon.maximize" 
 				title="<s:text name="workzone.icon.maximize" />" 
 				class="icon icon_gridview" 
 				onclick="event.stopPropagation();
 					document.getElementById('workzone.catalogcontents.container').classList.remove('slideItemsContainer');
					document.getElementById('workzone.catalogcontents.container').classList.add('slideItemsBigContainer');
					document.getElementById('downside').classList.remove('slideItemsSmallSizeContainer');
					document.getElementById('downside').classList.add('slideItemsBigSizeContainer');
					document.getElementById('workzone.catalogcontents.icon.maximize').style.display='none';
					document.getElementById('workzone.catalogcontents.icon.minimize').style.display='block';
	 				"></a>
	 				
	 			<!-- Minimize -->
   				<a  href="#" id="workzone.catalogcontents.icon.minimize" 
 				title="<s:text name="workzone.icon.minimize" />" 
 				class="icon icon_slideview" 
 				style="display:none"
 				onclick="event.stopPropagation();
 					document.getElementById('workzone.catalogcontents.container').classList.add('slideItemsContainer');
					document.getElementById('workzone.catalogcontents.container').classList.remove('slideItemsBigContainer');
					document.getElementById('downside').classList.add('slideItemsSmallSizeContainer');
					document.getElementById('downside').classList.remove('slideItemsBigSizeContainer');
					document.getElementById('workzone.catalogcontents.icon.maximize').style.display='block';
					document.getElementById('workzone.catalogcontents.icon.minimize').style.display='none';
	 				"></a>	

			</td>
	 		<td >
	 				  <fieldset id="workzone.catalogcontents.icon.multiselectCmds" class="multiselectCmds_fieldset" 
	 				  			style="display:none;">
	 				  	<legend class="multiselectCmds_legend"><span id="workzone.catalogcontents.icon.multiselectCmds.nbSelectedElems"></span> 
	 				  										 <s:text name="workzone.icon.selected" />
						</legend>
						<c:if test="${!selectedCatalog.virtual}">
		 					<a  href="#" id="workzone.catalogcontents.icon.removeElements" 
		 						
				 				title="<s:text name="workzone.icon.removeElements" /> <s:property value="selectedCommunity.vocabulary.catalogTraduction" />" 
				 				class="icon icon_removeElement" 			 				
				 				onclick="event.stopPropagation(); 
				 						removeMultiSelectedStaticElements('<s:property value="selectedCommunity.selectedCatalog.catalogId"/>',
				 																	   '<s:property value="selectedCommunity.selectedCatalog.name"/>');" ></a>
					   </c:if>
	 					<a  href="#" id="workzone.catalogcontents.icon.deleteElements" 
	 						 
			 				title="<s:text name="workzone.icon.deleteElements" /> <s:property value="selectedCommunity.vocabulary.elementsTraduction" />" 
			 				class="icon icon_deleteElement" 			 				
			 				onclick="event.stopPropagation();deleteMultiSelectedElements();"></a>
	 				</fieldset>
	 		</td>
	 		<td>
 				<a  href="#" id="workzone.catalogcontents.icon.deleteAll" 
 				title="<s:text name="workzone.icon.deleteAll" /> <s:property value="selectedCommunity.vocabulary.elementsTraduction" />" 
 				class="" 
 				onclick="event.stopPropagation();deleteAllElements();">
 				DeleteAll
 				</a>	 				
			</td>
	 		<td>
	 		
	    	<!-- Real Fast Search input -->	 		
 			<input type="text" name="elementsFastSearch" id="workzone.catalogcontents.elementsFastSearch" style="display:none"
	    			placeholder="<s:text name="workzone.catalog.innerSearch" />" 
	    			list="workzone.catalogcontents.elementsInnerSearch.datalist" autocomplete="on"
	    			onkeypress="if (event.which==13||event.keycode==13) {
		    				if (document.getElementById('workzone.catalogcontents.elementsFastSearch').value != '') {
	    						updateCatalogContents(document.getElementById('workzone.catalogcontents.elementsFastSearch').value);
		    				}
	    				}"
	    	>
	    	<span id="catalogcontents-loadprogress" style="width:150px; display:none"  >	    	
					<div id="catalogcontents-loadprogress-container" class="progressbar_container">
						<div id="catalogcontents-loadprogress-bar" class="progressbar"></div>
						<div id="catalogcontents-loadprogress-text" class="progressbar_text"></div>
					</div>
	    	</span>
	    			<!-- Datalist containing the data used for the search -->
   				<datalist id="workzone.catalogcontents.elementsInnerSearch.datalist">   					   					
   				</datalist> 	
   					
	 		</td>

			 		</tr></table>	
 		</legend>
 	<!-- Form for changing current element -->
		<form id="workzone.chooseelement.form" action="<c:url value="/selectElement" />" method="post" >
 			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
     		<input type="hidden"  id="workzone.chooseelement.form.elementId" name="nextElementId" value=""/>
     </form>	
			      
	<!-- Items container itself -->
	<div  id="workzone.catalogcontents.container" class="slideItemsContainer" 
		oncontextmenu="return false;"  >
	   	
	   			  
			      
			      
			      
			      <%-- Contents are dynamically filled, populated by WebSocket download from server --%>
	    		<%-- for each element of the catalog --%>
	    	    <%--s:iterator value="selectedCatalog.elements" var="curElement" status="elementsIterator">
	    	    	
	    	    		<table ><tr><td >	   
		    	    	
				        <fieldset id="workzone.catalogcontents.<s:property value="elementId"/>"
				        		<c:choose >
									<c:when test="${curElement.hasThumbnail()}">
			        					title="<s:property value="name"/>"
			      				 	 </c:when>
									<c:otherwise>
										title="<s:property value="comment"/>"  			
									</c:otherwise>
								</c:choose>
				        		class="slideItemContent        	
					        	<c:if test="${selectedCatalog.isStaticElement(curElement.elementId) eq false}" >dynamicSlideItem</c:if>			         
					        	<c:if test="${selectedCatalog.isStaticElement(curElement.elementId) eq true}" >staticSlideItem</c:if>			         
					        	<c:if test="${curElement.template}" >templateSlideItem</c:if>
					        	<c:if test="${curElement.templateLoadError}" >errorTemplateSlideItem</c:if>	        					
					        	<c:if test="${selectedCatalog.selectedElement.elementId eq curElement.elementId and not curElement.templateLoadError}">selectedSlideItem</c:if>
					        	<c:if test="${selectedCatalog.selectedElement.elementId eq curElement.elementId and curElement.templateLoadError}">selectedErrorTemplateSlideItem</c:if>
					        	<c:if test="${selectedCatalog.selectedElement.elementId eq curElement.elementId
					        					and curElement.template}">selectedTemplateSlideItem</c:if>
					        	
					        	" 	  					        	
					        	      			        	 
				        	 onclick="
				        	 		event.stopPropagation();
				        	 		if (event.shiftKey && (event.ctrlKey||event.metaKey)) {
				        	 			selectRange(lastSelectedPos,<s:property value="%{#elementsIterator.index}"/>);
				        	 		}
				        	 		else if (event.ctrlKey||event.metaKey) {				        	 						
				        	 		 switchMultiselection(<s:property value="elementId"/>);
				        	 		}
				        	 		else if (event.shiftKey) {
				        	 			selectNewRange(lastSelectedPos,<s:property value="%{#elementsIterator.index}"/>);
				        	 		}
				        	 		else { 
										document.getElementById('workzone.chooseelement.form.elementId').setAttribute('value','<s:property value="elementId"/>');
										document.getElementById('workzone.chooseelement.form').submit();
									}
									lastSelectedPos=<s:property value="%{#elementsIterator.index}"/>;"
				
							onmouseover="document.getElementById('workzone.catalogcontents.icons.<s:property value="elementId"/>').style.display='block';"
							onmouseout="document.getElementById('workzone.catalogcontents.icons.<s:property value="elementId"/>').style.display='none';"		
				    	    draggable="true"
				        	ondragstart="event.stopPropagation();handleDragStartElement('<s:property value="elementId" />','<s:property value="name" />',this,event);"
							ondragend="handleDragEndElement(this,event);"
							>
							
								<!-- this space is used here to force the height of the legend so that it does not move 
									when we mouse over -->
					 			 <legend>&nbsp;
				    	    		<span id="workzone.catalogcontents.icons.<s:property value="elementId"/>" >
								    		<a  href="#" id="workzone.catalogcontents.icon.addElement" 
								 				title="<s:text name="workzone.icon.addElement" /> <s:property value="selectedCommunity.vocabulary.catalogTraduction" />" 
								 				class="tinyicon icon_addElement" 
								 				onclick="event.stopPropagation();
								 						 document.getElementById('workzone.addStaticElement.elementName').innerHTML='<s:property value="name"/>';
								 						 document.getElementById('workzone.addStaticElement.form.elementId').value='<s:property value="elementId"/>';
								 						 document.getElementById('workzone.addStaticElement.form.modal').style.display='table';"></a>
								 						 
								 						 
								 			<%--we can only remove from catalog static elements -%>
								 			
							 				<c:if test="${selectedCatalog.isStaticElement(curElement.elementId) eq true}" >
									 			<a  href="#" id="workzone.catalogcontents.icon.removeElement.<s:property value="elementId"/>" 
									 				title="<s:text name="workzone.icon.removeElement" /> <s:property value="selectedCommunity.vocabulary.catalogTraduction" />" 
									 				class="tinyicon icon_removeElement" 
									 				onclick="event.stopPropagation();
									 				 		 document.getElementById('workzone.removeStaticElement.elementName').innerHTML='<s:property value="name"/>';
									 						 document.getElementById('workzone.removeStaticElement.form.elementId').value='<s:property value="elementId"/>';
									 						 document.getElementById('workzone.removeStaticElement.catalogName').innerHTML='<s:property value="selectedCommunity.selectedCatalog.name"/>';
									 						 document.getElementById('workzone.removeStaticElement.form.catalogId').value='<s:property value="selectedCommunity.selectedCatalog.catalogId"/>';
									 						 document.getElementById('workzone.removeStaticElement.form.modal').style.display='table';"></a>
							 				</c:if>
							 												        		
								 			<a  href="#"  
									 				title="<s:text name="workzone.icon.deleteElement" /> <s:property value="selectedCommunity.vocabulary.elementTraduction" />" 
									 				class="tinyicon icon_deleteElement" 
									 				onclick="	event.stopPropagation();
									 							document.getElementById('workzone.deleteElement.form.formElementId').value='<s:property value="elementId"/>';
									 							document.getElementById('workzone.deleteElement.form.displayElementId').innerHTML='<s:property value="elementId"/>';
									 							document.getElementById('workzone.deleteElement.form.elementname').innerHTML='<s:property value="name"/>';
									 							document.getElementById('workzone.deleteElement.form.elementcomment').innerHTML='<s:property value="comment"/>';									 							
									 							document.getElementById('workzone.deleteElement.form.modal').style.display='table';">
									 		</a>
								 							 					 				
							 		</span>
						 		</legend>    
						 		<!-- This is needed to allow a Drag start on the empty part of the item -->   	
							<div style="height:100%;">
																
									<c:choose >
										<c:when test="${curElement.hasThumbnail()}">
	
											<img src="<s:property value="thumbnailPicWebLink"/>" class="catalogThumbnail"
													title="<s:property value="name"/>"
													alt="[ <s:property value="name"/> ]"
													onclick="if (!event.shiftKey && !event.ctrlKey && !event.metaKey) {
																document.getElementById('workzone.chooseelement.form.elementId').setAttribute('value','<s:property value="elementId"/>');
															 	document.getElementById('workzone.chooseelement.form').submit();
															 }"
								
													>
													
										</c:when>
										<c:otherwise>
											<div
												style="height:80%;display:flex;align-items: center;justify-content: center;"
												class="clickable"
												title="<s:property value="comment"/>"  
												onclick="if (!event.shiftKey && !event.ctrlKey && !event.metaKey) {
															document.getElementById('workzone.chooseelement.form.elementId').setAttribute('value','<s:property value="elementId"/>');
															document.getElementById('workzone.chooseelement.form').submit();
														 }"	
													>
													<s:property value="name"/>
												</div>			
										</c:otherwise>
									</c:choose>
						
							</div>
									<span class="comment slideItemElementId"><s:property value="elementId"/></span>
									<c:if test="${curElement.templated}" >
										<span class="slideItemTemplateAnchor"><s:text name="workzone.slideitem.templateAnchorText"/></span>
									</c:if>	
			       		</fieldset>
			       		</td></tr></table>						
		       		 
		       		 
						<script>
							sorted_elements_ids.push("<s:property value="elementId"/>");
							elementsPosition[<s:property value="elementId"/>]=<s:property value="%{#elementsIterator.index}" />;							
							document.getElementById("workzone.catalogcontents.elementsInnerSearch.datalist").innerHTML+="<option value=\"<s:property value="elementId"/>\" ><s:property value="searchText"/></option>";							
						</script>
		       		 
			    </s:iterator--%>	     
	   			
	</div>	
</fieldset>


</center>

<s:include value="catalogcontents.modals.jsp" ></s:include>
<s:include value="javascript.jsp" ></s:include>
