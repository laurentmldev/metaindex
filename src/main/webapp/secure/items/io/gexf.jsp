<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 


<script type="text/javascript">

// ---------------- DOWNLOAD GEXF File-----------------


function _buildGexfForm(catalogDescr,sortedTermsNames) {
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
		MxGuiHeader.hideInfoModal();
	}
	
	return { "body":previsuNode, "footer":previsuNodeFooter };
}


function _buildGexfGroupByForm(catalogDescr,sortedTermsNames) {
	// body
	let previsuNode=document.getElementById('gexfgroupby_contents_previsu_body_download').cloneNode(true);
	previsuNode.style.display='block';
	
	// nodes data
	let fieldsSelectionInsertSpot=previsuNode.querySelector("._groupTerm_selector_");
	for (var termIdx=0;termIdx<sortedTermsNames.length;termIdx++) {
		let termName=sortedTermsNames[termIdx];		
		let termDescr = catalogDescr.terms[termName];
		let termDatatype = termDescr.datatype;
		if (termDatatype=="LINK") { continue; }
		termTranslation=mx_helpers_getTermName(termDescr, catalogDescr)
		
		let termChoice=document.createElement("option");
		fieldsSelectionInsertSpot.appendChild(termChoice);
		termChoice.value=termDescr.id;
		termChoice.innerHTML=termTranslation;
		if (termIdx==0) { fieldsSelectionInsertSpot.value=termDescr.id; }
				
	}	
	
	
	// edges data
	let edgesSelectionInsertSpot=previsuNode.querySelector("._edgeTerm_selector_");	
	for (var termIdx=0;termIdx<sortedTermsNames.length;termIdx++) {
		let termName=sortedTermsNames[termIdx];		
		let termDescr = catalogDescr.terms[termName];
		let termDatatype = termDescr.datatype;
		if (termDatatype!="LINK") { continue; }
		termTranslation=mx_helpers_getTermName(termDescr, catalogDescr)
		
		let termChoice=document.createElement("option");
		edgesSelectionInsertSpot.appendChild(termChoice);
		termChoice.value=termDescr.id;
		termChoice.innerHTML=termTranslation;		
		if (termIdx==0) { edgesSelectionInsertSpot.value=termDescr.id; }
	}	
	
	// footer
	let previsuNodeFooter=document.getElementById('gexfgroupby_contents_previsu_download_footer').cloneNode(true);
	
	// Go button
	let downloadBtn=previsuNodeFooter.querySelector('._downloadGroupByBtn_');
	downloadBtn.onclick=function(event) {
		
		let groupTermNode=previsuNode.querySelector("._groupTerm_selector_");
		let edgeTermNode=previsuNode.querySelector("._edgeTerm_selector_");
		let groupTermId=groupTermNode.value;					
		let edgeTermId=edgeTermNode.value
				
		let query = MxGuiHeader.getCurrentSearchQuery();
		let selectedFiltersNames=MxGuiLeftBar.getSelectedFiltersNames();
		let sortString = MxGuiHeader.getCurrentSearchSortString();
		let reversedOrder = MxGuiHeader.getCurrentSearchReversedOrder();
		ws_handlers_requestDownloadGraphGroupByFile(groupTermId,edgeTermId,query,selectedFiltersNames,sortString,reversedOrder);
		MxGuiHeader.hideInfoModal();
	}
	
	return { "body":previsuNode, "footer":previsuNodeFooter };
}


MxGuiLeftBar.showDownloadGexfPrevisu=function() {
	
	let catalogDescr = MxGuiDetails.getCurCatalogDescription();
	let sortedTermsNames = Object.keys(catalogDescr.terms).sort();
	
	let bodyAndFooter = _buildGexfForm(catalogDescr,sortedTermsNames);
	let bodyAndFooterGroupBy = _buildGexfGroupByForm(catalogDescr,sortedTermsNames);

	let center=document.createElement("center");	
	let gexfModeSelector=document.getElementById("gexfModeSelector").cloneNode(true);
	center.append(gexfModeSelector);
	gexfModeSelector.style.display="block";

	let choiceNormal=gexfModeSelector.querySelector("._option_normal_");
	choiceNormal.onclick=function(e) {
		bodyAndFooter.body.style.display='block';
		bodyAndFooter.footer.style.display='block';
		bodyAndFooterGroupBy.body.style.display='none';
		bodyAndFooterGroupBy.footer.style.display='none';
	}
	let choiceGroupBy=gexfModeSelector.querySelector("._option_groupby_");
	choiceGroupBy.onclick=function(e) {
		bodyAndFooter.body.style.display='none';
		bodyAndFooter.footer.style.display='none';
		bodyAndFooterGroupBy.body.style.display='block';
		bodyAndFooterGroupBy.footer.style.display='block';
	}
	
	
	let bodyMainContent = document.createElement("div");
	bodyMainContent.append(center);
	bodyMainContent.append(bodyAndFooter.body);
	bodyMainContent.append(bodyAndFooterGroupBy.body);
	
	let footerContents = document.createElement("div");
	footerContents.append(bodyAndFooter.footer);
	footerContents.append(bodyAndFooterGroupBy.footer);
	
	choiceNormal.onclick();
	// show
	MxGuiHeader.showInfoModal("<s:text name='Items.downloadItems.asGexf' />",bodyMainContent,footerContents);
	
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
 		  
 		  <table id="gexfModeSelector" style="display:none"><tr>
 		  	<td><select class="modals-form-control-dropdown modals-form-control form-control bg-light border-0 small"
 		  				style="width:40%;margin:1rem;height:auto;">
 		  			<option class="_option_normal_" value="normal"><s:text name="Items.downloadItems.gexf.modeNormal" /></option>
 		  			<option class="_option_groupby_" value="groupby"><s:text name="Items.downloadItems.gexf.modeGroupBy" /></option>
 		  		</select>
 		  	</td>
 		  	<td>
 		  	<span title="S.O.S" 
	                	onclick="event.stopPropagation();event.preventDefault();
	                			this.parentNode.parentNode.parentNode.querySelector('._sos_text_').style.display='block';">
	                   <i class="mx-help-icon far fa-question-circle" style="color:grey"></i>    
	          </span>
 		  	</td>
 		  </tr>
 		  <tr style="display:none;margin:1rem;" class="_sos_text_" >
 		  	<td><div style="border:1px dashed grey;overflow:auto;height:4rem;background:#eee;font-size:0.8rem;"><s:text name="Items.downloadItems.gexf.modeHelp" /></div> </td></tr>
 		  </table>
 		  
 		 <div id="gexf_contents_previsu_body_download" style="display:none">
 		 	<h5><s:text name="Items.downloadItems.gexf.nodesData"/></h5>
 		  	 <div class="_nodes_data_selection_insertspot_">	 		  	
		  	</div>
		  	<hr/>
		  	<h5><s:text name="Items.downloadItems.gexf.links"/></h5>
		  	<div class="_edges_selection_insertspot_">	 		  	
		  	</div> 		  			 		  		 
		  </div>
		  
		  <div id="gexfgroupby_contents_previsu_body_download" style="display:none">
 		 	<h5><s:text name="Items.downloadItems.gexf.groupby"/></h5>
 		  	 <select class="_groupTerm_selector_" >
 		  	 </select>	 		  	
		  	
		  	<hr/>
		  	<h5><s:text name="Items.downloadItems.gexf.link"/></h5>
		  	<select class="_edgeTerm_selector_">	 		  	
		  	</select> 		  			 		  		 
		  </div>
		  <div id="gexf_contents_previsu_download_footer" style="display:none">
 		  		 <label class="_downloadBtn_ d-none d-sm-inline-block btn-big btn btn-info shadow-sm mx-left-button"  >
 		  				<i class="fas fa-download fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.downloadItems.generateGexf"></s:text>
 		  		</label> 		  		
		  </div>
		  <div id="gexfgroupby_contents_previsu_download_footer" style="display:none"> 		  		 
 		  		<label class="_downloadGroupByBtn_ d-none d-sm-inline-block btn-big btn btn-info shadow-sm mx-left-button"  >
 		  				<i class="fas fa-download fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.downloadItems.generateGexf"> </s:text>
 		  		</label>
 		  		
 		  		
		  </div>
 		 