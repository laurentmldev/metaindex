<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 

<!--------------- LONG_TEXT -------------->		  
 <script type="text/javascript" >

function createEditableFullTextPopup(textArea,itemId,termDesc,catalogDesc,
		getFullFieldContentsCallback,
		onChangeCallback,successCallback,onUpdateErrorCallback,showWorkInProgress) {
	
	let popupWindow = MxGuiPopups.newBlankPopup("<s:text name="Catalogs.field.Editing" /> '"
			+mx_helpers_getTermName(termDesc, catalogDesc)+"'",
				"<s:text name="Catalogs.field.Save" />","80vw","100%",
							"#aaa",null,"90vh"); 		
	 MxGuiDetailsRightBar.addContents(popupWindow);
	 
	// body
		let popupBody=popupWindow.querySelector(".modal-body");
		textArea.classList.add("mx-perspective-textarea");
		textArea.disabled=true;
		textArea.value="<s:text name="Items.pleaseWaitWhileLoadingFullContents" />";
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
			showWorkInProgress();  				 
			onChangeCallback(itemId,termDesc.name,textArea.value,successCallback,onUpdateErrorCallback); 			 
		}
	
		// loading full contents for this field
		let setFullValue=function(fullValue) {
			textArea.value=fullValue;
			textArea.disabled=false;			
		}				 		
		popupWindow.show();
		
		// retrieve the full value of the field
		// to populate our text area
		getFullFieldContentsCallback(itemId,termDesc.name,setFullValue);	
}


function createROFullTextPopup(textArea,itemId,termDesc,catalogDesc,
		getFullFieldContentsCallback) {
	
	let popupWindow = MxGuiPopups.newBlankPopup("<s:text name="Catalogs.field" /> '"
			+mx_helpers_getTermName(termDesc, catalogDesc)+"'",
				"<s:text name="Items.close" />","80vw","100%",
							"#aaa",null,"90vh"); 		
	 MxGuiDetailsRightBar.addContents(popupWindow);
	 
	// body
		let popupBody=popupWindow.querySelector(".modal-body");
		textArea.innerHTML="<div class='mx-perspective-field-longtext-pleasewait'><s:text name="Items.pleaseWaitWhileLoadingFullContents" /></div>";
		textArea.style["width"]="90%";
		textArea.style["height"]="90%";
		//textArea.style["font-size"]="0rem";
		textArea.classList.add("mx-perspective-field-longtext");
		popupBody.appendChild(textArea);
		
		// loading full contents for this field
		let setFullValue=function(fullValue) {
			textArea.innerHTML=fullValue;
		}				 		
		popupWindow.show();
		
		// retrieve the full value of the field
		// to populate our text area
		getFullFieldContentsCallback(itemId,termDesc.name,setFullValue);	
}
	
	
 function _commons_perspective_buildEditableLongTextTerm(catalogDesc,tabIdx,sectionIdx,fieldIdx,
		 fieldContainerNode,fieldVisuDesc,termDesc,
		 itemId,fieldValue,successCallback,onChangeCallback,getFullFieldContentsCallback) {
 	
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
 	 valueNode.innerHTML=fieldValue;	
 	 if (fieldValue.length==0) {
 		valueNode.innerHTML="<s:text name='Items.clickToEditLongField' />";
 	 }
 	 
 	 // input for popup fullpage editing 	 
 	 let textArea=document.createElement("textarea");

 	 // edition callbacks
	 let localSuccessCallback=function(fieldName,newValue) {
		successCallback(fieldName,newValue);
		valueNode.innerHTML=newValue;
		textArea.value=newValue;
		valueNode.style.border="none";
		footer_showAlert(SUCCESS, "<s:text name="Catalogs.field.UpdateDone" />");
		popupWindow.hide();
		
	 }
	 let onUpdateErrorCallback=function(msg) {
		 footer_showAlert(WARNING, "<s:text name="Catalogs.field.couldNotUpdate" /> : "+msg); 
	 } 			  				 
	
 	 let showWorkInProgress=function() {
 		valueNode.style.border="2px solid yellow"; 
 	 }
 	  
 	// open popup to get full contents 	 
 	valueNode.onclick=function(e) {
 		 // open popup
 		createEditableFullTextPopup(textArea,itemId,termDesc,catalogDesc,
					getFullFieldContentsCallback,
					onChangeCallback,localSuccessCallback,onUpdateErrorCallback,showWorkInProgress)
 	 }
 	
 	 
 	 return fieldNode;
  }
 
 
	
 function _commons_perspective_build_readonly_field_longtext(itemId,catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,
		 fieldVisuDesc,termDesc,fieldValue,getFullFieldContentsCallback) {
 		 
 	 let fieldNode=document.getElementById("_commons_perspectives_field_readonly_template_longtext").cloneNode(true);
 	 fieldNode.id="";
 	 fieldNode.style.display="block";
 	 
 	 // title
 	 let title = fieldNode.querySelector("._title_");
 	 if (fieldVisuDesc.showTitle==true) { title.innerHTML=mx_helpers_getTermName(termDesc, catalogDesc)+": "; }
 	 else { title.style.display='none'; }
 	 
 	// input for popup fullpage editing 	 
 	 let textArea=document.createElement("pre");
 	
 	 // value
 	 let valueNode = fieldNode.querySelector("._value_");
 	 valueNode.title=termDesc.name; 	 
 	 valueNode.innerHTML=fieldValue;	  	 
	 valueNode.onclick=function(e) {
		 // open popup
		createROFullTextPopup(textArea,itemId,termDesc,catalogDesc, getFullFieldContentsCallback);
	 }
 	
 	 fieldContainerNode.appendChild(fieldNode);
  }
 
</script>



<div style="display:none;width:100%;" class="mx-perspective-field" id="_commons_perspectives_field_readonly_template_longtext"  >
	<div class="_title_ "></div>
	<pre class="_value_ mx-perspective-field-longtext"  style="max-height:8vh;max-width:15vw;" ></pre>	               
</div>

<div style="display:none;width:100%;" class="mx-perspective-field" id="_commons_perspectives_field_editable_template_longtext"  >
	<div class="_title_"></div>
	<pre class="_value_ mx-perspective-field-longtext"  style="max-height:8vh;max-width:15vw;"
		title="<s:text name="Catalogs.field.FullEdit" />"
	></pre>
	
</div>