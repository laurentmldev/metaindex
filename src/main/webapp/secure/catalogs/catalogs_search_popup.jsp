<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 


<script type="text/javascript">

function buildCatalogsSearchPopup(parentNode) {
	let catalogsSearchPopupNode=MxGuiPopups.newBlankPopup("<s:text name="Catalogs.searchCatalog" />","<s:text name="global.cancel" />","80vw","90vh","rgba(255, 255, 255,1)");
	let bodynode = catalogsSearchPopupNode.querySelector(".modal-body");
	
	catalogsSearchPopupNode.id=MX_CATALOGS_SEARCH_POPUP_ID;
	parentNode.appendChild(catalogsSearchPopupNode);
	
	let catalogsTable = document.createElement("table");
	catalogsTable.id="catalogsSearchTable";
	catalogsTable.classList.add("table");
	catalogsTable.classList.add("table-striped");
	bodynode.append(catalogsTable)
	
}

function updateCatalogsSearchList() {
	let catalogsTable = document.getElementById('catalogsSearchTable');
	clearNodeChildren(catalogsTable);
	
	// header
	let headerRow=document.createElement("tr");
	catalogsTable.append(headerRow)
	
	let id=document.createElement("th");
	id.innerHTML="ID";
	headerRow.append(id);
	
	let name=document.createElement("th");
	name.innerHTML="Name";
	headerRow.append(name);
	
	let desc=document.createElement("th");
	desc.innerHTML="Description";
	headerRow.append(desc);
	
	let askForAccess=document.createElement("th");
	askForAccess.innerHTML="";
	headerRow.append(askForAccess);
	
	let refreshCatalogsList=function(catalogsArray) {
		for (catalogIdx in catalogsArray) {
			let catalodDesc=catalogsArray[catalogIdx];
			
			let catRow=document.createElement("tr");
			catalogsTable.append(catRow)
			
			let catid=document.createElement("td");
			catid.innerHTML=catalodDesc.name;
			catRow.append(catid);
			
			let catname=document.createElement("td");
			catname.innerHTML=catalodDesc.vocabulary.name;
			catRow.append(catname);
			
			let catdesc=document.createElement("td");
			catdesc.innerHTML=catalodDesc.vocabulary.comment;
			catRow.append(catdesc);
			
			let cataskForAccess=document.createElement("td");
			let requestAccessButton=document.createElement("a");
			requestAccessButton.classList=("_openBtn_ d-none d-sm-inline-block btn-big btn btn-sm btn-info shadow-sm");
			requestAccessButton.href="#";
			requestAccessButton.innerHTML="<s:text name="Catalogs.users.requestCatalogJoinSend"/>";
			requestAccessButton.onclick=function(event) {
				
				successCallback=function() {
					footer_showAlert(SUCCESS, "<s:text name="Catalogs.users.requestingCatalogJoinSuccess"/>");
				}
				errorCallback=function(msg) {
					footer_showAlert(ERROR, "<s:text name="Catalogs.users.requestingCatalogJoinFailure"/> : "+msg);
				}
				cataskForAccess.innerHTML="<s:text name="Catalogs.users.requestCatalogJoinSent" />";
				cataskForAccess.style="font-style:italic";
				footer_showAlert(INFO, "<s:text name="Catalogs.users.requestingCatalogJoin"/>");
				ws_handlers_joinCatalog(catalodDesc.id,successCallback,errorCallback);
			}
			cataskForAccess.append(requestAccessButton);
			catRow.append(cataskForAccess);
		}
	}
	MxApi.requestGetCatalogs({'catalogId':-1, 'successCallback':refreshCatalogsList});
}
</script>


