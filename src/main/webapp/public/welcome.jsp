<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<c:url value="/" var="mxurl"/>
<c:url value="/loginprocess" var="loginUrl"/>

<!DOCTYPE html>
<html lang="en">

<head>

  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="">
  <meta name="author" content="">

  
  <title>MetaindeX - Welcome</title>

  <!-- Custom fonts for this template-->
  <link href="${mxurl}public/commons/deps/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">

  <!-- Custom styles for this template-->
  <s:include value="/public/commons/style/style_global.jsp" />  							 

</head>

<body class="bg-gradient-primary" style="background-color:#fff;background-image:none;">

  <div class="container" style="width:100%;" >
    <!-- Outer Row -->
    <div class="row justify-content-center"  >

     	
        <div class="card o-hidden border-0  my-5">
          <div class="card-body p-0">
            <!-- Nested Row within Card Body -->
            <div class="row">
            	<div class="app-title" style="padding:0;margin:0">M<span class="app-title2">etainde</span>X<span 
            		style="text-shadow:1px 1px 1px;vertical-align: super; font-size:1.5rem;">&reg;</span>
            	</div>
            
            	<div class="app-title" 
	            	style="text-shadow:none;color:grey;font-size:0.8rem;padding:0;margin:1rem;" >
	            	<s:text name="globals.copyright"/>
            	</div>
            	    
            </div>
          </div>
        </div>
        						
				<div class="card-deck" >
				  <div class="card" >
				    <div class="card-body text-center">
				      <p class="card-text">Store Data</p>
				    </div>
				  </div>
				 
				
				  <div class="card">
				    <div class="card-body text-center">
				      <p class="card-text">Explore Data</p>
				    </div>
				  </div>
				  
				  <div class="card">
				    <div class="card-body text-center">
				      <p class="card-text">Compute Data</p>
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
