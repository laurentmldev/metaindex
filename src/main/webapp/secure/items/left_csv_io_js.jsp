<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<c:url value="/" var="mxurl"/>

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
function _buildCsvColumnsTable(fileHandle,nbEntriesNode,csvColsTable) {
	
	let reader = new FileReader();
    reader.onload = function (file_contents) 
    { 
    	let CSVrows = file_contents.target.result.split("\n");
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
    reader.readAsText(fileHandle);
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

MxGuiLeftBar.showCsvPrevisu=function(fileHandle) {
	// body
	let previsuNode=document.getElementById('csv_contents_previsu_body').cloneNode(true);
	previsuNode.style.display='block';
	
	let fileName=previsuNode.querySelector("._filename_");
	fileName.innerHTML=fileHandle.files[0].name;
	let nbEntries=previsuNode.querySelector("._nbEntries_");
	let csvColsTable=previsuNode.querySelector("._csv_columns_tbl_");
	
	_buildCsvColumnsTable(fileHandle.files[0],nbEntries,csvColsTable)
	
	// footer
	let previsuNodeFooter=document.getElementById('csv_contents_previsu_footer').cloneNode(true);
	previsuNodeFooter.style.display='block';
	let uploadBtn=previsuNodeFooter.querySelector('._uploadBtn_');
	uploadBtn.onclick=function() {
		let selectedCsvColsDef = _getSelectedCsvColumnsDef(csvColsTable);
		ws_handlers_requestUploadCsvFile(fileHandle,selectedCsvColsDef); 
	}
	
	// show
	MxGuiHeader.showInfoModal('CSV Upload',previsuNode,previsuNodeFooter);
	
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


