<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<c:url value="/media/img/" var="imgdir"/>
<c:url value="/media/img/icons/gui/${selected_guitheme}" var="guiIconsUrl"/>
 
<style type="text/css" >
@charset "UTF-8";

html {
background : #474747 url("${imgdir}/bg.png");
color : #999
}


a,a:link,.clickable {
color:#aaa;
}

a:hover, .clickable:hover {
color:#ccc;
}

a:active,.clickable:active {
color:#eee;
}

table tr td {
	border-radius:2px;
}
.horizontalLineTable tr:nth-child(odd) {
	background-color: transparent;
	}

.horizontalLineTable tr:nth-child(even) {
	background-color: #555555;
	}
	
.fieldtitle {
color:#BB9;
}

.metadaText {
color:#AAA;
}
.comment {
color:#777;
}

.legendcomment {
color:#707070;
font-size:0.8em;
}

.usermessages {
background: rgba(0, 0, 0, 0.2);
}


#chatroom {
border:1px solid grey;
background:#333335;
border-radius:3px;
opacity:0.6;
}

#chat-contents {
font-family:monospace; 
color:grey;
background:#444;
font-size:0.8em;
border-radius:5px;
}

.chat-entry {
background:grey;
font-size:1m;
border-radius:7px;
}

.chat-entry legend {
background:lightblue;
color:darkblue;
border-radius:6px;
font-style:italic;
}

.chat-connect-button {
font-size:0.6em;
}
.negative {
	color: #222; 
}


.positive {
}

.volumeText {
color: grey;
}

	
.editshadowcard {
	background: rgba(45, 45, 45, 0.5);
}
.modal,.closemodal {
	background: rgba(65, 65, 65, 0.9);
}

.menushadowcard {
	background: linear-gradient(to bottom, #565959 0%, #353838 100%);	
}
.flatzone {
}

.modifyable {
	color:#88AA88;
}
.emptyModifyable {
	font-style: italic;
}

.errorFieldValue {
	color:#DD8888;
	padding:4px;
}
.dynamicCatalog {
	font-style: italic;
}

.templateCatalogInCatalogsList {
	color:#666;
}
.templateCatalogInCatalogsList:hover {
	color:#777;
}

.selectedItem {
	border : none;
  	background: #123456;
}
.slideItem {
background: #303030;
}
.slideItem:hover {
background: #454545;

}
.slideItemContent:hover {
-webkit-box-shadow: 0 0 5px rgba(150, 150, 150, 0.8);
box-shadow: 0 0 5px rgba(150, 150, 150, 0.8);
}

.slideItemElementId {
	font-size:0.8em;
}
.slideItemTemplateAnchor {
	color:#88AA88;
	font-size:0.7em;
}

.dynamicSlideItem {
	background: #454545;
}
.staticSlideItem {
	background: #353535;
}

.templateSlideItem {
	border:3px dashed grey;
}

.errorTemplateSlideItem {
	background:#633;	
}

.selectedErrorTemplateSlideItem {
	border:3px solid #DD8888;
		-webkit-box-shadow: 0 0 10px rgba(250, 150, 150, 0.7);
	box-shadow: 0 0 10px rgba(250, 150, 150, 0.7);
}
.selectedSlideItem,.multiSelectedSlideItem {
background: #345;
	-webkit-box-shadow: 0 0 10px rgba(150, 150, 250, 0.4);
	box-shadow: 0 0 10px rgba(150, 180, 250, 0.4);

}

.selectedSlideItem {
border:3px solid #7799BB;
}
.selectedTemplateSlideItem {
	border-style: dashed ;
}


.multiselectCmds_fieldset {
  	border-color: #9AB;
  	background:#456;
} 
.multiselectCmds_legend {
  	color: #9AB;  	
} 

.multiselectCmds_legend span {
	color: white;
}
/* ------------ Menu Bar ------------ */

.elementId {
background:#303030;
}

.editModeDataset {
background:#444;
border:1px grey solid;
}
.editModeMetadata {
background:#404850;
border:1px grey solid;
}
.editModeDatasetRO {
border:1px grey dotted;
background:#444; 
}
.editModeMetadataRO {
border:1px grey solid;
background:#555;
}
.editModeDatasetMouseOver,.editModeDatasetMouseOverRO {

border:1px solid #668866;
}

.editModeMetadataMouseOver,.editModeMetadataMouseOverRO {
border:1px solid #66BB66;
}

.editModeDatasetSelected,.editModeDatasetSelectedRO {
background:#656565;
border:2px solid #66BB66;
box-shadow: 1px 2px 5px rgba(30, 30, 30, 0.8);
}

.editModeMetadataSelected {
background:#404850;
border:2px solid #66BB66;
box-shadow: 1px 2px 5px rgba(30, 30, 30, 0.8);
}
.editModeMetadataSelectedRO {
background:#656565;
border:2px solid #66BB66;
box-shadow: 1px 2px 5px rgba(30, 30, 30, 0.8);
}
 .dropping {
	 background: #543210;
 }

.dropzone_catalog {
	border:1px solid grey;
	background: #345;
	border-radius: 5px;
}



.dropping_element {
	font-size:1.3em;
	background: #543210;
	color:white;
	border:1px solid grey;
	box-shadow:none;
	padding:5px;
	text-align:center;
}

.dropzone_dataset,.dropzone_metadata {
	border:1px dotted grey;
}


.insertzone_metadata div,.insertzone_dataset div {
	background:#558855;
 }

.insertzone_metadata,.insertzone_dataset {
	background:#558855;
}
.uploadcsvcatalog_dataset {
	border-radius:5px;
	background-color:#345678;
}

.uploadcsvcatalog_regex_table {
	border-radius:3px; 
	border:1px dashed #999;
}
.uploadcsvcatalog_resultregex_row {	
	background-color:#345634;
}

.dropzone_metadata_draghover, .dropzone_dataset_draghover {
	 border:3px solid grey;
	 color:black;
	 background:#123456;	 
 }

.treechoice_dataset,.treechoice_metadata  {
	color:#888;
}
.treechoice_dataset:hover,.treechoice_metadata:hover  {
	color:#99BBCC;	
}

.editModeDatasetMouseOverMenu,.editModeMetadataMouseOverMenu {
color:#99BBCC;
}
.editModeDatasetSelectedMenu {
color:#CCC;
}
.editModeMetadataSelectedMenu {
color:#CCC;
}


li.menutree label {
	color:#99BBCC;
}

.progressbar {
	background: #d65946;
	background: -moz-linear-gradient(top,  #d65946 0%, #cb4b2f 40%, #a02300 56%, #e35121 100%);
 		background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#d65946), color-stop(40%,#cb4b2f), color-stop(56%,#a02300), color-stop(100%,#e35121));
 		background: -webkit-linear-gradient(top,  #d65946 0%,#cb4b2f 40%,#a02300 56%,#e35121 100%);
 		background: -o-linear-gradient(top,  #d65946 0%,#cb4b2f 40%,#a02300 56%,#e35121 100%);
 		background: -ms-linear-gradient(top,  #d65946 0%,#cb4b2f 40%,#a02300 56%,#e35121 100%);
 		background: linear-gradient(top,  #d65946 0%,#cb4b2f 40%,#a02300 56%,#e35121 100%);
 		filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#d65946', endColorstr='#e35121',GradientType=0 );
}

#cssmainmenu ul,
#cssmainmenu li,
#cssmainmenu a {
  list-style: none;
  margin: 0;
  padding: 0;
  border: 0;
  line-height: 1;
}

#cssmainmenu {

  width: auto;
}

#cssmainmenu ul {
  background: #777;
  background: linear-gradient(to bottom, #777 0%, #aaa 100%);
  
 padding: 4px 15px 4px 15px;
  display: block;
  text-decoration: none;
  text-shadow: 1px 2px 2px #777;
  color:#ddd;
  font-size: 1.5em;
  border-radius: 3px;
}


#cssmainmenu li a {
  color:#ddd;
}

#cssmainmenu ul,
#cssmainmenu li,
#cssmainmenu a {
  list-style: none;
  margin: 0;
  padding: 0;
  border: 0;
  line-height: 1;
}
#cssmainmenu {
  border: 1px solid #444;
  border-radius: 3px;
  width: auto;
}
#cssmainmenu ul {
  zoom: 1;
padding: 5px 10px;  
}
#cssmainmenu ul:before {
  content: '';
  display: block;
}
#cssmainmenu ul:after {
  content: '';
  display: table;
  clear: both;
}
#cssmainmenu li {
  float: left;
  margin: 0 5px 0 0;

}

#cssmainmenu .language, #cssmainmenu .language a, #cssmainmenu .language span {
	float: right;
	padding-left: 0px;
	padding-right: 0px;
}

#cssmainmenu .language .separator, #cssmainmenu .language .separator:hover {
	color:#ddd;
}



#cssmainmenu a {
  padding: 4px 15px 4px 15px;
  display: block;
  text-decoration: none;
  text-shadow: 1px 2px 2px #777;
  color:#ddd;
  font-weight : bold;
 
}

#cssmainmenu li a {
  padding: 4px 15px 4px 15px;
  display: block;
  text-decoration: none;
  text-shadow: 1px 2px 2px #777;
  color:#ddd;
  
}
#cssmainmenu li.active {
  text-shadow: 1px 2px 2px #777;
  color:#ddd;
  
}
#cssmainmenu li.active a {
text-shadow: 1px 2px 2px #444;
  color:#fff;
}
#cssmainmenu li:hover {
text-shadow: 1px 2px 2px #444;
  color:#fff;
}
#cssmainmenu li:hover a {
 text-shadow: 1px 2px 2px #444;
  color:#fff;
}
</style>
