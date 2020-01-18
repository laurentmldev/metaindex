<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 



 <div class="right-sidebar-wrapper" >
 		
        	<center>
        	
        	<div id="rightbar.title" class="sidebar-brand-text mx-3" style="display:none"></div>
        	<div id="right_sidebar_insertspot" />
        	
        	
        	</center>
        </div>
   
    <script>
    
    function details_rightbar_setTitle(str,onClickFunction) {
    	var rightbarTitleNode = document.getElementById("rightbar.title");
    	rightbarTitleNode.innerHTML=capWords(str);     
    	rightbarTitleNode.onclick=function(event) {
        	event.stopPropagation();
        	if (onClickFunction!=null) { onClickFunction(event) };
        }
    	rightbarTitleNode.style.display='block';        
    }
    
    function details_rightbar_addContents(nodeToAdd) {
    	var insertSpot = document.getElementById("right_sidebar_insertspot");
    	insertSpot.appendChild(nodeToAdd);
    }
    // Public interface
    MxGuiDetailsRightBar={};
    MxGuiDetailsRightBar.setTitle=details_rightbar_setTitle;
    MxGuiDetailsRightBar.addContents=details_rightbar_addContents;
    
    </script>
