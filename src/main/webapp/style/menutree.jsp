<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>

<style type="text/css" >

	ul.menutree 
	{ 
		padding:0 0 0 0; 
		position:relative;
		
	}
	
	li.menutree { 
		padding:0 0 0 0; 
		list-style:none; 
		position:relative;
	     cursor: pointer;
	}

	li.menutree input { display:block; }
	
	li.menutree label::before {
		font-size:2em;
		content: '+ ';
	}
	li.menutree input:checked + label:before {
		font-size:3em;
		content: '-  '; 
	}
	
	/* special for the root of the list: */
	ul.menutree li.menutree {margin-left:10px; }
	ul.menutree > li.menutree:first-child:before { display:none; }
	ul.menutree input { display:none; }
	
	/* here's the part that does the expanding and collapsing: */
	input + label + span + ul.menutree { display:none; }
	input:checked + label + span + ul.menutree { display:block; }
	
 
.treechoice:hover {
	cursor: pointer;
}

.treechoice {

}

li.menutree label {
 	font-size:0.7em;
 }   
li.menutree label:hover {
	cursor: pointer;
 }   

.treechoice_dataset,.treechoice_metadata {
	padding-right:10px;	
	padding-top:3px;
	padding-bottom:3px;
	border-radius:10px;
}

.editModeDatasetMouseOverMenu,.editModeMetadataMouseOverMenu,.treechoice_dataset:hover,.treechoice_metadata:hover {
	
}
.treechoice_dataset:hover,.treechoice_metadata:hover  {	
	cursor: pointer;
}
.treechoice_dataset {
	font-size:1em;
}

.treechoice_metadata {
	font-size:0.8em;
	padding-left:20px;
}
</style>
