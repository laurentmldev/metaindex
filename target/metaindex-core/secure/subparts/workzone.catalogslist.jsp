<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<!-- Modal window for Delete catalog -->
		<form id="workzone.deleteCatalog.form" action="<c:url value="/deleteCatalogProcess" />" method="post" >
			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<input type="hidden"  id="workzone.deleteCatalog.form.formCatalogId" name="formCatalogId" value=""/>
				<!-- Create Element Modal window -->
			<div class="modal_shadow" id="workzone.deleteCatalog.form.modal" ><div class="modal_back">
			<fieldset class="modal" style="width:400px">
				<legend>			
		       		<a href="#close" title="Close" class="modalclose" 
		       		onclick="document.getElementById('workzone.deleteCatalog.form.modal').style.display='none';">X</a>
		       	</legend>	
		   		<h4 class="negative" >
		   			<s:text name="workzone.deletecatalog" /> <s:property value="selectedCommunity.vocabulary.catalogTraduction" /> 
		   		</h4>
		   		<center>
		   		<table>
			   		<tr><td><span class="fieldtitle"><s:text name="workzone.element.name" /></span></td><td><span id="workzone.deleteCatalog.form.catalogName"></span></td></tr>
			   		<tr><td><span class="fieldtitle"><s:text name="workzone.element.comment" /></span></td><td><span id="workzone.deleteCatalog.form.catalogComment"></span></td></tr>
		   		</table>
		   		</center>
		   		<br/>
		   		<a href="#" onclick="document.getElementById('workzone.deleteCatalog.form').submit()" ><span class="bigchoice"><s:text name="global.submit" /></span></a>
	    	
	    	</fieldset></div></div>
		</form>	
		
<div id="workzone.catalogslist" >

		<!-- Catalogs List -->
	    <fieldset class="catalogslist" >
	    	<legend><span class="fieldsetTitle" ><s:text name="workzone.catalogsList" /></span></legend>
	    	
			    <form id="workzone.addCatalog.form" action="<c:url value="/addCatalogProcess" />" method="post" >    
	    	<span class="dynamicCatalog modifyable">
	    		
	    			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
	        		
	    		<span  onclick="event.stopPropagation();this.style.display='none'; 
	    						document.getElementById('workzone.addCatalog.form.catalogName.edit').style.display='block';">
	    			<s:text name="workzone.icon.createElement" /> <s:property value="selectedCommunity.vocabulary.catalogTraduction" />
	    		</span>
	    		
	    		<input id="workzone.addCatalog.form.catalogName.edit" type="text"
	    										name="newCatalogName"
												style="display:none;"  
												placeholder="<s:text name="workzone.createnewelement.enterName"/>"
												onchange="event.stopPropagation();
															document.getElementById('workzone.addCatalog.form').submit();" 
											/>
				
	    	</span>
	    	</form>
	    	<form id="workzone.selectCatalog.form" action="<c:url value="/selectCatalogProcess" />" method="post" >
	    			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
	        		<input type="hidden"  name="formSelectedCatalogId" id="workzone.selectCatalogs.form.catalogId" value=""/>
	        			    	
       		<table>	    		    	
	    	    <s:iterator value="selectedCommunity.catalogs" var="curCatalog">
			        <tr><td
			        	<%-- if current catalog is the selected catalog then we highlight it --%>
			        	class="<c:if test="${curCatalog.catalogId eq selectedCatalog.catalogId}">selectedItem</c:if>" 
			        	
			        	<%-- Virtual catalogs don't have static elements by definition, so no drop allowed on them --%>
			        	<c:if test="${!curCatalog.virtual}">
						onmouseover="document.getElementById('workzone.catalogsList.icon.deleteElement.<s:property value="#curCatalog.catalogId"/>').style.display='block';"
			        	onmouseout="document.getElementById('workzone.catalogsList.icon.deleteElement.<s:property value="#curCatalog.catalogId"/>').style.display='none';"
			        		ondragover="handleDragOverCatalog(this,event);"
							ondragenter="handleDragEnterCatalog(this,event);"
							ondragleave="handleDragLeaveCatalog(this,event);"
							ondrop="event.stopPropagation();event.preventDefault();handleDropCatalog(<s:property value="#curCatalog.catalogId"/>,'<s:property value="#curCatalog.name"/>',this,event);"
						</c:if>	
			        >
			        	<table>	<tr>	
						<td >
			        	<span  
			        			id="<c:if test="${curCatalog.catalogId eq selectedCatalog.catalogId}">workzone.catalogdetails.catalogname.readonly</c:if>"  
								onclick="<c:if test="${!selectedCatalog.virtual and curCatalog.catalogId eq selectedCatalog.catalogId}" >
														event.stopPropagation();
														document.getElementById('workzone.catalogdetails.catalogname.selector').style.display='none';
														document.getElementById('workzone.catalogdetails.catalogname.nbelems').style.display='none';
														document.getElementById('workzone.catalogdetails.catalogname.edit').style.display='block';														
										</c:if> "
										
										
									>
										
										
									<a href="#selectCatalog"  
										id="<c:if test="${curCatalog.catalogId eq selectedCatalog.catalogId}">workzone.catalogdetails.catalogname.selector</c:if>"									
										onclick="<c:if test="${curCatalog.catalogId ne selectedCatalog.catalogId}">	
															event.stopPropagation();							
															document.getElementById('workzone.selectCatalogs.form.catalogId').setAttribute('value','<s:property value="#curCatalog.catalogId"/>');
															document.getElementById('workzone.selectCatalog.form').submit();
												</c:if>"
															
										class="<c:if test="${!curCatalog.virtual}">modifyable</c:if>
												<c:if test="${curCatalog.dynamic}">dynamicCatalog</c:if>
												<c:if test="${curCatalog.catalogId eq '-2'}">templateCatalogInCatalogsList</c:if>
												"
										 																									
																	
									>
										<s:property value="name" />						
									</a>
									<c:if test="${curCatalog.catalogId eq selectedCatalog.catalogId}">
											<input id="workzone.catalogdetails.catalogname.edit" type="text"
												style="display:none;"  
												value="<s:property value="name"/>"
												onchange="event.stopPropagation();
															document.getElementById('workzone.modifycatalog.form.formCatalogName').value=this.value;
															<%-- This is a workaround because the form 'workzone.selectCatalog.form' is also automatically invoked
																 when the user press ENTER key, so we ask to switch to the same catalog so that's there is no apparent effect ...--%>
															document.getElementById('workzone.selectCatalogs.form.catalogId').value='<s:property value="#curCatalog.catalogId"/>';
															document.getElementById('workzone.modifycatalog.form').submit();"
												onfocus="this.select();" 
												onblur="this.style.display='none';														
														document.getElementById('workzone.catalogdetails.catalogname.selector').style.display='block';
														document.getElementById('workzone.catalogdetails.catalogname.nbelems').style.display='block';" 
											/>
									</c:if>	
									<span id="<c:if test="${curCatalog.catalogId eq selectedCatalog.catalogId}">workzone.catalogdetails.catalogname.nbelems</c:if>" class="comment">(<s:property value="#curCatalog.elementsCount"/>)</span>
						
						
						</span></td>
						<td><c:if test="${!curCatalog.virtual}">
						<a  href="#"  id="workzone.catalogsList.icon.deleteElement.<s:property value="#curCatalog.catalogId"/>"
			 				title="<s:text name="workzone.icon.deleteElement" /> <s:property value="selectedCommunity.vocabulary.catalogTraduction" />" 
			 				class="tinyicon icon_deleteElement" style="display:none;"  
			 				onclick="	event.stopPropagation();
			 					document.getElementById('workzone.deleteCatalog.form.formCatalogId').value='<s:property value="#curCatalog.catalogId"/>';							
								document.getElementById('workzone.deleteCatalog.form.catalogName').innerHTML='<s:property value="name"/>';
								document.getElementById('workzone.deleteCatalog.form.catalogComment').innerHTML='<s:property value="comment"/>';									 							
								document.getElementById('workzone.deleteCatalog.form.modal').style.display='table';">
		 				</a>						
							
						</c:if></td>
						</tr></table>
					
						       		
		       		</td></tr>			    	
			    </s:iterator>
			    
	   		</table>
	   		</form>
		</fieldset>
		
		<!-- Current catalog details -->
		<fieldset id="workzone.catalogdetails"><legend><span class="fieldsetTitle" ><s:text name="workzone.catalogDetails" /></span></legend>
			<form id="workzone.modifycatalog.form" action="<c:url value="/updateCatalogProcess" />" method="post" >
	    			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
	    			<input type="hidden" id="workzone.modifycatalog.form.formCatalogName"  name="formCatalogName" value="<s:property value="selectedCatalog.name"/>" />
			<table>

				<tr><td colspan="2">
							<span id="workzone.catalogdetails.catalogcomment.readonly"
								class="<c:if test="${!selectedCatalog.virtual}" >modifyable</c:if>"  
								onclick="<c:if test="${!selectedCatalog.virtual}" >this.style.display='none';
												document.getElementById('workzone.catalogdetails.catalogcomment.edit').style.display='block';</c:if> "								
								>
								<s:property value="selectedCatalog.comment" />
								<c:if test="${selectedCatalog.comment==''}"><span class="emptyModifyable"><s:text name="workzone.clickToAdd" /> <s:text name="workzone.createnewelement.enterComment" /></span></c:if>
							</span>
					
						<input id="workzone.catalogdetails.catalogcomment.edit" type="text"
							style="display:none;"  
							 name="formCatalogComment" 
							 value="<s:property value="selectedCatalog.comment"/>" 							
							placeholder="<s:text name="catalog.comment"/>"							
							onchange="document.getElementById('workzone.modifycatalog.form').submit();" />
					
					
				</td></tr>
				<tr><td colspan="2"><c:if test="${selectedCatalog.searchQuery!=''}"><s:text name="workzone.catalog.searchQuery"/>:</c:if>
				
					<span id="workzone.catalogdetails.catalogsearchquery.readonly"  
								class="<c:if test="${!selectedCatalog.virtual or selectedCatalog.catalogId eq -1}" >modifyable</c:if>"
								onclick="<c:if test="${!selectedCatalog.virtual or selectedCatalog.catalogId eq -1}" >this.style.display='none';
												document.getElementById('workzone.catalogdetails.catalogsearchquery.edit').style.display='block';</c:if> "								
								>
								<s:property value="selectedCatalog.searchQuery" />
								<c:if test="${selectedCatalog.searchQuery==''}"><span class="emptyModifyable"><s:text name="workzone.clickToAdd" /> <s:text name="workzone.catalog.searchQuery" /></span></c:if>
							</span>
					
						<input id="workzone.catalogdetails.catalogsearchquery.edit" type="text"  name="formCatalogSearchQuery" value="<s:property value="selectedCatalog.searchQuery"/>" 
						style="display:none;"
							onchange="document.getElementById('workzone.modifycatalog.form').submit();" />
							
							
														
				</td></tr>
				<tr><td colspan="2"><hr/></td></tr>
				<tr><td colspan="2">
					<table>
						<tr><td><s:text name="workzone.catalog.static"/>:</td><td><s:property value="selectedCatalog.nbStaticElements"/> <s:property value="selectedCommunity.vocabulary.elementsTraduction"/></td></tr>
						<tr><td><s:text name="workzone.catalog.dynamic"/>:</td><td><s:property value="selectedCatalog.nbDynamicElements"/> <s:property value="selectedCommunity.vocabulary.elementsTraduction"/></td></tr>
					</table>
				</td></tr>										
			</table>
			</form>
		</fieldset>
		
		
	</div>
		
