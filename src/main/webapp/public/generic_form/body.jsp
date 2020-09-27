<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<c:url value="/" var="mxurl"/>
<c:url value="/loginprocess" var="loginUrl"/>



<c:if test="${mxDevMode == true}" >
  	<nav class="navbar navbar-expand topbar static-top"
  			style="background:orange;height:2rem;color:white;font-weight:bold;">
  	Dev-Mode Active
  	</nav>
  </c:if>
  

  <div class="container p-5" style="width:50%;">
	
	
    <!-- Outer Row -->
    <div class="row justify-content-center"  >

      <div class="col-xl-10 col-lg-12 col-md-9">		
        <div class="card o-hidden border-0 shadow-lg my-5">
          <div class="card-body p-0">
            <!-- Nested Row within Card Body -->
            <div class="row" style="display:block;">            	
            	<div class="app-title" style="font-size:6vw;padding:0;margin:0;width:auto;">
			 		<span class="scale-color-white">M</span><span class="app-title2 scale-color-white" style="color:white;">etainde</span><span class="scale-color-white">X</span>            		            			
            	</div>
            	 
            </div>
            
            <div class="row">
              
              <div class="col-lg-12">
                <div class="p-2">
           	<c:if test="${mxStatus == 'MAINTENANCE'}">
           		<div class="maintenance"><p><s:text name="global.maintenance" /></p></div>
           	</c:if>
	
           	<c:if test="${mxStatus == 'ACTIVE'}">
           	
                 <div id="contentsInsertSpot" ></div>
                 
                 </c:if>
                </div>
                
             
              </div>
            </div>
        
        	<div class="row" >
        			<div style="width:100%;text-align:center">
        			  <style type="text/css">a.eussl_seal:hover .eussl_bubble { visibility:visible; } a.eussl_seal .eussl_bubble { visibility:hidden; width:145.5px; height:200px; margin-left:3.75px; border:1px solid #6c81b3; border-radius:0 0 3px 3px; overflow:hidden; border-top:0; }</style>	 		 	
					  <a class="eussl_seal eussltip" href="https://secure.europeanssl.eu" target="_blank" >
					    <img src="https://secure.europeanssl.eu/seal/metaindex.fr/150" alt="Secured by EuropeanSSL.eu" />					    	         			
					  </a>
				    </div>	
            	<div class="app-title mx-copyright" 
            	style="" >
            	<s:text name="globals.copyright"/> - MetaindeX v<s:property value="mxVersion"/></div>
            </div>
          </div>
        </div>

      </div>

    </div>

  </div>

  <!-- Bootstrap core JavaScript-->
  <script src="${mxurl}public/commons/deps/jquery/jquery.min.js"></script>
  <script src="${mxurl}public/commons/deps/bootstrap/js/bootstrap.bundle.min.js"></script>

  <!-- Core plugin JavaScript-->
  <script src="${mxurl}public/commons/deps/jquery-easing/jquery.easing.min.js"></script>

  <!-- Custom scripts for all pages-->
  <script src="${mxurl}public/commons/js/sb-admin-2.min.js"></script>

