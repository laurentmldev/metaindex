<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

  <script type="text/javascript" >
  
  function websocketsStartup() {
	  mx_ws_connect(mxHost,mxApiConnectionParams,onWsConnect /* from ws_handlers */);	  
  }
 
  window.onload=function(){
	  
	websocketsStartup();
	
	$(document).ready(function() {
	  //toggle `popup` / `inline` mode
	  $.fn.editable.defaults.mode = 'popup';	
	 });
		
 }
    
</script>
  
  
  
