<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  

<c:url value="/signupprocess" var="signupUrl"/>

<!DOCTYPE html>
<html lang="en">

<s:include value="/public/commons/js/helpers.jsp" />
<s:include value="generic_form/head.jsp" />

<body  class="mx-public-form"
onkeypress="if (event.which==13||event.keycode==13) {
	checkAndSendForm();
	}"
	onload="createForm();"
	>
	
	<form id="form" class="user" action="${signupUrl}" method="post" >
	<table style="width:100%;"><tr>
 	
 	<td style="width:50%">
 	
 	<div id="termsBody" 
               		style=" margin-left:1rem;
               				padding-left:1rem;
               				margin-top:6rem;
               				font-size:1rem;
               				display:block;
               				height:90vh;
               				overflow:auto;padding-bottom:3rem;text-align:left;background:#f5f5f5" >
               		<s:text name="signup.termsAndCondition.body" />	   
               		
 					<table style="margin-left:20%;margin-bottom:1rem;" ><tr>
                  		<td><input id="termsCheckbox" type="checkbox" name='acceptTermsAndConditions' class="form-control form-control-user" /></td>
                  		<td style="padding-left:0.5rem;">
                  			<span style="font-weight:bold;font-size:1rem;"><s:text name="signup.accept" /> <s:text name="signup.termsAndCondition" /></span>                  			
                  		</td>
	  					</tr></table>                 	
               	</div>
               	
 	</td>
 	<td style="width:50%;">
<s:include value="generic_form/body.jsp"  >
	<s:param name="width">100%</s:param>
</s:include>
 
 	
 <div id="contents" style="display:none" >
 	<center>
 		<h3><s:text name="signup.createYourAccount" /></h3>
 		<div style="font-size:0.8rem;text-align:left;"><s:text name="signup.freeAccountLimitations" /></div>
 	</center>
 	
	
          <input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="form-group">
              	<c:if test="${param.emailalreadyused != null}"> <p class="alert-danger"><s:text name="signup.emailNotAvailable" /></p></c:if>
              	<c:if test="${param.newemailalreadywaiting != null}"> <p class="alert-warning"><s:text name="signup.emailAlreadyAwaiting" />
              			</p>
              	</c:if>	                  	              	
	
			</div>

               <div class="form-group">
               <center>
                 <input type="email" id="email" name='email' class="form-control form-control-user mx_welcome_input" 
                 	aria-describedby="emailHelp" placeholder="<s:text name="Profile.email" />" required>
                 	</center>
               </div>
               <div class="form-group"><center>
                 <input type="text" id="nickname" name='nickname' class="form-control form-control-user mx_welcome_input" 
                  	 placeholder="<s:text name="Profile.nickname" />" required>
               </center>
               </div>
               
               <div class="form-group"><center>
               
               <div style="margin-bottom:0.5rem"><s:text name="Profile.category" /></div>
               	<select id="category" name="category" class="form-control  mx_welcome_input" style="font-size:0.8rem;" >
               	<!-- values shall match corresponding enum defined in SQL DB -->
				  <option value="PERSONAL" ><s:text name="Profile.category.PERSONAL" /></option>
				  <option value="STUDENT_SEARCHER" selected><s:text name="Profile.category.STUDENT_SEARCHER" /></option>
				  <option value="NONPROFIT" ><s:text name="Profile.category.NONPROFIT" /></option>
				  <option value="ADMINISTRATION" ><s:text name="Profile.category.ADMINISTRATION" /></option>
				  <option value="BUSINESS" ><s:text name="Profile.category.BUSINESS" /></option>				  
				</select>				
               </center>
               </div>
               
               <div class="form-group" style="width:100%;">
               <center>
               	
               	
               	
               	 
               	
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
		  	onclick="checkAndSendForm();">
	                   <s:text name="signup.create" />
	                 </a>
	     </center> </td>
       </tr>
       <tr><td colspan=2><center>
       	 <table><tr>
      		<td><a onclick="window.location.href=URL_add_parameter(location.href, 'language', 'en');"><img src="${webAppBaseUrl}/public/commons/media/img/flags/UK.png" class="mx-lang-flag scale" /></a></td>
      		<td><a onclick="window.location.href=URL_add_parameter(location.href, 'language', 'fr');"><img src="${webAppBaseUrl}/public/commons/media/img/flags/France.png" class="mx-lang-flag scale"/></a></td>
  		</tr></table></center></td>
       </tr>
       </table>
	                 </center>
	               </div>
	   
       
       
 </div>
 
 
       </td></tr>
       </table>
       </form>
 <script type="text/javascript">
 
 function checkAndSendForm() {
	 if (document.getElementById('email').value.length==0) { alert('<s:text name="signup.pleaseGiveEmail"/>'); document.getElementById('email').focus(); }
	 else if (document.getElementById('nickname').value.length==0) { alert('<s:text name="signup.pleaseGiveNickname"/>'); document.getElementById('nickname').focus(); }
     else if (document.getElementById('termsCheckbox').checked==false) { alert('<s:text name="signup.pleaseAcceptTerms"/>');document.getElementById('termsCheckbox').focus(); }
     else { document.getElementById('form').submit(); }
 }
 function createForm() {
		let formInsertSpot=document.getElementById('contentsInsertSpot');
		let formContents=document.getElementById('contents');
		formInsertSpot.append(formContents);
		formContents.style.display='block';
	}
 </script>
</body>

</html>

