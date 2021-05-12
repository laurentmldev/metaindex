<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 

<!--------------- Generic Customizable Field -------------->		  
 <script type="text/javascript" >

 function _commons_perspective_build_readonly_field(catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,fieldVisuDesc,termDesc,itemId,fieldValue) {
 	 if (fieldValue==null) { fieldValue=""; }
 	if (termDesc.datatype=="IMAGE_URL") {
 		_commons_perspective_build_readonly_field_image_url(catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,fieldVisuDesc,termDesc,fieldValue);
 	}
 	else if (termDesc.datatype=="PAGE_URL" 
 			|| termDesc.datatype=="AUDIO_URL"
 			|| termDesc.datatype=="VIDEO_URL") {
 		_commons_perspective_build_readonly_field_link_url(catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,fieldVisuDesc,termDesc,fieldValue);
 	}
 	else if (termDesc.datatype=="LINK") {
 		_commons_perspective_build_readonly_field_reference(catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,fieldVisuDesc,termDesc,itemId,fieldValue);
 	}
 	else if (termDesc.datatype=="LONG_TEXT") {
 		_commons_perspective_build_readonly_field_longtext(catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,fieldVisuDesc,termDesc,fieldValue);
 	}
 	else {
 		_commons_perspective_build_readonly_field_tinytext(catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,fieldVisuDesc,termDesc,fieldValue);
 	}
  }
  
 
</script>





