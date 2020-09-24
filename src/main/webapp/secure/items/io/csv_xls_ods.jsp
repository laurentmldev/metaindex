<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<c:url value="/" var="mxurl"/>

<script type="text/javascript" src="${mxurl}public/commons/deps/sheetjs/shim.js"></script>
<script type="text/javascript" src="${mxurl}public/commons/deps/sheetjs/xlsx.js"></script>

<script type="text/javascript">

//showCsvPrevisu : build a line of the 'CSV-columns table'
function _getColTypeNode(csvColName,checkBox) {
	let colTermNodeSelect=document.createElement("select");
	colTermNodeSelect.onchange=function() { checkBox.checked=true; }
	let found=false;
	for (termName in MxGuiHeader.getCurCatalogTermsList()) {
		let choiceNode=document.createElement("option");
		choiceNode.value=termName;
		choiceNode.innerHTML=mx_helpers_getTermName(MxGuiHeader.getCurCatalogDescr().terms[termName],MxGuiHeader.getCurCatalogDescr());
		if (termName==csvColName) { 
			choiceNode.selected=true;
			found=true;
		}		
		colTermNodeSelect.appendChild(choiceNode);
	}
	
	let idChoiceNode=document.createElement("option");
	idChoiceNode.value="_id";
	idChoiceNode.innerHTML="- ID -";
	colTermNodeSelect.appendChild(idChoiceNode);
	idChoiceNode.onclick=function() { checkBox.checked=true; }
	if (csvColName=="_id") {
		idChoiceNode.selected=true;
		found=true;
	}
	
	let choiceIgnoreNode=document.createElement("option");
	choiceIgnoreNode.value="ignore";
	choiceIgnoreNode.innerHTML="- ignore -";
	choiceIgnoreNode.onclick=function() { checkBox.checked=false; }
	if (found==false) { choiceIgnoreNode.selected=true; }
	colTermNodeSelect.appendChild(choiceIgnoreNode);
	
	
	for (datatypeIdx in mx_helpers_FIELDS_DATATYPES) {
		let datatypeStr=mx_helpers_FIELDS_DATATYPES[datatypeIdx];
		let choiceNode=document.createElement("option");
		choiceNode.value="__new__"+datatypeStr;
		choiceNode.innerHTML=datatypeStr+" (new)";
		colTermNodeSelect.appendChild(choiceNode);
		choiceNode.onclick=function() { checkBox.checked=true; }
		
		// use TINY_TEXT as default new data type
		if (datatypeStr=="TINY_TEXT") {
			checkBox.onclick=function() {
				if (checkBox.checked==true) { choiceNode.selected=true; }
				else { choiceIgnoreNode.selected=true; }
			}
		}
	}
	
	if (found==true) { checkBox.checked=true; }
	
	checkBox.getCsvColName=function() { return csvColName; }; 
	checkBox.getTermName=function() { return colTermNodeSelect.value; }
	checkBox.classList.add("_csvColCheck_");	
	
	return colTermNodeSelect;
}

// showCsvPrevisu : build CSV columns table
function _buildCsvColumnsTable(CSVrows,nbEntriesNode,csvColsTable) {
 
   	let curLineNb=0;
   	let nbEntries=0;
   	
   	// count total nb entries
   	while (curLineNb<CSVrows.length) {
   		if (	   CSVrows[curLineNb].length>0 
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
   		
   		// field type
   		let colType=document.createElement("td");
   		newRow.appendChild(colType);    		
   		colTypeNode=_getColTypeNode(curFieldStr,checkBox);
   		colType.appendChild(colTypeNode);
   		    		
   		csvColsTable.appendChild(newRow);
   	}    	
   
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

function _showCsvPrevisu(CSVrows,fileNameTxt) {
	// body
	let previsuNode=document.getElementById('datafile_contents_previsu_body').cloneNode(true);
	previsuNode.style.display='block';
	
	let fileName=previsuNode.querySelector("._filename_");
	fileName.innerHTML=fileNameTxt;
	let nbEntries=previsuNode.querySelector("._nbEntries_");
	let csvColsTable=previsuNode.querySelector("._csv_columns_tbl_");
	
	_buildCsvColumnsTable(CSVrows,nbEntries,csvColsTable)
	
	// footer
	let previsuNodeFooter=document.getElementById('datafile_contents_previsu_footer').cloneNode(true);
	previsuNodeFooter.style.display='block';
	let uploadBtn=previsuNodeFooter.querySelector('._uploadBtn_');
	uploadBtn.onclick=function() {
		footer_showAlert(INFO,"<s:text name="global.pleasewait"/>",null,5000);
		let selectedCsvColsDef = _getSelectedCsvColumnsDef(csvColsTable);
		ws_handlers_requestUploadCsvFile(CSVrows,selectedCsvColsDef); 
		ws_handlers_refreshItemsGui();
		MxGuiHeader.hideInfoModal();		
	}
	
	// show
	MxGuiHeader.showInfoModal('CSV Upload',previsuNode,previsuNodeFooter);
	
}


function _handleCsvDataFile(fileHandle) {
	var reader = new FileReader();
	reader.onload = function (file_contents) {
		let fileName = fileHandle.files[0].name;
    	let CSVrows = file_contents.target.result.split("\n");    	
    	_showCsvPrevisu(CSVrows,fileName);
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
	let popupChoice = MxGuiPopups.newDropdownInputPopup(choices,"Choose Sheet from your file",handleChoosenSheetName);	
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
		var workbook = XLSX.read(data,{ type:'array'});		
		let result = [];
		function handleSheetContents(sheetName,fileName) {
			var csv = XLSX.utils.sheet_to_csv(workbook.Sheets[sheetName]);
			if(csv.length){
				_showCsvPrevisu(csv.split('\n'),fileName);
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
	let fileName = fileHandle.files[0].name;
	var csvRegex= /(\.txt|\.csv)$/;
	var xlsRegex= /(\.xls|\.xlsx|\.ods|\.odt|\.xml)$/;
	
	if (csvRegex.test(fileName)) { _handleCsvDataFile(fileHandle); }
	else if (xlsRegex.test(fileName)) { _handleExcelDataFile(fileHandle); }
	else {
		alert('this kind of file is not supported sorry');
	}
	
}


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
		let selectedFiltersNames=MxGuiLeftBar.getSelectedFiltersNames();
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
 		  	class="_openBtn_ d-none d-sm-inline-block btn btn-sm btn-info shadow-sm mx-left-button"  >
 		  	<i class="fas fa-upload fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.uploadItems.fromDataFile"></s:text>
 		  	 <span title="S.O.S" 
	                	onclick="event.stopPropagation();event.preventDefault();
	                			MxGuiHeader.showInfoModal('<s:text name="help.items.data_upload.title" />','<s:text name="help.items.data_upload.body" />')">
	                   <i class="mx-help-icon far fa-question-circle" style="color:white"></i>    
	          </span>
 		  </label>
 		  
 		  <div id="datafile_contents_previsu_body" style="display:none">
 		  		 <div><span class="_filename_"></span> : <span class="_nbEntries_"></span> entries</div>
 		  		 <table style="margin-top:1rem;" class="_csv_columns_tbl_">
 		  		 	<tr><th style="padding-right:1rem;min-width:2rem;"></th><th>Csv Column</th><th>Catalog Field</th></tr>
 		  		 </table>
			</div>
			<div id="datafile_contents_previsu_footer" style="display:none">
 		  		 <label class="_uploadBtn_ d-none d-sm-inline-block btn btn-sm btn-info shadow-sm mx-left-button"  >
 		  				<i class="fas fa-upload fa-sm text-white" style="margin-right:1em"></i><s:text name="global.go"></s:text>
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
 		  	class="_openBtn_ d-none d-sm-inline-block btn btn-sm btn-info shadow-sm mx-left-button" 
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
 		  		 <label class="_downloadBtn_ d-none d-sm-inline-block btn btn-sm btn-info shadow-sm mx-left-button"  >
 		  				<i class="fas fa-download fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.downloadItems.generate"></s:text>
 		  		</label>
		  </div>
 		 
 		  

