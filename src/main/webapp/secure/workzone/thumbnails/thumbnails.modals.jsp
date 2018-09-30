<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

		<form id="workzone.createElement.form" action="<c:url value="/addElementDataProcess" />" method="post" >
			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
				
				<!-- Create Element Modal window -->
			<div class="modal_shadow" id="workzone.createElement.form.modal" ><div class="modal_back">
			<fieldset class="modal" style="width:400px;">
				<legend>			
		       		<a href="#close" title="Close" class="modalclose" 
		       		onclick="document.getElementById('workzone.createElement.form.modal').style.display='none';">X</a>
		       	</legend>	
		   		<h4 class="negative" >
		   			<s:text name="workzone.createnewelement" /> <s:property value="selectedCommunity.vocabulary.elementTraduction" />
		   		</h4>
		   		
		   		<table >
			   		<tr>
			   			<td><s:text name="workzone.createnewelement.enterName" /></td>
				   		<td>
				   			<input type="text" name="newElementName" id="workzone.createElement.form.newElementName"
				   				   onkeypress="event.stopPropagation();" onkeydown="event.stopPropagation();"/>
		   				</td>
			   		</tr>
		   			<tr>
			   			<td><s:text name="workzone.createnewelement.enterComment" /></td>
				   		<td>
				   			<input type="text" name="newElementComment" id="workzone.createElement.form.newElementComment"
				   				   onkeypress="event.stopPropagation();" onkeydown="event.stopPropagation();"/>
		   				</td>
			   		</tr>
		   			<tr>
				   		<td><span class="fieldtitle" 
				   			>
				   			<s:text name="workzone.createnewelement.catalogs" /> <s:property value="selectedCommunity.vocabulary.catalogsTraduction" /></span>
						</td>
						<td>						
				   			<select name="newElementCatalogs" multiple="multiple" style="min-width:150px;border-radius:5px">				   			
				   				<option value="0" <c:if test="${selectedCommunity.selectedCatalog.virtual}" >selected</c:if> >- none -</option>
				   				<s:iterator value="selectedCommunity.catalogs" var="curCatalog">
				   					<c:if test="${not curCatalog.virtual}" >
				   						<option value="<s:property value="#curCatalog.catalogId"/>" 
				   							<c:if test="${curCatalog.catalogId eq selectedCatalog.catalogId}" >
				   								selected
				   							</c:if>
				   						><s:property value="#curCatalog.name"/></option>
				   					</c:if>				   				
				   				</s:iterator>
				   			</select>
				   		</td>
	   				</tr>
	   			</table>
		   		<br/><br/><br/>
		   		<a href="#" onclick="document.getElementById('workzone.createElement.form').submit()" ><span class="bigchoice"><s:text name="global.submit" /></span></a>
	    	
	    	</fieldset></div></div>
		</form>
			
		<!-- Modal window for Delete Element -->
		<form id="workzone.deleteElement.form" action="<c:url value="/deleteElementDataProcess" />" method="post" >
			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<input type="hidden"  id="workzone.deleteElement.form.formElementId" name="formElementId" value=""/>
				<!-- Create Element Modal window -->
			<div class="modal_shadow" id="workzone.deleteElement.form.modal" ><div class="modal_back">
			<fieldset class="modal" style="width:400px">
				<legend>			
		       		<a href="#close" title="Close" class="modalclose" 
		       		onclick="document.getElementById('workzone.deleteElement.form.modal').style.display='none';">X</a>
		       	</legend>	
		   		<h4 class="negative" >
		   			<s:text name="workzone.deleteelement" /> <s:property value="selectedCommunity.vocabulary.elementTraduction" /> 
		   		</h4>
		   		<center>
		   		<table>
			   		<tr><td><span class="fieldtitle">ID</span></td><td><span id="workzone.deleteElement.form.displayElementId"></span></td></tr>
			   		<tr><td><span class="fieldtitle"><s:text name="workzone.element.name" /></span></td><td><span id="workzone.deleteElement.form.elementname"></span></td></tr>
			   		<tr><td><span class="fieldtitle"><s:text name="workzone.element.comment" /></span></td><td><span id="workzone.deleteElement.form.elementcomment"></span></td></tr>
		   		</table>
		   		</center>
		   		<br/>
		   		<a href="#" onclick="document.getElementById('workzone.deleteElement.form').submit()" ><span class="bigchoice"><s:text name="global.submit" /></span></a>
	    	
	    	</fieldset></div></div>
		</form>	

		<!-- Modal window for add static element -->
		<form id="workzone.addStaticElement.form" action="<c:url value="/addStaticElementToCatalogProcess" />" method="post" >
			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<input id="workzone.addStaticElement.form.elementId" type="hidden" name="formElementToAddId" value="" />	
				<!-- Add Static Element Modal window -->
			<div class="modal_shadow" id="workzone.addStaticElement.form.modal" ><div class="modal_back">
			<fieldset class="modal" style="width:400px">
				<legend>			
		       		<a href="#close" title="Close" class="modalclose" 
		       		onclick="document.getElementById('workzone.addStaticElement.form.modal').style.display='none';">X</a>
		       	</legend>	
		   		<h4 class="negative" >
		   			<s:text name="workzone.addstaticelement" /> <s:property value="selectedCommunity.vocabulary.elementTraduction" /> "<span id="workzone.addStaticElement.elementName"></span>"
		   		</h4>
		   		
		   		<table >
			   		<tr>
			   			<td><s:text name="workzone.addstaticelement.chooseCatalogs" /> <s:property value="selectedCommunity.vocabulary.elementsTraduction" /></td>
				   		<td>					
				   			<select name="formElementStaticCatalogs" multiple="multiple" style="min-width:150px;border-radius:5px">				   			
				   				
				   				<s:iterator value="selectedCommunity.catalogs" var="curCatalog">
				   					<c:if test="${not curCatalog.virtual}" >
				   						<option value="<s:property value="#curCatalog.catalogId"/>" 
				   							<c:if test="${curCatalog.catalogId eq selectedCatalog.catalogId}" >
				   								selected
				   							</c:if>
				   						><s:property value="#curCatalog.name"/></option>
				   					</c:if>				   				
				   				</s:iterator>
				   			</select>
				   		</td>
	   				</tr>
	   			</table>
		   		<br/><br/><br/>
		   		<a href="#" onclick="document.getElementById('workzone.addStaticElement.form').submit()" ><span class="bigchoice"><s:text name="global.submit" /></span></a>
	    	
	    	</fieldset></div></div>
		</form>

		<!-- Modal window for add static element to catalog-->
		<form id="workzone.addStaticElementToCatalog.form" action="<c:url value="/addStaticElementToCatalogProcess" />" method="post" >
			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<input id="workzone.addStaticElementToCatalog.form.elementId" type="hidden" name="formElementToAddId" value="" />	
			<input id="workzone.addStaticElementToCatalog.form.catalogId" type="hidden" name="formTargetCatalogId" value="" />
				<!-- Add Static Element Modal window -->
			<div class="modal_shadow" id="workzone.addStaticElementToCatalog.form.modal" ><div class="modal_back">
			<fieldset class="modal" style="width:400px">
				<legend>			
		       		<a href="#close" title="Close" class="modalclose" 
		       		onclick="document.getElementById('workzone.addStaticElementToCatalog.form.modal').style.display='none';">X</a>
		       	</legend>	
		   		<h4 class="negative" >
		   			<s:text name="workzone.addstaticelement" /> <s:property value="selectedCommunity.vocabulary.elementTraduction" /> '<span id="workzone.addStaticElementToCatalog.elementName"></span>'
		   			<br/><s:text name="workzone.addstaticelement.target" /> : <s:property value="selectedCommunity.vocabulary.capCatalogTraduction" /> '<span id="workzone.addStaticElementToCatalog.catalogName"></span>'
		   		</h4>
		   		
		   		<br/><br/><br/>
		   		<a href="#" onclick="document.getElementById('workzone.addStaticElementToCatalog.form').submit()" ><span class="bigchoice"><s:text name="global.submit" /></span></a>
	    	
	    	</fieldset></div></div>
		</form>
				

		<!-- Modal window for add static element to catalog-->
		<form id="workzone.addStaticElementsToCatalog.form" action="<c:url value="/addStaticElementToCatalogProcess" />" method="post" >
			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<input id="workzone.addStaticElementsToCatalog.form.elementsIds" type="hidden" name="formElementsToAdd" value="" />	
			<input id="workzone.addStaticElementsToCatalog.form.catalogId" type="hidden" name="formTargetCatalogId" value="" />
				<!-- Add Static Element Modal window -->
			<div class="modal_shadow" id="workzone.addStaticElementsToCatalog.form.modal" ><div class="modal_back">
			<fieldset class="modal" style="width:400px">
				<legend>			
		       		<a href="#close" title="Close" class="modalclose" 
		       		onclick="document.getElementById('workzone.addStaticElementsToCatalog.form.modal').style.display='none';">X</a>
		       	</legend>	
		   		<h4 class="negative" >
		   			<s:text name="workzone.addstaticelement" /> <span id="workzone.addStaticElementsToCatalog.nbElements"></span> <s:property value="selectedCommunity.vocabulary.elementsTraduction" />
		   			<br/><s:text name="workzone.addstaticelement.target" /> : <s:property value="selectedCommunity.vocabulary.capCatalogTraduction" /> '<span id="workzone.addStaticElementsToCatalog.catalogName"></span>'
		   		</h4>
		   		
		   		<br/><br/><br/>
		   		<a href="#" onclick="document.getElementById('workzone.addStaticElementsToCatalog.form').submit()" ><span class="bigchoice"><s:text name="global.submit" /></span></a>
	    	
	    	</fieldset></div></div>
		</form>
				
		<!-- Modal window for Delete Element -->
		<form id="workzone.deleteElement.form" action="<c:url value="/deleteElementDataProcess" />" method="post" >
			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<input type="hidden"  id="workzone.deleteElement.form.formElementId" name="formElementId" value=""/>
				<!-- Create Element Modal window -->
			<div class="modal_shadow" id="workzone.deleteElement.form.modal" ><div class="modal_back">
			<fieldset class="modal" style="width:400px">
				<legend>			
		       		<a href="#close" title="Close" class="modalclose" 
		       		onclick="document.getElementById('workzone.deleteElement.form.modal').style.display='none';">X</a>
		       	</legend>	
		   		<h4 class="negative" >
		   			<s:text name="workzone.deleteelement" /> <s:property value="selectedCommunity.vocabulary.elementTraduction" /> 
		   		</h4>
		   		<center>
		   		<table>
			   		<tr><td><span class="fieldtitle">ID</span></td><td><span id="workzone.deleteElement.form.displayElementId"></span></td></tr>
			   		<tr><td><span class="fieldtitle"><s:text name="workzone.element.name" /></span></td><td><span id="workzone.deleteElement.form.elementname"></span></td></tr>
			   		<tr><td><span class="fieldtitle"><s:text name="workzone.element.comment" /></span></td><td><span id="workzone.deleteElement.form.elementcomment"></span></td></tr>
		   		</table>
		   		</center>
		   		<br/>
		   		<a href="#" onclick="document.getElementById('workzone.deleteElement.form').submit()" ><span class="bigchoice"><s:text name="global.submit" /></span></a>
	    	
	    	</fieldset></div></div>
		</form>	

		<!-- Modal window for Delete Element -->
		<form id="workzone.deleteElements.form" action="<c:url value="/deleteElementDataProcess" />" method="post" >
			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<input type="hidden"  id="workzone.deleteElements.form.elementsIds" name="formElementsIds" value=""/>
				<!-- Create Element Modal window -->
			<div class="modal_shadow" id="workzone.deleteElements.form.modal" ><div class="modal_back">
			<fieldset class="modal" style="width:400px">
				<legend>			
		       		<a href="#close" title="Close" class="modalclose" 
		       		onclick="document.getElementById('workzone.deleteElements.form.modal').style.display='none';">X</a>
		       	</legend>	
		   		<h4 class="negative" >
		   			<s:text name="workzone.deleteelement" />  <span id="workzone.deleteElements.nbElems"></span>  <s:property value="selectedCommunity.vocabulary.elementsTraduction" /> ? 
		   		</h4>
		   		
		   		<br/>
		   		<a href="#" onclick="document.getElementById('workzone.deleteElements.form').submit()" ><span class="bigchoice"><s:text name="global.submit" /></span></a>
	    	
	    	</fieldset></div></div>
		</form>	
		<!-- Modal window for remove static element from catalog-->
		<form id="workzone.removeStaticElement.form" action="<c:url value="/removeStaticElementToCatalogProcess" />" method="post" >
			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<input id="workzone.removeStaticElement.form.elementId" type="hidden" name="formElementToRemoveId" value="" />	
			<input id="workzone.removeStaticElement.form.catalogId" type="hidden" name="formCatalogId" value="" />
				<!-- Add Static Element Modal window -->
			<div class="modal_shadow" id="workzone.removeStaticElement.form.modal" ><div class="modal_back">
			<fieldset class="modal" style="width:400px">
				<legend>			
		       		<a href="#close" title="Close" class="modalclose" 
		       		onclick="document.getElementById('workzone.removeStaticElement.form.modal').style.display='none';">X</a>
		       	</legend>	
		   		<h4 class="negative" >
		   			<s:text name="workzone.removestaticelement" /> <s:property value="selectedCommunity.vocabulary.elementTraduction" /> "<span id="workzone.removeStaticElement.elementName"></span>" <s:property value="selectedCommunity.vocabulary.catalogTraduction" /> "<span id="workzone.removeStaticElement.catalogName"></span>"
		   		</h4>
		   				   		
		   		<br/><br/><br/>
		   		<a href="#" onclick="document.getElementById('workzone.removeStaticElement.form').submit()" ><span class="bigchoice"><s:text name="global.submit" /></span></a>
	    	
	    	</fieldset></div></div>
		</form>

		<!-- Modal window for remove static elements from catalog-->
		<form id="workzone.removeStaticElements.form" action="<c:url value="/removeStaticElementToCatalogProcess" />" method="post" >
			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<input id="workzone.removeStaticElements.form.elementsIds" type="hidden" name="formElementsToRemoveIds" value="" />	
			<input id="workzone.removeStaticElements.form.catalogId" type="hidden" name="formCatalogId" value="" />
				<!-- Modal window -->
			<div class="modal_shadow" id="workzone.removeStaticElements.form.modal" ><div class="modal_back">
			<fieldset class="modal" style="width:400px">
				<legend>			
		       		<a href="#close" title="Close" class="modalclose" 
		       		onclick="document.getElementById('workzone.removeStaticElements.form.modal').style.display='none';">X</a>
		       	</legend>	
		   		<h4 class="negative" >
		   			<s:text name="workzone.removestaticelement" /> <span id="workzone.removeStaticElements.nbElems"></span> <s:property value="selectedCommunity.vocabulary.elementsTraduction" /> "
		   			<br/> 
		   			<s:text name="workzone.addstaticelement.target" /> : <span id="workzone.removeStaticElements.catalogName"></span>
		   		</h4>
		   				   		
		   		<br/><br/><br/>
		   		<a href="#" onclick="document.getElementById('workzone.removeStaticElements.form').submit()" ><span class="bigchoice"><s:text name="global.submit" /></span></a>
	    	
	    	</fieldset></div></div>
		</form>
