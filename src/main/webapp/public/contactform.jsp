<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<c:url value="/" var="mxurl"/>
<c:url value="/contactformprocess" var="contactUsUrl"/>

<!DOCTYPE html>
<html lang="en">

<s:include value="generic_form/head.jsp" />

<body class=""

	onload="createForm();
			if ('${param.email}' != '') { document.getElementById('email').style.display='none'; }			
	       "
	>
	
	
	<script type="text/javascript">
	
	// from https://stackoverflow.com/questions/46155/how-to-validate-an-email-address-in-javascript
	function validateEmail(email) {
	    const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	    return re.test(String(email).toLowerCase());
	}
	
	</script>
<s:include value="generic_form/body.jsp" />
 
 <div id="contents" style="display:none" >
 	<center><h1 style="margin-bottom:2rem; ">
 	<s:text name="contactform.title" /></h1></center>
	<form id="form" class="user" action="${contactUsUrl}" method="post" >
          <input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <input type="hidden"  name="origin" value="${param.origin}"/>
             	
             
               <div class="form-group">
                <center>
                
                
               	<select id="topics" name="topics" style="padding-left:1rem;padding-right:1rem;" value="info">
				  <option value="info"><s:text name="contactform.topic.info" /></option>
				  <option value="bug"><s:text name="contactform.topic.bug" /></option>
				  <option value="other"><s:text name="contactform.topic.other" /></option>
				</select>
				
				<input id="email" type="email" class="form-control form-control-user"name="email" placeholder="email" 
					style="margin:1rem;padding:0.6rem;width:60%;"
						value="${param.email}"/>
                 </center>
               </div>
               <div class="form-group">
                 <textarea id="msg" name='msg' class="form-control form-control-user" placeholder="Your text ..." 
                 	rows="5" style="border-radius:5px;padding:0.4rem;"></textarea>
               </div>
               <div class="form-group" style="width:100%;">
               <center>
               	
	  
	  <input id="bigtext" type="text" class="form-control form-control-user"name="email" placeholder="" 
					style="margin:1rem;padding:0.6rem;width:60%;"
						value="<s:text name="contactform.emptyme" />" />
				
				
	  <a href="#" class="btn btn-primary btn-user btn-block scale" style="max-width:30%;" 
	  	onclick="
	  	
	  			if (!validateEmail(document.getElementById('email').value)) { 
						alert('<s:text name="contactform.providevalidemail" />'); 
						document.getElementById('email').focus();
					}
	  	
	  			else if (document.getElementById('msg').value=='') { 
	  				alert('<s:text name="contactform.providetext" />'); 
	  				document.getElementById('msg').focus();
	  			}
	  			else if (document.getElementById('bigtext').value!='') {
	  				alert('<s:text name="contactform.emptyme.explanation" />'); 
	  				document.getElementById('bigtext').focus();
	  			}
	  	         else { document.getElementById('form').submit(); }">
                   <s:text name="contactform.send" />
                 </a>
                 
        <a href="#" class="btn btn-primary btn-user btn-block scale" 
        		style="max-width:30%;margin-top:1rem;padding:0.4rem;background:#999;border:none;" 
	  	onclick="window.location.href='${param.origin}'">
                   <s:text name="globals.goback" />
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

