
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  

<s:if test="not actionErrors.empty or not actionMessages.empty">

<script>

var myMsgTimer = setInterval(clearMsg, 5000);
function clearMsg() {    
    document.getElementById('footerMessages').style.display='none';
    clearTimeout(myMsgTimer);   
}

</script>

<footer>

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
		 	
	 	</tr>
	 	</table>
	 	
	 	</div>
	 </center>
 
</footer>

</s:if>
