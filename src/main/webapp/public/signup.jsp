<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<c:url value="/" var="mxurl"/>
<c:url value="/signupprocess" var="signupUrl"/>

<!DOCTYPE html>
<html lang="en">

<s:include value="generic_form/head.jsp" />

<body  class="mx-public-form"
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
               <center>
                 <input type="email" name='email' class="form-control form-control-user mx_welcome_input" 
                 	aria-describedby="emailHelp" placeholder="<s:text name="Profile.email" />" required>
                 	</center>
               </div>
               <div class="form-group"><center>
                 <input type="text" name='nickname' class="form-control form-control-user mx_welcome_input" 
                  	 placeholder="<s:text name="Profile.nickname" />" required>
               </center></div>
               
               <div class="form-group" style="width:100%;">
               <center>
               	
               	<div id="termsBody" 
               		style="padding-left:0.2rem;padding-right:0.2rem;padding-bottom:1rem;margin-bottom:1rem;border-bottom:1px dotted grey;border-top:1px dotted grey;font-size:0.8rem;display:block;max-height:15vh;overflow:auto;padding-bottom:3rem;text-align:left;background:#f5f5f5" >
               		<s:text name="signup.termsAndCondition.body" />	   
               		
 					<table style="margin-left:20%;margin-bottom:1rem;" ><tr>
                  		<td><input id="termsCheckbox" type="checkbox" name='acceptTermsAndConditions' class="form-control form-control-user" /></td>
                  		<td style="padding-left:0.5rem;">
                  			<span style="font-weight:bold;font-size:1rem;"><s:text name="signup.accept" /> <s:text name="signup.termsAndCondition" /></span>                  			
                  		</td>
	  					</tr></table>                 	
               	</div>
               	
               	 
               	
	  <table style="width:100%"><tr>
	  <td style="width:50%"><center>           
	       <a href="#" class="btn btn-primary btn-user btn-block scale" 
	        		style="max-width:40%;height:3rem;background:#999;border:none;padding:0;padding:0;padding-top:0.8rem;" 
		  	onclick="window.location.href='${param.origin}'">
	                   <s:text name="globals.goback" />
	                 </a>
      </center> </td>
	  <td style="width:50%"><center>
		  <a href="#" class="btn btn-primary btn-user btn-block scale" style="height:3rem;max-width:70%;padding:0;padding-top:0.8rem;background:#6c6;color:white;border:none" 
		  	onclick="if (document.getElementById('termsCheckbox').checked==false) { alert('<s:text name="signup.pleaseAcceptTerms"/>'); }
		  	         else { document.getElementById('form').submit(); }">
	                   <s:text name="signup.create" />
	                 </a>
	     </center> </td>
       </tr></table>
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

