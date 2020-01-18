
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<footer>
 
 <s:if test="not actionErrors.empty or not actionMessages.empty">
	 <center>
		 <div id="footerMessages" class="usermessages" >
		 
		 <table style="width:90%">
		 <tr>
			 <td style="width:90%;horizontal-align:left">
				 <center>
				 	<s:actionerror />
				 	<s:actionmessage />
			 	</center>
		 	</td>
		 	<td style="width:10%">
			 	<input type="button" value="Hide" onclick="document.getElementById('footerMessages').style.display='none'" />
		 	</td>
	 	</tr>
	 	</table>
	 	
	 	</div>
	 </center>
 
 </s:if>
 
</footer>
