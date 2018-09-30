<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<s:include value="/secure/workzone/contents/retrieve_element_data_ws.jsp" />
<s:include value="/secure/workzone/contents/workzone.contents.javascript.jsp" />
<s:include value="/secure/workzone/contents/workzone.contents.modals_and_forms.jsp" />
   	
   	
   	<script>
	   	function updateElementDataContents(elementData) {
	   		
	   		// name
			var insertspotField_name = document.getElementById("elementContents_name_insertspot");
			var fieldNode = new_editable_field(
									/*name*/ "elementProperties_name", /*input name*/ "selectedElement.name", /*input style*/ ";", /*input placeholder*/ "title",
									/*onchange Func*/ 
									function(e) { 
										document.getElementById('edit.element.maindetails.form').submit(); 
									}
								);
			insertspotField_name.innerHTML=fieldNode.innerHTML;		
			fieldNode.setValue(elementData.elementName,elementData.readOnly);
			
			// comment
			var insertspotField_comment = document.getElementById("elementContents_comment_insertspot");
			fieldNode = new_editable_field(
								/*name*/ "elementProperties_comment", /*input name*/ "selectedElement.comment", /*input style*/ "font-size:1.5em;", /*input placeholder*/ "comment",
								/*onchange Func*/ 
								function(e) {
									document.getElementById('edit.element.maindetails.form').submit(); 
								}
							);
			insertspotField_comment.innerHTML=fieldNode.innerHTML;		
			fieldNode.setValue(elementData.elementComment,elementData.readOnly);
			
			// dropzones etc
	   		if (elementData.nbDatasets==0) { document.getElementById('elementContents_emptyElement_msg').style.display='block'; }
	   		else { document.getElementById('elementContents_emptyElement_msg').style.display='none'; }
	   		
	   		var shouldInsertDropZones = !elementData.readOnly && !elementData.templated;
	   		insertSpotNode=document.getElementById('elementContents_datasets_insertspot');
	   		insertSpotNode.innerHTML="";
	   	 	insert_dataset_dropzone(insertSpotNode,0,elementData.elementId);
	   	 	
	   	 	hideEditModeZones();
	   	}

	 	
   	</script>
   	
      <center>
		
    	
    <div onclick="if (editModeActive) { deselectAll(); }" >
    
    		<form 	id="edit.element.maindetails.form" action="<c:url value="/updateElementDataProcess" />" method="post" >
					<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
		
 				<h3 class="positive" id="elementContents_name_insertspot"></h3>
 			 
	    		<div  class="comment" style="text-align:center;font-size:1.3em;"  id="elementContents_comment_insertspot"  ></div>
    		</form>				
    		
	    		<%-- if no dataset in this element, we display a 'this is empty' message --%>
	    		<h2 style="display:none;" id="elementContents_emptyElement_msg" ><span  class="negative" ><s:text name="workzone.empty" ></s:text></span></h2>
	    		
				<s:include value="/secure/workzone/contents/workzone.contents.datasets.jsp" />
					   					    	    			    	 
			    <!-- Leave Space below (TODO improve that crappy stuff with proper styling) -->
				<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
							
	 </div>
	      
	      
	    </center>
	
	
