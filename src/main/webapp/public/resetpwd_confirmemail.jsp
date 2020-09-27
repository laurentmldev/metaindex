<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<c:url value="/" var="mxurl"/>
<c:url value="/signupprocess" var="signupUrl"/>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<c:url value="/" var="mxurl"/>


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
    <s:text name="passwordreset.emailsent.explanation" />
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


            			
