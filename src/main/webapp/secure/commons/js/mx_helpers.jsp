<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>  


 <script type="text/javascript" >
 
 
  function clearNodeChildren(node) {
	var rangeItems = document.createRange();
	rangeItems.selectNodeContents(node);
	rangeItems.deleteContents();	   
  }	
  
  // capitalize first letter of each word
  function capWords(str) {
	  if (str==null) { return null; }
	  return str.replace(/\w\S*/g, function(txt){
	        return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
	    });
  	
  }
  
  function bgTransit(node,transitbgcolor) {
	  if (transitbgcolor==null) { transitbgcolor="yellow"; }
	  let bgColor = $(node).css('background-color');      
	  $(node).css('background-color', transitbgcolor);
	  setTimeout(function(){
		  $(node).css('background-color', bgColor);
		  node.classList.add('editable-bg-transition');
	      setTimeout(function(){
	    	  node.classList.remove('editable-bg-transition');  
	      }, 1700);
	  }, 10);	  
  }
  
  function copyToClipBoard(text) {
	  navigator.clipboard.writeText(text);	  
  }
  function redirectToPage(url) {
	  window.location.href=url;
  }
  
  function scrollTo(anchorName) {
    location.hash = "#" + anchorName;
  }
  
  function str2json(jsonStr) {	  
	  if (jsonStr.length==0) { jsonStr="{}"; }
	  return JSON.parse(jsonStr);	  
  }
  function json2str(jsonObj) {
	  return JSON.stringify(jsonObj);
  }
  
  
  function dumpStructure(struct,indent) {
	  if (indent==null) { indent=""; }
	  for (key in struct) {
		  let val=struct[key];
		  if (		 typeof val === 'string' 
				  || typeof val === 'number' 
				  || typeof val === 'boolean') { console.log(indent+key+":"+val); }
		  else if (typeof val === 'object') {
			  console.log(indent+key+":"); 
			  dumpStructure(val,indent+"	"); 
		   }
		  else { console.log(indent+key+" -> "+typeof val); }
	  }
  }
  
 function findAncestorNode (el, className) {
	 let parent = el.parentNode;
	 if (parent===null) { return null; }
	 if (parent.className.indexOf(className) >= 0) { return parent; }
	 return findAncestorNode(parent,className);
    
}
 
 function downloadContentsAsFile(filename, text) {
	    var pom = document.createElement('a');
	    pom.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
	    pom.setAttribute('download', filename);

	    if (document.createEvent) {
	        var event = document.createEvent('MouseEvents');
	        event.initEvent('click', true, true);
	        pom.dispatchEvent(event);
	    }
	    else {
	        pom.click();
	    }
	}

function stripStr(str) { return str.replace(/^\s*/,"").replace(/\s*$/,""); }

function array2str(arrayVal) {
	 let strVal="";
	 for (var i=0;i<arrayVal.length;i++) {
		 if (i>0) { strVal+=","; }
		 strVal+=arrayVal[i];
	 }
	 console.log("array2str ["+arrayVal.length+"] --> '"+strVal+"'")
	 return strVal;	 
}

// receivedItemContentsCallback(itemDesc)
// where itemDesc is null when not found
function mx_helpers_getItemDetailsById(itemId, receivedItemContentsCallback) {

	retrieveItemsSuccess=function(itemsAnswerMsg) {
		if (itemsAnswerMsg.totalHits==0) {
			console.log("no such item : '"+itemId+"'");
			receivedItemContentsCallback(null);
			return;
		}
		if (itemsAnswerMsg.totalHits>1) { 
			console.log("more than one item matching : '"+itemId+"'");
			receivedItemContentsCallback(null);
			return;
		}
		receivedItemContentsCallback(itemsAnswerMsg.items[0]);
	}
	
	 retrieveItemsError=function(msg) { footer_showAlert(ERROR, msg.rejectMessage); }
	 
	 MxApi.requestCatalogItems({"query":"_id:"+itemId,
		 						"successCallback":retrieveItemsSuccess,
		 						"errorCallback":retrieveItemsError});
}

// matches Metaindex DB table 'catalog_terms', enum of field 'datatype'
// used to generate dropdown box in Field type
//var mx_helpers_FIELDS_DATATYPES = [	"TINY_TEXT","RICH_TEXT","DATE","INTEGER","FLOAT","PAGE_URL","IMAGE_URL","AUDIO_URL","VIDEO_URL","GEO_POINT",
//									,"LINK"];
var mx_helpers_FIELDS_DATATYPES = [	"TINY_TEXT","DATE","INTEGER","FLOAT","PAGE_URL","IMAGE_URL","LINK"];
//var mx_helpers_FIELDS_DATATYPES_text = ["TINY_TEXT","RICH_TEXT","PAGE_URL","IMAGE_URL","AUDIO_URL","VIDEO_URL","LINK"];
var mx_helpers_FIELDS_DATATYPES_text = ["TINY_TEXT","PAGE_URL","IMAGE_URL","LINK"];
var mx_helpers_FIELDS_DATATYPES_date = ["DATE"];
var mx_helpers_FIELDS_DATATYPES_integer = ["INTEGER"];
var mx_helpers_FIELDS_DATATYPES_float = ["FLOAT"];
var mx_helpers_FIELDS_DATATYPES_geo_point = ["GEO_POINT"];
var mx_helpers_FIELDS_DATATYPES_geo_point = ["GEO_POINT"];

//curDatatype : if null, returns all available datatypes
// 			  if defined, returns only compatible datatypes (i.e. based on same under-lying ElasticSearch mapping type)
function mx_helpers_getDataTypesChoice(curDatatype) {
	
	dataTypesList=mx_helpers_FIELDS_DATATYPES;
	if (curDatatype!=null) {
		if (curDatatype=="DATE") { dataTypesList=mx_helpers_FIELDS_DATATYPES_date;}
		else if (curDatatype=="INTEGER") { dataTypesList=mx_helpers_FIELDS_DATATYPES_integer;}
		else if (curDatatype=="FLOAT") { dataTypesList=mx_helpers_FIELDS_DATATYPES_float;}
		else if (curDatatype=="GEO_POINT") { dataTypesList=mx_helpers_FIELDS_DATATYPES_geo_point;}
		else if (curDatatype=="LINK") { dataTypesList=mx_helpers_FIELDS_DATATYPES_text;}
		// text
		else { dataTypesList=mx_helpers_FIELDS_DATATYPES_text;}
	}
	
	 var mxDatatypesChoice = [];
	 for (var i=0;i<dataTypesList.length;i++) {
		 let datatype=dataTypesList[i];
		 mxDatatypesChoice.push({'value':datatype,'text':datatype});
	 }
	 return mxDatatypesChoice;
}

function mx_helpers_isDatatypeEnumOk(datatype) {
	return datatype=="TINY_TEXT" 
		|| datatype=="INTEGER"
		|| datatype=="FLOAT"
		|| datatype=="DATE"
		|| datatype=="PAGE_URL"
		|| datatype=="IMAGE_URL"
		|| datatype=="AUDIO_URL"
		|| datatype=="VIDEO_URL"
		|| datatype=="LINK" // query matching possible referenced elements 
		;
				
}


// multi-enum not possible on numerical types, because then
// ElasticSearch does not understand them as numbers ...
// unless using vectors in ES which is a non-free feature
function mx_helpers_isDatatypeMultiEnumOk(datatype) {
	return datatype=="TINY_TEXT" 
		|| datatype=="PAGE_URL"
		|| datatype=="IMAGE_URL"
		|| datatype=="AUDIO_URL"
		|| datatype=="VIDEO_URL"
		|| datatype=="LINK";
}


function mx_helpers_getTermName(termDesc, catalogDesc) {
	let termName=termDesc.name;
	let langName=catalogDesc.vocabulary.guiLanguageShortName;
	if (termDesc.vocabularies!=null && termDesc.vocabularies[langName]!=null) { termName=termDesc.vocabularies[langName].name; }
	return termName;
}

function mx_helpers_isCatalogReadable(accessRights) {
	return accessRights=="CATALOG_ADMIN" || accessRights=="CATALOG_EDIT" || accessRights=="CATALOG_READ"; 
}
function mx_helpers_isCatalogWritable(accessRights) {
	return accessRights=="CATALOG_ADMIN" || accessRights=="CATALOG_EDIT"; 
}
function mx_helpers_isCatalogAdmin(accessRights) {
	return accessRights=="CATALOG_ADMIN"; 
}
function mx_helpers_isAdmin() {
	return "<s:property value="currentUserProfile.role"/>"=="ROLE_ADMIN";
}
function mx_helpers_isUser() {
	return "<s:property value="currentUserProfile.role"/>"=="ROLE_ADMIN"
		|| "<s:property value="currentUserProfile.role"/>"=="ROLE_USER";
}
function mx_helpers_isObserver() {
	return "<s:property value="currentUserProfile.role"/>"=="ROLE_ADMIN"
		|| "<s:property value="currentUserProfile.role"/>"=="ROLE_USER"
		|| "<s:property value="currentUserProfile.role"/>"=="ROLE_OBSERVER";
}
  </script>
