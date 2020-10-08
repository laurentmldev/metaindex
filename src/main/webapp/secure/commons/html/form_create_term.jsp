<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
  
  
  
   <!-- Create Term form -->
     <div class="dropdown-list dropdown-menu dropdown-menu-right shadow" 
           		aria-labelledby="createTermDropdown"
           		id="_form_create_term_">
            
            <div class="form-inline mr-auto w-auto navbar-search" >
                <div class="input-group">
                  <input  type="text" class="form-control bg-light border-0 small _details_createTerm_name_" 
                  		style="min-width:50px;margin:0.2rem;font-size:0.7rem;"		
                  		onclick="event.stopPropagation();"
                  		onkeypress="
                  			event.stopPropagation();   
                  			
                  			if (event.which==13||event.keycode==13) {
                  				this.parentNode.querySelector('._create_term_form_ok_button_').onclick(event);
                  			}
                  			"
                  		onkeydown="
                  			event.stopPropagation();                     			
                  			"
                  		placeholder="<s:text name="global.Name" />"               			              		 
                  		>
                  <br/> 
                  <select   class="form-control bg-light border-0 small _details_createTerm_datatype_" 
                  		style="min-width:50px;margin:0.2rem;font-size:0.7rem;"	
                  		onclick="event.stopPropagation();"	
                  		onkeypress="if (event.which==13||event.keycode==13) {
                  			event.stopPropagation();
                  			this.parentNode.querySelector('._create_term_form_ok_button_').onclick(event);
                  			}"						                    		
                  		aria-label="Filter" aria-describedby="basic-addon2"              			              		 
                  		aria-label="Create Term Type" aria-describedby="basic-addon2">
                  		
                  		<!-- Options field by javascript (down this page) -->					
                  </select>	
                  <div class="input-group-append" style="margin:0.2rem">
                    <button class="btn btn-primary _create_term_form_ok_button_" type="button"
                     	onclick="" style="font-size:0.7rem;">
                      <i class="fas fa-check fa-sm"></i>
                    </button>
                    <button class="btn btn-primary _create_term_form_cancel_button_" 
                    	type="button" style="font-size:0.7rem;" >
                      <i class="fa fa-times fa-sm"></i>
                    </button>
                  </div>	                    
                </div>
              </div>
        </div>
        
     
<script type="text/javascript">


function _updateDatatypes(formNode) {
	let createTermTypeButton = formNode.querySelector("._details_createTerm_datatype_");
	clearNodeChildren(createTermTypeButton);
	mx_helpers_FIELDS_DATATYPES.forEach(function(datatype) {
		let option = document.createElement("option");
		option.value=datatype;
		option.innerHTML=datatype;	
		createTermTypeButton.appendChild(option);	
	});
		
}

function _createTerm(formNode,termName,termDatatype,onSuccessCallback,onErrorCallback) {	
	
	function innerSuccessCallback() {
		formNode.querySelector("._details_createTerm_name_").value="";
		onSuccessCallback(formNode,termName,termDatatype);
	}
	if (!formNode.checkTermName(termName)) {
		footer_showAlert(WARNING, 
				 "<s:text name="Catalogs.field.termSyntaxNotGood" /><br/><br/>"
				+"<s:text name="Catalogs.field.termNameForInteroperability" />");
		return;
	}
 	MxApi.requestCreateTerm({
 			"catalogId":MxGuiDetails.getCurCatalogDescription().id,
 			"termName":termName,
 			"termDatatype":termDatatype,
 			"successCallback":innerSuccessCallback,
 			"errorCallback":onErrorCallback
 			});	  
 	
 }


formCreateTerm={}

formCreateTerm.buildNewCreateTermForm=function(rootNodeId,onSuccessCallback,onErrorCallback,onCancelCallback) {
	let newForm = document.getElementById("_form_create_term_").cloneNode(true);
	newForm.id=rootNodeId;
	
	newForm.toggleShowHide=function() {
		if (newForm.style.display=='none') { newForm.show(); }
		else { newForm.hide(); }
	}
	newForm.show=function() { newForm.style.display='block'; }
	newForm.hide=function() { newForm.style.display='none'; }
	
	// okay button
	let okayButton=newForm.querySelector("._create_term_form_ok_button_");
	okayButton.onclick=function(e) {
		_createTerm(
				newForm,
				newForm.querySelector("._details_createTerm_name_").value,
				newForm.querySelector("._details_createTerm_datatype_").value,	 			
	 			onSuccessCallback,
	 			onErrorCallback);		
	}
	
	// cancel button	
	let cancelButton=newForm.querySelector("._create_term_form_cancel_button_");
	cancelButton.onclick=function(e) {
		if (onCancelCallback!=null) { onCancelCallback(newForm); }
	}
	
	newForm.updateDatatypes=function() { _updateDatatypes(newForm); }
	
	newForm.checkTermName=formCreateTerm.checkTermName;
	return newForm;
}


var reTermName = /^[a-zA-Z][a-zA-Z0-9_]{2,}$/;
formCreateTerm.checkTermName=function(termName) {
	if (termName.length==0) { return false; }
	if (termName=="id" || termName=="_id") { return true; }
	return reTermName.test(termName);	
}




</script>