<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 


<script type="text/javascript">

function buildAdminInfoPopup(parentNode) {
	let adminPopupNode=MxGuiPopups.newBlankPopup("<s:text name="Admin.title" />","<s:text name="global.ok" />","80vw","90vh","rgba(255, 255, 255,1)");
	let bodynode = adminPopupNode.querySelector(".modal-body");
	
	adminPopupNode.id=MX_APP_ADMIN_POPUP_ID;
	parentNode.appendChild(adminPopupNode);
	
	let adminWarning=document.createElement("span");
	adminWarning.innerHTML="<s:text name="Admin.warning" />";
	adminWarning.style["margin-top"]="0.3rem";
	adminWarning.style["margin-bottom"]="0.3rem";
	bodynode.append(adminWarning);
	
	let refreshButton=document.createElement("button");
	refreshButton.classList.add("btn");
	refreshButton.classList.add("btn-big");
	refreshButton.classList.add("btn-info");
	refreshButton.style["margin"]="1rem";
	refreshButton.innerHTML="<s:text name="Admin.refresh" />";
	refreshButton.onclick=updateAdminInfo;
	bodynode.append(refreshButton);
	
	let adminInfosTable = document.createElement("table");
	let adminInfosTableBody = document.createElement("tbody");
	adminInfosTable.append(adminInfosTableBody)
	adminInfosTableBody.id="adminInfoTable";
	adminInfosTable.classList.add("table");
	adminInfosTable.classList.add("table-striped");
	bodynode.append(adminInfosTable)
	
}

function updateAdminInfo() {
	
	let updateAdminInfoCallback=function(receivedInfo) {
		let adminInfosTable = document.getElementById('adminInfoTable');
		clearNodeChildren(adminInfosTable);
		
		// header
		let headerRow=document.createElement("tr");
		adminInfosTable.append(headerRow)
		
		let title=document.createElement("th");
		title.innerHTML="Title";
		headerRow.append(title);
		
		let value=document.createElement("th");
		value.innerHTML="<s:text name="global.name" />";
		headerRow.append(value);
		
		
		// nb active users
		let infoRow=document.createElement("tr");
		adminInfosTable.append(infoRow)
				
		let infoTitle=document.createElement("td");
		infoTitle.classList.add("mx-admintbl-field-title");
		infoTitle.style["font-weight"]="bold";
		infoTitle.innerHTML="<s:text name="Admin.nbActiveUsers" />";
		infoRow.append(infoTitle);				
		let infoValue=document.createElement("td");
		infoValue.classList.add("mx-admintbl-field-value");
		infoValue.innerHTML=receivedInfo.activeUsersIdList.length;
		infoRow.append(infoValue);		
		infoValue.onclick=function() { console.log(receivedInfo.activeUsersIdList); }
		
		// nb active processing tasks
		infoRow=document.createElement("tr");
		adminInfosTable.append(infoRow)
		infoTitle=document.createElement("td");
		infoTitle.classList.add("mx-admintbl-field-title");
		infoTitle.innerHTML="<s:text name="Admin.nbActiveProcessingTasks" />";		
		infoRow.append(infoTitle);				
		infoValue=document.createElement("td");
		infoValue.classList.add("mx-admintbl-field-value");
		infoValue.innerHTML=receivedInfo.runningTasksNameList.length;
		infoRow.append(infoValue);
		infoValue.onclick=function() { console.log(receivedInfo.runningTasksNameList); }
		
		// nb threads
		infoRow=document.createElement("tr");
		adminInfosTable.append(infoRow)
		infoTitle=document.createElement("td");
		infoTitle.classList.add("mx-admintbl-field-title");
		infoTitle.style["font-weight"]="bold";
		infoTitle.innerHTML="<s:text name="Admin.nbThreads" />";
		infoRow.append(infoTitle);				
		infoValue=document.createElement("td");		
		infoValue.classList.add("mx-admintbl-field-value");
		infoValue.innerHTML=receivedInfo.nbThreads;
		infoRow.append(infoValue);
		
		footer_showAlert(INFO,"Admin Info Refreshed",null,400);
		
	}
	MxApi.requestAdminMonitoringInfo(updateAdminInfoCallback);
	
}
</script>


