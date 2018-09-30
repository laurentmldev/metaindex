<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<c:url value="/" var="homeUrl"/>
<c:url value="/workzone" var="workzoneUrl"/>
<c:url value="/community" var="communityUrl"/>
<script type="text/javascript">
function mainmenu_switch(newSelectedMenu)
{
	document.getElementById('mainmenu.home').className='';
	document.getElementById('mainmenu.workzone').className='';
	document.getElementById('mainmenu.community').className='';
	if (newSelectedMenu != "") { document.getElementById(newSelectedMenu).className='active'; }
}
</script>

<c:url value="/logoutprocess" var="logoutUrl"/>
<c:url value="/profile" var="profileUrl"/>
<form id="logoutform" action="${logoutUrl}" method="post">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	
</form>


<div id='cssmainmenu'>
<ul>
   <li id="mainmenu.home" class='active'><a href='${homeUrl}' onclick="mainmenu_switch('mainmenu.home');" ><span><s:text name="mainmenu.home" /></span></a></li>
   <li id="mainmenu.workzone"><a href='${workzoneUrl}' onclick="mainmenu_switch('mainmenu.workzone');"><span><s:text name="mainmenu.workzone" /></span></a></li>
   <li id="mainmenu.community"><a href='${communityUrl}' onclick="mainmenu_switch('mainmenu.community');"><span><s:text name="mainmenu.community" /></span></a></li>
   <li class="language" ><span><a href="#" onclick="logoutform.submit()"> <s:text name="session.logout" /></a></span></li>
   <li class="language"><span><a href="${profileUrl}"> <s:text name="profile.linkname" /></a></span></li>
</ul>

</div>
