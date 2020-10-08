<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:url value="/" var="mxurl"/>
<c:url value="/metaindex/Items" var="mxCurPage"/>
<c:url value="/logoutprocess" var="logoutUrl"/>

<!DOCTYPE html>
<html lang="en">

<head>

  <title>MetaindeX</title>
  <s:include value="../commons/html/head-meta.jsp" />
  <s:include value="ws_handlers.jsp" />    
  <s:include value="../commons/html/head-onload.jsp" />
  
</head>


<body id="page-top" 
	
	onkeypress="event.stopPropagation();	
				if (event.originalTarget.parentNode.classList.contains('editable-input')) { return; }
				
				if (event.key=='e') { 
					if (MxGuiDetails.getCurItemCard()!=null) { MxGuiDetails.switchEditMode(); } 
				}
				else if (event.key=='p') { 
					if (MxGuiDetails.getCurItemCard()!=null) { MxGuiDetails.switchPerspective(); } 
				}
				else if (event.key=='s') {
					document.getElementById('details-wrapper').classList.toggle('toggled');
				}
				
				"
	onkeydown="event.stopPropagation();
				// ignore events comming from xeditable typing
				if (event.originalTarget.parentNode.classList.contains('editable-input')) { return; }
				
				if (event.key=='ArrowRight') { MxGuiCards.selectNext(); }
				else if (event.key=='ArrowLeft') { MxGuiCards.selectPrevious(); }
				//escape : closing any open modal
				if (event.which==27||event.keycode==27) {
	  				let modals = document.getElementById('leftbar.operations.insertspot').querySelectorAll('.modal');	  				
	  				for (var idx=0;idx<modals.length;idx++) { modals[idx].style.display='none';}
	  			}
				"
	>

  
  <!-- Page Wrapper -->
  <div id="wrapper">

	<s:include value="../commons/html/left.jsp" />
    <s:include value="left.jsp"></s:include>
	
    <!-- Content Wrapper -->
    <div id="content-wrapper" class="d-flex flex-column">

      <!-- Main Content -->
      <div id="content">

        <!-- header -->
        <s:include value="./header.jsp"></s:include>
        
        <!-- Cards Container -->
        
      <div id="details-wrapper">       
        <div id="cards-content-wrapper" class="container-fluid" >
          <s:include value="../commons/html/details-right.jsp"></s:include>
          <div class="row">
            <div id="files-dropzone" class="col-lg-12" ondrop="dropFile(event);">
           	  
	 		  <!-- current selection details -->
	 		  <s:include value="../commons/html/details.jsp"></s:include>
	          <s:include value="details.jsp"></s:include>
	          
	          <!-- Contents -->
	          <div id="MxGui.cards.insertspot" class="card-deck col-sm-2 " style="max-width:100%">          	
					<!-- CARDS ARE INSERTED HERE -->
	  		  </div>
			  <div id="empty-placeholder" style="color:#aaa;display:none;width:100%" ><center>
				  <h5 style="margin-top:2rem;margin-bottom:3rem;"><s:text name="Items.catalogIsEmpty"/></h5>
				  <div id="empty-placeholder-writable">
					  <h6 style="margin-bottom:1rem;"><s:text name="Items.dragAndDropFile" /></h6>
					  <img src="public/commons/media/img/excel.png" style="opacity:0.5;width:50%;margin-bottom:4rem;" />
				  </div>
			  </center></div>
	   		 <s:include value="../commons/html/footer.jsp"></s:include>
	   	   </div></div>
   		 
    	</div><!-- End of Cards Container -->
      </div>
      </div><!-- End of Main content -->
          
    </div><!-- End of Contents Wrapper -->
  
  </div><!-- End of Page Wrapper -->
  
  <!-- Scroll to Top Button-->
  <a class="scroll-to-top rounded" href="#page-top">
    <i class="fas fa-angle-up"></i>
  </a>
  
  <s:include value="../commons/html/foot-meta.jsp" />
  
  
  <div id="kibana_set_dashboard" style="display:none;margin-top:0.5rem;" >
  	
         	  
     <a class="btn btn-primary"  href="#" style="margin-right:1.5rem;margin-top:0.1rem;margin-bottom:0.1rem;"
  		onclick="document.getElementById('details-wrapper').classList.toggle('toggled');">
          <i class="fas fa-angle-double-right" style="font-size:1.4rem;" title="Close Panel"></i>
    </a>  
    <a class="btn btn-primary"  href="#" style="margin-top:0.1rem;margin-bottom:0.1rem;"
  		onclick="  		
  			var iframe = document.getElementById('kibana-iframe');
  			iframe.parentNode.replaceChild(iframe.cloneNode(), iframe);">
          <i class="fas fa-sync-alt" style="font-size:1.4rem;" title="Refresh"></i>
    </a>       	  
  	<a class="btn btn-primary"  href="#" style="margin-top:0.1rem;margin-bottom:0.1rem;"
  		onclick="document.getElementById('set_catalog_kibana_iframe_popup').style.display='block';">
          <i class="fas fa-code" style="font-size:1.4rem;" title="<s:text name="Items.setKibanaIFrame" />"></i>
    </a>
  </div>
  <div id="kibana_frame_cont" style="display:none" >
  	<!--
  		if Kibana is dead page might take some time to load.
  	 -->
  	 <s:property value="currentUserProfile.currentCatalogKibanaIFrameHtml" escapeHtml="false" />
  </div>
  
  <script type="text/javascript">
  // called (if defined) from commons/html/head-onload.jsp
  	function local_onload() {	  	
		enableKibanaFrame();
  	}
  </script>
  <script type="text/javascript">
//configure Kibana Frame
  function enableKibanaFrame() {
	  let kibanaFrameParent=document.getElementById("kibana_frame_cont");
	  let kibanaFrame=kibanaFrameParent.querySelector("iframe");
	  
	  if (kibanaFrame!=null) {
		  kibanaFrame.id="kibana-iframe"
		  kibanaFrame.width="";
		  kibanaFrame.height="";
		  kibanaFrame.style="margin-top:0.5rem;";
		  kibanaFrame.classList.add("kibana-iframe");  
		  MxGuiDetailsRightBar.addContents(kibanaFrame);
	  }
	  
	  let kibanaSetiFrame=document.getElementById("kibana_set_dashboard");
	  kibanaSetiFrame.style.display='block';
	  MxGuiDetailsRightBar.addContents(kibanaSetiFrame);
	  let iframePopup = MxGuiPopups.newTextInputPopup("<s:text name="Items.pastHereKibanaIFrame" />",
			  											"<s:text name="Items.setKibanaIFrame" />",
			  	function(iframeCode) {
		  		
		  			let requestObj = {
		  					userId:"<s:property value="currentUserProfile.id" />",
		  					catalogId:"<s:property value="currentCatalog.id" />",
		  					kibanaIFrame:iframeCode,
		  					successCallback:function() { 
		  						console.log("refreshed!"); 
		  						redirectToPage("${mxUrl}/metaindex/Items"); 
		  					},
		  					errorCallback:function(msg) { console.log("Unable to set iframe : "+msg); }
		  			}
		  			MxApi.requestSetUserCatalogCustomization(requestObj);
		  			
		  		});
	  iframePopup.id="set_catalog_kibana_iframe_popup";
	  MxGuiDetailsRightBar.addContents(iframePopup);
		  
	  if (kibanaFrame!=null) { MxGuiDetailsRightBar.addContents(kibanaFrame); }
	  else {
		  let noIframeMsg = document.createElement("div");
		  noIframeMsg.style="margin-top:2rem;padding:2rem;text-align:center;font-size:0.8rem"
		  noIframeMsg.innerHTML="<s:text name="Items.noKibanaIframe" />";
		  MxGuiDetailsRightBar.addContents(noIframeMsg);
	  }
}
  </script>
 
 
 
  <script type="text/javascript">
  MxGuiMain={}
  
  
  //configure files drop-zone
  MxGuiMain.enableFileDropzone=function() {
	  let fileDropZone=document.getElementById("files-dropzone");
	  	  
	  fileDropZone.ondragleave=function(e) {
		  e.preventDefault();
		  fileDropZone.classList.remove('dropzone-ondragover');
	  }
	  fileDropZone.ondragover=function(e) { 
  		  e.preventDefault(); 
		  fileDropZone.classList.add('dropzone-ondragover'); 
	  }
	  fileDropZone.ondragstart=function(e) { e.preventDefault(); }		  
	  fileDropZone.ondragenter=function(e) { e.preventDefault(); }
	  fileDropZone.ondragend=function(e) { e.preventDefault(); }
	  fileDropZone.ondrop=function(ev) {
		  ev.preventDefault();
		  ev.stopPropagation();
		  fileDropZone.classList.remove('dropzone-ondragover');
		  
		  // from https://developer.mozilla.org/en-US/docs/Web/API/HTML_Drag_and_Drop_API/File_drag_and_drop
		  if (ev.dataTransfer.items) {
			  if (ev.dataTransfer.items.length>1) {
				  alert("<s:text name="Items.uploadItems.onlyOneFileIsAllowed" />");
				  return;
			  }
			  if (ev.dataTransfer.items[0].kind !== 'file') {
				  alert("<s:text name="Items.uploadItems.notAFile" />");
				  return;
			  }
			  
		      let files = { files : [  ev.dataTransfer.items[0].getAsFile() ] }
		      MxGuiLeftBar.handleDataFileToUpload(files);
		      
		  } else {
			  if (ev.dataTransfer.files.length>1) {
				  alert("<s:text name="Items.uploadItems.onlyOneFileIsAllowed" />");
				  return;
			  }
			  MxGuiLeftBar.handleDataFileToUpload(ev.dataTransfer);			  	    
		  }
		  
		  
	  }
  }
  
  MxGuiMain.disableFileDropzone=function() {
	  let fileDropZone=document.getElementById("files-dropzone");
	  fileDropZone.classList.remove('dropzone-ondragover');
	  fileDropZone.ondragleave=function(e) { e.preventDefault(); }
	  fileDropZone.ondragover=function(e) { e.preventDefault(); }
	  fileDropZone.ondragstart=function(e) { e.preventDefault(); }		  
	  fileDropZone.ondragenter=function(e) { e.preventDefault(); }
	  fileDropZone.ondragend=function(e) { e.preventDefault(); }
	  fileDropZone.ondrop=function(ev) { e.preventDefault(); }
		  
  }
  
  
  MxGuiMain.showTextEmptyCatalog=function(isCatalogWritable) {
	  if (isCatalogWritable==null) { isCatalogWritable=false; }
	  document.getElementById('empty-placeholder').style.display="block";
	  if (isCatalogWritable) {
		  document.getElementById('empty-placeholder-writable').style.display="block";
	  } else {
		  document.getElementById('empty-placeholder-writable').style.display="none";
	  }
  }
            
  MxGuiMain.hideTextEmptyCatalog=function() {
	  document.getElementById('empty-placeholder').style.display="none";
  }
  </script>
</body>

</html>
