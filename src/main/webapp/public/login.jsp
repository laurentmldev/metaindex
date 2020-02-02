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

  
  <title>MetaindeX - Login</title>

  <!-- Custom fonts for this template-->
  <link href="${mxurl}public/commons/deps/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">

  <!-- Custom styles for this template-->
  <s:include value="/public/commons/style/style_global.jsp" />  							 

</head>

<body class="bg-gradient-primary"
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
            <div class="row">
            	<div class="app-title" >M<span class="app-title2">etainde</span>X<span 
            	style="text-shadow:1px 1px 1px;vertical-align: super; font-size:1.5rem;">&reg;</span></div>
            </div>
            
            <div class="row">
              
              <div class="col-lg-12">
                <div class="p-2">
           	<c:if test="${mxStatus == 'MAINTENANCE'}">
           		<div class="maintenance"><p><s:text name="global.maintenance" /></p></div>
           	</c:if>
	
           	<c:if test="${mxStatus == 'OK'}">
           	
                  <form id="loginform" class="user" action="${loginUrl}" method="post" >
                  	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
                  	
                  	<div class="form-group">
	                  	<c:if test="${param.error != null}"> <p><s:text name="session.loginerror" /></p></c:if>
						<c:if test="${param.logout != null}"><p><s:text name="session.logoutmessage" /></p></c:if>
						<c:if test="${param.expired != null}"><p><s:text name="session.expiredmessage" /></p></c:if>
					</div>
					
                    <div class="form-group">
                      <input type="email" name='username' class="form-control form-control-user" aria-describedby="emailHelp" placeholder="Enter Email Address...">
                    </div>
                    <div class="form-group">
                      <input type="password" name='password' class="form-control form-control-user" placeholder="<s:text name="session.password" />">
                    </div>
                    <!--div class="form-group">
                      <div class="custom-control custom-checkbox small">
                        <input type="checkbox" class="custom-control-input" id="customCheck">
                        <label class="custom-control-label" for="customCheck">Remember Me</label>
                      </div>
                    </div-->
                    <a href="#" class="btn btn-primary btn-user btn-block" onclick="document.getElementById('loginform').submit();">
                      Login
                    </a>
                    <hr>                 
                  </form>
                  <!--
                  <div class="text-center">
                    <a class="small" href="forgot-password.html">Forgot Password?</a>
                  </div>
                  <div class="text-center">
                    <a class="small" href="register.html">Create an Account!</a>
                  </div>
                  -->
                 </c:if>
                </div>
                
             
              </div>
            </div>
        
        	<div class="row">
            	<div class="app-title" 
            	style="text-shadow:none;color:grey;font-size:0.8rem;padding:0;margin:2rem;" >
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
