<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<c:url value="/" var="mxurl"/>
<c:url value="/signupprocess" var="signupUrl"/>

<!DOCTYPE html>
<html lang="en">

<head>

  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="">
  <meta name="author" content="">

  
  <title>MetaindeX - Sign-Up Confirm</title>
  <link rel="icon" type="image/svg" href="public/commons/media/img/favicon.png">
  <!-- Custom fonts for this template-->
  <link href="${mxurl}public/commons/deps/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">

  <!-- Custom styles for this template-->
  <s:include value="/public/commons/style/style_global.jsp" />  							 

</head>

<body class=""
onkeypress="if (event.which==13||event.keycode==13) {
		document.getElementById('loginform').submit();
	}"
	>


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
            		<div style="font-size:1rem;font-weight:normal;text-shadow:none;">
            			Thank you for your registration!<br/><br/>
            			An email has been sent to <b><s:property value='email'/></b>.<br/><br/>
            			You shall receive it shortly (check your spams if not) for confirmation instructions.</div>
            	</div>
            	 
            </div>
            
            <div class="row">
              
              <div class="col-lg-12">
                <div class="p-2">
		           	<c:if test="${mxStatus == 'MAINTENANCE'}">
		           		<div class="maintenance"><p><s:text name="global.maintenance" /></p></div>
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

</body>

</html>
