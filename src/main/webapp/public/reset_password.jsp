<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  

<c:url value="/resetpassword" var="passwordUrl"/>

<!DOCTYPE html>
<html lang="en">
<s:include value="generic_form/head.jsp" />


<body  class="mx-public-form"
onkeypress="if (event.which==13||event.keycode==13) {
		document.getElementById('form').submit();
	}"
	onload="createForm();document.getElementById('pwd1').focus();"
	>
	

	<script type="text/javascript">
	
	function checkPassword(str) {
		 var re = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{6,}$/;
	      return re.test(str);
	}
	function checkForm(showMessage)
    {
	  if (showMessage==null) { showMessage=false; }
      let pwd1=document.getElementById("pwd1");
      let pwd2=document.getElementById("pwd2");
		
      if(!checkPassword(pwd1.value)) {
          pwd1.focus();
          pwd1.classList.remove("form-input-success");
          pwd2.classList.remove("form-input-success");
          pwd2.classList.remove("form-input-error");
          pwd1.classList.add("form-input-error");
          if (showMessage) {
        	  alert("<s:text name="passwordreset.notcomplexenough" />\n<s:text name="passwordreset.complexity" />");
          }
          return false;
       } else {
    	  pwd1.classList.remove("form-input-error");
    	  pwd1.classList.add("form-input-success");
       }
      
      if(pwd2.value!=pwd1.value) {
    	  pwd2.classList.remove("form-input-success");
          pwd2.classList.add("form-input-error");
          if (showMessage) {
        	  alert("<s:text name="passwordreset.notmatching" />");
          }
          return false;
       } else {
    	  pwd2.classList.remove("form-input-error");
    	  pwd2.classList.add("form-input-success");
       }
      
      return true;
      
    };
    
	    
	  
	</script>
	
<s:include value="generic_form/body.jsp" />
 
 <div id="contents" style="display:none" >
	<center>
		<h3><s:text name="passwordreset.text" /></h3>
		<h6><s:text name="passwordreset.complexity" /></h6>		
	</center>
                  <form id="form" class="user" action="${passwordUrl}" method="post" >
                  	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
                  	<input type="hidden"  name="requestId" value="${requestId}"/>
                  	<input type="hidden"  name="email" value="${email}"/>
                  	
                  	<div class="form-group">
	                  	<c:if test="${param.error != null}"> <p><s:text name="session.loginerror" /></p></c:if>
						<c:if test="${param.logout != null}"><p><s:text name="session.logoutmessage" /></p></c:if>
						<c:if test="${param.expired != null}"><p><s:text name="session.expiredmessage" /></p></c:if>
					</div>
					
                    
                    <div class="form-group"><center>
                      <input id="pwd1" type="password" name='clearPassword' class="form-control form-control-user mx_welcome_input" 
                      		placeholder="<s:text name="session.password" />"
                      		 title="<s:text name="passwordreset.complexity" />"
                      		 onkeyup="checkForm();"
                      		 >
                    </center></div>
                    <div class="form-group"><center>
                      <input id="pwd2" type="password" name='password_confirm' class="form-control form-control-user mx_welcome_input" 
                      		placeholder="<s:text name="session.password.confirm" />"
                      		title="<s:text name="passwordreset.matching" />" 
                      		onkeyup="checkForm();"
                      		>
                    </center></div>
                    
                    <center>
                    <a href="#" class="btn btn-primary btn-user btn-block scale" style="max-width:30%;" 
                    			onclick="
                    					if (checkForm(true)) {
                    						document.getElementById('form').submit();
                    					} 
                   				">
                      <s:text name="global.confirm" />
                    </a>
                    </center>
                    <hr>                 
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
