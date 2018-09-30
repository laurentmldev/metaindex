<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<script>

function insert_metadata_TinyTextRO(insertSpotId,m) {
	var insertSpotNode = document.getElementById(insertSpotId);
	var metadataContentsNode = document.getElementById("elementContents.template.metadata.tinytextRO").cloneNode(true);
	metadataContentsNode.id="readonly.metadata."+m.metadataId;
	metadataContentsNode.style.display='block';
	
	// title
	if (m.layoutDoDisplayName) {
		var title = metadataContentsNode.querySelector("._title_");
		title.innerHTML=m.metadataName+" : ";
		title.style.display='block';
	}
	
	// data
	var data = metadataContentsNode.querySelector("._data_");
	data.innerHTML=m.metadataName+" : ";
	if (m.fontWeight=='Italic') {
		data.style+="font-style:italic;";
	} else {
		data.style+="font-weight:"+m.fontWeight+";";
	}
	data.innerHTML=m.text
	insertSpotNode.appendChild(metadataContentsNode);
}

</script>

<span id="elementContents.template.metadata.tinytextRO"  class="matadataTextEdit"  style="display:none" >

		<span class="_title_ fieldtitle" style="display:none" ></span>
			
		<span class="_data_"
			style="" 
				>
			<s:property value="#curMetadata.asTinyText.text"/>
		</span>
		
</span>


	    	
	    			
		
