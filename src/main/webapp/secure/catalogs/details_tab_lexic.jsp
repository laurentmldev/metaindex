<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 		  

 <script type="text/javascript" >

 var catalogVocabularyEntities = [ "name","comment","item","items","user","users"];
 
 // function used by details.jsp:details_buildContents
 function details_buildContents_lexic(newPopulatedCatalogDetails,catalogCard) {
	 
	 let lexicRootNode=newPopulatedCatalogDetails.querySelector("._details_lexic_rootnode_");
	 let lexicContents=document.getElementById("details_lexic").cloneNode(true);
	 lexicContents.style.display='block';
	 //console.log(lexicContents);
	 
	 lexicRootNode.appendChild(lexicContents);
	 
	 let navInsertSpot=lexicContents.querySelector("._nav_insertspot_");
	 let contentsInsertSpot=lexicContents.querySelector("._contents_insertspot_");
	 // remove existing lexic contens if any
	 navInsertSpot.innerHTML="";
	 contentsInsertSpot.innerHTML="";
	 
	 //console.log(catalogCard.descr.vocabularies);
	 for (var vocIdx in catalogCard.descr.vocabularies) {
		 
		 let curCatalogVocabularyData=catalogCard.descr.vocabularies[vocIdx];
		 let langName=curCatalogVocabularyData.guiLanguageShortName;
		 let langTitle=curCatalogVocabularyData.guiLanguageFullName;
		 
		 // nav
		 let curTabNav=document.getElementById("nav-lexic-comm-template-tab").cloneNode(true);
		 curTabNav.style.display='block';
		 curTabNav.id="nav-lexic-comm-"+langName+"-tab";
		 curTabNav.href="#nav-lexic-comm-"+langName;
		 curTabNav.setAttribute("aria-controls","nav-lexic-comm-"+langName+"-tab");
		 curTabNav.innerHTML=langTitle;
		 if (langName=="EN") { curTabNav.classList.add("active"); }
		 navInsertSpot.appendChild(curTabNav);
		 
		 // contents
		 let curTabContents=document.getElementById("nav-lexic-comm-template").cloneNode(true);
		 curTabContents.style.display='';
		 curTabContents.id="nav-lexic-comm-"+langName;
		 curTabContents.setAttribute("aria-labelledby","nav-lexic-comm-"+langName+"-tab");
		 contentsInsertSpot.appendChild(curTabContents);
		 if (langName=="EN") { curTabContents.classList.add("active"); curTabContents.classList.add("show"); }
		 
	// catalog lexic
		 let catalogLexicTitle = curTabContents.querySelector("._catalog_lexic_title_");
		 catalogLexicTitle.innerHTML="<s:text name='Catalogs.lexic.globallexic'/>";
		 let catalogLexicTable=curTabContents.querySelector("._catalog_lexic_insertspot_");
		 
		 for (var idx in catalogVocabularyEntities) {
			 let curVocEntry=catalogVocabularyEntities[idx];			 
			 let entry=document.createElement("tr");
			 let title=document.createElement("th"); title.innerHTML=curVocEntry;
			 let value=document.createElement("td");
			 
			 let details_onLexicChange=function(pk,fieldName,newValue,successCallback, errorCallback){	 
				 MxApi.requestSetCatalogLexicEntry({
					 	  "catalogId":_curCatalogDesc.id,
					 	  "langShortName":langName,
						  "entryName":curVocEntry,
						  "entryTranslation":newValue, 
						  "successCallback":successCallback,
					 	  "errorCallback":errorCallback
				 });
			}
			let successCallbackOnChange=function(fieldName,newValue) {
				 curCatalogVocabularyData[curVocEntry]=newValue;			
			}	
			
			if (mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)) {
				let editableFieldLexicNode = xeditable_create_text_field(
							'lexic_'+langName+"_"+curVocEntry /* pk */,
							'lexic_'+langName+"_"+curVocEntry,false /*show Name*/,
							curCatalogVocabularyData[curVocEntry] /* cur value */,		
							details_onLexicChange,
							successCallbackOnChange);
				value.append(editableFieldLexicNode);
				editableFieldLexicNode.disableMaxWidth();
			} else { value.innerHTML=curCatalogVocabularyData[curVocEntry]; }
			
			 entry.appendChild(title);
			 entry.appendChild(value);
			 catalogLexicTable.appendChild(entry);
		 }
		
		
		 
		 // fields lexic
		 let fieldsLexicTitle = curTabContents.querySelector("._fields_lexic_title_");
		 fieldsLexicTitle.innerHTML="<s:text name='Catalogs.lexic.fieldslexic'/>";
		 let fieldsLexicTable=curTabContents.querySelector("._fields_lexic_insertspot_");
		 for (fieldIdx in catalogCard.descr.terms) {
			 let curTerm = catalogCard.descr.terms[fieldIdx];
			 let entry=document.createElement("tr");
			 let title=document.createElement("th"); title.innerHTML=curTerm.name;
			 let value=document.createElement("td");
			 let curValue = curTerm.name;
			 if (curTerm.vocabularies[langName]!=null) { curValue=curTerm.vocabularies[langName].name; }
			 
			 let details_onLexicChange=function(pk,fieldName,newValue,successCallback, errorCallback){	 
				 MxApi.requestSetTermLexicEntry({
					 	  "catalogId":_curCatalogDesc.id,
					 	  "langShortName":langName,
						  "termName":curTerm.name,
						  "entryTranslation":newValue, 
						  "successCallback":successCallback,
					 	  "errorCallback":errorCallback
				 });
			}
			let successCallbackOnChange=function(fieldName,newValue) {
				if (curTerm.vocabularies[langName]==null) { curTerm.vocabularies[langName]={}; }
				curValue=curTerm.vocabularies[langName].name=newValue;			
			}	
			if (mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)) {
				let editableFieldLexicNode = xeditable_create_text_field(
							'lexic_'+langName+"_"+curTerm.name /* pk */,
							'lexic_'+langName+"_"+curTerm.name,false /*show Name*/,
							curValue /* cur value */,		
							details_onLexicChange,
							successCallbackOnChange);
				
				 value.appendChild(editableFieldLexicNode);
				 editableFieldLexicNode.disableMaxWidth();
			} 
			else { value.innerHTML=curValue; }
			 entry.appendChild(title);
			 entry.appendChild(value);
			 fieldsLexicTable.appendChild(entry);			 
		 }
		 
	 }
	 
	 
 }		
	
 </script>
 
 <!--  --------Templates--------- -->
 <div  id="details_lexic" style="display:none">
					  
	    <nav>
			<div class="nav nav-tabs nav-fill _nav_insertspot_" id="nav-tab-lexic-catalog" role="tablist">
			</div>
		</nav>
		<div class="tab-content py-3 px-3 px-sm-0 _contents_insertspot_" id="nav-lexic-tab-contents-container">
		</div>
						  						
</div>

<a class="nav-item nav-link mx-tab-tiny-shadow " id="nav-lexic-comm-template-tab" 
	data-toggle="tab" href="#nav-lexic-comm-xx" role="tab" aria-controls="nav-lexic-comm-xx-tab" aria-selected="false"
	style="display:none">
	</a>

<div class="tab-pane fade" id="nav-lexic-comm-template" role="tabpanel" aria-labelledby="nav-lexic-comm-xx-tab"
		style="display:none" >
	<div class="row w3-padding">
		<div class="mx-lexic-column col-sm-6 w3-padding-16">
   			<h3 class="_catalog_lexic_title_" ></h3>
   			<table class="table table-striped" ><tbody class=" _catalog_lexic_insertspot_"></tbody></table>
   		</div>
		<div class="mx-lexic-column col-sm-6 w3-padding-16">
		    <h3  class="_fields_lexic_title_" ></h3>
		    <table class="table table-striped" ><tbody class="_fields_lexic_insertspot_" ></tbody></table>
		</div>
	</div>  		
</div> 

<!--  --------end of Templates--------- -->

<div class="tab-pane fade _details_lexic_rootnode_" id="nav-lexic" role="tabpanel" aria-labelledby="nav-lexic-tab">

					  						
</div>
