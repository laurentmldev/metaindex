<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<div id="profile.communities.view" >
	    <fieldset><legend><span class="fieldsetTitle" ><s:text name="profile.communitiesList" /></span></legend>
	    	<form id="profile.opencommunity.form" action="<c:url value="/openCommunity" />" method="post" >
	    			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
	        		<input type="hidden"  id="profile.opencommunity.form.idName" name="idName" value=""/>
			        
	    	<table>
	    	    <s:iterator value="userCommunities">
			        <tr><td>
			        	
							<a href="#openCommunity"  onclick="
								document.getElementById('profile.opencommunity.form.idName').setAttribute('value','<s:property value="idName"/>');
								document.getElementById('profile.opencommunity.form').submit()">
								<s:property value="vocabulary.communityNameTraduction"/> </a><span class="comment"> <s:property value="idName"/></span> 
			       		
		       		</td></tr>			    	
			    </s:iterator>
	   		</table>
	   		</form>
		</fieldset>
	</div>	
