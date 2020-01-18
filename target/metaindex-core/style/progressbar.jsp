<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>

<style type="text/css" >

	.progressbar_container{
   		width:100%;
   		height:15px;
   		border:1px solid #000;
   		overflow:hidden;
   		background: #cedce7;
   		background: -moz-linear-gradient(top,  #cedce7 0%, #596a72 100%);
   		background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#cedce7), color-stop(100%,#596a72));
   		background: -webkit-linear-gradient(top,  #cedce7 0%,#596a72 100%);
   		background: -o-linear-gradient(top,  #cedce7 0%,#596a72 100%);
   		background: -ms-linear-gradient(top,  #cedce7 0%,#596a72 100%);
   		background: linear-gradient(top,  #cedce7 0%,#596a72 100%);
   		filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#cedce7', endColorstr='#596a72',GradientType=0 );
   	}
   	.progressbar {
   		width:0%;
   		height:15px;
   		border-right: 1px solid #000000;   		
   		
   	}
   	.progressbar_text {
   		color: #222;
   	    font-size: 0.8em;
   	    font-style: italic;
   	    font-weight: bold;
   	    left: 25px;
   	    position: relative;
   	    top: -12px;
   	}
   	
 </style>
