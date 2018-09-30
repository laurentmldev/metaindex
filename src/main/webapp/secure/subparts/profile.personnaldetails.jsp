<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<!-- View Profile Details  -->
	<div id="profile.details.view" >
	    <fieldset><legend><span class="fieldsetTitle" ><s:text name="profile.details" /></span></legend>
	    	<table>
	    	<tr><td><span class="fieldtitle"><s:text name="profile.username" /></span></td><td><s:property value='username'/></td></tr>
			<tr><td><span class="fieldtitle"><s:text name="profile.email" /></span></td><td><s:property value='email'/></td></tr>
			<tr><td><span class="fieldtitle"><s:text name="profile.language" /></span></td><td><s:property value='guiLanguage'/></td></tr>
			<tr><td><span class="fieldtitle"><s:text name="profile.theme" /></span></td><td><s:property value='guiTheme'/></td></tr>
			<tr><td><br/><center><a href="#"  
								onclick="document.getElementById('profile.details.view').style.setProperty('display','none');
									document.getElementById('profile.details.edit').style.setProperty('display','block');
									document.getElementById('profile.details').setAttribute('class','cell, editshadowcard');" >
								<s:text name="profile.edit" /></a></center></td></tr>
			</table>
		</fieldset>
	</div>
		
<!-- Edit Profile Details  -->
    <div id="profile.details.edit" style="display:none">
    			    		
		<form id="profile.details.edit.form" action="<c:url value="/editProfileProcess" />" method="post" >
		<fieldset><legend><span class="fieldsetTitle" ><s:text name="editProfile.title" /></span></legend>
		<center>
			<c:if test="${param.error != null}"> <p><s:text name="editProfile.error" /></p></c:if>		
			<s:fielderror fieldName="email" />		
			<table>
				<tr><td><span class="fieldtitle"><s:text name="profile.username" /></span></td><td><s:property value='username'/></td></tr>
				<tr><td><span class="fieldtitle"><s:text name="profile.email" /></span></td><td><s:property value='email'/></td></tr>
				<tr><td><span class="fieldtitle"><s:text name="profile.language" /></span></td><td><s:select name="guiLanguageId" list="guiLanguages" listKey="id" listValue="name" headerKey="1" /></td></tr>
				<tr><td><span class="fieldtitle"><s:text name="profile.theme" /></span></td><td><s:select name="guiThemeId" list="guiThemes" listKey="id" listValue="name" headerKey="1" /></td></tr>				
				<tr><td colspan='2'>
				<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
				<br/>
					<a href="" onclick="document.getElementById('profile.details.edit.form').submit()"><s:text name="global.submit" /></a>
					&nbsp;&nbsp;<a href="" onclick="document.getElementById('profile.details.view').style.setProperty('display','block');
									document.getElementById('profile.details.edit').style.setProperty('display','none');
									document.getElementById('profile.details').setAttribute('class','cell');" >
									<s:text name="global.cancel" /></a>				
				</td></tr>
			</table>
			</center></fieldset>
		</form>		
	</div>	
