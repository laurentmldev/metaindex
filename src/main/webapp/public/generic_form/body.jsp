<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  

<c:url value="/loginprocess" var="loginUrl"/>

<c:if test="${mxDevMode == true}" >
  	<nav class="navbar navbar-expand topbar static-top"
  			style="background:orange;height:2rem;color:white;font-weight:bold;">
  	Dev-Mode Active
  	</nav>
  </c:if>
  

  <div class="container" style="width:${param.width};">
	
	
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
           		
           	<%--mxAppStatus: STOPPED, RUNNING, FAILURE--%>
           	<c:if test="${mxAppStatus == 'RUNNING'}">
           		
                 <div id="contentsInsertSpot" ></div>
                 
                 </c:if>
                </div>
                
             
              </div>
            </div>
        
        	<div class="row" >
        			<div class="app-title mx-copyright" 
            	style="" >            	
            	<span><b><s:text name="globals.copyright"/></b> - MetaindeX <s:text name="mxRunMode"/> v<s:property value="mxVersion"/><br/><s:property value="mxFooterInfo"/></span>
            	</div>
            </div>
          </div>
        </div>

      </div>

    </div>

  </div>

  <!-- Bootstrap core JavaScript-->
  <script src="${webAppBaseUrl}/public/commons/deps/jquery/jquery.min.js"></script>
  <script src="${webAppBaseUrl}/public/commons/deps/bootstrap/js/bootstrap.bundle.min.js"></script>

  <!-- Core plugin JavaScript-->
  <script src="${webAppBaseUrl}/public/commons/deps/jquery-easing/jquery.easing.min.js"></script>

  <!-- Custom scripts for all pages-->
  <script src="${webAppBaseUrl}/public/commons/js/sb-admin-2.min.js"></script>

