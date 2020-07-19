<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<c:url value="/" var="mxurl"/>
<c:url value="/resetpassword" var="passwordUrl"/>

<!DOCTYPE html>
<html lang="en">

<head>

  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="">
  <meta name="author" content="">

  
  <title>MetaindeX - Finalize your Account</title>
  <link rel="icon" type="image/svg" href="public/commons/media/img/favicon.png">
  <!-- Custom fonts for this template-->
  <link href="${mxurl}public/commons/deps/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">

  <!-- Custom styles for this template-->
  <s:include value="/public/commons/style/style_global.jsp" />  							 

	<script type="text/javascript">
	
	  // polyfill for RegExp.escape from https://github.com/benjamingr/RegExp.escape
	  if(!RegExp.escape) {
	    RegExp.escape = function(s) {
	      return String(s).replace(/[\\^$*+?.()|[\]{}]/g, '\\$&');
	    };
	  }
	
	  document.addEventListener("DOMContentLoaded", function() {

	    // JavaScript form validation

	    var checkPassword = function(str)
	    {
	      var re = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{6,}$/;
	      return re.test(str);
	    };

	    var checkForm = function(e)
	    {
	      
	      if(this.pwd1.value != "" && this.pwd1.value == this.pwd2.value) {
	        if(!checkPassword(this.pwd1.value)) {
	          alert("The password you have entered is not valid!");
	          this.pwd1.focus();
	          e.preventDefault();
	          return;
	        }
	      } else {
	        alert("Error: Please check that you've entered and confirmed your password!");
	        this.pwd1.focus();
	        e.preventDefault();
	        return;
	      }
	      alert("Both username and password are VALID!");
	    };

	    var myForm = document.getElementById("passwordform");
	    myForm.addEventListener("submit", checkForm, true);

	    // HTML5 form validation

	    var supports_input_validity = function()
	    {
	      var i = document.createElement("input");
	      return "setCustomValidity" in i;
	    }

	    if(supports_input_validity()) {

	      var pwd1Input = document.getElementById("field_pwd1");
	      pwd1Input.setCustomValidity(pwd1Input.title);

	      var pwd2Input = document.getElementById("field_pwd2");

	      // input key handlers
	      pwd1Input.addEventListener("keyup", function(e) {
	        this.setCustomValidity(this.validity.patternMismatch ? pwd1Input.title : "");
	        if(this.checkValidity()) {
	          pwd2Input.pattern = RegExp.escape(this.value);
	          pwd2Input.setCustomValidity(pwd2Input.title);
	        } else {
	          pwd2Input.pattern = this.pattern;
	          pwd2Input.setCustomValidity("");
	        }
	      }, false);

	      pwd2Input.addEventListener("keyup", function(e) {
	        this.setCustomValidity(this.validity.patternMismatch ? pwd2Input.title : "");
	      }, false);

	    }

	  }, false);
	  
	</script>
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
            		<div style="font-size:1rem;text-shadow:none;">Long is the road, but we can see the light now ...</div>
            	</div>
            	 
            </div>
            
            <div class="row">
              
              <div class="col-lg-12">
                <div class="p-2">
           	<c:if test="${mxStatus == 'MAINTENANCE'}">
           		<div class="maintenance"><p><s:text name="global.maintenance" /></p></div>
           	</c:if>
	
           	<c:if test="${mxStatus == 'ACTIVE'}">
           	
                  <form id="passwordform" class="user" action="${passwordUrl}" method="post" >
                  	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
                  	<input type="hidden"  name="requestId" value="${requestId}"/>
                  	<input type="hidden"  name="email" value="${email}"/>
                  	
                  	<div class="form-group">
	                  	<c:if test="${param.error != null}"> <p><s:text name="session.loginerror" /></p></c:if>
						<c:if test="${param.logout != null}"><p><s:text name="session.logoutmessage" /></p></c:if>
						<c:if test="${param.expired != null}"><p><s:text name="session.expiredmessage" /></p></c:if>
					</div>
					
                    Okay ${nickname}, please enter your new password:<br/><br/>  
                    
                    <div class="form-group">
                      <input id="field_pwd1" type="password" name='clearPassword' class="form-control form-control-user" 
                      		placeholder="<s:text name="session.password" />"
                      		 title="Password must contain at least 6 characters, including UPPER/lowercase and numbers."
                      		 required pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{6,}" 
                      		 >
                    </div>
                    <div class="form-group">
                      <input id="field_pwd2" type="password" name='password_confirm' class="form-control form-control-user" 
                      		placeholder="<s:text name="session.password.confirm" />"
                      		title="Please enter the same Password as above." 
                      		required pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{6,}"
                      		>
                    </div>
                    
                    <center>
                    <a href="#" class="btn btn-primary btn-user btn-block scale" style="max-width:30%;" onclick="document.getElementById('passwordform').submit();">
                      Go!
                    </a>
                    </center>
                    <hr>                 
                  </form>

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
