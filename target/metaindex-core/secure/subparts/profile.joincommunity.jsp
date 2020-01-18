<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div id="profile.communities.view" >

	    
	    <!-- Create Community -->	
	<a href="#" onclick="document.getElementById('profile.createcommunity.modal').style.setProperty('display','table');" ><s:text name="profile.createnewcommunity" /></a>
	<form id="profile.createcommunity.form" action="<c:url value="/createCommunityProcess" />" method="post" >
			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
			
	 <div class="modal_shadow" id="profile.createcommunity.modal" ><div class="modal_back">
		<fieldset class="modal" style="width:300px" >		
	       	<legend>
	       		<a href="#close" title="Close"  class="modalclose" onclick="document.getElementById('profile.createcommunity.modal').style.setProperty('display','none');">X</a>
	       	</legend>
	   		<h4 class="negative" ><s:text name="profile.createnewcommunity" /></h4>
	   		<s:text name="profile.createnewcommunity.enterId" /><br/><br/>
	   		<input type="text" name="idName" id="profile.createcommunity.form.idNameToJoin"/>
   			
	   		<br/><br/><br/>
	   		<a href="#" onclick="document.getElementById('profile.createcommunity.form').submit()" ><span class="bigchoice"><s:text name="global.submit" /></span></a>
    	</fieldset></div></div>
	</form>

	<!-- Join Community -->	    	
	<fieldset><legend><span class="fieldsetTitle" ><s:text name="profile.joincommunity" /></span></legend>
	    
			
<!-- The community search field -->	    
		<input type="text" name="idNameToJoin" id="profile.joincommunity.searchcommunity"
	    			placeholder="<s:text name="global.search" />" 
	    			list="searchresults" autocomplete="on"
	    			onkeypress="if (event.which==13||event.keycode==13) {
	    				document.getElementById('profile.joincommunity.form.idNameToJoin').setAttribute('value',document.getElementById('profile.joincommunity.searchcommunity').value);
	    				document.getElementById('profile.joincommunity.modal.'+document.getElementById('profile.joincommunity.searchcommunity').value).
	    																		style.setProperty('display','block');}" >
			<datalist id="searchresults">
				<s:iterator value="otherCommunities">
				<option><s:property value="idName"/></option>
				</s:iterator>
			</datalist>
		
	    	<br/><br/>
	    		
<!-- Here start the real form -->																
		<form id="profile.joincommunity.form" action="<c:url value="/joinCommunityProcess" />" method="post" >
			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<input type="hidden" name="communityIdName" id="profile.joincommunity.form.idNameToJoin"/>
			<!-- Default value for group name, to be implemented when needed -->
	    	<input type="hidden" name="communityGroupName" id="profile.joincommunity.form.groupNameToJoin" value="Workers"/>
	    	<div style="height:250px;overflow:auto;">
	    	<table>
	    	
	    	    <s:iterator value="otherCommunities">
			        <tr><td >
			        	<a href="#" class="listitem" 
			        	title="<s:property value="vocabulary.communityCommentTraduction"/>"
			        	onclick="document.getElementById('profile.joincommunity.form.idNameToJoin').setAttribute('value','<s:property value="idName"/>');
			        			 document.getElementById('profile.joincommunity.modal.<s:property value="idName"/>').style.setProperty('display','table')">
			        		<s:property value="idName"/></a>
			        	<!-- Modal for joining this community -->
			        	<div class="modal_shadow" id="profile.joincommunity.modal.<s:property value="idName"/>" ><div class="modal_back">
			        	<fieldset class="modal" style="width:430px" >					        	
				        	<legend>
				        		<a href="#close" title="Close" class="modalclose" onclick="document.getElementById('profile.joincommunity.modal.<s:property value="idName"/>').style.setProperty('display','none');">X</a>
			        		</legend>
				    		<span class="negative" ><s:text name="profile.joincommunity" /></span>
				    		<h4><s:property value="idName"/></h4>
				    		<s:property value="vocabulary.communityCommentTraduction"/><br/><br/>
				    		<i><s:property value="vocabulary.communityVocabularyDescription"/></i>
				    		<br/><br/><br/>
				    		<a href="#" onclick="document.getElementById('profile.joincommunity.form').submit()" ><span class="bigchoice"><s:text name="global.join" /></span></a>
				    	</fieldset></div></div>
			        	
			        </td></tr>
			    </s:iterator>
			  
	   		</table>
	   		</div>
	   		</form>
		</fieldset>
		
	</div>	
