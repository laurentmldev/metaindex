<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 

<!--------------- LINKURL -------------->		  
 <script type="text/javascript" >


 function _commons_perspective_build_readonly_field_link_url(catalogDesc,fieldContainerNode,fieldVisuDesc,termDesc,fieldValue) {
 	
 	 let fieldNode=document.getElementById("_commons_perspectives_field_readonly_template_link_url").cloneNode(true);
 	 fieldNode.id="";
 	 fieldNode.style.display="block";
 	 
 	 // title
 	 let titleNode = fieldNode.querySelector("._title_");
 	 if (fieldVisuDesc.showTitle==true) { 
 		titleNode.innerHTML=mx_helpers_getTermName(termDesc, catalogDesc)+": ";
 		titleNode.title=termDesc.name;
 	 }
 	 else { titleNode.style.display='none'; }
 	 
 	 
 	 // value
 	 let valueNode = fieldNode.querySelector("._value_");
 	 
 	// base text class
 	 valueNode.classList.add("mx-perspective-field-text-value");
 	 
 	 // size
 	 let textSizeClass="mx-perspective-field-text-size-"+fieldVisuDesc.size;
 	 fieldNode.classList.add(textSizeClass);
 	 
 	 var regexAbsoluteUrl = /^http/;
 	 let imgUrl=fieldValue;
 	 if (imgUrl=="") { valueNode.style.display="none"; }
 	 
 	 // add prefix from Catalog params, if given URL is relative
 	 if (imgUrl!="" && imgUrl!=null 
 			 && catalogDesc.itemsUrlPrefix!="" && catalogDesc.itemsUrlPrefix!=null 
 			 && regexAbsoluteUrl.test(imgUrl)==0) {
 	 	imgUrl=catalogDesc.itemsUrlPrefix+"/"+imgUrl; 
 	 }
 	
 	 valueNode.href=imgUrl;
 	 valueNode.innerHTML=fieldValue;
 	 valueNode.title=imgUrl;	 
 	 
 	 fieldContainerNode.appendChild(fieldNode);
  }
  
 MxGuiPerspective.buildUrl_RO=_commons_perspective_build_readonly_field_link_url;

</script>


 <div style="display:none;" class="mx-perspective-field" id="_commons_perspectives_field_readonly_template_link_url"  >
 	<table style="height:100%;width:100%" ><tr>
 		<td class="mx-perspective-field-title"><span class="_title_"></span>: </td>
 		<td><a target="_blank" class="_value_ mx-perspective-field-link mx-perspective-field-text-size-normal" href="" ></a></td>
 	<tr></table>	               
 </div>




