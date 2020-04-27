<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>  
  
  <!-- Footer -->
  		
      <footer class="sticky-footer bg-white">
        <div id="gui_messages_insertspot" 
        	  class="container fixed-bottom" 
        	  style="margin-bottom:22px;margin-left:22px;max-width:50%;" >
        </div>
        <div class="container my-auto">
          <div class="mx-copyright text-center my-auto">
            <span><s:text name="globals.copyright"/> - MetaindeX v<s:property value="mxVersion"/></span>
          </div>
        </div>
      </footer>
      
<!-- End of Footer -->

<div id="_mx_gui_alert_" class="alert fade show alert-dismissible" 
	style="display:none;overflow:auto;max-height:30vh;"
	onclick="clearInterval(this.timer);" >
	  <a href="#" class="close" data-dismiss="alert" aria-label="close" title="close" >×</a>
	  <span class="_text_" data-toggle="collapse" data-target="" >alert message here</span>		  
	  <div class="container">
		  <button type="button" style="display:none" class="btn btn-info _details_button_" data-toggle="collapse" data-target="">See Details</button>
		  <div id="" class="_details_ collapse" style="font-size:0.5em"></div>
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
		let detailsButtonNode = newMsgNode.querySelector("._details_button_");
		detailsButtonNode.setAttribute("data-target","#MxGui_alert_"+alertNb+"_details");
		detailsButtonNode.style.display='block';
		
		let detailsNode = newMsgNode.querySelector("._details_");
		detailsNode.id="MxGui_alert_"+alertNb+"_details";
		detailsNode.innerHTML+="<ul>";
		for (let i=0;i<details.length;i++) {
			let curStr=details[i];
			detailsNode.innerHTML+="<li>"+curStr+"</li>";			
		}		
	}
	
	guiMessagesInsertSpot.append(newMsgNode);
	
	newMsgNode.timer = setInterval(function() { 
		clearInterval(newMsgNode.timer); 
		guiMessagesInsertSpot.removeChild(newMsgNode); 
	}, timeToLiveMs);
	
	
}

</script>
