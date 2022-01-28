<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<script type="text/javascript" src="${webAppBaseUrl}/public/commons/deps/sheetjs/shim.js"></script>
<script type="text/javascript" src="${webAppBaseUrl}/public/commons/deps/sheetjs/xlsx.js"></script>

<script type="text/javascript">




//showCsvPrevisu : build a line of the 'CSV-columns table'
function _getColTypeNode(csvColName,checkBox,badTermName,onColsSelectionsChangeFunc,isExcelFile) {
	
	let typeCol=document.createElement("div");
	let colTermNodeSelect=document.createElement("select");
	typeCol.append(colTermNodeSelect);
	
	let commentZone=document.createElement("div");
	commentZone.style["font-size"]="0.8rem";
	commentZone.classList.add("alert-warning");
	typeCol.append(commentZone);
	
	colTermNodeSelect.onchange=function() { checkBox.checked=true; onColsSelectionsChangeFunc(); }
	let found=false;
	let matchingChoiceNode=null;
	let alertExcelDates=false;
	
	let choiceIgnoreNode=document.createElement("option");
	choiceIgnoreNode.value="ignore";
	choiceIgnoreNode.innerHTML="- ignore -";
	choiceIgnoreNode.onclick=function() { 
		clearNodeChildren(commentZone);
		checkBox.checked=false; onColsSelectionsChangeFunc(); 
	}
	if (found==false) { choiceIgnoreNode.selected=true; }
	colTermNodeSelect.appendChild(choiceIgnoreNode);
	
	for (termName in MxGuiHeader.getCurCatalogTermsList()) {
		let choiceNode=document.createElement("option");
		choiceNode.value=termName;
		choiceNode.innerHTML=mx_helpers_getTermName(MxGuiHeader.getCurCatalogDescr().terms[termName],MxGuiHeader.getCurCatalogDescr());
		if (termName==csvColName.toLowerCase()) { 
			choiceNode.selected=true;
			found=true;			
			matchingChoiceNode=choiceNode;
		}		
		choiceNode.onclick=function(e) { clearNodeChildren(commentZone); }
		colTermNodeSelect.appendChild(choiceNode);
	}
	
	
	let idChoiceNode=document.createElement("option");
	idChoiceNode.value="_id";
	idChoiceNode.innerHTML="* ID *";
	colTermNodeSelect.appendChild(idChoiceNode);
	idChoiceNode.onclick=function() {
		clearNodeChildren(commentZone);
		checkBox.checked=true; onColsSelectionsChangeFunc(); 
	}
 	if (csvColName=="_id" || csvColName=="id") {
		idChoiceNode.selected=true;
		found=true;	
		matchingChoiceNode=idChoiceNode;
	}
	
	if (badTermName!=true && found==false) {
		for (datatypeIdx in mx_helpers_FIELDS_DATATYPES) {
			let datatypeStr=mx_helpers_FIELDS_DATATYPES[datatypeIdx];
			let choiceNode=document.createElement("option");
			choiceNode.value=ws_handlers_itemsUploadBuildNewTermRequest(csvColName,datatypeStr);
			choiceNode.innerHTML="<s:text name="Items.uploadItems.newField" />: "+datatypeStr;
			colTermNodeSelect.appendChild(choiceNode);
			choiceNode.onclick=function() {
				clearNodeChildren(commentZone);
				checkBox.checked=true; onColsSelectionsChangeFunc(); 
			}
			
			// use TINY_TEXT as default new data type
			if (datatypeStr=="TINY_TEXT") {
				checkBox.onclick=function() {
					clearNodeChildren(commentZone);
					if (checkBox.checked==true) { choiceNode.selected=true; }
					else { choiceIgnoreNode.selected=true; }
					onColsSelectionsChangeFunc();
				}
				matchingChoiceNode=choiceNode;
			}
							
		}
	}
	
	if (found==true) { 
		checkBox.checked=true;
		
		checkBox.onclick=function() {
			clearNodeChildren(commentZone);
			if (checkBox.checked==true) { matchingChoiceNode.selected=true; }
			else { choiceIgnoreNode.selected=true; }
			onColsSelectionsChangeFunc();
		}
	}
	
	checkBox.getCsvColName=function() { return csvColName; }; 
	checkBox.getTermName=function() { return colTermNodeSelect.value; }
	checkBox.classList.add("_csvColCheck_");	
	
	return typeCol;
}

// showCsvPrevisu : build CSV columns table
function _buildCsvColumnsTable(CSVrows,nbEntriesNode,csvColsTable,onColsSelectionsChangeFunc,isExcelFile) {
 
   	let curLineNb=0;
   	let nbEntries=0;
   	
   	// count total of actual nb entries
   	while (curLineNb<CSVrows.length) {
   		if (	   CSVrows[curLineNb].length>0
   				&& curLineNb>0 // ignore anyway first line which is the header (and might not have a comment '#' marker')
   				&& CSVrows[curLineNb][0]!='#' 
   				&& !CSVrows[curLineNb].match(/^\s*$/)
   				&& !CSVrows[curLineNb].match(/^\s*#/)) {   			
   			nbEntries++;
   		}    			
   		curLineNb++;	    		   		
   	}
   	nbEntriesNode.innerHTML=nbEntries;    	
	    
   	// parse fields names and types
   	let fieldsDefStr=CSVrows[0];
   	fieldsDefStr=stripStr(fieldsDefStr.replace("#",""));
   	let fieldsDefsArray=fieldsDefStr.split(';');
   	if (fieldsDefsArray.length==1) { 
   		fieldsDefsArray=fieldsDefStr.split(','); 
   	}
   	if (fieldsDefsArray.length==1) { 
   		fieldsDefsArray=fieldsDefStr.split('\t'); 
   	}
   	
   	for (curFieldIdx in fieldsDefsArray) {
   		curField=fieldsDefsArray[curFieldIdx];	  
   		let curFieldStr=stripStr(curField)  
   		let badTermName=false;
   		if (!formCreateTerm.checkTermName(curFieldStr)) {   			
   			badTermName=true;
   		} 
   		
   		let newRow=document.createElement("tr");
   		
   		// checkbox selected
   		let colSelected=document.createElement("td");
   		newRow.appendChild(colSelected);
   		let checkBox=document.createElement("input");
   		checkBox.type='checkbox';
   		colSelected.appendChild(checkBox);
   		
   		// field Name
   		let colName=document.createElement("td");
   		newRow.appendChild(colName);
   		colName.innerHTML=curFieldStr;
   		if (badTermName) { 
   			colName.classList.add("alert-danger");
   			let icon=document.createElement("i");
			icon.classList.add("mx-help-icon");
			icon.classList.add("far");
			icon.classList.add("fa-question-circle");
			icon.style.color="grey";
			colName.append(icon);
			icon.onclick=function(event) {
   				
   				let helpText=document.createElement("div");
   				helpText.style["font-size"]="0.7rem";
   				helpText.innerHTML="<s:text name="Catalogs.field.termSyntaxNotGood" /><br/><br/>"
   								   +"<s:text name="Catalogs.field.termNameForInteroperability" />"
   				
   				colName.appendChild(helpText);
   			
   			}
   		}
   		
   		// field type
   		let colType=document.createElement("td");
   		newRow.appendChild(colType);    		   				
		colTypeNode=_getColTypeNode(curFieldStr,checkBox,badTermName,onColsSelectionsChangeFunc,isExcelFile);
   		colType.appendChild(colTypeNode);
	 	
		csvColsTable.appendChild(newRow);
   		
   	}    	

   return nbEntries;
}

//showCsvPrevisu : get list and type/name of selected CSV fields for upload
function _getSelectedCsvColumnsDef(csvColsTable) {
	let csvColsTermsMap={};
	let checkboxes = csvColsTable.querySelectorAll("._csvColCheck_");
	for (idx in checkboxes) {
		checkbox=checkboxes[idx];	
		if (typeof(checkbox)!='object') { continue; }
		//console.log("--- "+checkbox.getCsvColName()+" -> "+checkbox.getTermName());
		let curCsvColName=checkbox.getCsvColName();
		let curTermName=checkbox.getTermName();
		let isChecked=checkbox.checked;
		if (isChecked==true) { csvColsTermsMap[curCsvColName]=curTermName; }
	}
	return csvColsTermsMap;
}

function _showCsvPrevisu(CSVrows,fileNameTxt,isExcelFile,fileHandle) {
	
	let previsuNode=document.getElementById('datafile_contents_previsu_body').cloneNode(true);
	let previsuNodeFooter=document.getElementById('datafile_contents_previsu_footer').cloneNode(true);
	
	// body
	previsuNode.style.display='block';
	
	let fileName=previsuNode.querySelector("._filename_");
	fileName.innerHTML=fileNameTxt;
	let nbEntriesNode=previsuNode.querySelector("._nbEntries_");
	let csvColsTable=previsuNode.querySelector("._csv_columns_tbl_");
	
	let labelWarningIdChangeDocuments = previsuNodeFooter.querySelector("._warning_update_id_");
	let labelWarningNoIdSelected = previsuNodeFooter.querySelector("._warning_update_noid_");
	let selectedCsvColsDef = null;
	function onColsSelectionsChangeFunc() {
		selectedCsvColsDef = _getSelectedCsvColumnsDef(csvColsTable);
		let isIdSelected=false;
		for (csvColName in selectedCsvColsDef) {
			if (selectedCsvColsDef[csvColName]=="_id") {
				isIdSelected=true;
				break;
			}
		}
		if (isIdSelected==true) { 
			labelWarningIdChangeDocuments.style.display="block"; 
			labelWarningNoIdSelected.style.display="none";			
		} else { 
			labelWarningIdChangeDocuments.style.display="none";
			labelWarningNoIdSelected.style.display="block"; 
		}

	}
	
	let nbEntries=_buildCsvColumnsTable(CSVrows,nbEntriesNode,csvColsTable,onColsSelectionsChangeFunc,isExcelFile);
	// ignore first row for excel files. For CSV, it shall be commented out (start with a '#') 
	// so it is already ignored in the count
	if (isExcelFile==true) { nbEntries--; }
	onColsSelectionsChangeFunc();
	
	// footer
	previsuNodeFooter.style.display='block';
	let uploadBtn=previsuNodeFooter.querySelector('._uploadBtn_');
	uploadBtn.onclick=function() {
		footer_showAlert(INFO,"<s:text name="global.pleasewait"/>",null,5000);			
		ws_handlers_requestUploadCsvFile(nbEntries,selectedCsvColsDef,fileHandle, CSVrows /*only used for .ods files*/); 
		ws_handlers_refreshItemsGui();
		MxGuiHeader.hideInfoModal();		

	}
	let uploadDriveBtn=previsuNodeFooter.querySelector('._uploadDriveBtn_');
	uploadDriveBtn.onclick=function() {
		footer_showAlert(INFO,"<s:text name="global.pleasewait"/>",null,5000);	
		
		let onUploadSuccessCallback=function() {}
		let onUploadFailureCallback=function(errorMsg) { footer_showAlert(ERROR, errorMsg); }
		
		// if file is .ods, then send CSV rows (no convincing .ods read lib (TBC)) 
		ws_handlers_uploadFiles(MxGuiHeader.getCurCatalogDescr(),fileHandle.files,
										onUploadSuccessCallback,onUploadFailureCallback);		
		
		MxGuiHeader.hideInfoModal();
		
	}
	
	// show
	MxGuiHeader.showInfoModal("<s:text name="Items.uploadItems.fromDataFile" />",previsuNode,previsuNodeFooter);
	
}


function _handleCsvDataFile(fileHandle) {
	var reader = new FileReader();
	reader.onload = function (file_contents) {
		let fileName = fileHandle.files[0].name;
    	let CSVrows = file_contents.target.result.split("\n");
    	_showCsvPrevisu(CSVrows,fileName,false,fileHandle);
    }
    reader.readAsText(fileHandle.files[0]);
}


function _showChooseCalcSheet(sheetNames,fileName,choiceCallback) {
	
	let choices = [];
	
	for (index in sheetNames) {
		let curSheetName=sheetNames[index];
		choices.push({'value':curSheetName, 'text':curSheetName});
	}
	function handleChoosenSheetName(chosenSheetName) {
		let popup=document.getElementById("chooseCalcPopup");
		popupChoice.id="";
		popupChoice.hide();
		choiceCallback(chosenSheetName,fileName);
		
	}
	let popupChoice = MxGuiPopups.newDropdownInputPopup(choices,"<s:text name="items.uploadItems.chooseSheet" />",handleChoosenSheetName);	
	popupChoice.id="chooseCalcPopup";
	MxGuiHeader.addPopup(popupChoice);
	popupChoice.show();	
}


function _handleExcelDataFile(fileHandle) {

	var f = fileHandle.files[0];
	var fileName=f.name;
	
	var reader = new FileReader();
	reader.onload = function(e) {			
		var data = e.target.result;
		data = new Uint8Array(data);
		var workbook = XLSX.read(data,
				{
			  type: 'array',
			  cellDates: true,
			  cellNF: false,
			  cellText: false
			}
		);
				
		let result = [];
		function handleSheetContents(sheetName,fileName) {
			let isExcelFile=fileName.match(/\.(xlsx?|ods)$/)!=null;
			var csv = XLSX.utils.sheet_to_csv(workbook.Sheets[sheetName], {raw:false});
			if(csv.length){
				//console.log(csv);		    	
				_showCsvPrevisu(csv.split('\n'),fileName,isExcelFile,fileHandle);
			}
		}
		if (workbook.SheetNames.length>1) {
			_showChooseCalcSheet(workbook.SheetNames,fileName,handleSheetContents);
		}
		else {
			 handleSheetContents(workbook.SheetNames[0],fileName);
		}
						
	};
	reader.readAsArrayBuffer(f);
	
	
}

MxGuiLeftBar.handleDataFileToUpload=function(fileHandle) {
	let onUploadSuccessCallback=function() {}
	let onUploadFailureCallback=function(errorMsg) { footer_showAlert(ERROR, errorMsg); }

	if (fileHandle.files.length>1) {				
		
		ws_handlers_uploadFiles(MxGuiHeader.getCurCatalogDescr(),fileHandle.files,
									onUploadSuccessCallback,onUploadFailureCallback);
		return;
	}
	
	// if only one file, if it is a csv or calc file, use to to upload documents
	// otherwise consider it as data files
	let fileName = fileHandle.files[0].name;
	var csvRegex= /(\.csv)$/;
	var xlsRegex= /(\.xls|\.xlsx|\.ods|\.odt)$/;
	
	if (csvRegex.test(fileName)) { _handleCsvDataFile(fileHandle); }
	else if (xlsRegex.test(fileName)) { _handleExcelDataFile(fileHandle); }
	else {
		ws_handlers_uploadFiles(MxGuiHeader.getCurCatalogDescr(),fileHandle.files,
									onUploadSuccessCallback,onUploadFailureCallback);
	}
	
}
//ws_handlers_uploadFiles(catalogDescr,filesList,onCreationSuccessCallback,onCreationFailureCallback);

// ---------------- DOWNLOAD -----------------


// showDownloadCsvPrevisu : build CSV columns table
function _buildCsvDownloadColumnsTable(csvColsTable) {
 	
	for (termName in MxGuiHeader.getCurCatalogTermsList()) {
		
		let newRow=document.createElement("tr");
		
		// checkbox selected
		let colSelected=document.createElement("td");
		newRow.appendChild(colSelected);
		let checkBox=document.createElement("input");
		checkBox.type='checkbox';
		checkBox.classList.add("_csvColCheck_");
		colSelected.appendChild(checkBox);
		checkBox.termName=termName;
		checkBox.checked=true;
		// term Name
		let colName=document.createElement("td");
		newRow.appendChild(colName);
		colName.innerHTML=mx_helpers_getTermName(MxGuiHeader.getCurCatalogDescr().terms[termName],MxGuiHeader.getCurCatalogDescr());
	
		csvColsTable.appendChild(newRow);
	}	
	
	let mainCheckBox=csvColsTable.querySelector("._global_checkbox_");
	mainCheckBox.onclick=function() {
		let checkboxesList = csvColsTable.querySelectorAll("._csvColCheck_");
		for (idx in checkboxesList) {
			curCheckBox=checkboxesList[idx];
			if (typeof(curCheckBox)!='object') { continue; }
			curCheckBox.checked=mainCheckBox.checked;		
		}
	}
	mainCheckBox.checked=true;
}

MxGuiLeftBar.showDownloadCsvPrevisu=function() {
	// body
	let previsuNode=document.getElementById('csv_contents_previsu_body_download').cloneNode(true);
	previsuNode.style.display='block';
	
	let nbEntries=previsuNode.querySelector("._maxNbEntries_");
	let csvColsTable=previsuNode.querySelector("._csv_columns_tbl_");
	
	_buildCsvDownloadColumnsTable(csvColsTable)
	
	// footer
	let previsuNodeFooter=document.getElementById('csv_contents_previsu_download_footer').cloneNode(true);
	previsuNodeFooter.style.display='block';
	
	// Go button
	let downloadBtn=previsuNodeFooter.querySelector('._downloadBtn_');
	downloadBtn.onclick=function() {
		let selectedTermNames=[];
		let checkboxesList = csvColsTable.querySelectorAll("._csvColCheck_");
		for (idx in checkboxesList) {
			curCheckBox=checkboxesList[idx];
			if (typeof(curCheckBox)!='object') { continue; }
			if (curCheckBox.checked) { selectedTermNames.push(curCheckBox.termName); }		
		}	
		
		let query = MxGuiHeader.getCurrentSearchQuery();
		let selectedFiltersNames=MxGuiHeader.getSelectedFiltersNames();
		let sortString = MxGuiHeader.getCurrentSearchSortString();
		let reversedOrder = MxGuiHeader.getCurrentSearchReversedOrder();
		ws_handlers_requestDownloadCsvFile(selectedTermNames,query,selectedFiltersNames,sortString,reversedOrder); 
		
	}
	
	
	
	// show
	MxGuiHeader.showInfoModal("<s:text name='Items.downloadItems.asCsv' />",previsuNode,previsuNodeFooter);	
}
</script> 

 	 	 
 		    			 
		  <!-- Custom FileUpload button -->
 		  <label for="fileUpload"  id="datafile_upload_label" 
 		  	class="_openBtn_ d-none d-sm-inline-block  btn-big btn btn-info shadow-sm mx-left-button"  >
 		  	<i class="fas fa-upload fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.uploadItems.fromDataFile"></s:text>
 		  	 <span title="S.O.S" 
	                	onclick="event.stopPropagation();event.preventDefault();
	                			MxGuiHeader.showInfoModal('<s:text name="help.items.data_upload.title" />','<s:text name="help.items.data_upload.body" />')">
	                   <i class="mx-help-icon far fa-question-circle" style="color:white"></i>    
	          </span>
 		  </label>
 		  
 		  <div id="datafile_contents_previsu_body" style="display:none">
 		  		 <div><span class="_filename_"></span> : <span class="_nbEntries_"></span> <s:text name="Items.serverside.uploadItems.lines" /></div>
 		  		 <table style="margin-top:1rem;" class="_csv_columns_tbl_">
 		  		 	<tr><th style="padding-right:1rem;min-width:2rem;"></th>
 		  		 		<th><s:text name="Items.serverside.uploadItems.fileColumn"/></th>
 		  		 		<th><s:text name="Items.serverside.uploadItems.catalogField"/></th>
 		  		 	</tr>
 		  		 </table>
			</div>
			<div id="datafile_contents_previsu_footer" style="display:none">
				
				<div class="_warning_update_id_" style="margin-bottom:1rem;font-size:0.8rem;display:'none'">
					<s:text name="Items.uploadItems.warningOverridingContents"/>
				</div>
				<div class="_warning_update_noid_" style="margin-bottom:1rem;font-size:0.8rem;display:'none'">
					<s:text name="Items.uploadItems.warningNotOverridingContents"/>
				</div>
				 <label class="_uploadBtn_ d-none d-sm-inline-block btn-big btn btn-info shadow-sm mx-left-button"  >
 		  				<i class="fas fa-upload fa-sm text-white" style="margin-right:1em"></i><s:text name="global.go"></s:text>
 		  		</label>
 		  		<label class="_uploadDriveBtn_ d-none d-sm-inline-block btn-big btn btn-info shadow-sm mx-left-button"  
							style="background:grey">
 		  				<i class="fas fa-upload fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.serverside.uploadItems.importAsRawFile"></s:text>
 		  		</label>
 		  		
			</div>
 		  <!-- not displayed but used for the file input -->
 		 <span style="display:none">	 		 	
		 		 <input id="fileUpload" type="file"
		 		  	accept=".csv,.txt,.xls,.xlsx,.ods,.odt,.xml"
		 		  	name="formFile"  
		 		  	onChange="MxGuiLeftBar.handleDataFileToUpload(this);" />				
 		  </span>
 		  
	 		   
	 		 
	 	 <!-- Custom CSV FileDownload button -->
          <label id="csv_download_label"
 		  	class="_openBtn_ d-none d-sm-inline-block btn-big btn btn-info shadow-sm mx-left-button" 
 		  	onclick="MxGuiLeftBar.showDownloadCsvPrevisu();" >
 		  	<i class="fas fa-download fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.downloadItems.asCsv"></s:text>
 		  	 <span title="S.O.S" 
	                	onclick="event.stopPropagation();event.preventDefault();
	                			MxGuiHeader.showInfoModal('<s:text name="help.items.csv_download.title" />','<s:text name="help.items.csv_download.body" />')">
	                   <i class="mx-help-icon far fa-question-circle" style="color:white"></i>    
	          </span>
 		  </label>
 		  
 		 <div id="csv_contents_previsu_body_download" style="display:none">
 		  	 
	 		  	<s:text name="Items.currentSearch" /> : <span id="MxGui.left.csvdownload.nbMatchDocs"></span>  <s:property value='currentUserProfile.catalogVocabulary.itemsCap'/> 		  			 		  		 
		  		 <hr/>
		  		 <table style="margin-top:1rem;margin-left:2rem;" class="_csv_columns_tbl_">
		  		 	<tr><th style="padding-right:1rem;">
		  		 		<input class="_global_checkbox_" type="checkbox" /></th><th><s:text name="Items.downloadItems.csvSelectedCols" /></th> 
		  		 	</tr>
		  		 </table>
		  </div>
		  <div id="csv_contents_previsu_download_footer" style="display:none">
 		  		 <label class="_downloadBtn_ d-none d-sm-inline-block btn-big btn btn-info shadow-sm mx-left-button"  >
 		  				<i class="fas fa-download fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.downloadItems.generate"></s:text>
 		  		</label>
		  </div>
 		 
 		  

