<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<script type="text/javascript">


// ---------------- DOWNLOAD -----------------

MxGuiQuizModal={}

// showQuizGeneratorForm : build CSV columns table
function _makeNewFieldsChoiceRow(parentTbl,previsuNode,quizConfTablePres,quizConfTableQuestion) {
 	
	let newRow=document.createElement("tr");
	newRow.classList.add("fields-list-row");
	
	// current list
	let curListCell=document.createElement("td");
	curListCell.classList.add("quiz-fields-list-cell");
	curListCell.style="padding-top:0;padding-bottom:0;"
	newRow.appendChild(curListCell);	
	let contentsList=document.createElement("div");
	curListCell.appendChild(contentsList);
	contentsList.classList.add("quiz-fields-list-contents");
	
	// dropdown to add an element
	let fieldsChoiceCol=document.createElement("td");
	fieldsChoiceCol.style="padding-top:0;padding-bottom:0;"
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
		G_currentConfig=generateJsonConfig(previsuNode,quizConfTablePres,quizConfTableQuestion);		
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
	removeButtonCol.style="padding-top:0;padding-bottom:0;"
	newRow.appendChild(removeButtonCol);	
	delButton=document.getElementById("quiz_button_del").cloneNode(true);
	removeButtonCol.appendChild(delButton);
	delButton.style.display="block";
	delButton.onclick=function(e) {
		parentTbl.removeChild(newRow);
	}
	
	
	return newRow;	
	
}

function generateJsonConfig(previsuNode,quizConfTablePres,quizConfTableQuestion) {
	
	jsonStruct={}
	
	// pres fields
	let presFields=[]
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
	let quizFieldsQuestion=[]
	for (var fieldsListIdx=0;fieldsListIdx<quizConfTableQuestion.children.length;fieldsListIdx++) {
		let curValues=[]
		let contents=quizConfTableQuestion.children[fieldsListIdx].querySelector(".quiz-fields-list-contents");
		let values=contents.innerHTML.split(",");
		for (valueIdx in values) {
			let value=stripStr(values[valueIdx]);
			if (value.length>0) { curValues.push(value); }
		}
		if (curValues.length>0) {
			quizFieldsQuestion.push(curValues);
		}
	}
	jsonStruct["question_fields"]=quizFieldsQuestion;
	
	// config
	let config={}
	config["quiz_title"]=previsuNode.querySelector("._quiz-config-quiz_title_").value;
	config["quiz_description"]=previsuNode.querySelector("._quiz-config-quiz_description_").value;
	//config["quiz_pic"]=previsuNode.querySelector("._quiz-config-quiz_pic_").value;
	config["subject_title"]=previsuNode.querySelector("._quiz-config-subject_title_").value;
	config["subject_description"]=previsuNode.querySelector("._quiz-config-subject_description_").value;
	config["subject_pic"]=previsuNode.querySelector("._quiz-config-subject_pic_").value;
	config["author_name"]=previsuNode.querySelector("._quiz-config-author_name_").value;
	config["author_email"]=previsuNode.querySelector("._quiz-config-author_email_").value;
	config["author_website"]=previsuNode.querySelector("._quiz-config-author_website_").value;
	config["author_pic"]=previsuNode.querySelector("._quiz-config-author_pic_").value;
	config["duration"]=previsuNode.querySelector("._quiz-config-duration_").value;
	config["duration_per_img_msec"]=previsuNode.querySelector("._quiz-config-duration_").value*1000;
	config["language"]=previsuNode.querySelector("._quiz-config-language_").value;
	config["nb_answers"]=previsuNode.querySelector("._quiz-config-nb_answers_").value;
	config["nb_questions"]=previsuNode.querySelector("._quiz-config-nb_questions_").value;
	
   	jsonStruct["config"]=config;
	
   	return jsonStruct;
}

function areEmptyPresFields(configJsonStruct) {
	for (idx in configJsonStruct["pres_fields"]) {
		curTermsList=configJsonStruct["pres_fields"][idx];
		for (curTermIdx in curTermsList) {
			curTerm=curTermsList[curTermIdx];
			if (curTerm.length>0) { return false; }
		} 		
	}
	return true;
}
function areEmptyQuestionFields(configJsonStruct) {
	for (idx in configJsonStruct["question_fields"]) {
		curTermsList=configJsonStruct["question_fields"][idx];
		for (curTermIdx in curTermsList) {
			curTerm=curTermsList[curTermIdx];
			if (curTerm.length>0) { return false; }
		} 		
	}
	return true;
}

var G_currentConfig={};

function extractTermsList(configJsonStruct) {
	
	let selectedTermNames=[];
	for (idx in configJsonStruct["pres_fields"]) {
		curTermsList=configJsonStruct["pres_fields"][idx];
		for (curTermIdx in curTermsList) {
			curTerm=curTermsList[curTermIdx];
			if (!selectedTermNames.includes(curTerm)) {
				selectedTermNames.push(curTerm);
			}
		} 		
	}
	for (idx in configJsonStruct["question_fields"]) {
		curTermsList=configJsonStruct["question_fields"][idx];
		for (curTermIdx in curTermsList) {
			curTerm=curTermsList[curTermIdx];
			if (!selectedTermNames.includes(curTerm)) {
				selectedTermNames.push(curTerm);
			}
		} 		
	}
	return selectedTermNames;
}

MxGuiQuizModal.showQuizGeneratorForm=function() {
	// body
	let previsuNode=document.getElementById('quiz_contents_previsu_body_download').cloneNode(true);
	previsuNode.style.display='block';
	
	let nbEntries=previsuNode.querySelector("._maxNbEntries_");
	
	let quizConfTablePres=previsuNode.querySelector("._quiz_columns_tbl_pres_");
	let quizConfTableQuestion=previsuNode.querySelector("._quiz_columns_tbl_questions_");
	
	// add Presentation row button
	let addPresRowBtn=previsuNode.querySelector('._addPresItemBtn_');
	addPresRowBtn.onclick=function(e) {
		quizConfTablePres.appendChild(_makeNewFieldsChoiceRow(quizConfTablePres,previsuNode,quizConfTablePres,quizConfTableQuestion));
	}

	// add Question row button
	let addQuestionBtn=previsuNode.querySelector('._addQuestionItemBtn_');
	addQuestionBtn.onclick=function(e) {
		quizConfTableQuestion.appendChild(_makeNewFieldsChoiceRow(quizConfTableQuestion,previsuNode,quizConfTablePres,quizConfTableQuestion));
	}
	
	console.log(G_currentConfig["config"]);
	// Quiz info default values
	if (Object.keys(G_currentConfig).length==0) { 
		G_currentConfig["config"]={}
		G_currentConfig["config"]["nb_questions"]="<s:text name="Items.quiz.config.nb_questions.default" />"; 
		G_currentConfig["config"]["quiz_title"]="<s:text name="Items.quiz.config.quiz_title.default" /> "+"<s:property value='currentUserProfile.catalogVocabulary.name'/> ";
		G_currentConfig["config"]["quiz_description"]="<s:text name="Items.quiz.config.quiz_description.default" /> "+"<s:property value='currentUserProfile.catalogVocabulary.name'/> ";
		let selectedFiltersNames=MxGuiHeader.getSelectedFiltersNames();
		if (selectedFiltersNames.length>0) {
			
			let filtersList="";
			for (var i in selectedFiltersNames) { 
				if (filtersList.length>0) { filtersList+=", "; }
				filtersList+=selectedFiltersNames[i];
			}
			G_currentConfig["quiz_description"]+=" ("+filtersList+")";		
		}
		G_currentConfig["config"]["quiz_pic"]="<s:text name="Items.quiz.config.quiz_pic.default" />";
		G_currentConfig["config"]["subject_title"]="<s:property value='currentUserProfile.catalogVocabulary.name'/> ";
		G_currentConfig["config"]["subject_description"]="<s:property value='currentUserProfile.catalogVocabulary.comment'/> ";
		G_currentConfig["config"]["subject_pic"]=_curCatalogDesc.thumbnailUrl;
		G_currentConfig["config"]["author_name"]="<s:property value='currentUserProfile.nickname'/>";
		G_currentConfig["config"]["author_email"]="<s:property value='currentUserProfile.name'/>";
		G_currentConfig["config"]["author_website"]="";
		G_currentConfig["config"]["author_pic"]="";	
		G_currentConfig["config"]["duration"]="<s:text name="Items.quiz.config.duration.default" />";
		G_currentConfig["config"]["language"]="<s:text name="Items.quiz.config.language.default" />";
		G_currentConfig["config"]["nb_answers"]="<s:text name="Items.quiz.config.nb_answers.default" />";
		
	}
	
	
	previsuNode.querySelector("._quiz-config-nb_questions_").value=G_currentConfig["config"]["nb_questions"];	
	previsuNode.querySelector("._quiz-config-quiz_title_").value=G_currentConfig["config"]["subject_title"];
	previsuNode.querySelector("._quiz-config-quiz_description_").value=G_currentConfig["config"]["subject_description"];	
	//previsuNode.querySelector("._quiz-config-quiz_pic_").value=G_currentConfig["subject_pic"];
	previsuNode.querySelector("._quiz-config-subject_title_").value=G_currentConfig["config"]["subject_title"];
	previsuNode.querySelector("._quiz-config-subject_description_").value=G_currentConfig["config"]["subject_description"];
	previsuNode.querySelector("._quiz-config-subject_pic_").value=G_currentConfig["config"]["subject_pic"];
	previsuNode.querySelector("._quiz-config-author_name_").value=G_currentConfig["config"]["author_name"];
	previsuNode.querySelector("._quiz-config-author_email_").value=G_currentConfig["config"]["author_email"];
	previsuNode.querySelector("._quiz-config-author_website_").value=G_currentConfig["config"]["author_website"];
	previsuNode.querySelector("._quiz-config-author_pic_").value=G_currentConfig["config"]["author_pic"];
	previsuNode.querySelector("._quiz-config-duration_").value=G_currentConfig["config"]["duration"];
	previsuNode.querySelector("._quiz-config-language_").value=G_currentConfig["config"]["language"];
	previsuNode.querySelector("._quiz-config-nb_answers_").value=G_currentConfig["config"]["nb_answers"];
	
	
	let noPres=true;
	let noQuestion=true;
	
	// fill-in pres fields if any value alreay entered by user previously
	for (var fieldsListIdx in G_currentConfig["pres_fields"]) {
		let fieldsListArray=G_currentConfig["pres_fields"][fieldsListIdx];
		let fieldsListStr="";
		for (var fieldIdx in fieldsListArray) {
			if (fieldsListStr.length>0) { fieldsListStr=fieldsListStr+",";}
			fieldsListStr=fieldsListStr+fieldsListArray[fieldIdx];
		}
		let curFieldsRow=_makeNewFieldsChoiceRow(quizConfTablePres,previsuNode,quizConfTablePres,quizConfTableQuestion)
		let fieldList=curFieldsRow.querySelector(".quiz-fields-list-contents");
		fieldList.innerHTML=fieldsListStr;
		quizConfTablePres.appendChild(curFieldsRow);
		noPres=false;
	}
	
	// fill-in question fields if any value alreay entered by user previously
	for (var fieldsListIdx in G_currentConfig["question_fields"]) {
		let fieldsListArray=G_currentConfig["question_fields"][fieldsListIdx];
		let fieldsListStr="";
		for (var fieldIdx in fieldsListArray) {
			if (fieldsListStr.length>0) { fieldsListStr=fieldsListStr+",";}
			fieldsListStr=fieldsListStr+fieldsListArray[fieldIdx];
		}
		let curFieldsRow=_makeNewFieldsChoiceRow(quizConfTableQuestion,previsuNode,quizConfTablePres,quizConfTableQuestion)
		let fieldList=curFieldsRow.querySelector(".quiz-fields-list-contents");
		fieldList.innerHTML=fieldsListStr;
		quizConfTableQuestion.appendChild(curFieldsRow);
		noQuestion=false;
	}
	
	
	if (noPres==true) { quizConfTablePres.appendChild(_makeNewFieldsChoiceRow(quizConfTablePres,previsuNode,quizConfTablePres,quizConfTableQuestion)); } 
	if (noQuestion==true) { quizConfTableQuestion.appendChild(_makeNewFieldsChoiceRow(quizConfTableQuestion,previsuNode,quizConfTablePres,quizConfTableQuestion)); }
	
	// footer
	let previsuNodeFooter=document.getElementById('quiz_contents_previsu_download_footer').cloneNode(true);
	previsuNodeFooter.style.display='block';
	
	
	
	function sendQuizRequest(asJson) {
		let nb_questions=previsuNode.querySelector("._quiz-config-nb_questions_").value;
		let jsonStruct=generateJsonConfig(previsuNode,quizConfTablePres,quizConfTableQuestion);
		G_currentConfig=jsonStruct;
       	let selectedTermNames=extractTermsList(jsonStruct);		
       	if (areEmptyPresFields(jsonStruct) || areEmptyQuestionFields(jsonStruct)) {
       		footer_showAlert(ERROR, "<s:text name="Items.quiz.download_config.error_fields_list_empty" />", null, 30000);
       		return;
       	}
		let query = MxGuiHeader.getCurrentSearchQuery();
		let selectedFiltersNames=MxGuiHeader.getSelectedFiltersNames();
		let sortString = MxGuiHeader.getCurrentSearchSortString();
		let reversedOrder = MxGuiHeader.getCurrentSearchReversedOrder();
		ws_handlers_requestGenerateQuiz(selectedTermNames,query,selectedFiltersNames,sortString,reversedOrder,
									jsonStruct,nb_questions, asJson);
	}
	// Button Generate QCM quiz
	let downloadBtnQcm=previsuNodeFooter.querySelector('._downloadBtnQcm_');
	downloadBtnQcm.onclick=function() { sendQuizRequest(false); }		
	
	// Button Generate JSON quiz
	let downloadBtnJson=previsuNodeFooter.querySelector('._downloadBtnJson_');
	downloadBtnJson.onclick=function() { sendQuizRequest(true); }
	
	// Button Download JSONConfig	
	let downloadBtnJsonConfig=previsuNodeFooter.querySelector('._downloadBtnJsonConfig_');
	downloadBtnJsonConfig.onclick=function() {		
		let jsonStruct=generateJsonConfig(previsuNode,quizConfTablePres,quizConfTableQuestion);
       	console.log(JSON.stringify(jsonStruct));
       	downloadContentsAsFile("quiz_config.json", JSON.stringify(jsonStruct))
       	footer_showAlert(SUCCESS, "<s:text name="Items.quiz.download_config.success" />", [JSON.stringify(jsonStruct)], 30000);
	}
	

	// show
	MxGuiHeader.showInfoModal("<s:text name='Items.downloadItems.asQuiz' />",previsuNode,previsuNodeFooter,"60vw");

}

function updateFormDefaultValue(event,valueName) {
	//console.log('changed:'+valueName+"="+event.target.value);
	G_currentConfig[valueName]=event.target.value;
}
</script> 

 	 	 
	 		 
	 	 
          <label id="quiz_download_label"
 		  	class="_openBtn_ d-none d-sm-inline-block btn-big btn btn-info shadow-sm mx-left-button" 
 		  	onclick="MxGuiQuizModal.showQuizGeneratorForm();" >
 		  	<i class="fas fa-download fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.downloadItems.asQuiz"></s:text>
 		  	 <span title="S.O.S" 
	                	onclick="event.stopPropagation();event.preventDefault();
	                			MxGuiHeader.showInfoModal('<s:text name="Items.quiz.title" />','<s:text name="Items.quiz.help.body" />')">
	                   <i class="mx-help-icon far fa-question-circle" style="color:white"></i>    
	          </span>
 		  </label>
 		  
 		 <div id="quiz_contents_previsu_body_download" style="display:none">
 		  	 
 		  	 	<div style="font-size:0.9rem" ><s:text name="Items.quiz.description" /></div>
 		  	 	 <hr/>
	 		  	<div style="font-size:0.8rem;font-weight:bold"><s:text name="Items.currentSearch" /> : <span id="MxGui.left.quizdownload.nbMatchDocs"></span>  <s:property value='currentUserProfile.catalogVocabulary.itemsCap'/></div> 		  			 		  		 
		  		
		  		<fieldset id="quiz_form" class="quiz-fieldset">
		  		 	<legend class="quiz-fieldset-legend" >
		  		 	<div style="font-size:0.8rem;" ><s:text name="Items.quiz.form_description" /></div>		  		 
		  		 	</legend>
			  		 <center>
			  		 <table class="_quiz_columns_tbl_config_ table table-striped quiz-config-table">
			  		 	<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.nb_questions" /></div></td><td><input type="text" onchange="updateFormDefaultValue(event,'nb_questions');" onkeypress="event.stopPropagation();"  class="form-control bg-light border-0 small quiz-input-text _quiz-config-nb_questions_" /></td></tr>
			  		 	<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.quiz_title" /></div></td><td><input type="text" onchange="updateFormDefaultValue(event,'title');" onkeypress="event.stopPropagation()"  class="form-control bg-light border-0 small quiz-input-text _quiz-config-quiz_title_" /></td></tr>
			  		 	<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.quiz_description" /></div></td><td><input type="text" onchange="updateFormDefaultValue(event,'description');" class="form-control bg-light border-0 small quiz-input-text _quiz-config-quiz_description_" /></td></tr>
			  		 	<!-- picked automatically if left empty -->
			  		 	<!-- tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.quiz_pic" /></div></td><td><input type="text" onchange="updateFormDefaultValue(event,'quiz_pic');" class="form-control bg-light border-0 small quiz-input-text " /></td></tr> -->
			  		 	<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.subject_title" /></div></td><td><input type="text" onchange="updateFormDefaultValue(event,'subject_title');"  class="form-control bg-light border-0 small quiz-input-text _quiz-config-subject_title_" /></td></tr>
			  		 	<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.subject_description" /></div></td><td><input type="text" onchange="updateFormDefaultValue(event,'subject_description');" class="form-control bg-light border-0 small quiz-input-text _quiz-config-subject_description_" /></td></tr>
			  		 	<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.subject_pic" /></div></td><td><input type="text" onchange="updateFormDefaultValue(event,'subject_pic');" class="form-control bg-light border-0 small quiz-input-text _quiz-config-subject_pic_" /></td></tr>
			  		 	<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.author_name" /></div></td><td><input type="text" onchange="updateFormDefaultValue(event,'author_name');" class="form-control bg-light border-0 small quiz-input-text _quiz-config-author_name_" /></td></tr>
						<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.author_email" /></div></td><td><input type="text" onchange="updateFormDefaultValue(event,'author_email');" class="form-control bg-light border-0 small quiz-input-text _quiz-config-author_email_" /></td></tr>
						<tr style="display:none"><td><div class="quiz-input-title"><s:text name="Items.quiz.config.author_website" /></div></td><td><input type="text" onchange="updateFormDefaultValue(event,'author_website');" class="form-control bg-light border-0 small quiz-input-text _quiz-config-author_website_" /></td></tr>
						<tr style="display:none"><td><div class="quiz-input-title"><s:text name="Items.quiz.config.author_pic" /></div></td><td><input type="text" onchange="updateFormDefaultValue(event,'author_pic');" class="form-control bg-light border-0 small quiz-input-text _quiz-config-author_pic_" /></td></tr>
						<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.duration" /></div></td><td><input type="text" onchange="updateFormDefaultValue(event,'duration');" class="form-control bg-light border-0 small quiz-input-text _quiz-config-duration_" /></td></tr>
						<tr style="display:none" ><td><div class="quiz-input-title"><s:text name="Items.quiz.config.language" /></div></td><td><input type="text" onchange="updateFormDefaultValue(event,'language');" class="form-control bg-light border-0 small quiz-input-text _quiz-config-language_" /></td></tr>
						<tr><td><div class="quiz-input-title"><s:text name="Items.quiz.config.nb_answers" /></div></td><td><input type="text" onchange="updateFormDefaultValue(event,'nb_answers');" class="form-control bg-light border-0 small quiz-input-text _quiz-config-nb_answers_" /></td></tr>																		
			  		 </table>
			  		 </center>
		 		</fieldset>
		  		 <fieldset id="quiz_presentation_fields" class="quiz-fieldset">
		  		 	<legend class="quiz-fieldset-legend" >
		  		 	<s:text name="Items.quiz.question_descriptions" />
		  		 	<div style="font-size:0.6rem;"><s:text name="Items.quiz.config.explanation.pres"></s:text></div>
		  		 	</legend>
			  		 <center>
			  		 <table style="margin-top:0rem;margin-bottom:0rem;margin-left:2rem;width:80%;" class="_quiz_columns_tbl_pres_ table table-striped">
			  		 	
			  		 </table>
			  		 
			  		 
			  		 <button  type="button" class="_addPresItemBtn_ btn btn-default btn-sm editable-cancel alert alert-success"
			 			style="margin:0.2em;padding-top:0.2rem;padding-bottom:0.2rem;"
			 			onclick="event.stopPropagation();" >
			 				<i class="fa fa-plus" aria-hidden="true"></i> 				 
		 			</button>
		 			</center>
		 		</fieldset>
		 		<fieldset id="quiz_question_fields" class="quiz-fieldset">
		 			<legend class="quiz-fieldset-legend" >
		 				<s:text name="Items.quiz.question_propositions" />
		 				<div style="font-size:0.6rem;"><s:text name="Items.quiz.config.explanation.question"></s:text></div>
		 			</legend>
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
 		  		
 		  		 <label class="_downloadBtnQcm_ d-none d-sm-inline-block btn-big btn btn-info shadow-sm mx-left-button"  >
 		  				<i class="fas fa-download fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.quiz.generate_quiz_qcm"></s:text>
 		  		</label>
 		  		<label class="_downloadBtnJson_ d-none d-sm-inline-block btn-big btn btn-info shadow-sm mx-left-button" 
 		  				style="background:#999" >
 		  				<i class="fas fa-download fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.quiz.generate_quiz_json"></s:text>
 		  		</label>
 		  		<label class="_downloadBtnJsonConfig_ d-none d-sm-inline-block btn-big btn btn-info shadow-sm mx-left-button"  
 		  			style="background:#ccc">
 		  				<i class="fas fa-download fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.quiz.generate_json_config"></s:text>
 		  		</label>
		  </div>
		  
		  
		  <button id="quiz_button_del" type="button" class="btn btn-default btn-sm editable-cancel alert alert-danger"
 			style="margin:0.2em;display:none;padding-top:0.2rem;padding-bottom:0.2rem;" >
 				<i class="fa fa-times" aria-hidden="true"></i> 				 
 			</button>
 		 
 		  

