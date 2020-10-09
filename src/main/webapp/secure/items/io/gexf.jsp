<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<c:url value="/" var="mxurl"/>

<script type="text/javascript">

// ---------------- DOWNLOAD GEXF File-----------------

MxGuiLeftBar.showDownloadGexfPrevisu=function() {
	
	let catalogDescr = MxGuiDetails.getCurCatalogDescription();
	let sortedTermsNames = Object.keys(catalogDescr.terms).sort();
	
	// body
	let previsuNode=document.getElementById('gexf_contents_previsu_body_download').cloneNode(true);
	previsuNode.style.display='block';
	
	// nodes data
	let fieldsSelectionInsertSpot=previsuNode.querySelector("._nodes_data_selection_insertspot_");
	for (var termIdx=0;termIdx<sortedTermsNames.length;termIdx++) {
		let termName=sortedTermsNames[termIdx];		
		let termDescr = catalogDescr.terms[termName];
		let termDatatype = termDescr.datatype;
		if (termDatatype=="LINK") { continue; }
		termTranslation=mx_helpers_getTermName(termDescr, catalogDescr)
		
		let termChoice=document.createElement("div");
		fieldsSelectionInsertSpot.appendChild(termChoice);
		let termCheck=document.createElement("input");
		termChoice.appendChild(termCheck);
		termCheck.termDescr=termDescr;
		termCheck.classList.add("_graph_node_data_field_");
		termCheck.checked=true;
		termCheck.setAttribute("type","checkbox");
		
		let termNameNode=document.createElement("span");
		termChoice.appendChild(termNameNode);
		termNameNode.innerHTML=termTranslation;
		termNameNode.style="padding-left:1rem";
		if (termTranslation!=termName) { termNameNode.innerHTML+= " ("+termName+")"; }
		
	}	
	
	// edges data
	let edgesSelectionInsertSpot=previsuNode.querySelector("._edges_selection_insertspot_");	
	for (var termIdx=0;termIdx<sortedTermsNames.length;termIdx++) {
		let termName=sortedTermsNames[termIdx];		
		let termDescr = catalogDescr.terms[termName];
		let termDatatype = termDescr.datatype;
		if (termDatatype!="LINK") { continue; }
		termTranslation=mx_helpers_getTermName(termDescr, catalogDescr)
		
		let termChoice=document.createElement("div");
		edgesSelectionInsertSpot.appendChild(termChoice);
		let termCheck=document.createElement("input");
		termChoice.appendChild(termCheck);
		termCheck.termDescr=termDescr;
		termCheck.classList.add("_graph_edge_field_");
		termCheck.checked=true;
		termCheck.setAttribute("type","checkbox");
		
		let termNameNode=document.createElement("span");
		termChoice.appendChild(termNameNode);
		termNameNode.innerHTML=termTranslation;
		termNameNode.style="padding-left:1rem";
		if (termTranslation!=termName) { termNameNode.innerHTML+= " ("+termName+")"; }
		
	}	
	
	// footer
	let previsuNodeFooter=document.getElementById('gexf_contents_previsu_download_footer').cloneNode(true);
	previsuNodeFooter.style.display='block';
	
	// Go button
	let downloadBtn=previsuNodeFooter.querySelector('._downloadBtn_');
	downloadBtn.onclick=function() {
		
		
		let nodesDataTermIds=[];
		let edgesTermIds=[];
		
		let nodesDataFields=previsuNode.querySelectorAll("._graph_node_data_field_");					
		for (let nodeDataFieldIdx=0;nodeDataFieldIdx<nodesDataFields.length;nodeDataFieldIdx++) {			
			let curNodeDataFieldCheckBox=nodesDataFields[nodeDataFieldIdx];
			if (curNodeDataFieldCheckBox.checked==true) {
				nodesDataTermIds.push(curNodeDataFieldCheckBox.termDescr.id);			
			}
		}
		let edgesFields=previsuNode.querySelectorAll("._graph_edge_field_");
		for (let edgeFieldIdx=0;edgeFieldIdx<edgesFields.length;edgeFieldIdx++) {
			let curEdgeFieldCheckBox=edgesFields[edgeFieldIdx];
			if (curEdgeFieldCheckBox.checked==true) {
				edgesTermIds.push(curEdgeFieldCheckBox.termDescr.id);
			}
		}
		
		let query = MxGuiHeader.getCurrentSearchQuery();
		let selectedFiltersNames=MxGuiLeftBar.getSelectedFiltersNames();
		let sortString = MxGuiHeader.getCurrentSearchSortString();
		let reversedOrder = MxGuiHeader.getCurrentSearchReversedOrder();
		ws_handlers_requestDownloadGraphFile(nodesDataTermIds,edgesTermIds,query,selectedFiltersNames,sortString,reversedOrder);
			
	}
	
	// show
	MxGuiHeader.showInfoModal("<s:text name='Items.downloadItems.asGexf' />",previsuNode,previsuNodeFooter);
	
};
</script>



 	 	  <!-- Custom gexf FileDownload button -->
          <label id="gexf_download_label"
 		  	class="_openBtn_ d-none d-sm-inline-block btn-big btn btn-info shadow-sm mx-left-button" 
 		  	onclick="MxGuiLeftBar.showDownloadGexfPrevisu();" >
 		  	<i class="fas fa-download fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.downloadItems.asGexf"></s:text>
 		  	 <span title="S.O.S" 
	                	onclick="event.stopPropagation();event.preventDefault();
	                			MxGuiHeader.showInfoModal('<s:text name="help.items.gexf_download.title" />','<s:text name="help.items.gexf_download.body" />')">
	                   <i class="mx-help-icon far fa-question-circle" style="color:white"></i>    
	          </span>
 		  </label>
 		  
 		 <div id="gexf_contents_previsu_body_download" style="display:none">
 		 	<h5>Nodes Data</h5>
 		  	 <div class="_nodes_data_selection_insertspot_">	 		  	
		  	</div>
		  	<hr/>
		  	<h5>Edges</h5>
		  	<div class="_edges_selection_insertspot_">	 		  	
		  	</div> 		  			 		  		 
		  </div>
		  <div id="gexf_contents_previsu_download_footer" style="display:none">
 		  		 <label class="_downloadBtn_ d-none d-sm-inline-block btn-big btn btn-info shadow-sm mx-left-button"  >
 		  				<i class="fas fa-download fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.downloadItems.generateGexf"></s:text>
 		  		</label>
		  </div>
 		 