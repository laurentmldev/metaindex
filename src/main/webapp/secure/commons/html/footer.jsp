<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>  
  
  
  <script>
  function showTermsAndConditions() {
	  
	  let textNode=document.createElement("div");
	  textNode.innerHTML='<s:text name="signup.termsAndCondition.body" />';
	  textNode.style["font-size"]="0.8rem";
	  
	  MxGuiHeader.showInfoModal(
  			"<s:text name='signup.termsAndCondition' />",
  			textNode
  			);
  }
  </script>
  <!-- Footer -->
  		
      <footer class="sticky-footer bg-white">
        <div id="gui_messages_insertspot" 
        	  class="container fixed-bottom" 
        	  style="margin-bottom:22px;margin-left:22px;max-width:50%;" >
        </div>
        <div class="container my-auto">
          <div class="mx-copyright text-center">
            <div><s:text name="globals.copyright"/> - MetaindeX v<s:property value="mxVersion"/> <s:property value="mxFooterInfo"/></div>
            <a href="#" onclick="showTermsAndConditions();"><s:text name="signup.termsAndCondition"/></a>            
          </div>
          
        </div>
      </footer>
      

      
<!-- End of Footer -->

<div id="_mx_gui_alert_" class="alert fade show alert-dismissible" 
	style="display:none;overflow:auto;max-height:30vh;opacity:0.8"
	onclick="clearInterval(this.timer);" >
	  <a href="#" class="close" data-dismiss="alert" aria-label="close" title="close" >×</a>
	  <span class="_text_" data-toggle="collapse" data-target="" >alert message here</span>		  
	  <div class="container">
		  <button type="button" style="display:none" class="btn btn-info _details_button_" >
		  	<s:text name="Footer.msg.seeDetails" />
		  </button>		  
	  </div>
 </div>

 
<script>

var alertNb=0;
var INFO_GUI_MESSAGES_TIMETOLIVE_MS = 2000;
var ERROR_GUI_MESSAGES_TIMETOLIVE_MS = 10000;
var INFO="INFO";
var WARNING="WARNING";
var ERROR="ERROR";
var SUCCESS="SUCCESS";
// level : SUCCESS|ERROR|WARNING|INFO
// return: the displayed msg
function footer_showAlert(level, msg, details, timeToLiveMs) {
	alertNb++;
	if (details==null) { details=[]; }
	
	if (timeToLiveMs==null) { 		
		if (level==ERROR || level==WARNING) { timeToLiveMs = ERROR_GUI_MESSAGES_TIMETOLIVE_MS; }
		else { timeToLiveMs = INFO_GUI_MESSAGES_TIMETOLIVE_MS; }
	}
	let guiMessagesInsertSpot = document.getElementById("gui_messages_insertspot");
	let newMsgNode=document.getElementById("_mx_gui_alert_").cloneNode(true);
	newMsgNode.id="";
	
	if (level==ERROR) { 
		newMsgNode.classList.add("alert-danger"); 
	}
	else if (level==SUCCESS) { newMsgNode.classList.add("alert-success"); }
	else if (level==WARNING) { newMsgNode.classList.add("alert-warning"); }
	else if (level==INFO) { newMsgNode.classList.add("alert-info"); }
	else { 
		console.log("unknown GUI msg level '"+level+"' : unable do display msg '"+msg+"'"); 
		newMsgNode.classList.add("alert-info");
	}
	
	newMsgNode.style.display='block';
	let txtNode = newMsgNode.querySelector("._text_");
	txtNode.innerHTML=msg;
	txtNode.setAttribute("data-target","#MxGui_alert_"+alertNb+"_details");
	
	if (details.length>0) {
		
		// build 'error details' modal
		let plansPopupNode=MxGuiPopups.newBlankPopup("<s:text name="Footer.msg.detailsTitle" />",
				"<s:text name="global.ok" />",
				"80vw","80vh","rgba(255, 255, 255,0.7)");
		let bodynode = plansPopupNode.querySelector(".modal-body");
		
		for (let i=0;i<details.length;i++) {
			let curStr=details[i];
			let curError = document.createElement("div");
			curError.innerHTML=curStr;
			curError.classList.add("mx-error-detail");
			bodynode.append(curError);			
		}
		
		
		let detailsButtonNode = newMsgNode.querySelector("._details_button_");
		//detailsButtonNode.setAttribute("data-target","#MxGui_alert_"+alertNb+"_details");
		detailsButtonNode.style.display='block';
		detailsButtonNode.onclick=function()
		{		
			let popupsContainer=document.getElementById("errors_popup_container");
			clearNodeChildren(popupsContainer);
			popupsContainer.appendChild(plansPopupNode);
			plansPopupNode.toggleShowHide();
			//MxGuiHeader.showInfoModal("Error Details",detailsNode,null,'50vw');
		}
	}
	
	newMsgNode.clear=function() { guiMessagesInsertSpot.removeChild(newMsgNode); };
	
	guiMessagesInsertSpot.append(newMsgNode);
	
	newMsgNode.timer = setInterval(function() { 
		clearInterval(newMsgNode.timer); 
		newMsgNode.clear(); 
	}, timeToLiveMs);
	
	
	return newMsgNode;
}

</script>
