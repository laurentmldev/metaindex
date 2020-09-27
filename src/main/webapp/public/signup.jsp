<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<c:url value="/" var="mxurl"/>
<c:url value="/signupprocess" var="signupUrl"/>

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
 	<center><h3><s:text name="signup.createYourAccount" /></h3></center>
	<form id="form" class="user" action="${signupUrl}" method="post" >
          <input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
             	<div class="form-group">
              	<c:if test="${param.emailalreadyused != null}"> <p>This email is already used, please try another one.</p></c:if>
              	<c:if test="${param.newemailalreadywaiting != null}"> <p>This email is already waiting confirmation. 
              			Please check your 'spams' mail folder if you did not received yet the confirmation instructions.
              			</p>
              	</c:if>	                  	
              	<c:if test="${param.error != null}"> <p>Woops sorry something wrong occured on server side, please retry in a few moments.</p></c:if>
	
</div>

               <div class="form-group">
                 <input type="email" name='email' class="form-control form-control-user" aria-describedby="emailHelp" placeholder="Email address" required>
               </div>
               <div class="form-group">
                 <input type="text" name='nickname' class="form-control form-control-user" placeholder="Nickname" required>
               </div>
               <div class="form-group" style="width:100%;">
               <center>
               	
               	<div id="termsBody" style="font-size:0.8rem;display:none;max-height:15vh;overflow:auto;padding-bottom:3rem;text-align:left;" >
               		<s:text name="signup.termsAndCondition.body" />	                    	
               	</div>
               	
               	 
               	 <table ><tr>
                  		<td><input id="termsCheckbox" type="checkbox" name='acceptTermsAndConditions' class="form-control form-control-user" /></td>
                  		<td style="padding-left:0.5rem;">
                  			<s:text name="signup.accept" />
                  			<a href="#" style="color:#57e" onclick="document.getElementById('termsBody').style.display='block';">
                  				<s:text name="signup.termsAndCondition" />
                  				</a>
                  		</td>
	  </tr></table>
	  
	  <a href="#" class="btn btn-primary btn-user btn-block scale" style="max-width:30%;" 
	  	onclick="if (document.getElementById('termsCheckbox').checked==false) { alert('<s:text name="signup.pleaseAcceptTerms"/>'); }
	  	         else { document.getElementById('form').submit(); }">
                   <s:text name="signup.create" />
                 </a>
                 
                 </center>
               </div>
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

