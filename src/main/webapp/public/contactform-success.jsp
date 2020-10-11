<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<c:url value="/" var="mxurl"/>
<c:url value="/welcome" var="welcomeUrl"/>

<!DOCTYPE html>
<html lang="en">

<s:include value="generic_form/head.jsp" />

<body  class="mx-public-form" onload="createForm();" >
	
	
<s:include value="generic_form/body.jsp" />
 
 <div id="contents" style="display:none" >
 	<center><h4 style="margin-bottom:2rem; ">
 	<s:text name="contactform.success" /></h4>
	
						
	  <a href="#" class="btn btn-primary btn-user btn-block scale" style="max-width:30%;" 
	  	onclick="window.location.href='${origin}'">
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

