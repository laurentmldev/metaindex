<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:url value="/" var="mxurl"/>
<c:url value="/metaindex/Catalogs" var="mxCurPage"/>

<!DOCTYPE html>
<html lang="en">

<head>
  <title>MetaindeX</title>  
  <s:include value="../commons/html/head-meta.jsp" />
  <s:include value="ws_handlers.jsp" />    
  <s:include value="../commons/html/head-onload.jsp" />
</head>

<body id="page-top" onload="websocketsStartup();">

  <!-- Page Wrapper -->
  <div id="wrapper">

	<s:include value="../commons/html/left.jsp" />
    <s:include value="left.jsp"></s:include>
	
    <!-- Content Wrapper -->
    <div id="content-wrapper" class="d-flex flex-column">

      <!-- Main Content -->
      <div id="content">

        <!-- header -->
        <s:include value="../commons/html/header.jsp"></s:include>
        
        <!-- Cards Container -->
        <div class="container">
          
          <s:include value="../commons/html/details.jsp"></s:include>
          <s:include value="details.jsp"></s:include>
       
          <!-- Contents -->
          <div id="MxGui.cards.insertspot" class="card-deck col-sm-2 " style="max-width:100%">          	
				<!-- CARDS ARE INSERTED HERE -->
  		  </div><!-- End of cards insert spot -->
		
		  <div id="empty-placeholder" style="color:#aaa;width:100%" ><center>
			  <h5 style="margin-top:2rem;margin-bottom:3rem;"><s:text name="Catalogs.empty"/></h5>			  
			  <h6 style="margin-bottom:1rem;">&larr; <s:text name="Catalogs.emptyHowTo" /></h6>			  
			  <img src="public/commons/media/img/mx-title.png" style="opacity:0.5;width:50%;margin-bottom:4rem;" />		  
		  </center></div>
   		 <s:include value="../commons/html/footer.jsp"></s:include>
   		 
    	</div><!-- End of Cards Container -->
    	
      </div><!-- End of Main content -->
          
    </div><!-- End of Contents Wrapper -->
  
  </div><!-- End of Page Wrapper -->
  
  <!-- Scroll to Top Button-->
  <a class="scroll-to-top rounded" href="#page-top">
    <i class="fas fa-angle-up"></i>
  </a>
  
  <s:include value="../commons/html/foot-meta.jsp" />
  <script type="text/javascript">

  MxGuiMain={}
  MxGuiMain.showTextEmptyCatalog=function() {
	  document.getElementById('empty-placeholder').style.display="block";	  
  }
            
  MxGuiMain.hideTextEmptyCatalog=function() {
	  document.getElementById('empty-placeholder').style.display="none";
  }
  
	</script>
</body>

</html>
