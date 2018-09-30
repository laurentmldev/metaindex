<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>

<style type="text/css" >


[draggable] {
  -moz-user-select: none;
  -khtml-user-select: none;
  -webkit-user-select: none;
  user-select: none;
}

 
 .dropping {
 	opacity:0.7; 	
 }
 .dropzone_dataset,.dropzone_metadata,.insertzone_metadata,.insertzone_dataset {
 
 	width:100px;
 	height:3px;
	text-align:center;
	border-radius:10px; 
	display:none;
	margin:5px;	
	cursor:pointer;
 } 



.insertzone_metadata:hover,.insertzone_dataset:hover {
	height:30px;
}
 .insertzoneMetadataInputs,.insertzoneDatasetInputs {
 	width:180px;
 	clear:both;
 	position:static;
 	padding:15px;
 	border-radius:10px;
	box-shadow: 3px 3px 6px #111111;
	border:1px solid white;
	
 }
 
 .dropzone_dataset_draghover,.dropzone_metadata_draghover {	  
 }
 
.insertzone_metadata div,.insertzone_dataset div {
	display:none;
}
  
    
/* disable dropzone children to fire drag 'enter' and 'leave' drag events */
.dropzone_metadata *,.dropzone_dataset  *
 {
     pointer-events: none;
 }
 
    
</style>
