<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>

<style type="text/css" >

.modal_shadow {
border:2px white solid;
	display:none;
	position: fixed;
	top:0;
	left:0;
	z-index:10;
	width:100%;
	height:100%;
	background-color: rgba(0,0,0,0,0.75);
}
.modal_back{
border:2px white solid;
	display:table-cell;
	vertical-align:middle;
}
.modal {
border:2px white solid;
	margin:0 auto 0 auto;
	padding:20px;
	text-align: center;
	border-radius: 10px;
	display:table;
	margin-top:5px;
	box-shadow: 0 0 0 9999px rgba(0,0,0,0.5);
}

.modalclose {
	z-index: 11;
	background: #606061;
	color: #FFFFFF;
	line-height: 25px;
	position: relative;
	text-align: center;
	padding:3px;
	width: 24px;
	text-decoration: none;
	font-weight: bold;
	-webkit-border-radius: 12px;
	-moz-border-radius: 12px;
	border-radius: 12px;
	-moz-box-shadow: 1px 1px 3px #000;
	-webkit-box-shadow: 1px 1px 3px #000;
	box-shadow: 1px 1px 3px #000;
}

.modal table {
	margin:10px;
}

.modal table tr td {
	padding:10px;
}

    
</style>
