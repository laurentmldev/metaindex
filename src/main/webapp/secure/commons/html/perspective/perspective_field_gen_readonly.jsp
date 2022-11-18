<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 

<!--------------- Generic Customizable Field -------------->		  
 <script type="text/javascript" >

 function _commons_perspective_build_readonly_field(catalogDesc,tabIdx,sectionIdx,fieldIdx,
		 							fieldContainerNode,fieldVisuDesc,termDesc,itemId,fieldValue,
		 							getLongFieldFullValueCallback/*for long_text fields only*/) {
 	 if (fieldValue==null) { fieldValue=""; }
 	if (termDesc.datatype=="IMAGE_URL") {
 		MxGuiPerspective.buildImgUrl_RO(catalogDesc,fieldContainerNode,fieldVisuDesc,termDesc,fieldValue);
 	}
 	else if (termDesc.datatype=="PAGE_URL" 
 			|| termDesc.datatype=="AUDIO_URL"
 			|| termDesc.datatype=="VIDEO_URL") {
 		MxGuiPerspective.buildUrl_RO(catalogDesc,fieldContainerNode,fieldVisuDesc,termDesc,fieldValue);
 	}
 	else if (termDesc.datatype=="LINK") {
 		MxGuiPerspective.buildLink_RO(catalogDesc,fieldContainerNode,fieldVisuDesc,termDesc,itemId,fieldValue);
 	}
 	else if (termDesc.datatype=="LONG_TEXT") {
 		MxGuiPerspective.buildLongText_RO(itemId,catalogDesc,fieldContainerNode,fieldVisuDesc,termDesc,fieldValue,getLongFieldFullValueCallback);
 		
 	}
 	else {
 		MxGuiPerspective.buildTinyText_RO(catalogDesc,fieldContainerNode,fieldVisuDesc,termDesc,fieldValue);
 	}
  }
  
 
</script>





