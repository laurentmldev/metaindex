<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 		  

 <script type="text/javascript" >

 function _handlerCatalogUsers(usersData) {
	 let usersTable=document.getElementById("usersRolesTable");
	 clearNodeChildren(usersTable);
	 
	 let headerRow=document.createElement("tr");
	 usersTable.append(headerRow);
	 let headerName=document.createElement("th");
	 headerName.innerHTML="<s:text name="Profile.nickname" />";
	 headerRow.append(headerName);
	 let headerEmail=document.createElement("th");
	 headerEmail.innerHTML="<s:text name="Profile.email" />";
	 headerRow.append(headerEmail);
	 let headerRights=document.createElement("th");
	 headerRights.innerHTML="<s:text name="Catalogs.overview.userAccessRights" />";
	 headerRow.append(headerRights);
	 
	 for (let userIdx in usersData.users) {
		 let curUserData = usersData.users[userIdx];
		 //console.log(curUserData);
		 
		 let row=document.createElement("tr");
		 usersTable.append(row);
		 let name=document.createElement("td");
		 name.innerHTML=curUserData.nickname;
		 row.append(name);
		 let email=document.createElement("td");
		 email.innerHTML=curUserData.name;
		 row.append(email);
		 let rights=document.createElement("td");
		 
		 let choicesDef = [ 
			 				/* possible values defined ICatalogUser.java */
			 				{value:"CATALOG_ADMIN", text:"<s:text name="Profile.userAccessRights.CATALOG_ADMIN" />"},
			 				{value:"CATALOG_EDIT", text:"<s:text name="Profile.userAccessRights.CATALOG_EDIT" />"},
			 				{value:"CATALOG_READ", text:"<s:text name="Profile.userAccessRights.CATALOG_READ" />"},
			 				{value:"NONE", text:"<s:text name="Profile.userAccessRights.NONE" />"}
			 			  ];
		 
		 let onchangeEditRightsCallback=function(pk,fieldName,newValue,successCallback, errorCallback) {
			 
			 let catalogId = "<s:property value="currentCatalog.id"/>";
			 if (catalogId=="") { return; }
			 ws_handlers_setCatalogUserAccess(catalogId, curUserData.id, newValue,successCallback,errorCallback);
		 }
		 let successEditRightsCallback=function(fieldName,newValue) {
			 curUserData.catalogAccessRights=newValue;			
		 }				 
		 let editableAccessRightsNode = xeditable_create_dropdown_field(
					'accessRights_'+curUserData.id /* pk */,
					'Access Rights',false /*show Name*/,
					curUserData.catalogAccessRights /* cur value */,	
					choicesDef,
					onchangeEditRightsCallback,
					successEditRightsCallback);			
			
		 rights.append(editableAccessRightsNode);
		 row.append(rights);
	 }
	 xeditable_finishEditableFields();
 }
 function _updateCatalogUsers() {
	 let curCatalogId="<s:property value="currentCatalog.id"/>";
	 if (curCatalogId!=null) {		 
	 	ws_handlers_retrieveCatalogUsers(curCatalogId,_handlerCatalogUsers);
	 }
 }
 
 MxGuiCatalogUsersTab={};
 MxGuiCatalogUsersTab.updateUsersList=_updateCatalogUsers;
 </script>

<!--  --------Templates--------- -->
 
<!--  --------end of Templates--------- -->

<div class="tab-pane fade _details_users_insertspot_" id="nav-users" role="tabpanel" aria-labelledby="nav-users-tab">
					  						  						
	<table id="usersRolesTable" class="table table-stripped">
		
	</table>
</div>
					
 	