<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

   
<script type="text/javascript" >


//-------- Blank --------
 function _commons_popups_makeBlankPopup(title,closeButtonText,maxWidth,maxHeight,bgColor) {
	 let newPopup = document.getElementById("_commons_popups_blank_input_template_").cloneNode(true);
	 newPopup.id="";
	 
	 let modal=newPopup.querySelector(".modal-dialog");
	 let styleStr="";
	 if (maxWidth!=null) { styleStr+="max-width:"+maxWidth+";"; }
	 if (bgColor!=null) { styleStr+="background-color:"+bgColor+";"; }
	 if (styleStr!="") { modal.style=styleStr; }
	 
	 modal=newPopup.querySelector(".modal-content");
	 styleStr="";
	 if (maxHeight!=null) { styleStr+="max-height:"+maxHeight+";"; }
	 if (styleStr!="") { modal.style=styleStr; }
	 
	 
	 // header
	 let titleNode=newPopup.querySelector("._title_");
	 titleNode.innerHTML=title;
	 
	 // 'close' button
	 let closeButton=newPopup.querySelector("._button_close_");
	 closeButton.onclick=function(event) { newPopup.style.display="none"; }
	 if (closeButtonText!=null) {
		 closeButton.innerHTML=closeButtonText;
	 }
	 newPopup.show=function() { newPopup.style.display="block"; }
	 newPopup.hide=function() { newPopup.style.display='none'; }
	 newPopup.toggleShowHide=function() {
		 if (newPopup.style.display=='block') { newPopup.hide(); }
		 else { newPopup.show(); }
	 }
	 
	 
	 return newPopup;
 }
 
 </script>

  <div class="modal mx-modal _modal_root_" id="_commons_popups_blank_input_template_"
  	onkeypress="event.stopPropagation();
  		if (event.which==13||event.keycode==13) { this.querySelector('._button_close_').click(); this.style.display='none'; }"
 	onkeydown="event.stopPropagation();
  			// when pressing escape, close modal
  			if (event.which==27||event.keycode==27) {
  				this.style.display='none';
  			}" >
    <div class="modal-dialog">
      <div class="modal-content">
      
        <!-- Modal Header -->
        <div class="modal-header">
          <h4 class="modal-title _title_"></h4>
          <button type="button" class="close" data-dismiss="modal"
          			onclick="event.preventDefault();event.stopPropagation();
								findAncestorNode(this,'_modal_root_').style.display='none';
								"><i class="fa fa-times fa-sm"></i></button>
        </div>
        
        <!-- Modal body -->
        <div class="modal-body" style="max-height:80vh;overflow:auto;">
        </div>
        
        <!-- Modal footer -->
        <div class="modal-footer">
        	<button class="_button_close_ btn-big btn btn-info" >
				<i class="fa fa-check fa-sm text-grey-50"></i>
		 	</button>
          
        </div>
        
      </div>
    </div>
  </div>
  
  
  
<script type="text/javascript" >
 // -------- Dropdown --------
 // choices : [ { value:xxx, text:xxx}, ... ]
 function _commons_popups_makeDropdownInputPopup(choices,title,onValidCallback) {
	 let newPopup = document.getElementById("_commons_popups_dropdown_input_template_").cloneNode(true);
	 newPopup.id="";	 
	 
	 // input
	 let inputNode=newPopup.querySelector("._input_");
	 inputNode.onkeypress=function(event) {
								 if (event.which==13||event.keycode==13) {
										event.preventDefault();event.stopPropagation(); 
										onValidCallback(inputNode.value);											
								 }
							 };
	 inputNode.title=title;
	 for (var i=0;i<choices.length;i++) {
		  let curChoice=choices[i];
		  let optionNode = document.createElement("option");
		  
		  // choices : { value:xxx, text:xxx}
		  optionNode.setAttribute("value",curChoice.value);
		  optionNode.innerHTML=curChoice.text;
		  inputNode.appendChild(optionNode);
	 }
	 
	 // header
	 let titleNode=newPopup.querySelector("._title_");
	 titleNode.innerHTML=title;
	 
	 // ok button
	 let okButton=newPopup.querySelector("._button_ok_");
	 okButton.onclick=function(event) { onValidCallback(inputNode.value); }
	 
	 newPopup.show=function() { newPopup.style.display="block"; }
	 newPopup.hide=function() { newPopup.style.display="none"; }
	 return newPopup;
 }

 </script>
 <div class="modal mx-modal _modal_root_" id="_commons_popups_dropdown_input_template_" 
 	onkeypress="event.stopPropagation();
 				if (event.which==13||event.keycode==13) { this.querySelector('._button_ok_').click(); this.style.display='none';}
 				"
 	onkeydown="event.stopPropagation();
  			// when pressing escape, close modal
  			if (event.which==27||event.keycode==27) {
  				this.style.display='none';
  			}"
  			>
   <div class="modal-dialog">
     <div class="modal-content">
     
       <!-- Modal Header -->
       <div class="modal-header">
         <h4 class="modal-title _title_"></h4>
          <button type="button" class="close" data-dismiss="modal"
         			onclick="event.preventDefault();event.stopPropagation();
								findAncestorNode(this,'_modal_root_').style.display='none';
								"><i class="fa fa-times fa-sm"></i></button>
       </div>
       
       <!-- Modal body -->
       <div class="modal-body">
         <select  class="form-control bg-light border-0 small _input_" >
          </select>
       </div>
       
       <!-- Modal footer -->
       <div class="modal-footer">
       	<button class="_button_ok_ btn  btn-success" >
				<i class="fa fa-check fa-sm text-grey-50"></i>
		 	</button>
         
       </div>
       
     </div>
   </div>
 </div>
 
 <script type="text/javascript" >
//-------- Text --------
 function _commons_popups_makeTextInputPopup(placeHolder,title,onValidCallback) {
	 let newPopup = document.getElementById("_commons_popups_text_input_template_").cloneNode(true);
	 newPopup.id="";	 
	 
	 // input
	 let inputNode=newPopup.querySelector("._input_");
	 inputNode.onkeypress=function(event) {
								 if (event.which==13||event.keycode==13) {
										event.preventDefault();event.stopPropagation(); 
										onValidCallback(inputNode.value);					
								 }
							 };
	 inputNode.placeholder=placeHolder;
	 inputNode.title=title;
	 
	 // header
	 let titleNode=newPopup.querySelector("._title_");
	 titleNode.innerHTML=title;
	 
	 // ok button
	 let okButton=newPopup.querySelector("._button_ok_");
	 okButton.onclick=function(event) { onValidCallback(inputNode.value); newPopup.hide();  }
	 
	 newPopup.show=function() { newPopup.style.display="block"; }
	 newPopup.hide=function() { newPopup.style.display="none"; }
	 return newPopup;
 }
 
 </script>

  <div class="modal mx-modal _modal_root_" id="_commons_popups_text_input_template_"
  	onkeypress="event.stopPropagation();
  		if (event.which==13||event.keycode==13) { this.querySelector('._button_ok_').click(); this.style.display='none'; }"
 	onkeydown="event.stopPropagation();
  			// when pressing escape, close modal
  			if (event.which==27||event.keycode==27) {
  				this.style.display='none';
  			}" >
    <div class="modal-dialog">
      <div class="modal-content">
      
        <!-- Modal Header -->
        <div class="modal-header">
          <h4 class="modal-title _title_"></h4>
          <button type="button" class="close" data-dismiss="modal"
          			onclick="event.preventDefault();event.stopPropagation();
								findAncestorNode(this,'_modal_root_').style.display='none';
								"><i class="fa fa-times fa-sm"></i></button>
        </div>
        
        <!-- Modal body -->
        <div class="modal-body">
          <input type="text" class="form-control bg-light border-0 small _input_" 
	                    		style="display:block;min-width:200px;max-width:20rem;">
        </div>
        
        <!-- Modal footer -->
        <div class="modal-footer">
        	<button class="_button_ok_ btn btn-success" >
				<i class="fa fa-check fa-sm text-grey-50"></i>
		 	</button>
          
        </div>
        
      </div>
    </div>
  </div>
  
  
 <script type="text/javascript" >
//-------- Multi-fields Popup Form --------

// fieldsList: [ { id:"xxx",type:"text",title:"xxx",defaultValue:"xxx", important:'false',disabled:'false' },
//				 { id:"xxx",type:"dropdown",defaultValue:"xxx", values:[{text:'xxx',value:'xxx'}], important:'true',disabled:'true' }]
// 				 { id:"xxx",type:"dropdown",defaultValue:"xxx", values:['val1','val2'], important:'true',disabled:'false' }]
// 				 { id:"xxx",type:"multiselect",values:['val1','val2'], important:'true' }
//				 { id:"xxx",type:"file-url",title:"xxx",defaultValue:"xxx", important:'false',disabled:'false' },
//				]
// text fields can have optional 'datatype' attribute : text|number. 
// 		- text : free text
//		- number : check number syntax
//
// onValidCallback(choices)
// 	choice[id]="xxx"

function _commons_popups_createFieldInput(curFieldDescr,resultFields, resultFiles) {
	
	let newFormInput=null;	
	
	// for file-url, behaviour is specialized down there for drag and drop,
	// but basic behaviour is a normal text field
	if (curFieldDescr.type=="text" || curFieldDescr.type=="file-url") {
		if (curFieldDescr.type=="file-url") { newFormInput=document.getElementById("_commons_popups_formfileurl_input_template_").cloneNode(true); }
		else { newFormInput=document.getElementById("_commons_popups_formtext_input_template_").cloneNode(true); }
		newFormInput.id=curFieldDescr.id;
		newFormInput.style.display="block";
		newFormInput.title=curFieldDescr.title;		
		
		// legend
		let legend = newFormInput.querySelector("._legend_");
		legend.innerHTML=curFieldDescr.title;
		
		// input text
		let newFormInputText=newFormInput.querySelector('._form_input_');
		newFormInputText.clearContents=function(force) {
			
			if (!force && (
					curFieldDescr.disabled=='true'
					|| newFormInput.getAttribute("locked")=='true')
				) { return; }
			
			newFormInputText.value='';
			resultFields[curFieldDescr.id]="";
		}
		
		if (curFieldDescr.placeholder!=null) {
			newFormInputText.placeholder=curFieldDescr.placeholder;
		}
		newFormInputText.value=curFieldDescr.defaultValue;
		newFormInputText.onchange=function(event) {
			
			if (curFieldDescr.datatype=="number") {
				if (isNaN(this.value)) {
					newFormInputText.classList.add("form-input-error");
				} else {
					newFormInputText.classList.remove("form-input-error");
				}
			}
			resultFields[curFieldDescr.id]=this.value; 
		}
		newFormInputText.onfocusout=function(event) { resultFields[curFieldDescr.id]=this.value; }
		
		if (curFieldDescr.disabled=='true') {
			newFormInputText.disabled=true;
			newFormInputText.classList.add("form-text-input-frozen")
		}
		
	}
	
	else if (curFieldDescr.type=="dropdown") {
		newFormInput=document.getElementById("_commons_popups_formdropdown_input_template_").cloneNode(true);
		newFormInput.id=curFieldDescr.id;
		newFormInput.style.display="block";
		
		let legend = newFormInput.querySelector("._legend_");
		legend.innerHTML=curFieldDescr.title;
				
		let dropdown = newFormInput.querySelector("._form_input_");
		dropdown.title=curFieldDescr.title;
		for (var enumIdx in curFieldDescr.values) {
			let curEnumVal=curFieldDescr.values[enumIdx];
			let newOption = document.createElement("option");			
			let text=curEnumVal;
			let value=curEnumVal;
			if (typeof(curEnumVal)=="object") {
				text=curEnumVal.text;
				value=curEnumVal.value;					
			}
			newOption.value=value;
			newOption.innerHTML=text;
			dropdown.appendChild(newOption);			
			if (enumIdx==0) { dropdown.value=value; resultFields[curFieldDescr.id]=value; }
		}
					
				
		dropdown.clearContents=function(force) {
			if (!force && (
					curFieldDescr.disabled=='true'
					|| newFormInput.getAttribute("locked")=='true')
				) { return; }
			dropdown.value=""; 
			resultFields[curFieldDescr.id]=""; 			 
		}
		
		dropdown.onchange=function(event) { resultFields[curFieldDescr.id]=this.value; }
		dropdown.onfocusout=function(event) { resultFields[curFieldDescr.id]=this.value; }		
		
		// set to default value
		dropdown.value=curFieldDescr.defaultValue; 
		resultFields[curFieldDescr.id]=curFieldDescr.defaultValue;
		
	}
	else if (curFieldDescr.type=="multiselect") {
		newFormInput=document.getElementById("_commons_popups_formmultiselect_input_template_").cloneNode(true);
		newFormInput.id=curFieldDescr.id;
		newFormInput.style.display="flex";
		newFormInput.value=curFieldDescr.defaultValue;
		newFormInput.title=curFieldDescr.title;
		
		let legend = newFormInput.querySelector("._legend_");
		legend.innerHTML=curFieldDescr.title;
		
		onchangeFunc=function(event) {
			let valStr="";
			let valuesChecks=newFormInput.querySelectorAll("._checkbox_");
			for (idx in valuesChecks) {
				let valueCheckbox=valuesChecks[idx];
				if (valueCheckbox.getAttribute==null) { continue; }
				if (valueCheckbox.checked) {
					if (valStr.length>0) { valStr+=","; }
					valStr+=valueCheckbox.getAttribute("enum-val");
				}				
			}	
			resultFields[curFieldDescr.id]=valStr;
			
			//newFormInput.value=valStr;			 
		}
		
		// build check-boxes for the multi-enum choice
		for (var enumIdx in curFieldDescr.values) {
			
			let curEnumVal=curFieldDescr.values[enumIdx];
			
			let newCheckbox = document.createElement("div");
			newCheckbox.classList.add("card");
			newCheckbox.classList.add("mb-6");
			newCheckbox.classList.add("form-check");			
			newCheckbox.style.border="none";
			
			let label = document.createElement("label");
			label.classList.add("modals-form-control-checkbox");
			label.classList.add("form-check-label");	
			
			let input = document.createElement("input");
			input.type="checkbox";
			input.classList.add("form-check-input");
			input.classList.add("_checkbox_");
			input.classList.add("_form_input_");
			input.title=curEnumVal;
			input.setAttribute("enum-val",curEnumVal);
			input.onchange=onchangeFunc;
			input.onfocusout=onchangeFunc;
			input.clearContents=function(force) {
				
				if (!force && (
						curFieldDescr.disabled=='true'
						|| newFormInput.getAttribute("locked")=='true')
					) { return; }
				
				input.checked=false; 
				input.onchange();
			}
			
			let text = document.createElement("span");
			text.innerHTML=curEnumVal;
			
			label.appendChild(input);
			label.appendChild(text);			
			newCheckbox.appendChild(label);
			newFormInput.appendChild(newCheckbox);
		}
			
	}
	else {
		console.log("unhandled popup type : '"+curFieldDescr.type+"'");
		return null;
	}
	

	// configure drag-n-drop behaviour for files URLs
	if  (curFieldDescr.type=="file-url" && curFieldDescr.disabled!='true') {
		// input file dropzone
		
		
		let newFormInputFile=newFormInput.querySelector('._form_input_');
		let filesListNode = newFormInput.querySelector("._files_list_");
		let newFormInputText=newFormInput.querySelector('._form_input_');
		newFormInputFile.classList.remove("border-0");
		newFormInputFile.style="width:90%;border:1px dashed grey;"
		
		newFormInputFile.clearContents=function(force) {			
			// always clean the files-to-upload table
			// once uploaded, we won't do it twice
			resultFiles[curFieldDescr.id]=null;
			filesListNode.innerHTML="";
			
			if (!force && (
					curFieldDescr.disabled=='true'
					|| newFormInput.getAttribute("locked")=='true')
				) { return; }
			
			newFormInputText.value='';
			resultFields[curFieldDescr.id]="";
			
		}
		let handlerDragEnterFunc=function(e) {
			e.preventDefault();
			e.stopPropagation();
			newFormInputFile.classList.add("form-text-input-active");
		}
		let handlerDragOverFunc=function(e) {
			e.preventDefault();
			e.stopPropagation();
			newFormInputFile.classList.add("form-text-input-active");
		}
		let handlerDragLeaveFunc=function(e) {
			e.preventDefault();
			e.stopPropagation();
			newFormInputFile.classList.remove("form-text-input-active");
		}
		let handlerDragDropFunc=function(e) {
			e.preventDefault();
			e.stopPropagation();
			newFormInputFile.classList.remove("form-text-input-active");
			let dt = e.dataTransfer;
			let files = dt.files;
			if (files.length>1) {
				footer_showAlert(ERROR,"<s:text name="Items.uploadItems.dropFileToUpload.tooMany"/>")
				return;
			}
			newFormInputFile.value="";
			
			filesListNode.innerHTML="";
			for (var i = 0;i<files.length;i++)
			{
				if (i>0) { newFormInputFile.value+=","; }
				let file=files[i];
				newFormInputFile.value+=file.name;	
				
				// create a preview if it's an image
				let newTr=document.createElement("tr");
				filesListNode.appendChild(newTr);
				let preview=document.createElement("td");
				newTr.appendChild(preview);
												
				if ( file.type.match(/image/)) {
					let reader = new FileReader()
					reader.readAsDataURL(file)
					reader.onloadend = function() {
						let img = document.createElement('img');
						img.style="max-width:80px;height:auto";
						img.src = reader.result;
						preview.appendChild(img);
					}
				}				
				
				
				// file title and size
				newTr=document.createElement("tr");
				filesListNode.appendChild(newTr);
				let name=document.createElement("td");
				name.innerHTML=file.name;
				newTr.appendChild(name);
				
				let size=document.createElement("td");
				let sizeValText=(Math.round(file.size/10.0)/100)+" KB";
				if (file.size>=1000000) { sizeValText=(Math.round(file.size/10000.0)/100)+" MB"; }
				name.innerHTML+="<br/>("+sizeValText+")";
				
				
				
				

			}
			resultFields[curFieldDescr.id]=this.value;
			resultFiles[curFieldDescr.id]=files;
			filesListNode.style.display="block";			
		}
		newFormInputFile.addEventListener('dragenter', handlerDragEnterFunc, false);
		newFormInputFile.addEventListener('dragleave', handlerDragLeaveFunc, false);
		newFormInputFile.addEventListener('dragover', handlerDragOverFunc, false);
  		newFormInputFile.addEventListener('drop', handlerDragDropFunc, false);
	}
	
	
	let locker = newFormInput.querySelector("._locked_");
	if (curFieldDescr.disabled=='true') { locker.style.display='none'; }
	else {
		newFormInput.lock=function() {		
			newFormInput.setAttribute("locked",true);
			locker.classList.remove('fa-lock-open');
			locker.classList.add('fa-lock');
			locker.classList.add('form-input-locked');
			locker.classList.remove('form-input-unlocked');
		}
		newFormInput.unlock=function() {
			newFormInput.setAttribute("locked",false);
			locker.classList.add('fa-lock-open');
			locker.classList.remove('fa-lock');
			locker.classList.remove('form-input-locked');
			locker.classList.add('form-input-unlocked');
		}
			
		locker.onclick=function(event) {		
			// switching lock state
			if (newFormInput.getAttribute("locked")=='true') { newFormInput.unlock(); }
			else { newFormInput.lock(); }
		}
		// set initial lock state, depending if this is an 'important' field or not
		// important fields are planned to be changed for every document
		// so they are unlocked by default
		if (curFieldDescr.important==true)  { newFormInput.unlock(); }
		else { newFormInput.lock(); }
	}
	
	
	return newFormInput;
}
 
 
//fieldsList: [ { id:"xxx",type:"text",title:"xxx",defaultValue:"xxx", important:'false',disabled:'false' },
//{ id:"xxx",type:"dropdown",defaultValue:"xxx", values:[{text:'xxx',value:'xxx'}], important:'true',disabled:'true' }]
// { id:"xxx",type:"dropdown",defaultValue:"xxx", values:['val1','val2'], important:'true',disabled:'false' }]
// { id:"xxx",type:"multiselect",values:['val1','val2'], important:'true' }]

 function _commons_popups_makeMultiInputsPopup(title,fieldsList,onValidCallback) {
		
	 let newPopup = document.getElementById("_commons_popups_multi_input_template_").cloneNode(true);
	 
	 let resultFields={};
	 let resultFiles=[];
	 
	 newPopup.id="";	 
	 newPopup.show=function() { newPopup.style.display='block'; }
	 newPopup.hide=function() { newPopup.style.display='none'; }
	 newPopup.toggleShowHide=function() {
		 if (newPopup.style.display=='block') { newPopup.hide(); }
		 else { newPopup.show(); }
	 }
	 newPopup.clearContents=function(forceAll) {
		 if (forceAll==null) { forceAll=false; }
		 let formNodes=newPopup.querySelectorAll("._form_input_");
		 for (var i=0;i<formNodes.length;i++) {
			 let curFormNode=formNodes[i];
			 curFormNode.clearContents(forceAll); 			 
		 }
	 }
	 
	 // header
	 let titleNode=newPopup.querySelector("._title_");
	 if (typeof(title)=="object") { titleNode.append(title); }
	 else { titleNode.innerHTML=title; }
	 
	 // body (each field)	 
	 let fieldsInsertSpot=newPopup.querySelector("._fields_insertspot_");
	 
	 let importantFieldsContainer = document.createElement("div");
	 importantFieldsContainer.classList.add("card-group");
	 fieldsInsertSpot.appendChild(importantFieldsContainer);
	 
	 // important fields
	 for (var idx=0;idx<fieldsList.length;idx++) {
		let curFieldDescr=fieldsList[idx];		
		if (curFieldDescr.important==null) { curFieldDescr.important="true"; }
		if (curFieldDescr.important==true || curFieldDescr.important=="true") {
			let newFormInput=_commons_popups_createFieldInput(curFieldDescr,resultFields,resultFiles);		
			importantFieldsContainer.appendChild(newFormInput);
			if (curFieldDescr.defaultValue!=null && curFieldDescr.defaultValue!="") {
				resultFields[curFieldDescr.id]=curFieldDescr.defaultValue;
			}			
		}
	 }
	 fieldsInsertSpot.appendChild(document.createElement("hr"));
	 
	 let secondaryFieldsContainer = document.createElement("div");
	 secondaryFieldsContainer.classList.add("card-group");
	 fieldsInsertSpot.appendChild(secondaryFieldsContainer);
		 
	// secondary fields
	
	// first text then dropdown then multi-chocie
	// text
	 for (var idx=0;idx<fieldsList.length;idx++) {
		let curFieldDescr=fieldsList[idx];
		if (curFieldDescr.type!="text") { continue; }
		if (curFieldDescr.important=="false") {
			let newFormInput=_commons_popups_createFieldInput(curFieldDescr,resultFields,resultFiles);		
			secondaryFieldsContainer.appendChild(newFormInput);
			if (curFieldDescr.defaultValue!=null && curFieldDescr.defaultValue!="") {
				resultFields[curFieldDescr.id]=curFieldDescr.defaultValue;
			}
		}
	 }
	 
	// dropdown
	 for (var idx=0;idx<fieldsList.length;idx++) {
		let curFieldDescr=fieldsList[idx];
		if (curFieldDescr.type!="dropdown") { continue; }
		if (curFieldDescr.important=="false") {
			let newFormInput=_commons_popups_createFieldInput(curFieldDescr,resultFields,resultFiles);		
			secondaryFieldsContainer.appendChild(newFormInput);
			if (curFieldDescr.defaultValue!=null && curFieldDescr.defaultValue!="") {
				resultFields[curFieldDescr.id]=curFieldDescr.defaultValue;
			}
		}
	 }
	
	// checkboxes
	 for (var idx=0;idx<fieldsList.length;idx++) {
		let curFieldDescr=fieldsList[idx];
		if (curFieldDescr.type!="multiselect") { continue; }
		if (curFieldDescr.important=="false") {
			let newFormInput=_commons_popups_createFieldInput(curFieldDescr,resultFields,resultFiles);		
			secondaryFieldsContainer.appendChild(newFormInput);
			if (curFieldDescr.defaultValue!=null && curFieldDescr.defaultValue!="") {
				resultFields[curFieldDescr.id]=curFieldDescr.defaultValue;
			}
		}
	 }
	 
	// files refs	
	 for (var idx=0;idx<fieldsList.length;idx++) {
		let curFieldDescr=fieldsList[idx];
		if (curFieldDescr.type!="file-url") { continue; }
		if (curFieldDescr.important=="false") {
			let newFormInput=_commons_popups_createFieldInput(curFieldDescr,resultFields,resultFiles);		
			secondaryFieldsContainer.appendChild(newFormInput);
			if (curFieldDescr.defaultValue!=null && curFieldDescr.defaultValue!="") {
				resultFields[curFieldDescr.id]=curFieldDescr.defaultValue;
			}
		}
	 }
	
	 // ok button
	 let okButton=newPopup.querySelector("._button_ok_");
	 okButton.onclick=function(event) { 
		 onValidCallback(resultFields,resultFiles);
		 newPopup.clearContents();
	 }
	
	 return newPopup;
 }
 
 </script>

  <div class="modal mx-modal _modal_root_" id="_commons_popups_multi_input_template_" 
  	onkeypress="event.stopPropagation();  	
 		// when pressing enter
  		if (event.which==13||event.keycode==13) { 
  			let formNodes=this.querySelectorAll('._form_input_');
 			 for (var i=0;i<formNodes.length;i++) {
 				 let curFormNode=formNodes[i];			 
 				 curFormNode.onchange();
 			 }
  			this.querySelector('._button_ok_').click(); 
  			this.style.display='none';
  		}"
 	onkeydown="event.stopPropagation();
  			// when pressing escape, close modal
  			if (event.which==27||event.keycode==27) {
  				this.style.display='none';
  			}" >
    <div class="mx-modal-dialog-big modal-dialog">
      <div class="modal-content" style="width:70vw;">
      
        <!-- Modal Header -->
        <div class="modal-header">
          <h4 class="modal-title _title_"></h4>
          
            
          <button class="_button_clear_ close "  title="<s:text name="globals.clearAllExplain" />" 
          		onclick="event.preventDefault();event.stopPropagation();
						 findAncestorNode(this,'_modal_root_').clearContents(true);"
								>
				<i class="fa fa-asterisk fa-sm "></i>
		 	</button>
         
          <button type="button" class="close" data-dismiss="modal"
          			onclick="event.preventDefault();event.stopPropagation();
								findAncestorNode(this,'_modal_root_').style.display='none';
								"><i class="fa fa-times fa-sm"></i></button>
								
		
        </div>
        
        <!-- Modal body -->
        <div class="mx-modal-body-big modal-body  _fields_insertspot_" >
         
        </div>
        
        <!-- Modal footer -->
        <div class="modal-footer">
        	
        	<button class="_button_ok_ btn btn-success" title="Create corresponding item">
				<i class="fa fa-check fa-sm text-grey-50"></i>
		 	</button>
          
        </div>
        
      </div>
    </div>
  </div>
  
   
  <fieldset id="_commons_popups_formfileurl_input_template_"  class="form-control-group card  modals-form-control" style="display:none;">
   <legend style="width:auto;margin:0;padding:0;" class="form-control-group-legend ">
   						<span class="_legend_" style="margin-left:0.5rem;margin-right:0.5rem;" ></span>
   						<i class="fas fa-fw fa-lock-open _locked_" style="margin-right:0.5rem;" 
   						title="<s:text name="globals.lockerExplain" />" ></i>
   	</legend>
   	<input  type="text" style="width:90%;border:2px dashed grey;"
   			class="_form_input_  card  modals-form-control bg-light small " 
   			placeholder="<s:text name="Items.uploadItems.dropFileToUpload"/>"
   			/>
   	<center>
	<table class=" table table-striped" style="font-size:0.8rem;" >
		<tbody class="_files_list_" style="display:none"></tbody>
	</table>
   	</center>
   </fieldset>
   
   
  <fieldset id="_commons_popups_formtext_input_template_"  class="form-control-group card  modals-form-control" style="display:none;">
   <legend style="width:auto;margin:0;padding:0;" class="form-control-group-legend ">
   						<span class="_legend_" style="margin-left:0.5rem;margin-right:0.5rem;" ></span>
   						<i class="fas fa-fw fa-lock-open _locked_" style="margin-right:0.5rem;" 
   						title="<s:text name="globals.lockerExplain" />" ></i>
   	</legend>
   <input  type="text" style="width:90%;border:none;"
   			class="_form_input_  card  modals-form-control bg-light border-0 small " 
   			/>
   
   		
   </fieldset>
   
   <fieldset id="_commons_popups_formdropdown_input_template_"  class="form-control-group card  modals-form-control" style="display:none;">
     <legend style="width:auto;margin:0;padding:0;" class="form-control-group-legend ">
   						<span class="_legend_" style="margin-left:0.5rem;margin-right:0.5rem;" ></span>
   						<i class="fas fa-fw fa-lock-open _locked_" style="margin-right:0.5rem;" 
   							title="<s:text name="globals.lockerExplain"/>" ></i>
   	</legend>
   	<select title="" class="modals-form-control-dropdown modals-form-control form-control bg-light border-0 small _form_input_ " style="width:90%" >
   	</select>
   </fieldset>
   
   <fieldset id="_commons_popups_formmultiselect_input_template_" title=""  class="form-control-group card card-deck col-sm-2  modals-form-control" style="display:none;">
   		<legend style="width:auto;margin:0;padding:0;" class="form-control-group-legend ">
   						<span class="_legend_" style="margin-left:0.5rem;margin-right:0.5rem;" ></span>
   						<i class="fas fa-fw fa-lock-open _locked_" style="margin-right:0.5rem;" 
   							title="<s:text name="globals.lockerExplain"/>" ></i>
   	</legend>
   </fieldset>
   
   
   
   
   
   
   
<script type="text/javascript">

MxGuiPopups={}
MxGuiPopups.newBlankPopup=_commons_popups_makeBlankPopup;
MxGuiPopups.newTextInputPopup=_commons_popups_makeTextInputPopup;
MxGuiPopups.newDropdownInputPopup=_commons_popups_makeDropdownInputPopup;
MxGuiPopups.newMultiInputsPopup=_commons_popups_makeMultiInputsPopup;

</script>
  
