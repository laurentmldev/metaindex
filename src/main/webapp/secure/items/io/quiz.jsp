<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<script type="text/javascript">


// ---------------- DOWNLOAD -----------------

MxGuiQuizModal={}

// showQuizGeneratorForm : build CSV columns table
function _makeNewFieldsChoiceRow(parentTbl) {
 	
	let newRow=document.createElement("tr");
	newRow.classList.add("fields-list-row");
	
	// current list
	let curListCell=document.createElement("td");
	curListCell.classList.add("quiz-fields-list-cell");	
	newRow.appendChild(curListCell);	
	let contentsList=document.createElement("div");
	curListCell.appendChild(contentsList);
	contentsList.classList.add("quiz-fields-list-contents");
	
	// dropdown to add an element
	let fieldsChoiceCol=document.createElement("td");
	newRow.appendChild(fieldsChoiceCol);
	let dropdown = document.createElement("select");
	fieldsChoiceCol.appendChild(dropdown);
	dropdown.id="globals.overview.itemsNames.dropdown";
	dropdown.classList.add("modals-form-control-dropdown");
	dropdown.classList.add("modals-form-control");
	dropdown.classList.add("form-control");
	dropdown.classList.add("bg-light");
	dropdown.classList.add("border-0");
	dropdown.classList.add("small");
	
	dropdown.style.width="auto";			
	dropdown.onchange=function(event) {
		let termName=dropdown.options[dropdown.selectedIndex].value;
		if (contentsList.innerHTML.length){
			contentsList.innerHTML+=", ";
		}
		contentsList.innerHTML+=termName;
	}
	
	// add with a blank option
	let option = document.createElement("option");
	option.innerHTML="- click to add a field -";
	dropdown.appendChild(option);
	
	let sortedTermsNames = Object.keys(MxGuiHeader.getCurCatalogTermsList()).sort();
	for (var termIdx=0;termIdx<sortedTermsNames.length;termIdx++) {
		let termName=sortedTermsNames[termIdx];
		//let termDescr=MxGuiHeader.getCurCatalogTermsList()[termName]
		//let termTranslation=mx_helpers_getTermName(termDescr, catalogCard.descr);
		let option = document.createElement("option");
		option.value=termName;
		option.innerHTML=termName;//termTranslation;
		option.id=termName;
		dropdown.appendChild(option);	
	}
	
	// clear button
	let removeButtonCol=document.createElement("td");		
	newRow.appendChild(removeButtonCol);	
	delButton=document.getElementById("quiz_button_del").cloneNode(true);
	removeButtonCol.appendChild(delButton);
	delButton.style.display="block";
	delButton.onclick=function(e) {
		parentTbl.removeChild(newRow);
	}
	
	
	return newRow;	
	
}

MxGuiQuizModal.showQuizGeneratorForm=function() {
	// body
	let previsuNode=document.getElementById('quiz_contents_previsu_body_download').cloneNode(true);
	previsuNode.style.display='block';
	
	let nbEntries=previsuNode.querySelector("._maxNbEntries_");
	
	// Presentation Fields
		let quizConfTablePres=previsuNode.querySelector("._quiz_columns_tbl_pres_");
		
		// add a first line
		quizConfTablePres.appendChild(_makeNewFieldsChoiceRow(quizConfTablePres));
		
		// add row button
		let addPresRowBtn=previsuNode.querySelector('._addPresItemBtn_');
		addPresRowBtn.onclick=function(e) {
			quizConfTablePres.appendChild(_makeNewFieldsChoiceRow(quizConfTablePres));
		}
	
	// Question Fields
		let quizConfTableQuestion=previsuNode.querySelector("._quiz_columns_tbl_questions_");
		
		// add a first line
		quizConfTableQuestion.appendChild(_makeNewFieldsChoiceRow(quizConfTableQuestion));
		
		// add row button
		let addQuestionBtn=previsuNode.querySelector('._addQuestionItemBtn_');
		addQuestionBtn.onclick=function(e) {
			quizConfTableQuestion.appendChild(_makeNewFieldsChoiceRow(quizConfTableQuestion));
		}
		
	
	// Quiz info default values
	previsuNode.querySelector("._quiz-config-quiz_title_").value="<s:text name="Items.quiz.config.quiz_title.default" />";
	previsuNode.querySelector("._quiz-config-quiz_description_").value="<s:text name="Items.quiz.config.quiz_description.default" />";
	previsuNode.querySelector("._quiz-config-quiz_pic_").value="<s:text name="Items.quiz.config.quiz_pic.default" />";
	previsuNode.querySelector("._quiz-config-subject_title_").value="<s:property value='currentUserProfile.catalogVocabulary.name'/> ";
	previsuNode.querySelector("._quiz-config-subject_description_").value="<s:property value='currentUserProfile.catalogVocabulary.comment'/> ";
	previsuNode.querySelector("._quiz-config-subject_pic_").value=_curCatalogDesc.thumbnailUrl;
	previsuNode.querySelector("._quiz-config-author_name_").value="<s:property value='currentUserProfile.nickname'/>";
	previsuNode.querySelector("._quiz-config-author_email_").value="<s:property value='currentUserProfile.name'/>";
	//previsuNode.querySelector("._quiz-config-author_website_").value="";
	//previsuNode.querySelector("._quiz-config-author_pic_").value="";
	previsuNode.querySelector("._quiz-config-duration_").value="<s:text name="Items.quiz.config.duration.default" />";
	previsuNode.querySelector("._quiz-config-language_").value="<s:text name="Items.quiz.config.language.default" />";
	previsuNode.querySelector("._quiz-config-imgprefix_").value="<s:text name="Items.quiz.config.imgprefix.default" />";
	
	// footer
	let previsuNodeFooter=document.getElementById('quiz_contents_previsu_download_footer').cloneNode(true);
	previsuNodeFooter.style.display='block';
	
	// Go button
	let downloadBtn=previsuNodeFooter.querySelector('._downloadBtn_');
	downloadBtn.onclick=function() {
		jsonStruct={}
		
		// pres fields
		presFields=[]
		for (var fieldsListIdx=0;fieldsListIdx<quizConfTablePres.children.length;fieldsListIdx++) {
			curValues=[]
			let contents=quizConfTablePres.children[fieldsListIdx].querySelector(".quiz-fields-list-contents");
			let values=contents.innerHTML.split(",");
			for (valueIdx in values) {
				let value=values[valueIdx];
				curValues.push(stripStr(value));
			}
			if (curValues.length>0) {
				presFields.push(curValues);
			}
		}
		jsonStruct["pres_fields"]=presFields;
		
		// quiz fields
		quizFields=[]
		for (var fieldsListIdx=0;fieldsListIdx<quizConfTableQuestion.children.length;fieldsListIdx++) {
			curValues=[]
			let contents=quizConfTableQuestion.children[fieldsListIdx].querySelector(".quiz-fields-list-contents");
			let values=contents.innerHTML.split(",");
			for (valueIdx in values) {
				let value=stripStr(values[valueIdx]);
				if (value.length>0) { curValues.push(value); }
			}
			if (curValues.length>0) {
				quizFields.push(curValues);
			}
		}
		jsonStruct["quiz_fields"]=quizFields;
		
		// config
		let config={}
		config["quiz_title"]=previsuNode.querySelector("._quiz-config-quiz_title_").value;
		config["quiz_description"]=previsuNode.querySelector("._quiz-config-quiz_description_").value;
		config["quiz_pic"]=previsuNode.querySelector("._quiz-config-quiz_pic_").value;
		config["subject_title"]=previsuNode.querySelector("._quiz-config-subject_title_").value;
		config["subject_description"]=previsuNode.querySelector("._quiz-config-subject_description_").value;
		config["subject_pic"]=previsuNode.querySelector("._quiz-config-subject_pic_").value;
		config["author_name"]=previsuNode.querySelector("._quiz-config-author_name_").value;
		config["author_email"]=previsuNode.querySelector("._quiz-config-author_email_").value;
		config["author_website"]=previsuNode.querySelector("._quiz-config-author_website_").value;
		config["author_pic"]=previsuNode.querySelector("._quiz-config-author_pic_").value;
		config["duration"]=previsuNode.querySelector("._quiz-config-duration_").value;
		config["language"]=previsuNode.querySelector("._quiz-config-language_").value;
		config["imgprefix"]=previsuNode.querySelector("._quiz-config-imgprefix_").value;
       	jsonStruct["config"]=config;
       	
       	console.log(JSON.stringify(jsonStruct));
       	downloadContentsAsFile("quiz_config.json", JSON.stringify(jsonStruct))
       	footer_showAlert(SUCCESS, "<s:text name="Items.quiz.download_config.success" />", null, 30000);
		
	}
	
	
	
	// show
	MxGuiHeader.showInfoModal("<s:text name='Items.downloadItems.asQuiz' />",previsuNode,previsuNodeFooter,"60vw");	
}

</script> 

 	 	 
	 		 
	 	 
          <label id="quiz_download_label"
 		  	class="_openBtn_ d-none d-sm-inline-block btn-big btn btn-info shadow-sm mx-left-button" 
 		  	onclick="MxGuiQuizModal.showQuizGeneratorForm();" >
 		  	<i class="fas fa-download fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.downloadItems.asQuiz"></s:text>
 		  	 <span title="S.O.S" 
	                	onclick="event.stopPropagation();event.preventDefault();
	                			MxGuiHeader.showInfoModal('<s:text name="Items.quiz.title" />','<s:text name="Items.quiz.body" />')">
	                   <i class="mx-help-icon far fa-question-circle" style="color:white"></i>    
	          </span>
 		  </label>
 		  
 		 <div id="quiz_contents_previsu_body_download" style="display:none">
 		  	 
	 		  	<s:text name="Items.currentSearch" /> : <span id="MxGui.left.quizdownload.nbMatchDocs"></span>  <s:property value='currentUserProfile.catalogVocabulary.itemsCap'/> 		  			 		  		 
		  		 <hr/>
		  		
			  		 <table class="_quiz_columns_tbl_config_ table table-striped quiz-config-table">
			  		 	<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.quiz_title" /></div></td><td><input type="text" onkeypress="event.stopPropagation()"  class="form-control bg-light border-0 small quiz-input-text _quiz-config-quiz_title_" /></td></tr>
			  		 	<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.quiz_description" /></div></td><td><input type="text" class="form-control bg-light border-0 small quiz-input-text _quiz-config-quiz_description_" /></td></tr>
			  		 	<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.quiz_pic" /></div></td><td><input type="text" class="form-control bg-light border-0 small quiz-input-text _quiz-config-quiz_pic_" /></td></tr>
			  		 	<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.subject_title" /></div></td><td><input type="text"  class="form-control bg-light border-0 small quiz-input-text _quiz-config-subject_title_" /></td></tr>
			  		 	<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.subject_description" /></div></td><td><input type="text" class="form-control bg-light border-0 small quiz-input-text _quiz-config-subject_description_" /></td></tr>
			  		 	<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.subject_pic" /></div></td><td><input type="text" class="form-control bg-light border-0 small quiz-input-text _quiz-config-subject_pic_" /></td></tr>
			  		 	<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.author_name" /></div></td><td><input type="text" class="form-control bg-light border-0 small quiz-input-text _quiz-config-author_name_" /></td></tr>
						<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.author_email" /></div></td><td><input type="text" class="form-control bg-light border-0 small quiz-input-text _quiz-config-author_email_" /></td></tr>
						<tr style="display:none"><td><div class="quiz-input-title"><s:text name="Items.quiz.config.author_website" /></div></td><td><input type="text" class="form-control bg-light border-0 small quiz-input-text _quiz-config-author_website_" /></td></tr>
						<tr style="display:none"><td><div class="quiz-input-title"><s:text name="Items.quiz.config.author_pic" /></div></td><td><input type="text" class="form-control bg-light border-0 small quiz-input-text _quiz-config-author_pic_" /></td></tr>
						<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.duration" /></div></td><td><input type="text" class="form-control bg-light border-0 small quiz-input-text _quiz-config-duration_" /></td></tr>
						<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.language" /></div></td><td><input type="text" class="form-control bg-light border-0 small quiz-input-text _quiz-config-language_" /></td></tr>
						<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.imgprefix" /></div></td><td><input type="text" class="form-control bg-light border-0 small quiz-input-text _quiz-config-imgprefix_" /></td></tr>						
			  		 </table>
			  		 
		 		
		  		 <fieldset id="quiz_presentation_fields" class="quiz-fieldset">
		  		 	<legend class="quiz-fieldset-legend" ><s:text name="Items.quiz.question_descriptions" /></legend>
			  		 <center>
			  		 <table style="margin-top:1rem;margin-left:2rem;width:80%;" class="_quiz_columns_tbl_pres_ table table-striped">
			  		 	
			  		 </table>
			  		 
			  		 
			  		 <button  type="button" class="_addPresItemBtn_ btn btn-default btn-sm editable-cancel alert alert-success"
			 			style="margin:0.2em;padding-top:0.2rem;padding-bottom:0.2rem;"
			 			onclick="event.stopPropagation();" >
			 				<i class="fa fa-plus" aria-hidden="true"></i> 				 
		 			</button>
		 			</center>
		 		</fieldset>
		 		<fieldset id="quiz_question_fields" class="quiz-fieldset">
		 			<legend class="quiz-fieldset-legend" ><s:text name="Items.quiz.question_propositions" /></legend>
			  		 <center>
			  		 <table style="margin-top:1rem;margin-left:2rem;width:80%;" class="_quiz_columns_tbl_questions_ table table-striped">
			  		 	
			  		 </table>
			  		 
			  		 
			  		 <button  type="button" class="_addQuestionItemBtn_ btn btn-default btn-sm editable-cancel alert alert-success"
			 			style="margin:0.2em;padding-top:0.2rem;padding-bottom:0.2rem;"
			 			onclick="event.stopPropagation();" >
			 				<i class="fa fa-plus" aria-hidden="true"></i> 				 
		 			</button>
		 			</center>
		 		</fieldset>
		  </div>
		  <div id="quiz_contents_previsu_download_footer" style="display:none">
 		  		 <label class="_downloadBtn_ d-none d-sm-inline-block btn-big btn btn-info shadow-sm mx-left-button"  >
 		  				<i class="fas fa-download fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.quiz.generate_json_config"></s:text>
 		  		</label>
		  </div>
		  
		  <button id="quiz_button_del" type="button" class="btn btn-default btn-sm editable-cancel alert alert-danger"
 			style="margin:0.2em;display:none;padding-top:0.2rem;padding-bottom:0.2rem;" >
 				<i class="fa fa-times" aria-hidden="true"></i> 				 
 			</button>
 		 
 		  

