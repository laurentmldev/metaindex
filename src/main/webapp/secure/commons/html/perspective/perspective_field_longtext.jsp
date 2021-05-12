<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 

<!--------------- LONG_TEXT -------------->		  
 <script type="text/javascript" >


	
 function _commons_perspective_buildEditableLongTextTerm(catalogDesc,tabIdx,sectionIdx,fieldIdx,
		 fieldContainerNode,fieldVisuDesc,termDesc,
		 itemId,fieldValue,successCallback,onChangeCallback) {
 	
 	 let fieldNode=document.getElementById("_commons_perspectives_field_editable_template_longtext").cloneNode(true);
 	 fieldNode.id="";
 	 fieldNode.style.display="block";
 	 
 	 // title
 	 let title = fieldNode.querySelector("._title_");
 	 if (fieldVisuDesc.showTitle==true) { title.innerHTML=mx_helpers_getTermName(termDesc, catalogDesc)+": "; }
 	 else { title.style.display='none'; }
 	 
 	 // value
 	 let valueNode = fieldNode.querySelector("._value_");
 	 valueNode.title=termDesc.name; 	 
 	 valueNode.value=fieldValue;	 
 	 valueNode.onkeypress=function(e) {
 		event.stopPropagation(); 
 	 }
 	 valueNode.onkeydown=function(event) {
 		event.stopPropagation();  		
 	 }
 	 // input for popup fullpage editing
 	 let popupWindow = MxGuiPopups.newBlankPopup("<s:text name="Catalogs.field.Editing" /> '"+mx_helpers_getTermName(termDesc, catalogDesc)+"'",
 				"<s:text name="Catalogs.field.Save" />","80vw","100%",
 							"#aaa",null,"90vh"); 		
 	 MxGuiDetailsRightBar.addContents(popupWindow);
 	 let textArea=document.createElement("textarea");

 	 // edition callbacks
	 let localSuccessCallback=function(fieldName,newValue) {
		successCallback(fieldName,newValue);
		valueNode.value=newValue;
		textArea.value=newValue;
		footer_showAlert(SUCCESS, "<s:text name="Catalogs.field.UpdateDone" />");
		popupWindow.hide();
		
	 }
	 let onUpdateErrorCallback=function(msg) {
		 footer_showAlert(WARNING, "<s:text name="Catalogs.field.couldNotUpdate" /> : "+msg); 
	 } 			  				 
	
 	 
 	 // ok button
 	 let okButton = fieldNode.querySelector("._ok_button_");
 	 okButton.onclick=function(e) {
 		event.preventDefault();
 		event.stopPropagation();
 		onChangeCallback(itemId,termDesc.name,valueNode.value,localSuccessCallback,onUpdateErrorCallback);
 	 }
 	 
 	// popup button
 	 let popupButton = fieldNode.querySelector("._popup_button_");
 	 popupButton.onclick=function(e) {
 		
 		
 		// body
 		let popupBody=popupWindow.querySelector(".modal-body");
 		textArea.classList.add("mx-perspective-textarea");
 		textArea.value=fieldValue;
 		textArea.style["width"]="90%";
 		textArea.style["height"]="90%";
 		textArea.style["font-size"]="1.2rem";
 		textArea.onkeypress=function(event) { event.stopPropagation(); }
 		textArea.onkeydown=function(event) {
 	 		if (event.key=='ArrowRight') { event.stopPropagation(); }
 			else if (event.key=='ArrowLeft') { event.stopPropagation(); }
 			else if (event.key=='Enter') { event.stopPropagation(); }
 	 	 }
 		popupBody.appendChild(textArea);
 		let saveButton = popupWindow.getCloseButton();
 		let basicCloseActionFunc=saveButton.onclick;
 		saveButton.onclick=function(e) {
 			basicCloseActionFunc(e);
 					  				 
 			 onChangeCallback(itemId,termDesc.name,textArea.value,localSuccessCallback,onUpdateErrorCallback);
 			 
 		}
 		
 		
 		popupWindow.show();
 	 }
 	
 	 
 	 return fieldNode;
  }
 
 
	
 function _commons_perspective_build_readonly_field_longtext(catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,fieldVisuDesc,termDesc,fieldValue) {
 		 
 	 let fieldNode=document.getElementById("_commons_perspectives_field_readonly_template_longtext").cloneNode(true);
 	 fieldNode.id="";
 	 fieldNode.style.display="block";
 	 
 	 // title
 	 let title = fieldNode.querySelector("._title_");
 	 if (fieldVisuDesc.showTitle==true) { title.innerHTML=mx_helpers_getTermName(termDesc, catalogDesc)+": "; }
 	 else { title.style.display='none'; }
 	 
 	 // value
 	 let valueNode = fieldNode.querySelector("._value_");
 	 valueNode.title=termDesc.name; 	 
 	 valueNode.innerHTML=fieldValue;	 
 	
 	 fieldContainerNode.appendChild(fieldNode);
  }
 
</script>



<div style="display:none;width:100%;" class="mx-perspective-field" id="_commons_perspectives_field_readonly_template_longtext"  >
	<div class="_title_"></div>
	<pre style="text-align:left;width:100%;max-height:10vh;overflow:auto;background:#eee;" class="_value_"></pre>	               
</div>

<div style="display:none;width:100%;" class="mx-perspective-field" id="_commons_perspectives_field_editable_template_longtext"  >
	<div class="_title_"></div>
	<textarea rows="5" style="" class="_value_ mx-perspective-textarea"></textarea>
			
	<button type="button" class="_popup_button_ btn btn-default btn-sm alert alert-info" 
				style="margin:0.2rem;padding:0.2rem;width:2rem;height:2rem;"
				title="<s:text name="Catalogs.field.FullEdit" />" >
				<i class="fa fa-align-left fa-sm"></i>
	</button>
	<button type="button" class="_ok_button_ btn btn-default btn-sm alert alert-success" 
				style="margin:0.2rem;padding:0.2rem;width:4rem;height:2rem;" >
				<s:text name="Catalogs.field.Save" />
	</button>
    	               
</div>