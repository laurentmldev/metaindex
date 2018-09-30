<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:url value="/communityLang" var="communityLangUrl"/>
<c:url value="/communityUsers" var="communityUsersUrl"/>


		<fieldset><legend><span class="fieldsetTitle" ><s:text name="community.manage" /></span></legend>
			<table>
				<tr><td><a href="${communityLangUrl}" class="listitem"><s:text name="community.terms"/> &amp; <s:text name="community.vocabulary"/></a></td></tr>
				<tr><td><a href="${communityUsersUrl}" class="listitem"><s:text name="community.details.users"/> &amp; <s:text name="community.details.userGroups"/></a></td></tr>
				
			</table>
		</fieldset>		
