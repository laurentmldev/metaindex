<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<c:url value="/" var="mxurl"/>
  
 <script type="text/javascript" >
 
 
 function ws_handlers_refreshCatalogsGui() {
	 MxGuiDetails.memGui();
	 MxApi.requestCatalogs();	 
 }
 
 function onWsConnect(isConnected) {
	if (!isConnected) {
		footer_showAlert(ERROR, "Unable to connect to Metaindex server, sorry");
	}
	else {
		footer_showAlert(SUCCESS, "Connected to Metaindex server");
 		MxApi.requestCatalogs();
 		
	}
 }
 
 function handleMxWsSelectedCatalog(msg) { 
	 if (msg.isSuccess==false) {
		 footer_showAlert(ERROR, "Sorry, unable to enter catalog, please refresh current page and try again.");
		 return;
	 }
	 //console.log("selected catalog '"+msg.catalogId+"'");	 
 }

 function handleMxWsSelectedItem(msg) {}
 function handleMxWsCreatedCatalog(msg) {
	 if (msg.isSuccess==false) {
		 footer_showAlert(ERROR, "Sorry, unable to create catalog : "+msg.rejectMessage);
		 return;
	 }
	 footer_showAlert(INFO, "Catalog created");
	 MxApi.requestCatalogs();
 }
 function handleMxWsCreatedTerm(msg) {	 
	 if (msg.isSuccess==false) {
		 footer_showAlert(ERROR, "Sorry, unable to add term : "+msg.rejectMessage);
		 return;
	 }
	 footer_showAlert(SUCCESS, "Term added!");	 	 
 }
 function handleMxWsDeletedTerm(msg) {}
 function handleMxWsDeletedFilter(msg) {}
 function handleMxWsUpdatedFilter(msg) {}
 function handleMxWsCsvUpload(msg) {}
 function handleMxWsCatalogContentsChanged(msg) {
	 
	 // if not current catalog, just ignore it
	 if (MxGuiDetails.getCurCatalogDescription()==null 
			 || msg.catalogName!=MxGuiDetails.getCurCatalogDescription().name) { 
		 return; 
	}
	 
	 // if not current user, just display notification ? disabled for now
	 if (msg.userNickname!="<s:property value='currentUserProfile.nickname'/>") {	
		 //footer_showAlert(INFO, msg.userNickname+" changed contents : "+msg.modifType);
	 } else {		 
		 // refresh view 
		 if (msg.modifType==MxApi.CATALOG_MODIF_TYPE.FIELDS_LIST
				 || msg.modifType==MxApi.CATALOG_MODIF_TYPE.CATALOGS_LIST
				 || msg.modifType==MxApi.CATALOG_MODIF_TYPE.FIELDS_LIST
				 ) 
		 {
			 ws_handlers_refreshCatalogsGui();			
		 }
		 
	}			 
 }
 
 function handleMxWsServerGuiMessage(msg) {
	 footer_showAlert(msg.level, msg.text);
 }
 


 function ws_handlers_getUserProfileData(userId,callbackFunc) {	
	function successCallback(msgData) {		
		let userData=msgData.users[userId];
		callbackFunc(userData);		
	}
	function errorCallback(msg) { footer_showAlert(WARNING, "Unable to retrieve data for user '"+userId+"' : "+msg); }
	
	let usersIds=[];
	usersIds.push(userId);
	MxApi.requestGetUsersProfiles({	"usersIds":usersIds,
 									"successCallback":successCallback,
 									"errorCallback":errorCallback
 									});		
 }
 
//refresh nb created catalogs
function handleNbCreatedCatalogs(profileData) {
	let curNbCatalogsCreated=profileData.curNbCatalogsCreated;
	let maxNbCatalogsCreated=profileData.maxNbCatalogsCreated;		
	MxGuiLeftBar.updateNbCatalogsCreated(curNbCatalogsCreated,maxNbCatalogsCreated);
}

 
 function handleMxWsCatalogs(msg) {
	 
	 if (msg.isSuccess==false) {
		 footer_showAlert(ERROR, "Sorry, unable to retrieve catalogs info.");
		 return;
	 }
	 //console.log(msg);
	 let curActiveCard=MxGuiCards.getActiveCard();
	 let newCurActiveCard=null;
	 
	 MxGuiCards.clearCards();
	 for (var i=0;i<msg.length;i++) {
		  curCatalogDescr=msg[i];
		  newCatalogCard=MxGuiCards.addNewCard(curCatalogDescr);		  
		  if (curCatalogDescr.isUserCurrentCatalog==true) { newCurActiveCard=newCatalogCard; }		  
	 }	 
	 if (newCurActiveCard!=null) { 
		 newCurActiveCard.select(); 
		 MxGuiDetails.restoreGui();
	 }
	 
	 ws_handlers_getUserProfileData(<s:property value='currentUserProfile.id'/>,handleNbCreatedCatalogs);
  }
 

 function ws_handlers_deleteTerm(termName) {
	 MxApi.requestDeleteTerm(MxGuiDetails.getCurCatalogDescription().id,termName);	 
 }
 
 function ws_handlers_createTerm(termName) {
	 MxApi.requestCreateTerm(MxGuiDetails.getCurCatalogDescription().id,termName);	 
 }
 

 function ws_handlers_deleteCatalog(catalogId) {
	
	function successCallback() {
		MxGuiCards.deselectAll();
		footer_showAlert(WARNING, "Catalog deleted");
		ws_handlers_refreshCatalogsGui();
	}
	function errorCallback(msg) { footer_showAlert(WARNING, "Catalog could not be deleted : "+msg); }
	
	MxApi.requestDeleteCatalog({	"catalogId":catalogId,
 									"successCallback":successCallback,
 									"errorCallback":errorCallback
 									});
	
 }

 
</script>
