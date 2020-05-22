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
          <div class="row"><div class="col-lg-12" >
           	  
	 		  <!-- current selection details -->
	 		  <s:include value="../commons/html/details.jsp"></s:include>
	          <s:include value="details.jsp"></s:include>
	          
	          <!-- Contents -->
	          <div id="MxGui.cards.insertspot" class="card-deck col-sm-2 " style="max-width:100%">          	
					<!-- CARDS ARE INSERTED HERE -->
	  		  </div>
	
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
          <i class="fas fa-code" style="font-size:1.4rem;" title="Set Kibana iFrame"></i>
    </a>
  </div>
  <div id="kibana_frame_cont" style="display:none" >
  	<!--
  		if Kibana is dead page might take some time to load.
  	 -->
  	 <s:property value="currentUserProfile.currentCatalogKibanaIFrameHtml" escapeHtml="false" />
  </div>
  <script>
 
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
  let iframePopup = MxGuiPopups.newTextInputPopup("Past Here Kibana Embedded iFrame Code","Set Kibana IFrame",
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
  
  </script>
 
</body>

</html>
