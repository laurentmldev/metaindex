<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>



<h3 class="positive"><s:text name="community.terms"/> &amp; <s:text name="community.vocabulary"/> - <s:property value="loggedUserProfile.guiLanguage"/></h3>
 		 
 		 <table><tr>
 		 <td style="width:70%;">	
 	<fieldset >
<legend ><span class="fieldsetTitle" >Description</span></legend>

<form id="community.details.vocabulary.form2" action="<c:url value="/updateCommunityProcess" />"  method="post">
	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
			
		<table style="padding:5px">
			
			<tr>
				<td><span class="fieldtitle"  ><s:text name="community.details.name"/></span></td>
				<td>						
					<s:include value="/secure/subparts/global.editablefield.jsp" >
						<s:param name="id" value="'community.details.vocabulary.form.name'" />
						<s:param name="text" value="selectedCommunity.vocabulary.communityNameTraduction" />
						<s:param name="inputname" value="'selectedCommunity.vocabulary.communityNameTraduction'" />	
						<s:param name="inputvalue" value="selectedCommunity.vocabulary.communityNameTraduction" />	
						<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
						<s:param name="inputonchange" value="'document.getElementById(\\'community.details.vocabulary.form2\\').submit();'" />				    											    						
						<s:param name="inputstyle" value="width:80px" />												
						<s:param name="readOnly" value="false" />
					</s:include>						
				</td>
			</tr>
			<tr>
				<td><span class="fieldtitle"  ><s:text name="community.details.comment"/></span></td>
				<td>						
					<s:include value="/secure/subparts/global.editablefield.jsp" >
						<s:param name="id" value="'community.details.vocabulary.form.comment'" />
						<s:param name="text" value="selectedCommunity.vocabulary.communityCommentTraduction" />
						<s:param name="inputname" value="'selectedCommunity.vocabulary.communityCommentTraduction'" />	
						<s:param name="inputvalue" value="selectedCommunity.vocabulary.communityCommentTraduction" />	
						<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
						<s:param name="inputonchange" value="'document.getElementById(\\'community.details.vocabulary.form2\\').submit();'" />				    											    						
						<s:param name="inputstyle" value="width:80px" />	
						<s:param name="readOnly" value="false" />											
					</s:include>						
				</td>
			</tr>
			</table>
</form></fieldset>
<fieldset >
<legend ><span class="fieldsetTitle" ><s:text name="community.terms"/></span></legend>

<form id="community.details.addterm.form" action="<c:url value="addCommunityTermProcess" />"  method="post">
	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
	<input type="hidden"  id="community.details.addterm.form.newTermIdName" name="newTermIdName" value=""/>
	<input type="hidden"  name="newTermDataTypeId" value="10"/><!-- Default data type is tiny text => id=10 -->
</form>
<form id="community.details.deleteterm.form" action="<c:url value="deleteCommunityTermProcess" />"  method="post">
	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
	<input type="hidden"  id="community.details.deleteterm.form.formTermId" name="formTermId" value=""/>
</form>
<form id="community.details.terms.form" action="<c:url value="/updateCommunityTermProcess" />"  method="post">
	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
	<input type="hidden"  id="community.details.terms.form.termIdName" name="formTermIdName" value=""/>		
		<table class="horizontalLineTable" style="padding:5px">
				<tr>
					<th><s:text name="community.term.idName"/></th>
					<th><s:text name="community.term.name"/></th>
					<th><s:text name="community.term.comment"/></th>
					<th><s:text name="community.term.datatype"/></th>
					<th></th>
				</tr>
			<s:iterator value="selectedCommunity.terms" var="curTerm">	
				<tr>
					<td><s:property value="#curTerm.idName"/></td>
					<td>											
						<s:include value="/secure/subparts/global.editablefield.jsp" >
							<s:param name="id" value="'community.details.terms.form.name.'+#curTerm.termId" />
							<s:param name="text" value="#curTerm.vocabulary.termNameTraduction" />
							<s:param name="inputname" value="'selectedCommunity.termsMap[\\''+#curTerm.idName+'\\'].vocabulary.termNameTraduction'" />	
							<s:param name="inputvalue" value="#curTerm.vocabulary.termNameTraduction" />	
							<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>									
							<s:param name="inputonchange" value="'document.getElementById(\\'community.details.terms.form.termIdName\\').value=\\''+#curTerm.idName+'\\';document.getElementById(\\'community.details.terms.form\\').submit();'" />				    											    						
							<s:param name="inputstyle" value="width:80px" />
							<s:param name="readOnly" value="false" />												
						</s:include>						
					</td>
					<td>											
						<s:include value="/secure/subparts/global.editablefield.jsp" >
							<s:param name="id" value="'community.details.terms.form.comment.'+#curTerm.termId" />
							<s:param name="text" value="#curTerm.vocabulary.termCommentTraduction" />
							<s:param name="inputname" value="'selectedCommunity.termsMap[\\''+#curTerm.idName+'\\'].vocabulary.termCommentTraduction'" />	
							<s:param name="inputvalue" value="#curTerm.vocabulary.termCommentTraduction" />	
							<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>									
							<s:param name="inputonchange" value="'document.getElementById(\\'community.details.terms.form.termIdName\\').value=\\''+#curTerm.idName+'\\';document.getElementById(\\'community.details.terms.form\\').submit();'" />				    											    						
							<s:param name="inputstyle" value="width:80px" />
							<s:param name="readOnly" value="false" />												
						</s:include>						
					</td>
					<td>											
						<a href="#" id="community.details.terms.form.datatype.readonly.<s:property value="#curTerm.termId" />"									
								onclick="event.stopPropagation();							
									document.getElementById('community.details.terms.form.datatype.edit.<s:property value="#curTerm.termId" />').style.display='block';
									document.getElementById('community.details.terms.form.datatype.readonly.<s:property value="#curTerm.termId" />').style.display='none';"
								class="modifyable" 							
						>
							<s:property value="#curTerm.datatypeName"/>	
						</a>
											
						<select name="selectedCommunity.termsMap['<s:property value="#curTerm.idName" />'].datatypeId"
								id="community.details.terms.form.datatype.edit.<s:property value="#curTerm.termId" />"
								style="display:none"
								onchange="document.getElementById('community.details.terms.form.termIdName').value='<s:property value="#curTerm.idName" />';document.getElementById('community.details.terms.form').submit();"
								>
							<s:iterator value="selectedCommunity.datatypes" var="curDatatype">
									<option value="<s:property value="#curDatatype.datatypeId"/>"  <c:if test="${curDatatype.datatypeId==curTerm.datatypeId}">selected</c:if> >
										<s:property value="#curDatatype.datatypeName"/></option>
								</s:iterator>
						</select>						
					</td>
					<td>
						<a  href="#" id="community.details.deleteTerm" 
			 				title="<s:text name="workzone.icon.deleteTerm" />" 
			 				class="smallicon icon_deleteElement" 
			 				onclick="event.stopPropagation();document.getElementById('community.details.deleteterm.form.formTermId').value='<s:property value="#curTerm.termId" />';document.getElementById('community.details.deleteterm.form').submit();"></a>
					</td>									
				</tr>
			</s:iterator>
				<tr>	
					<td><input
							placeholder="<s:text name="community.details.addTermIdNameTitle" />" 
							onchange="document.getElementById('community.details.addterm.form.newTermIdName').value=this.value;"/></td>
					<td colspan="3">
					<td>
						<a  href="#" id="community.details.addTerm" 
			 				title="<s:text name="workzone.icon.addTerm" />" 
			 				class="smallicon icon_addElement" 
			 				onclick="event.stopPropagation();document.getElementById('community.details.addterm.form').submit();"></a>
			 			</td>
			 			
				</tr>
		</table>
</form>
</fieldset> 		
 		</td>
 		 
 		 <td style="width:30%;">		
<fieldset >
<legend ><span class="fieldsetTitle" ><s:text name="community.vocabulary"/></span></legend>

<form id="community.details.vocabulary.form" action="<c:url value="/updateCommunityProcess" />"  method="post">
	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
			
		<table style="padding:5px">
			
			
			<tr>
				<td><span class="fieldtitle"  ><s:text name="community.details.element"/></span></td>
				<td>						
					<s:include value="/secure/subparts/global.editablefield.jsp" >
						<s:param name="id" value="'community.details.vocabulary.form.element'" />
						<s:param name="text" value="selectedCommunity.vocabulary.elementTraduction" />
						<s:param name="inputname" value="'selectedCommunity.vocabulary.elementTraduction'" />	
						<s:param name="inputvalue" value="selectedCommunity.vocabulary.elementTraduction" />	
						<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
						<s:param name="inputonchange" value="'document.getElementById(\\'community.details.vocabulary.form\\').submit();'" />				    											    						
						<s:param name="inputstyle" value="width:80px" />
						<s:param name="readOnly" value="false" />												
					</s:include>						
				</td>
			</tr>	
			<tr>
				<td><span class="fieldtitle"  ><s:text name="community.details.elements"/></span></td>
				<td>						
					<s:include value="/secure/subparts/global.editablefield.jsp" >
						<s:param name="id" value="'community.details.vocabulary.form.elements'" />
						<s:param name="text" value="selectedCommunity.vocabulary.elementsTraduction" />
						<s:param name="inputname" value="'selectedCommunity.vocabulary.elementsTraduction'" />	
						<s:param name="inputvalue" value="selectedCommunity.vocabulary.elementsTraduction" />	
						<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
						<s:param name="inputonchange" value="'document.getElementById(\\'community.details.vocabulary.form\\').submit();'" />				    											    						
						<s:param name="inputstyle" value="width:80px" />
						<s:param name="readOnly" value="false" />												
					</s:include>						
				</td>
			</tr>	<tr><td>&nbsp;</td></tr>
			<tr>
				<td><span class="fieldtitle"  ><s:text name="community.details.dataset"/></span></td>
				<td>						
					<s:include value="/secure/subparts/global.editablefield.jsp" >
						<s:param name="id" value="'community.details.vocabulary.form.dataset'" />
						<s:param name="text" value="selectedCommunity.vocabulary.datasetTraduction" />
						<s:param name="inputname" value="'selectedCommunity.vocabulary.datasetTraduction'" />	
						<s:param name="inputvalue" value="selectedCommunity.vocabulary.datasetTraduction" />	
						<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
						<s:param name="inputonchange" value="'document.getElementById(\\'community.details.vocabulary.form\\').submit();'" />				    											    						
						<s:param name="inputstyle" value="width:80px" />	
						<s:param name="readOnly" value="false" />											
					</s:include>						
				</td>
			</tr>
			<tr>
				<td><span class="fieldtitle"  ><s:text name="community.details.datasets"/></span></td>
				<td>						
					<s:include value="/secure/subparts/global.editablefield.jsp" >
						<s:param name="id" value="'community.details.vocabulary.form.datasets'" />
						<s:param name="text" value="selectedCommunity.vocabulary.datasetsTraduction" />
						<s:param name="inputname" value="'selectedCommunity.vocabulary.datasetsTraduction'" />	
						<s:param name="inputvalue" value="selectedCommunity.vocabulary.datasetsTraduction" />	
						<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
						<s:param name="inputonchange" value="'document.getElementById(\\'community.details.vocabulary.form\\').submit();'" />				    											    						
						<s:param name="inputstyle" value="width:80px" />
						<s:param name="readOnly" value="false" />												
					</s:include>						
				</td>
			</tr><tr><td>&nbsp;</td></tr>
			<tr>
				<td><span class="fieldtitle"  ><s:text name="community.details.metadata"/></span></td>
				<td>						
					<s:include value="/secure/subparts/global.editablefield.jsp" >
						<s:param name="id" value="'community.details.vocabulary.form.metadata'" />
						<s:param name="text" value="selectedCommunity.vocabulary.metadataTraduction" />
						<s:param name="inputname" value="'selectedCommunity.vocabulary.metadataTraduction'" />	
						<s:param name="inputvalue" value="selectedCommunity.vocabulary.metadataTraduction" />	
						<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
						<s:param name="inputonchange" value="'document.getElementById(\\'community.details.vocabulary.form\\').submit();'" />				    											    						
						<s:param name="inputstyle" value="width:80px" />	
						<s:param name="readOnly" value="false" />											
					</s:include>						
				</td>
			</tr>	
			<tr>
				<td><span class="fieldtitle"  ><s:text name="community.details.metadatas"/></span></td>
				<td>						
					<s:include value="/secure/subparts/global.editablefield.jsp" >
						<s:param name="id" value="'community.details.vocabulary.form.metadatas'" />
						<s:param name="text" value="selectedCommunity.vocabulary.metadatasTraduction" />
						<s:param name="inputname" value="'selectedCommunity.vocabulary.metadatasTraduction'" />	
						<s:param name="inputvalue" value="selectedCommunity.vocabulary.metadatasTraduction" />	
						<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
						<s:param name="inputonchange" value="'document.getElementById(\\'community.details.vocabulary.form\\').submit();'" />				    											    						
						<s:param name="inputstyle" value="width:80px" />	
						<s:param name="readOnly" value="false" />											
					</s:include>						
				</td>
			</tr><tr><td>&nbsp;</td></tr>	
			<tr>
				<td><span class="fieldtitle"  ><s:text name="community.details.catalog"/></span></td>
				<td>						
					<s:include value="/secure/subparts/global.editablefield.jsp" >
						<s:param name="id" value="'community.details.vocabulary.form.catalog'" />
						<s:param name="text" value="selectedCommunity.vocabulary.catalogTraduction" />
						<s:param name="inputname" value="'selectedCommunity.vocabulary.catalogTraduction'" />	
						<s:param name="inputvalue" value="selectedCommunity.vocabulary.catalogTraduction" />	
						<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
						<s:param name="inputonchange" value="'document.getElementById(\\'community.details.vocabulary.form\\').submit();'" />				    											    						
						<s:param name="inputstyle" value="width:80px" />
						<s:param name="readOnly" value="false" />												
					</s:include>						
				</td>
			</tr>	
			<tr>																													<tr>
				<td><span class="fieldtitle"  ><s:text name="community.details.catalogs"/></span></td>
				<td>						
					<s:include value="/secure/subparts/global.editablefield.jsp" >
						<s:param name="id" value="'community.details.vocabulary.form.catalogs'" />
						<s:param name="text" value="selectedCommunity.vocabulary.catalogsTraduction" />
						<s:param name="inputname" value="'selectedCommunity.vocabulary.catalogsTraduction'" />	
						<s:param name="inputvalue" value="selectedCommunity.vocabulary.catalogsTraduction" />	
						<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
						<s:param name="inputonchange" value="'document.getElementById(\\'community.details.vocabulary.form\\').submit();'" />				    											    						
						<s:param name="inputstyle" value="width:80px" />
						<s:param name="readOnly" value="false" />												
					</s:include>						
				</td>
			</tr><tr><td>&nbsp;</td></tr>
			<tr>
				<td><span class="fieldtitle"  ><s:text name="community.details.user"/></span></td>
				<td>						
					<s:include value="/secure/subparts/global.editablefield.jsp" >
						<s:param name="id" value="'community.details.vocabulary.form.user'" />
						<s:param name="text" value="selectedCommunity.vocabulary.userTraduction" />
						<s:param name="inputname" value="'selectedCommunity.vocabulary.userTraduction'" />	
						<s:param name="inputvalue" value="selectedCommunity.vocabulary.userTraduction" />	
						<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
						<s:param name="inputonchange" value="'document.getElementById(\\'community.details.vocabulary.form\\').submit();'" />				    											    						
						<s:param name="inputstyle" value="width:80px" />
						<s:param name="readOnly" value="false" />												
					</s:include>						
				</td>
			</tr>	
			<tr>
				<td><span class="fieldtitle"  ><s:text name="community.details.users"/></span></td>
				<td>						
					<s:include value="/secure/subparts/global.editablefield.jsp" >
						<s:param name="id" value="'community.details.vocabulary.form.users'" />
						<s:param name="text" value="selectedCommunity.vocabulary.usersTraduction" />
						<s:param name="inputname" value="'selectedCommunity.vocabulary.usersTraduction'" />	
						<s:param name="inputvalue" value="selectedCommunity.vocabulary.usersTraduction" />	
						<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
						<s:param name="inputonchange" value="'document.getElementById(\\'community.details.vocabulary.form\\').submit();'" />				    											    						
						<s:param name="inputstyle" value="width:80px" />
						<s:param name="readOnly" value="false" />												
					</s:include>						
				</td>
			</tr>	<tr><td>&nbsp;</td></tr>
			<tr>
				<td><span class="fieldtitle"  ><s:text name="community.details.userGroup"/></span></td>
				<td>						
					<s:include value="/secure/subparts/global.editablefield.jsp" >
						<s:param name="id" value="'community.details.vocabulary.form.userGroup'" />
						<s:param name="text" value="selectedCommunity.vocabulary.userGroupTraduction" />
						<s:param name="inputname" value="'selectedCommunity.vocabulary.userGroupTraduction'" />	
						<s:param name="inputvalue" value="selectedCommunity.vocabulary.userGroupTraduction" />	
						<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
						<s:param name="inputonchange" value="'document.getElementById(\\'community.details.vocabulary.form\\').submit();'" />				    											    						
						<s:param name="inputstyle" value="width:80px" />
						<s:param name="readOnly" value="false" />												
					</s:include>						
				</td>
			</tr>
			<tr>
				<td><span class="fieldtitle"  ><s:text name="community.details.userGroups"/></span></td>
				<td>						
					<s:include value="/secure/subparts/global.editablefield.jsp" >
						<s:param name="id" value="'community.details.vocabulary.form.userGroups'" />
						<s:param name="text" value="selectedCommunity.vocabulary.userGroupsTraduction" />
						<s:param name="inputname" value="'selectedCommunity.vocabulary.userGroupsTraduction'" />	
						<s:param name="inputvalue" value="selectedCommunity.vocabulary.userGroupsTraduction" />	
						<s:param name="inputplaceholder" value="" /><%--<s:text name="workzone.dataset.layout.elementName"/--%>		
						<s:param name="inputonchange" value="'document.getElementById(\\'community.details.vocabulary.form\\').submit();'" />				    											    						
						<s:param name="inputstyle" value="width:80px" />
						<s:param name="readOnly" value="false" />												
					</s:include>						
				</td>
			</tr>	<tr><td>&nbsp;</td></tr>																	
 		</table> 
</form>
 		</fieldset>
 	</td>
 	</tr></table>
 	<script>
 		  var notModifyables=document.querySelectorAll(".notInEditModeDataField");
 		  for  (var i=0;i<notModifyables.length;i++) { 
 			  notModifyables[i].style.display='none';	  
 		  }
 		  var modifyables=document.querySelectorAll(".inEditModeDataField");
 		  for  (var i=0;i<modifyables.length;i++) { 
 			  modifyables[i].style.display='block';	  
 		  }    
 	</script>
