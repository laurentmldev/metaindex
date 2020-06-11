<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<c:url value="/" var="mxurl"/>

<script type="text/javascript">

// ---------------- DOWNLOAD GEXF File-----------------



MxGuiLeftBar.showDownloadGexfPrevisu=function() {
	// body
	let previsuNode=document.getElementById('gexf_contents_previsu_body_download').cloneNode(true);
	previsuNode.style.display='block';
	
	let nbEntries=previsuNode.querySelector("._maxNbEntries_");
	let gexfColsTable=previsuNode.querySelector("._gexf_columns_tbl_");
	
	
	// footer
	let previsuNodeFooter=document.getElementById('gexf_contents_previsu_download_footer').cloneNode(true);
	previsuNodeFooter.style.display='block';
	
	// Go button
	let downloadBtn=previsuNodeFooter.querySelector('._downloadBtn_');
	downloadBtn.onclick=function() {
		let selectedTermNames=[];
		let checkboxesList = gexfColsTable.querySelectorAll("._gexfColCheck_");
		for (idx in checkboxesList) {
			curCheckBox=checkboxesList[idx];
			if (typeof(curCheckBox)!='object') { continue; }
			if (curCheckBox.checked) { selectedTermNames.push(curCheckBox.termName); }		
		}	
		
		let query = MxGuiHeader.getCurrentSearchQuery();
		let selectedFiltersNames=MxGuiLeftBar.getSelectedFiltersNames();
		let sortString = MxGuiHeader.getCurrentSearchSortString();
		let reversedOrder = MxGuiHeader.getCurrentSearchReversedOrder();
		ws_handlers_requestDownloadgexfFile(selectedTermNames,query,selectedFiltersNames,sortString,reversedOrder); 
		
	}
	
	
	
	// show
	MxGuiHeader.showInfoModal("<s:text name='Items.downloadItems.asgexf' />",previsuNode,previsuNodeFooter);
	
	
	
}
</script>



 	 	  <!-- Custom gexf FileDownload button -->
          <label id="gexf_download_label"
 		  	class="_openBtn_ d-none d-sm-inline-block btn btn-sm btn-info shadow-sm mx-left-button" 
 		  	onclick="MxGuiLeftBar.showDownloadGexfPrevisu();" >
 		  	<i class="fas fa-download fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.downloadItems.asgexf"></s:text>
 		  	 <span title="S.O.S" 
	                	onclick="event.stopPropagation();event.preventDefault();
	                			MxGuiHeader.showInfoModal('<s:text name="help.items.gexf_download.title" />','<s:text name="help.items.gexf_download.body" />')">
	                   <i class="mx-help-icon far fa-question-circle" style="color:white"></i>    
	          </span>
 		  </label>
 		  
 		 <div id="gexf_contents_previsu_body_download" style="display:none">
 		  	 
	 		  	<s:text name="Items.currentSearch" /> : <span id="MxGui.left.gexfdownload.nbMatchDocs"></span>  <s:property value='currentUserProfile.catalogVocabulary.itemsCap'/> 		  			 		  		 
		  		 <hr/>
		  		 <table style="margin-top:1rem;margin-left:2rem;" class="_gexf_columns_tbl_">
		  		 	<tr><th style="padding-right:1rem;">
		  		 		<input class="_global_checkbox_" type="checkbox" /></th><th><s:text name="Items.downloadItems.gexfSelectedCols" /></th> 
		  		 	</tr>
		  		 </table>
		  </div>
		  <div id="gexf_contents_previsu_download_footer" style="display:none">
 		  		 <label class="_downloadBtn_ d-none d-sm-inline-block btn btn-sm btn-info shadow-sm mx-left-button"  >
 		  				<i class="fas fa-download fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.downloadItems.generate"></s:text>
 		  		</label>
		  </div>
 		 