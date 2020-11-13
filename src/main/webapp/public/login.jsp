<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  

<c:url value="/loginprocess" var="loginUrl"/>

<!DOCTYPE html>
<html lang="en">

<s:include value="generic_form/head.jsp" />

<body  class="mx-public-form"
onkeypress="if (event.which==13||event.keycode==13) {
		document.getElementById('form').submit();
	}"
	onload="createForm();"
	>
<s:include value="/public/commons/js/helpers.jsp" />
<s:include value="generic_form/body.jsp" >
	<s:param name="width">50%</s:param>
</s:include>
 
 <div id="contents" style="display:none" >
 	
 	
  <form id="form" class="user" action="${loginUrl}" method="post" >
          <input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
 	<div class="form-group">
		<c:if test="${param.error != null}"> <p class="alert-danger p-2" ><s:text name="session.loginerror" /></p></c:if>
		<c:if test="${param.badcredentials != null}"> <p class="alert-danger p-2" ><s:text name="session.badcredentials" /></p></c:if>
		<c:if test="${param.logout != null}"><p class="alert-info p-2" ><s:text name="session.logoutmessage" /></p></c:if>
		<c:if test="${param.expired != null}"><p class="alert-warning p-2"><s:text name="session.expiredmessage" /></p></c:if>
		<c:if test="${param.passwordreset != null}"><p class="alert-success p-2" ><s:text name="session.passwordreset" /></p></c:if>
	</div>

<center>
		<h5><s:text name="signin.enterCredentials" /></h5>
        <div class="form-group">
	        <input id="email" type="email" name='username' class="form-control form-control-user mx_welcome_input" 
	        	aria-describedby="emailHelp" placeholder="<s:text name="Profile.email" />">
         </div>
        <div class="form-group">
            <input type="password" name='password' style="background:#f5f5f5" class="form-control form-control-user  mx_welcome_input" 
            	placeholder="<s:text name="session.password" />">
        </div>
       
        
        <a href="#" class="btn btn-primary btn-user btn-block scale" 
        		style="max-width:50%;background:#57a;border:none;color:white;padding:0.8rem" 
        		 onclick="document.getElementById('form').submit();">
          <s:text name="signin.signin" />
        </a>
       <hr/>
      <table style="width:100%"><tr>
      <td style="width:50%;"><center>
        <a href="#" class="btn  btn-user btn-block scale" style="max-width:70%;font-size:0.8rem;background:#ec9;color:white;padding:0.2rem" 
        	onclick="if (document.getElementById('email').value=='') { 
        				alert('<s:text name="passwordreset.pleaseFillEmailInTheForm" />');
        			 	document.getElementById('email').focus();
        			 } else {
        			 	window.location.href='resetpwdprocess?email='+document.getElementById('email').value;
        			 }
        			 ">
          <s:text name="passwordreset.emailsent.title" />
        </a>
      </center></td style="width:50%">
      <td><center>
        <a href="#" class="btn  btn-user btn-block scale" style="max-width:70%;font-size:0.8rem;background:#8d8;color:white;padding:0.2rem" 
        			onclick="window.location.href='signup?origin=loginform';">
          <s:text name="signup.createAccount" />
        </a>
      </center></td>  
     </tr></table>
      <table><tr>
      		<td><a onclick="window.location.href=URL_add_parameter(location.href, 'language', 'en');"><img src="${webAppBaseUrl}/public/commons/media/img/flags/UK.png" class="mx-lang-flag scale" /></a></td>
      		<td><a onclick="window.location.href=URL_add_parameter(location.href, 'language', 'fr');"><img src="${webAppBaseUrl}/public/commons/media/img/flags/France.png" class="mx-lang-flag scale"/></a></td>
  		</tr></table>
		<hr/>
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

