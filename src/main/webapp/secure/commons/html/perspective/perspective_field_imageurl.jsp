<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 

<!--------------- IMAGEURL -------------->		  
 <script type="text/javascript" >


 function _commons_perspective_build_readonly_field_image_url(catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,fieldVisuDesc,termDesc,fieldValue) {
 	
 	 let fieldNode=document.getElementById("_commons_perspectives_field_readonly_template_image_url").cloneNode(true);
 	 fieldNode.id="";
 	 fieldNode.style.display="block";
 	 
 	 // title
 	 let title = fieldNode.querySelector("._title_");
 	 if (fieldVisuDesc.showTitle==true) { title.innerHTML=mx_helpers_getTermName(termDesc, catalogDesc)+": "; }
 	 else { title.style.display='none'; }
 	 
 	 // value
 	 let valueNode = fieldNode.querySelector("._value_");
 	 valueNode.classList.add("mx-perspective-field-img-size-"+fieldVisuDesc.size);
 	 var regexAbsoluteUrl = /^http/;
 	 let imgUrl=fieldValue;
 	 if (imgUrl=="") { valueNode.style.display="none"; }
 	 
 	 // add prefix from Catalog params, if given URL is relative
 	 if (imgUrl!="" && imgUrl!=null 
 			 && catalogDesc.itemsUrlPrefix!="" && catalogDesc.itemsUrlPrefix!=null 
 			 && regexAbsoluteUrl.test(imgUrl)==0) {
 	 	imgUrl=catalogDesc.itemsUrlPrefix+"/"+imgUrl; 
 	 }
 	 valueNode.src=imgUrl;
 	 valueNode.title=imgUrl;
 	 
 	 fieldContainerNode.appendChild(fieldNode);
  }

</script>


<div style="display:none;" class="mx-perspective-field" id="_commons_perspectives_field_readonly_template_image_url"  >
	<table style="height:100%;width:100%" >	
		<tr><td ><img class="_value_ mx-perspective-field-img " src="" onclick="window.open(this.src,'_blank');" /></td></tr>
		<tr><td style="text-align:center" class="mx-perspective-field-title"><span class="_title_"></span></td></tr>
	</table>	               
</div>




