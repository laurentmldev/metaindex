<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 
 <!--
 	Expects a function called 'details_buildContents(card)' returning a node with details to be displayed
 	for given card. 
  -->
  
   <!-- Page Heading -->

<div class="d-sm-flex align-items-center justify-content-between mb-1">
	<div id="details_messages_insertspot" 
        	  class="container fixed-top" 
        	  style="margin-top:20vh;margin-left:40vw;max-width:30%;" >
     </div>
	<table><tr>
		<td><h1 id="MxGui.details.title.text" class="h3 mb-0 text-gray-800"></h1></td>  
		<td id="MxGui.details.count" class="text-center" style="padding-left:1rem;display:none">
			<span id="MxGui.details.count.matchNumber" >0</span> 
			/ 
			<span id="MxGui.details.count.totalNumber" class="text-center small">0</span>
					
		</td>
		<td>
		  <div  id="MxGui.details.bulkactions.button"
		  		class="dropdown no-arrow mx-1" style="padding-left:1em;">
              <a class="dropdown-toggle" href="#" id="actionsDropdown" 
              	role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-list fa-fw"></i>
                
              </a>
              <!-- Dropdown - Actions -->
              <div class="dropdown-list dropdown-menu dropdown-menu-right shadow" aria-labelledby="actionsDropdown"
              		id="MxGui.details.bulkactions.insertspot" >
                <h6 class="dropdown-header">
                  Actions pop
                </h6>
                
              </div>
              
          </div>
		</td>
		<td><div title="<s:text name="globals.prevElement"/>" style="margin-left:2rem;" onclick="_details_prev();"> <i id="details_prev_arrow" class="mx-history-disabled-nav-button mx-help-icon far fa-caret-left" style="display:none;font-weight:bold;font-size:2rem;"></i></div></td>
		<td><div title="<s:text name="globals.nextElement"/>" onclick="_details_next();"> <i id="details_next_arrow"  class="mx-history-disabled-nav-button mx-help-icon far fa-caret-right"  style="display:none;font-weight:bold;font-size:2rem;"></i></div></td>
		
	</tr></table>          
</div>
 
 
 
 <div id="MxGui.details.insertspot" class="navbar-nav  mx-active-item-details mx-current-item-area-closed container  " > 	 
 </div>
 <%-- s:include value="./details-right.jsp" /--%>

 <div id="_mx_details_alert_" class="mx-alert-light alert fade show alert-light alert-dismissible" 
	style="display:none;overflow:auto;max-height:30vh;"
	onclick="clearInterval(this.timer);" >
	  <a href="#" class="close" data-dismiss="alert" aria-label="close" title="close" >×</a>
	  <span class="_text_" data-toggle="collapse" data-target="" >alert message here</span>		  	 
 </div>
 
 <script type="text/javascript" >
 
 var _details_cards_history=[];
 var _details_cards_history_current_index=-1;
 
 var _detailsInsertSpot=document.getElementById("MxGui.details.insertspot");
 var _detailsBulkActionsInsertSpot=document.getElementById("MxGui.details.bulkactions.insertspot");
 
 _detailsInsertSpot.close=function() {
	 _detailsInsertSpot.classList.add("mx-current-item-area-closed");
	 _detailsInsertSpot.classList.remove("mx-current-item-area-open");		
 }
 _detailsInsertSpot.open=function() {
	 _detailsInsertSpot.classList.add("mx-current-item-area-open");
	 _detailsInsertSpot.classList.remove("mx-current-item-area-closed");		
 }

 function _details_prev() {
	 _details_cards_history_current_index--;
	 if (_details_cards_history_current_index<0) { 
		 _details_cards_history_current_index=0;
		 return;
	 }
	 
	 if (_details_cards_history_current_index==0) {
		 document.getElementById("details_prev_arrow").classList.add("mx-history-disabled-nav-button");
	 }
	 
	 if (_details_cards_history_current_index<_details_cards_history.length-1) {
		 document.getElementById("details_next_arrow").classList.remove("mx-history-disabled-nav-button");
	 }
	 
	 let prevCard=_details_cards_history[_details_cards_history_current_index];
	 if (prevCard==null) { return; }// just in case 
	 MxGuiCards.deselectAll();
	 MxGuiDetails.populate(prevCard,true /*don't modify history with it*/);
	 MxGuiDetails.open();
	 MxGuiDetails.showAlert(prevCard.getName(),1000);
 }
 

 function _details_next() {
	 _details_cards_history_current_index++;
	 if (_details_cards_history_current_index>_details_cards_history.length-1) {
	 	_details_cards_history_current_index=_details_cards_history.length-1; 
	 	return;
	 }
	 
	 if (_details_cards_history_current_index==_details_cards_history.length-1) {
		 document.getElementById("details_next_arrow").classList.add("mx-history-disabled-nav-button");
	 }
	 
	 if (_details_cards_history_current_index>0) {
		 document.getElementById("details_prev_arrow").classList.remove("mx-history-disabled-nav-button");
	 }
	 
	 let nextCard=_details_cards_history[_details_cards_history_current_index];
	 if (nextCard==null) { return; }// just in case 
	 
	 MxGuiDetails.showAlert(nextCard.getName(),1000);
	 MxGuiCards.deselectAll();
	 MxGuiDetails.populate(nextCard,true /*don't modify history with it*/);
	 MxGuiDetails.open();
 }
 
 function details_clear() {
	_detailsInsertSpot.close();
	clearNodeChildren(_detailsInsertSpot);		
 }

 function details_populate(card,nohistory) {
	 if (!nohistory) {
		 if (_details_cards_history_current_index!=_details_cards_history.length-1) {
			 _details_cards_history.length=_details_cards_history_current_index+1;
		 }
	 	_details_cards_history.push(card);
	 	_details_cards_history_current_index=_details_cards_history.length-1;
	 	if (_details_cards_history.length>1) {
	 		document.getElementById("details_prev_arrow").classList.remove("mx-history-disabled-nav-button");
	 	}
	 }
	 
	// function declared in specific details page
	var newDetailsNode = details_buildContents(card);
	details_clear();	
	_detailsInsertSpot.append(newDetailsNode);
 	_detailsInsertSpot.open();	
	// typically will activate 'x-editable' behaviour for relevant fields
	details_postBuildContents();
			 
 }
 
 // called from commons/details/details_populate() function
 // when called, elements created in details_buildContents are effectively
 // added to DOM tree
 function details_postBuildContents() {
	 xeditable_finishEditableFields();
	 bootstrap_confirmation_finishConfirmations();
 }
 
 function details_setNbMatchingItems(nbMatch) {
	 let nbCountNode=document.getElementById('MxGui.details.count');
	 nbCountNode.style.display="block";
	 
	 let nbMatchNode=document.getElementById('MxGui.details.count.matchNumber');	 
	 nbMatchNode.innerHTML=nbMatch; 
 }
 function details_getNbMatchingItems() {
	 let nbMatchNode=document.getElementById('MxGui.details.count.matchNumber');	 
	 return nbMatchNode.innerHTML; 
 }

 function details_setNbTotalItems(nbTotalItems) {
	 let nbCountNode=document.getElementById('MxGui.details.count');
	 nbCountNode.style.display="block";
	 
	 let nbTotalCountNode=document.getElementById('MxGui.details.count.totalNumber');	 
	 nbTotalCountNode.innerHTML=nbTotalItems; 
 }
 function details_getNbTotalItems() {
	 let nbTotalCountNode=document.getElementById('MxGui.details.count.totalNumber');
	 return nbTotalCountNode.innerHTML; 
 }
 
 function details_setTitle(title) {
	 document.getElementById('MxGui.details.title.text').innerHTML=title;
 }
 
 var INFO_DETAILS_MESSAGES_TIMETOLIVE_MS=500;
 var detailsAlertNb=0;
 function details_showAlert(msg,timeToLiveMs) {
		
	 detailsAlertNb++;
	 if (timeToLiveMs==null) { timeToLiveMs = INFO_DETAILS_MESSAGES_TIMETOLIVE_MS; }
		var detailsMessagesInsertSpot = document.getElementById("details_messages_insertspot");
		var newMsgNode=document.getElementById("_mx_details_alert_").cloneNode(true);
		
		
		newMsgNode.style.display='block';
		var txtNode = newMsgNode.querySelector("._text_");
		txtNode.innerHTML=msg;
		txtNode.setAttribute("data-target","#MxGui_alert_"+detailsAlertNb+"_details");
		
		detailsMessagesInsertSpot.append(newMsgNode);
		
		newMsgNode.timer = setInterval(function() { 
			clearInterval(newMsgNode.timer); 
			detailsMessagesInsertSpot.removeChild(newMsgNode); 
		}, timeToLiveMs);	
	}
 
 // Public interface
 var MxGuiDetails={};
 MxGuiDetails.populate=details_populate;
 MxGuiDetails.clear=details_clear;
 MxGuiDetails.open=_detailsInsertSpot.open;
 MxGuiDetails.close=_detailsInsertSpot.close;
 MxGuiDetails.getInsertSpot=function() { return _detailsInsertSpot; }
 MxGuiDetails.getBulkActionsInsertSpot=function() { return _detailsBulkActionsInsertSpot; }
 MxGuiDetails.HideBulkActionsButton=function() { document.getElementById("MxGui.details.bulkactions.button").style.display="none"; }
 MxGuiDetails.setNbMatchingItems=details_setNbMatchingItems;
 MxGuiDetails.getNbMatchingItems=details_getNbMatchingItems;
 MxGuiDetails.setNbTotalItems=details_setNbTotalItems;
 MxGuiDetails.getNbTotalItems=details_getNbTotalItems;
 MxGuiDetails.setTitle=details_setTitle;
 MxGuiDetails.showAlert=details_showAlert;
 // enable navigation within previously clicked elements
 MxGuiDetails.showHistoNavArrows=function() {
	 document.getElementById("details_prev_arrow").style.display='block';
	 document.getElementById("details_next_arrow").style.display='block'; 
 }
 </script>
