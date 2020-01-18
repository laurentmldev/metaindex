<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript">

var csvFields=new Array();
var baseFields=new Array();
var csvMapping=new Array();
var regexTransforms=new Array();

var ELEMENT_CSV_ID='___ELEMENT_NAME___';
var COMMENT_CSV_ID='___ELEMENT_COMMENT___';

var omcAction="<c:url value="/SynchronizeOMCCatalogProcess" />";
var csvAction="<c:url value="/uploadCSVCatalogProcess" />";

<s:iterator value="selectedCommunity.templateElements" var="curBase" >
	// Base Element <s:property value="#curBase.name" />
	baseFields[<s:property value="#curBase.elementId" />]=new Array();
		baseFields[<s:property value="#curBase.elementId" />].name="<s:property value="#curBase.name" />";
		baseFields[<s:property value="#curBase.elementId" />].datasets=new Array();
		<s:iterator value="#curBase.datasets" var="curDataset" >
			// Dataset  <s:property value="#curDataset.name" />
			datasetData=new Array();
			datasetData.id="<s:property value="#curDataset.datasetId" />";
			datasetData.name="<s:property value="#curDataset.name" />";
			datasetData.metadatas=new Array();
			<s:iterator value="#curDataset.metadata" var="curMetadata" >
				// Metadata  <s:property value="#curMetadata.name" />
				metadata=new Array();
				metadata.id="<s:property value="#curMetadata.metadataId" />";
				metadata.name="<s:property value="#curMetadata.name" />";
				metadata.comment="<s:property value="#curMetadata.comment" />";
				metadata.type="<s:property value="#curMetadata.term.datatypeName" />";
				datasetData.metadatas.push(metadata);				
			</s:iterator>			
			baseFields[<s:property value="#curBase.elementId" />].datasets.push(datasetData);			
			// -------------------------
		</s:iterator>
	// ---------------------------------
	
</s:iterator>


function loadFile() {

    CSVfile = document.getElementById("fileLoad");
    document.getElementById("fileName").innerHTML=CSVfile.value;
    document.getElementById("uploadFileName").style.display='block';
    document.getElementById("fileLoad").style.display='none';
    
    var regexCSV = /\.csv$/;
    var regexOMC = /\.omc$/;
    
    // if csv file
    if (regexCSV.test(CSVfile.value.toLowerCase())!=0) {
    	//alert('This is a CSV file')
    	document.getElementById("workzone.uploadCatalog.form").action=csvAction;
        if (typeof (FileReader) == "undefined") {
        	alert("Sorry, this browser does not support HTML5, unable to load file.");
            return
        }
        var reader = new FileReader();
        reader.onload = function (e) 
        { 
        	file_contents = e; 
        	CSVrows = file_contents.target.result.split("\n");
        	parseCSV(CSVrows);
        }
        reader.readAsText(CSVfile.files[0]);
     	        
    }
    // OMC (Open Metaindex Catalog)
    else if (regexOMC.test(CSVfile.value.toLowerCase())!=0) {
    	document.getElementById("workzone.uploadCatalog.form").action=omcAction;
    }
    else {
    	alert('Sorry, unknown file extension for file ' + CSVfile.value);
    }
    
}

function stripStr(str) {
	result= str.replace(/^\s*/,"")
	return result.replace(/\s*$/,"")
}

function parseCSV(CSVrows)
{
	var nbActiveLines=0;
	var commentRegex=/^\s*#\s*/;
	
	for (var curLine=0;curLine<CSVrows.length-1;curLine++)
	{
		var isComment=commentRegex.test(CSVrows[curLine][0])!=0;
		if (!isComment) { nbActiveLines++ };
		
		
		if (curLine==0) 
		{
			
			 if (!isComment) {
				 alert("<s:text name="workzone.uploadcatalog.csv_error_first_line_comment"/>");
				 return false;
			 }
			var curCells = CSVrows[curLine].split(";");
			for (curParamPos=0;curParamPos<curCells.length-1;curParamPos++) {
				var curParam=curCells[curParamPos];
				// remove the # for the first param
				if (curParamPos==0) {
					curParam=curParam.replace(/^\s*#\s*/,"");
					
				}
				curParam=stripStr(curParam);
				//console.log('detected param "'+curParam+'"');
				csvFields.push(curParam);
				
			}
			 
		}
	}
	
	document.getElementById("fileLength").innerHTML=nbActiveLines;	
	document.getElementById("baseElementTable").style.display='block';
	return true;
}

function addRegexTransformRow(id) {
	
	if (typeof regexTransforms[id] == 'undefined') {
		regexTransforms[id]={ matchRegexes:[""], transformRegexes:[""] };
	}
	
	regexTransforms[id].matchRegexes.push("");
	regexTransforms[id].transformRegexes.push("");
}

function removeRegexTransform(id,regexIdx) {
	regexTransforms[id].matchRegexes.splice(regexIdx,1);
	regexTransforms[id].transformRegexes.splice(regexIdx,1);
}

function getRegexTransformRows(id) {

	var addTransformRow="<a href='#' onclick='addRegexTransformRow(\""+id+"\");updateFieldsAssignation();applyRegexTransform(\""+id+"\");' ><s:text name="workszone.uploadcatalog.addTransformRegex" /></a>"
	if (typeof regexTransforms[id] == 'undefined') {
		return "<tr><td>"+addTransformRow+"</td></tr>";		
	}
	
	var matchRegexes=regexTransforms[id].matchRegexes;
	var transformRegexes=regexTransforms[id].transformRegexes;
	
	if (matchRegexes.length==1) {
		return "<tr><td>"+addTransformRow+"</td></tr>";
	}
	
	var exampleStr="<s:text name="workzone.uploadcatalog.sampleTextContents"/>";
	
	fieldsAssignHtml="<tr><td><table class='horizontalLineTable uploadcsvcatalog_regex_table' >";
	fieldsAssignHtml+="<tr><td>Sample Text</td><td class=\"fieldtitle\" style='font-size:0.8em' ><input type='text' title='<s:text name="workzone.uploadcatalog.sampleTextName"/>' id='regexTransformInput_"+id+"' value='"+exampleStr+"' onchange=\"applyRegexTransform('"+id+"')\"/></td><td/></tr>";
	
	for (idxRegexes=0;idxRegexes<matchRegexes.length-1;idxRegexes++) {
		matchRegexStr=matchRegexes[idxRegexes];
		transformRegexStr=transformRegexes[idxRegexes];
		
		fieldsAssignHtml+="<tr>"
			+"<td><input type='text' id=\"regexMatchingPattern_"+id+"_"+idxRegexes+"\" value='"+matchRegexStr+"' placeholder='<s:text name="workzone.uploadcatalog.matchPlaceholder"/>' onchange=\"updateRegexTransform('"+id+"','"+idxRegexes+"');applyRegexTransform('"+id+"')\"> </input></td>"
			+"<td><input type='text' id=\"regexResultPattern_"+id+"_"+idxRegexes+"\" value='"+transformRegexStr+"' placeholder='<s:text name="workzone.uploadcatalog.replacePlaceholder"/>' onchange=\"updateRegexTransform('"+id+"','"+idxRegexes+"');applyRegexTransform('"+id+"')\"></input></td>"
			+"<td><a href='#' onClick=\"removeRegexTransform('"+id+"','"+idxRegexes+"');updateFieldsAssignation();applyRegexTransform('"+id+"');\" >Remove</a></td>"
			+"</tr>";

	}
				
	// Result text	
	fieldsAssignHtml+="<tr><td colspan='3'>"+addTransformRow+"</td></tr>"												
	
	fieldsAssignHtml+="<tr><td colspan='3' class='uploadcsvcatalog_resultregex_row'>\"<span id='regexTransformResult_"+id+"' title='<s:text name="workzone.uploadcatalog.resultText" />' style='font-size:1.2em' >"+exampleStr+"</span>\"</td></tr>";
	fieldsAssignHtml+="</table></td></tr>";
	
	return fieldsAssignHtml;
}

function updateRegexTransform(id,regexIdx) {
	var matchingPatternDocId='regexMatchingPattern_'+id+"_"+regexIdx;
	var matchingPattern=document.getElementById(matchingPatternDocId).value
	regexTransforms[id].matchRegexes[regexIdx]=matchingPattern;
	
	var resultPatternDocId='regexResultPattern_'+id+"_"+regexIdx;
	var resultPattern=document.getElementById(resultPatternDocId).value;
	regexTransforms[id].transformRegexes[regexIdx]=resultPattern;	
}

function applyRegexTransform(id) {
	
	var matchRegexes=regexTransforms[id].matchRegexes;
	var transformRegexes=regexTransforms[id].transformRegexes;
	
	var inputStrDocId='regexTransformInput_'+id;
	var inputTxtEl=document.getElementById(inputStrDocId);
	if (! inputTxtEl) { return;}  
	var inputTxt=inputTxtEl.value;
	
	var curVal=inputTxt;
	
	for (idxRegexes=0;idxRegexes<matchRegexes.length-1;idxRegexes++) {
		var matchingPatternDocId='regexMatchingPattern_'+id+"_"+idxRegexes;
		var matchingPattern=document.getElementById(matchingPatternDocId).value
		
		var resultPatternDocId='regexResultPattern_'+id+"_"+idxRegexes;
		var resultPattern=document.getElementById(resultPatternDocId).value;
		
		curVal = curVal.replace(new RegExp(matchingPattern, "mg"),resultPattern);
	}
	//alert('s/'+matchingPattern+'/'+resultPattern+'/ => '+resultStr)
	var resultStrDocId='regexTransformResult_'+id;	
	var resultStrEl=document.getElementById('regexTransformResult_'+id);
	resultStrEl.innerHTML=curVal;
	
}


function updateFieldsAssignation() {	
	
	var selectedBaseElId=document.getElementById("selectedBaseElementId").value;
	document.getElementById("workzone.uploadCatalog.form.baseElementId").value=selectedBaseElId;
	
	var baseElInfo=baseFields[selectedBaseElId];
	
	fieldsAssignHtml="<table class='compactTable' >";

	// Element Name
	fieldsAssignHtml+="<tr><td>";
	fieldsAssignHtml+="<fieldset >"
		+"<legend >"
			+"<span class=\"fieldtitle\"  style='font-size:1.3em' ><s:property value="selectedCommunity.vocabulary.capElementTraduction" /></span>"											
		+"</legend>";
		
	fieldsAssignHtml+="<table><tr>";
	fieldsAssignHtml+="<td class=\"fieldtitle\" title=\"<s:text name="workzone.element.nameOfCurrent"/> <s:property value="selectedCommunity.vocabulary.capElementTraduction" />\"><s:text name="workzone.element.name"/></td>";	
	fieldsAssignHtml+="<td><select onChange=\"csvMapping['"+ELEMENT_CSV_ID+"']=this.value\">";
	fieldsAssignHtml+="<option value=\"\" >- <s:text name="workzone.uploadcatalog.chooseCSVfield"/> -</option>";
	for (k=0;k<csvFields.size();k++) {
		fieldsAssignHtml+="<option value=\""+k+"\" ";
		if (csvMapping[ELEMENT_CSV_ID]==k) { fieldsAssignHtml+="SELECTED" ; }
		fieldsAssignHtml+=">"+csvFields[k]+"</option>";
	}
	fieldsAssignHtml+="</select></td>";
	fieldsAssignHtml+="</tr>";
	fieldsAssignHtml+=getRegexTransformRows(ELEMENT_CSV_ID);
	
	
	fieldsAssignHtml+="<tr>";
	fieldsAssignHtml+="<td class=\"fieldtitle\" title=\"<s:text name="workzone.element.commentOfCurrent"/> <s:property value="selectedCommunity.vocabulary.capElementTraduction" />\"><s:text name="workzone.element.comment"/></td>";	
	fieldsAssignHtml+="<td><select onChange=\"csvMapping['"+COMMENT_CSV_ID+"']=this.value;\">";
	fieldsAssignHtml+="<option value=\"\" >- <s:text name="workzone.uploadcatalog.chooseCSVfield"/> -</option>";
	for (k=0;k<csvFields.size();k++) {
		fieldsAssignHtml+="<option value=\""+k+"\" ";
		if (csvMapping[COMMENT_CSV_ID]==k) { fieldsAssignHtml+="SELECTED" ; }
		fieldsAssignHtml+=">"+csvFields[k]+"</option>";
	}
	fieldsAssignHtml+="</select></td>";
	fieldsAssignHtml+="</tr>";
	fieldsAssignHtml+=getRegexTransformRows(COMMENT_CSV_ID);
	fieldsAssignHtml+="</table></fieldset>";
	
	fieldsAssignHtml+="</td></tr>";
	
	for (i=0;i<baseElInfo.datasets.size();i++){
		
		var curDataset=baseElInfo.datasets[i];	
		fieldsAssignHtml+="<tr><td class=\"fieldsetTitle uploadcsvcatalog_dataset\" style=\"font-size:1.2em\"><s:property value="selectedCommunity.vocabulary.capDatasetTraduction" /> <span class=\"fieldTitle\">"+curDataset.name+"</span></td></tr>";
		if (curDataset.metadatas.size()==0) { fieldsAssignHtml+="<tr><td><span class=\"negative\"><s:text name="workzone.uploadcatalog.noMetadataInDataset" /></span></td></tr>"; }
		else {
			fieldsAssignHtml+="<tr><td>";
			
			for (j=0;j<curDataset.metadatas.size();j++){
				var curMetadata=curDataset.metadatas[j];
				
				fieldsAssignHtml+="<fieldset >"
										+"<legend >"
											+"<s:property value="selectedCommunity.vocabulary.capMetadataTraduction" /> <span class=\"fieldtitle\"  style='font-size:1.3em' title=\""+curMetadata.comment+"\">"+curMetadata.name+"</span>"											
										+"</legend>";
				
					// Mapping				
					fieldsAssignHtml+="<table id='regexTransformsTable_"+curMetadata.id+"' class='compactTable'>"
						fieldsAssignHtml+="<tr><td><select onChange=\"csvMapping["+curMetadata.id+"]=this.value;applyRegexTransform('"+curMetadata.id+"')\">"
							+"<option value=\"\" >- <s:text name="workzone.uploadcatalog.chooseCSVfield"/> -</option>";
							for (k=0;k<csvFields.size();k++) {
								fieldsAssignHtml+="<option value=\""+k+"\" ";
								if (csvMapping[curMetadata.id]==k) { fieldsAssignHtml+="SELECTED" ; }
								fieldsAssignHtml+=">"+csvFields[k]+"</option>";
							}
						fieldsAssignHtml+="</select> ("+curMetadata.type+")</td></tr>";
						
						fieldsAssignHtml+=getRegexTransformRows(curMetadata.id);
					fieldsAssignHtml+="</table>"
					
				fieldsAssignHtml+="</fieldset>";
			}
			fieldsAssignHtml+="</td></tr>";
		}
				
	}
	fieldsAssignHtml+="</table>";
	document.getElementById("metadataMapping").innerHTML=fieldsAssignHtml;
}
function escape_regex(str) {
	str = str.replace(/\//g,"__SLASH__");
	str = str.replace(/,/g,"__COMA__");
	str = str.replace(/;/g,"__SEMICOLUMN__");
	
	return str;
}

function buildRegexesStrForId(id) {
	
	var matchRegexes=regexTransforms[id].matchRegexes;
	var transformRegexes=regexTransforms[id].transformRegexes;
	
	// if no regex for this metadata, no need to send empty data to the server
	// ... length==1 : don't know why it's not zero, but works fine like this ...
	if (matchRegexes.length==1) { return ""; }
	
	resultRegexesStr=id+"=";			
	for (idx=0; idx<matchRegexes.length-1; idx++) {
		var curMatchRegex=matchRegexes[idx];
		var curTransformRegex=transformRegexes[idx];
		resultRegexesStr+="/"+escape_regex(curMatchRegex)+"/"+escape_regex(curTransformRegex)+"/"+",";
	}	
	resultRegexesStr+=";";
	
	return resultRegexesStr;
}
function prepareMappingForSend()
{
	
	// CSV <-> Metadata mapping assignation
	csvMappingStr="";
	var selectedBaseElId=document.getElementById("selectedBaseElementId").value;
	var baseElInfo=baseFields[selectedBaseElId];

	if (csvMapping[ELEMENT_CSV_ID] != null 
			&& csvMapping[ELEMENT_CSV_ID].length>0) {  csvMappingStr+=ELEMENT_CSV_ID+"="+csvMapping[ELEMENT_CSV_ID]+";";	}
	if (csvMapping[COMMENT_CSV_ID] != null 
			&& csvMapping[COMMENT_CSV_ID].length>0) {  csvMappingStr+=COMMENT_CSV_ID+"="+csvMapping[COMMENT_CSV_ID]+";";	}
	for (i=0;i<baseElInfo.datasets.size();i++){	
		var curDataset=baseElInfo.datasets[i];	
		for (j=0;j<curDataset.metadatas.size();j++){
			var curMetadataId=curDataset.metadatas[j].id;
			if (csvMapping[curMetadataId] != null
					&& csvMapping[curMetadataId].length>0) { csvMappingStr+=curMetadataId+"="+csvMapping[curMetadataId]+";"; }
		}
	}
	document.getElementById("workzone.uploadCatalog.form.csvMappingStr").value=csvMappingStr;
	
	// Metadata Tranform Regexes
	// <metadataId>=/<match>/<replace>/,/<match>/<replace>/;
	var resultRegexesStr="";
	var selectedBaseElId=document.getElementById("selectedBaseElementId").value;
	var baseElInfo=baseFields[selectedBaseElId];	
	
	if (typeof regexTransforms[ELEMENT_CSV_ID] != 'undefined') { resultRegexesStr+=buildRegexesStrForId(ELEMENT_CSV_ID); }
	if (typeof regexTransforms[COMMENT_CSV_ID] != 'undefined') { resultRegexesStr+=buildRegexesStrForId(COMMENT_CSV_ID); }
	
	for (i=0;i<baseElInfo.datasets.size();i++){		
		
		var curDataset=baseElInfo.datasets[i];
		for (j=0;j<curDataset.metadatas.size();j++){
			
			var curMetadata=curDataset.metadatas[j];
			var curMetadataId=curMetadata.id;
			
			if (typeof regexTransforms[curMetadataId] == 'undefined') { continue; }
			resultRegexesStr+=buildRegexesStrForId(curMetadataId);
		}			
	}
	console.log("resultRegexesStr="+resultRegexesStr);
	document.getElementById("workzone.uploadCatalog.form.csvTransformRegexes").value=resultRegexesStr;
}
	
</script>
	
	<form id="workzone.uploadCatalog.form" action="<c:url value="/uploadCatalogProcess" />" method="post" enctype="multipart/form-data"  >
	
			   	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			   	<input type="hidden" name="populatePolicy" value="REPLACE_EXISTING_AND_CREATE_WHEN_NEW" />			   	
			   	<input type="hidden" id="workzone.uploadCatalog.form.csvMappingStr"  name="csvMappingStr" value="" />
		   		<input type="hidden" id="workzone.uploadCatalog.form.baseElementId" name="baseElementId" value="" />
			   	<input type="hidden" id="workzone.uploadCatalog.form.catalogId" name="catalogId" value="0" />
			   	<input type="hidden" id="workzone.uploadCatalog.form.csvTransformRegexes" name="csvTransformRegexes" value="" />
			   	
	<div class="modal_shadow" id="workzone.uploadCatalog.modal" ><div class="modal_back">
	
			<fieldset class="modal font-size:0.8em" style="width:500px;">
					<legend>			
			       		<a href="#close" title="Close" class="modalclose" 
			       				onclick="document.getElementById('workzone.uploadCatalog.modal').style.display='none';">X</a>			       						  		
				  	</legend>	
				  	<h4 class="negative" ><s:text name="workzone.uploadcatalog" /> </h4>
				  	
				  	<table id="uploadFileName"  style="display:none" >
				  		<tr><td class="fieldtitle" ><s:text name="workzone.fromfile"/></td><td><span id="fileName" ></span></td><td> (<span id="fileLength" ></span> <s:text name="workzone.uploadcatalog.entries" />)</td></tr>
			  		</table>
			  		
		   		  <input id="fileLoad" type="file" name="formFile"  onChange="loadFile()" />
			      	
			      
			      <span id="baseElementTable" style="display:none;">
				      <table class='compactTable' >
				      	<tr><td class="fieldtitle"><s:text name="workzone.templatefile"/></td>
				      	 <td>
				      		<select id="selectedBaseElementId" onChange="updateFieldsAssignation()"  >
					      		<option value="" >- Choose Base Element -</option>
						      	<s:iterator value="selectedCommunity.templateElements" var="curBase" >
						      		<option value="<s:property value="#curBase.elementId" />" ><s:property value="#curBase.name" /></option>
						      	</s:iterator>
					      </select>
					    </td></tr>
				      </table>
			      </span>
			      <div id="metadataMapping" style="text-align:center;max-height:400px;overflow:auto" ></div>
			      <span>	
				      <input type="submit" onclick="prepareMappingForSend();" />
			      </span>
		   		</fieldset>
   		
		</div></div>
	</form>
	