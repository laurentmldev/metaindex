<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 

<!--------------- TINYTEXT -------------->		  
 <script type="text/javascript" >

 var TINYTEXT_MAX_DISPLAY_NB_CHARS=120;
 
// from KooiInc https://stackoverflow.com/questions/1199352/smart-way-to-truncate-long-strings 
 function truncate( str, n, useWordBoundary ){
	  if (str.length <= n) { return str; }
	  const subString = str.substr(0, n-1); // the original check
	  return (useWordBoundary 
	    ? subString.substr(0, subString.lastIndexOf(" ")) 
	    : subString) + "&hellip;";
	};
	
	
 function _commons_perspective_build_readonly_field_tinytext(catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,fieldVisuDesc,termDesc,fieldValue) {
 	
 	 let fieldNode=document.getElementById("_commons_perspectives_field_readonly_template_tinytext").cloneNode(true);
 	 fieldNode.id="";
 	 fieldNode.style.display="block";
 	 
 	 // title
 	 let title = fieldNode.querySelector("._title_");
 	 if (fieldVisuDesc.showTitle==true) { title.innerHTML=mx_helpers_getTermName(termDesc, catalogDesc)+": "; }
 	 else { title.style.display='none'; }
 	 
 	 // value
 	 let valueNode = fieldNode.querySelector("._value_");
 	 let displayedStr=truncate(fieldValue,TINYTEXT_MAX_DISPLAY_NB_CHARS,true); 	 
 	 valueNode.innerHTML=displayedStr;
 	
 	// size 
 	 let textSizeClass="mx-perspective-field-text-size-"+fieldVisuDesc.size;
 	 fieldNode.classList.add(textSizeClass);	 
 	
 	 // base text class
 	 valueNode.classList.add("mx-perspective-field-text-value");
 	
 	 // color 
 	 let textColorClass="mx-perspective-field-text-color-"+fieldVisuDesc.color;
 	 valueNode.classList.add(textColorClass);	 
 	 
 	// weight 
 	 let textWeightClass="mx-perspective-field-text-weight-"+fieldVisuDesc.weight;
 	 valueNode.classList.add(textWeightClass);	 
 	 
 	 fieldContainerNode.appendChild(fieldNode);
  }
</script>



<div style="display:none;" class="mx-perspective-field" id="_commons_perspectives_field_readonly_template_tinytext"  >
	<table style="height:100%;width:100%"><tr>
		<td class="mx-perspective-field-title"><span class="_title_"></span></td>
		<td class="_value_"></td>
	</tr></table>               
</div>



