<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
  
 <script type="text/javascript" >
 
 
 function ws_handlers_refreshCatalogsGui() {
	 MxGuiDetails.memGui();
	 MxApi.requestGetCatalogs({'catalogId':0, 'successCallback':handleMxWsCatalogs});	 	 
 }
 
 function onWsConnect(isConnected) {
	if (!isConnected) {
		footer_showAlert(ERROR, "<s:text name="global.unableToConnectToServer" />");
	}
	else {
		footer_showAlert(SUCCESS, "<s:text name="global.connectedToServer" />");
		MxApi.requestGetCatalogs({'catalogId':0, 'successCallback':handleMxWsCatalogs});
 		
	}
 }
 
 function handleMxWsSelectedCatalog(msg) { 
	 if (msg.isSuccess==false) {
		 footer_showAlert(ERROR, "<s:text name="Catalogs.unableToEnterPleaseRefresh" />");
		 return;
	 }
	 <c:if test="${mxRunMode != 'standalone'}" >
	 MxGuiDetails.updateUsersTab();
	 </c:if>
	 //console.log("selected catalog '"+msg.catalogId+"'");	 
 }

 function handleMxWsSelectedItem(msg) {}
 function handleMxWsCreatedCatalog(msg) {
	 if (msg.isSuccess==false) {
		 footer_showAlert(ERROR, "<s:text name="Catalogs.unableToCreate" /> : "+msg.rejectMessage);
		 return;
	 }
	 footer_showAlert(INFO, "<s:text name="Catalogs.createdSuccessfully" />");
	 MxApi.requestGetCatalogs({'catalogId':0, 'successCallback':handleMxWsCatalogs});
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
	function errorCallback(msg) { footer_showAlert(WARNING, "<s:text name="Catalogs.unableToRetrieveUserData" /> '"+userId+"' : "+msg); }
	
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
		 footer_showAlert(ERROR, "<s:text name="Catalogs.unableToRetrieveCatalogInfo" />");
		 return;
	 }
	 //console.log(msg);
	 let curActiveCard=MxItemsView.getActiveItem();
	 let newCurActiveCard=null;	 
	 if (msg.length==0) { MxGuiMain.showTextEmptyCatalog(); }
	 else { MxGuiMain.hideTextEmptyCatalog(); }
	 
	 
	 MxItemsView.clearItems();
	 for (var i=0;i<msg.length;i++) {
		  curCatalogDescr=msg[i];
		  newCatalogCard=MxItemsView.addNewItem(curCatalogDescr);		  
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
 

 function ws_handlers_deleteCatalog(catalogId) {
	
	function successCallback() {
		MxItemsView.deselectAll();
		footer_showAlert(WARNING, "<s:text name="Catalogs.catalogDeletedSuccessfully" />");
		if (MxItemsView.getNbItemsInView()==1) { handleMxWsCatalogs([]); }
		ws_handlers_refreshCatalogsGui();
	}
	function errorCallback(msg) { footer_showAlert(WARNING, "<s:text name="Catalogs.unableToDeleteCatalog" /> : "+msg); }
	
	MxApi.requestDeleteCatalog({	"catalogId":catalogId,
 									"successCallback":successCallback,
 									"errorCallback":errorCallback
 									});
	
 }

 function ws_handlers_retrieveCatalogUsers(catalogId,userSuccessCallback) {
	 function successCallback(data) { userSuccessCallback(data); }
	 function errorCallback(msg) { footer_showAlert(WARNING, "<s:text name="Catalogs.unableToRetrieveCatalogUsers" /> : "+msg); }
		
		MxApi.requestGetCatalogUsers({	"catalogId":catalogId,
	 									"successCallback":successCallback,
	 									"errorCallback":errorCallback
	 									});
 }
 
 function ws_handlers_setCatalogUserAccess(catalogId,userId,accessRights,successCallback,errorCallback) {
	 	
		MxApi.requestSetCatalogUserAccess({	"catalogId":catalogId,
												"userId":userId,
												"accessRights":accessRights,
			 									"successCallback":successCallback,
			 									"errorCallback":errorCallback
	 									});
 }
 

 function ws_handlers_joinCatalog(catalogId,successCallback,errorCallback) {
	 	
		MxApi.requestJoinCatalog({	"catalogId":catalogId,
											"successCallback":successCallback,
		 									"errorCallback":errorCallback
	 									});
 }
</script>
