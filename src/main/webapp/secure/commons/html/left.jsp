<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<!-- Left Sidebar -->


    <ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">
		
      <!-- Sidebar - Brand -->
      <a class="sidebar-brand d-flex align-items-center justify-content-center" href="Catalogs">
              
        <div id="leftbar.uptitle" class="sidebar-brand-text mx-3" style="padding-bottom:0;padding-top:1rem;">MetaindeX</div>        
      </a>

     

      <!-- Nav Item - Dashboard -->
      <li class="nav-item active">
        <a class="nav-link"  id="leftbar.title-container" href="#">
          <i class="counter-text fas fa-fw fa-cog " style="color:#ccc"></i>
          <span id="leftbar.title" class="counter-text" ></span></a>
      </li>

      <!-- Divider -->
      <hr class="sidebar-divider">

      <!-- Heading -->
      <div id="leftbar.operations.title" class="sidebar-heading"></div>

      <!-- Nav Item - Pages Collapse Menu -->
      <div id="leftbar.operations.insertspot" ></div>
          
      <!-- Divider -->
      <hr class="sidebar-divider d-none d-md-block">

	  <div id="leftbar.progressbars.insertspot" ></div>
	  
      <!-- Sidebar Toggler (Sidebar) -->
      <div class="text-center d-none d-md-inline">
        <button class="rounded-circle border-0" id="sidebarToggle"></button>
      </div>

	<div class="sidebar-brand-text counter-text app-title " 
		style="text-align:center;width:100%;color:#ccc;font-size:0.8rem;padding:0;margin:0;line-height:0.5;">
		MetaindeX
		<div style="width:100%;margin-top:0.5rem;"><center> <table><tr>
        	<td style="width:10px"><span id="upstream_light" style="display:none;opacity:0.3;color:white;font-size:0.8rem;margin:0;padding:0;text-shadow:none;">&uarr;</span></td>
        	<td style="width:10px"><span id="downstream_light" style="display:none;opacity:0.3;color:white;font-size:0.8rem;margin:0;padding:0;text-shadow:none;">&darr;</span></td>
        </tr></table></center></div>
	</div>
	
    </ul>
   
    <!-- End of Sidebar -->
    
     <div id="progressbar-template" class="progress mx-progress " style="display:none; margin:1em;">
	  <div class="progress-bar  progress-bar-striped bg-success _pourcentage_" style="width:0%">
	  	<span class="_text_ mx-progress-bar-contents" style="padding-left:0.2em;"></span>
	  </div>
	 </div> 
	 
	 
    <script>
    
    var PROGRESS_FINISHED_DISAPPEAR_DELAY_SEC=3;
    
    var operationsTitleNode = document.getElementById("leftbar.operations.title");
    //operationsTitleNode.innerHTML= "<s:text name="leftbar.operations"></s:text>";
    operationsTitleNode.style.display='none';
    
    function leftbar_setProgressBar(processId, pourcentage, text, isActive) {
    	
    	var progressBarNode = document.getElementById("leftbar.progressbars."+processId);
    	if (progressBarNode==null) {
    		var insertSpot = document.getElementById("leftbar.progressbars.insertspot");
        	progressBarNode = document.getElementById("progressbar-template").cloneNode(true);
        	insertSpot.appendChild(progressBarNode);
    	}
    	
    	progressBarNode.id="leftbar.progressbars."+processId;
    	
    	var pourcentageNode = progressBarNode.querySelector("._pourcentage_");
    	pourcentageNode.style.width=pourcentage+"%";
    	
    	var textNode = progressBarNode.querySelector("._text_");
    	textNode.innerHTML=text;
    	
    	progressBarNode.style.display='block';
    
    	// when progress reaches 100%, progress bar disappear
    	if (pourcentage>=100 || isActive==false) {
    		if (pourcentage<100) { 
    			pourcentageNode.classList.add('bg-success');
    			pourcentageNode.classList.add('bg-warning');
    		}
    		
    		timeoutMs=PROGRESS_FINISHED_DISAPPEAR_DELAY_SEC*1000;
    		setTimeout(function() {
    			// might have already bean remove before if several '100%' progress are received
    			var progressBarNode = document.getElementById("leftbar.progressbars."+processId);
    			if (progressBarNode!=null) { progressBarNode.parentNode.removeChild(progressBarNode); }
    		}, timeoutMs);
    		
    	}
    	
    }
    
    function leftbar_addOperation(operationNode) {
    	var insertSpot = document.getElementById("leftbar.operations.insertspot");
    	insertSpot.append(operationNode);
    }
    function leftbar_clearOperations() {
    	var insertSpot = document.getElementById("leftbar.operations.insertspot");
    	clearNodeChildren(insertSpot);
    }
    function leftbar_setTitle(str,onClickFunction) {
    	var leftbarTitleNode = document.getElementById("leftbar.title");
        leftbarTitleNode.innerHTML=capWords(str);     
        var leftBarTitleContainer=document.getElementById("leftbar.title-container");
        leftBarTitleContainer.onclick=function(event) {
        	event.stopPropagation();
        	if (onClickFunction!=null) { onClickFunction() };
        }
    }
    function leftbar_setUpTitle(str,onClickFunction) {
    	var leftbarUpTitleNode = document.getElementById("leftbar.uptitle");
    	leftbarUpTitleNode.innerHTML=capWords(str);             
    }
    // Public interface
    MxGuiLeftBar={};
    MxGuiLeftBar.addOperation=leftbar_addOperation;
    MxGuiLeftBar.clearOperations=leftbar_clearOperations;
    MxGuiLeftBar.setTitle=leftbar_setTitle;
    MxGuiLeftBar.setUpTitle=leftbar_setUpTitle;
    MxGuiLeftBar.setProgressBar=leftbar_setProgressBar;
    
    </script>
