<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  

<c:url value="/contactformprocess" var="contactUsUrl"/>

<!DOCTYPE html>
<html lang="en">

<s:include value="generic_form/head.jsp" />

<body  class="mx-public-form"

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
<s:include value="generic_form/body.jsp"  >
	<s:param name="width">50%</s:param>
</s:include>
 
 <div id="contents" style="display:none" >
 	<center><h1 style="margin-bottom:2rem; ">
 	<s:text name="contactform.title" /></h1></center>
	<form id="form" class="user" action="${contactUsUrl}" method="post" >
          <input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <input type="hidden"  name="origin" value="${param.origin}"/>
             	
             <center>
               <div class="form-group" style="width:100%">
                
                
                
               	<select id="topics" name="topics" style="padding-left:1rem;padding-right:1rem;" >
				  <option value="info" selected><s:text name="contactform.topic.info" /></option>
				  <option value="bug"><s:text name="contactform.topic.bug" /></option>
				  <option value="other"><s:text name="contactform.topic.other" /></option>
				</select>
				
				<input id="email" type="email" class="form-control form-control-user mx_welcome_input"name="email" placeholder="<s:text name="session.email" />" 
					style="margin:1rem;padding:0.6rem;"
						value="${param.email}"/>
                 
               </div>
               <div class="form-group">
                 <textarea id="msg" name='msg' class="form-control form-control-user mx_welcome_input" placeholder="<s:text name="contactform.yourtext" /> ..." 
                 	rows="5" style="padding:0.4rem;"></textarea>
               </div>
               </center>
               <div class="form-group" style="width:100%;">
               <center>
               	
	  
	  <input id="bigtext" type="text" class="form-control form-control-user mx_welcome_input" name="very_important" placeholder="" 
					style="margin:1rem;padding:0.6rem;color:#68c;"
						value="<s:text name="contactform.emptyme" />" />
			
			
			
	 <table style="width:100%"><tr>
      <td style="width:50%;"><center>
      
       <a href="#" class="btn btn-user  btn-block scale"  
        	style="max-width:70%;font-size:0.8rem;background:#aaa;color:white;padding:0.2rem"
	  	onclick="window.location.href='${param.origin}'">
                   <s:text name="globals.goback" />
                 </a>
                 
       
      </center>
      </td style="width:50%">
      <td>
      <center>
      
       <a href="#" class="btn  btn-user btn-block scale" 
       	style="max-width:70%;font-size:0.8rem;background:#8d8;color:white;padding:0.2rem" 
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
                 
           
      </center></td>  
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

