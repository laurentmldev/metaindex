<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<s:include value="/secure/subparts/workzone.elementcontents.javascript.jsp" ></s:include>

<form id="workzone.create.metadata.form" action="<c:url value="/addMetadataProcess" />"  method="post">
	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
	<input type="hidden"  id="workzone.create.metadata.form.createInColumn" name="formColumn" value=""/>
	<input type="hidden"  id="workzone.create.metadata.form.createInPosition" name="formPosition" value=""/>
	<input type="hidden"  id="workzone.create.metadata.form.datasetId" name="formDatasetId" value=""/>
	<input type="hidden"  id="workzone.create.metadata.form.metadataName" name="formMetadataName" value=""/>
	<input type="hidden"  id="workzone.create.metadata.form.metadataComment" name="formMetadataComment" value=""/>
	<input type="hidden"  id="workzone.create.metadata.form.termId" name="formTermId" value=""/>
</form>

<form id="workzone.move.metadata.form" action="<c:url value="/moveMetadataProcess" />"  method="post">
	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
	<input type="hidden"  id="workzone.move.metadata.form.movedMetadataId" name="movedMetadataId" value=""/>
	<input type="hidden"  id="workzone.move.metadata.form.moveToColumn" name="moveToColumn" value=""/>
	<input type="hidden"  id="workzone.move.metadata.form.moveToPosition" name="moveToPosition" value=""/>
	<input type="hidden"  id="workzone.move.metadata.form.moveToDatasetId" name="moveToDatasetId" value=""/>
</form>

<form id="workzone.create.dataset.form" action="<c:url value="/addDatasetProcess" />"  method="post">
	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
	<input type="hidden"  id="workzone.create.dataset.form.createInPosition" name="formDatasetPosition" value=""/>
	<input type="hidden"  id="workzone.create.dataset.form.datasetName" name="formDatasetName" value=""/>
	<input type="hidden"  id="workzone.create.dataset.form.datasetComment" name="formDatasetComment" value=""/>
	<input type="hidden"  id="workzone.create.dataset.form.elementId" name="formDatasetElementId" value=""/>	
</form>
<form id="workzone.move.dataset.form" action="<c:url value="/moveDatasetProcess" />"  method="post">
	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
	<input type="hidden"  id="workzone.move.dataset.form.movedDatasetId" name="movedDatasetId" value=""/>	
	<input type="hidden"  id="workzone.move.dataset.form.moveToPosition" name="moveToPosition" value=""/>
</form>

<!-- Modal window for Delete Dataset -->
		<form id="workzone.deleteDataset.form" action="<c:url value="/deleteDatasetProcess" />" method="post" >
			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<input type="hidden"  id="workzone.deleteDataset.form.formDatasetId" name="formDatasetId" value=""/>
				<!-- Create Element Modal window -->
			<div class="modal_shadow" id="workzone.deleteDataset.form.modal" ><div class="modal_back">
			<fieldset class="modal" style="width:400px">
				<legend>			
		       		<a href="#close" title="Close" class="modalclose" 
		       		onclick="document.getElementById('workzone.deleteDataset.form.modal').style.display='none';">X</a>
		       	</legend>	
		   		<h4 class="negative" >
		   			<s:text name="workzone.deletedataset" /> <s:property value="selectedCommunity.vocabulary.datasetTraduction" /> 
		   		</h4>
		   		<center>
		   		<table>
			   		<tr><td><span class="fieldtitle"><s:text name="workzone.element.name" /></span></td><td><span id="workzone.deleteDataset.form.datasetName"></span></td></tr>
			   		<tr><td><span class="fieldtitle"><s:text name="workzone.element.comment" /></span></td><td><span id="workzone.deleteDataset.form.datasetComment"></span></td></tr>
		   		</table>
		   		</center>
		   		<br/>
		   		<a href="#" onclick="document.getElementById('workzone.deleteDataset.form').submit()" ><span class="bigchoice"><s:text name="global.submit" /></span></a>
	    	
	    	</fieldset></div></div>
		</form>	
    	
 <!-- Modal window for Delete Metadata -->
		<form id="workzone.deleteMetadata.form" action="<c:url value="/deleteMetadataProcess" />" method="post" >
			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<input type="hidden"  id="workzone.deleteMetadata.form.formMetadataId" name="formMetadataId" value=""/>
				<!-- Create Element Modal window -->
			<div class="modal_shadow" id="workzone.deleteMetadata.form.modal" ><div class="modal_back">
			<fieldset class="modal" style="width:400px">
				<legend>			
		       		<a href="#close" title="Close" class="modalclose" 
		       		onclick="document.getElementById('workzone.deleteMetadata.form.modal').style.display='none';">X</a>
		       	</legend>	
		   		<h4 class="negative" >
		   			<s:text name="workzone.deletemetadata" /> <s:property value="selectedCommunity.vocabulary.metadataTraduction" /> 
		   		</h4>
		   		<center>
		   		<table>
			   		<tr><td><span class="fieldtitle">Metadata ID</span></td><td><span id="workzone.deleteMetadata.form.id"></span></td></tr>
			   		<tr><td><span class="fieldtitle"><s:text name="workzone.element.name" /></span></td><td><span id="workzone.deleteMetadata.form.datasetName"></span></td></tr>
			   		<tr><td><span class="fieldtitle"><s:text name="workzone.element.comment" /></span></td><td><span id="workzone.deleteMetadata.form.datasetComment"></span></td></tr>
		   		</table>
		   		</center>
		   		<br/>
		   		<a href="#" onclick="document.getElementById('workzone.deleteMetadata.form').submit()" ><span class="bigchoice"><s:text name="global.submit" /></span></a>
	    	
	    	</fieldset></div></div>
		</form>	
    	
    	
    	<!--   -------------------------------    -->   	
    <div onclick="if (editModeActive) { deselectAll(); }" >
 				<h3 class="positive"><s:property value="selectedCatalog.selectedElement.name"/></h3>
 					   
	    		<div class="comment" style="text-align:center;font-size:1.3em;">
	    			<s:property value="selectedCatalog.selectedElement.comment"/>
    			</div>
    			    			
	    		<%-- if no dataset in this element, we display a 'this is empty' message --%>
	    		<c:if test="${selectedCatalog.selectedElement.nbDatasets eq 0}">
	    			<h2><span class="negative"><s:text name="workzone.empty" ></s:text></span></h2>
	    		</c:if>	    		
	    		<s:include value="/secure/subparts/workzone.elementcontents.dropzone.dataset.jsp" >
 								<s:param name="dropPosition" value="1" />		 															 									    											    						
				</s:include>		   				
	    	    <s:iterator value="selectedCatalog.selectedElement.datasets" var="curDataset">
					<s:include value="/secure/subparts/workzone.elementcontents.dataset.jsp" >
 								<s:param name="curDataset" value="#curDataset" />		    				    											    											    						
					</s:include>		      
		      		<s:include value="/secure/subparts/workzone.elementcontents.dropzone.dataset.jsp" >
 								<s:param name="dropPosition" value="#curDataset.layoutPosition + 1" />			    											    								
					</s:include>		      	   			    			       
			    </s:iterator> <%-- For each dataset --%>
			    	
			    <!-- Leave Space below -->
				<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>			
	 </div>
