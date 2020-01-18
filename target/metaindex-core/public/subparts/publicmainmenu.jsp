<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>

<c:url value="/" var="homeUrl"/>
<c:url value="/media/img/icons/countries" var="countryPicUrl"/>
<script type="text/javascript">
function mainmenu_switch(newSelectedMenu)
{
	document.getElementById('mainmenu.home').className='';
	if (newSelectedMenu != "") { document.getElementById(newSelectedMenu).className='active'; }
}
</script>

<s:url var="localeEN">
  <s:param name="request_locale" >en</s:param>
</s:url>
<s:url var="localeFR">
  <s:param name="request_locale" >fr</s:param>
</s:url>


<div id='cssmainmenu'>
<ul>
   <li id="mainmenu.metaindex" ><a href='${homeUrl}' onclick="mainmenu_switch('mainmenu.home');" ><span><s:text name="mainmenu.home" /></span></a></li>
   <li class="language"><span><s:a href="%{localeFR}" ><img class="icon" src="${countryPicUrl}/France.ico"/></s:a></span></li>
   <li class="language"><span ><s:a href="%{localeEN}" ><img class="icon" src="${countryPicUrl}/United-Kingdom.ico"/></s:a> </span></li>
   
</ul>
</div>
