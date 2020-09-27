<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<c:url value="/" var="mxurl"/>
<c:url value="/loginprocess" var="loginUrl"/>

<!DOCTYPE html>
<html lang="en">

<s:include value="generic_form/head.jsp" />

<body class=""
onkeypress="if (event.which==13||event.keycode==13) {
		document.getElementById('form').submit();
	}"
	onload="createForm();"
	>
	
<s:include value="generic_form/body.jsp" />
 
 <div id="contents" style="display:none" >
 	<center><h3><s:text name="signin.enterCredentials" /></h3></center>
  <form id="form" class="user" action="${loginUrl}" method="post" >
          <input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
 	<div class="form-group">
		<c:if test="${param.error != null}"> <p><s:text name="session.loginerror" /></p></c:if>
		<c:if test="${param.logout != null}"><p><s:text name="session.logoutmessage" /></p></c:if>
		<c:if test="${param.expired != null}"><p><s:text name="session.expiredmessage" /></p></c:if>
		<c:if test="${param.passwordreset != null}"><p><s:text name="session.passwordreset" /></p></c:if>
	</div>

        <div class="form-group">
	        <input id="email" type="email" name='username' class="form-control form-control-user" aria-describedby="emailHelp" placeholder="Enter Email Address...">
         </div>
        <div class="form-group">
            <input type="password" name='password' class="form-control form-control-user" placeholder="<s:text name="session.password" />">
        </div>
       
        <center>
        <a href="#" class="btn btn-primary btn-user btn-block scale" 
        		style="max-width:30%;" onclick="document.getElementById('form').submit();">
          <s:text name="signin.signin" />
        </a>
       <hr/>
        <a href="#" class="btn  btn-user btn-block scale" style="max-width:30%;font-size:0.8rem;background:#afa;padding:0.2rem" 
        	onclick="if (document.getElementById('email').value=='') { 
        				alert('<s:text name="passwordreset.pleaseFillEmailInTheForm" />');
        			 	document.getElementById('email').focus();
        			 } else {
        			 	window.location.href='resetpwdprocess?email='+document.getElementById('email').value;
        			 }
        			 ">
          <s:text name="passwordreset.emailsent.title" />
        </a>
        <hr/>
        <a href="#" class="btn  btn-user btn-block scale" style="max-width:30%;font-size:0.8rem;background:#aaf;padding:0.2rem" 
        			onclick="window.location.href='signup';">
          <s:text name="signup.createAccount" />
        </a>

        </center>
        
      </form>
 </div>
 
 <script type="text/javascript">
	function createForm() {
		let formInsertSpot=document.getElementById('contentsInsertSpot');
		let formContents=document.getElementById('contents');
		formInsertSpot.append(formContents);
		formContents.style.display='block';
	}
 </script>
</body>

</html>

