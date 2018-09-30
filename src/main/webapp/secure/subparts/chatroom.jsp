<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript" src="public/deps/prototype.js"></script>

    <script type="text/javascript">
    	var ws;
    	
    	function addChatText(author, text) {
    		document.getElementById("chat-contents").innerHTML="<fieldset class='chat-entry' ><legend>"+author.escapeHTML()+"</legend> "+text.escapeHTML() + "</fieldset>" + $("chat-contents").innerHTML;
         
        }

        document.observe("dom:loaded", function() {
            
            if (!window.WebSocket) {
                alert("FATAL: WebSocket not natively supported!");
            }

            

            $("uriForm").observe("submit", function(e) {
                e.stop();
                ws = new WebSocket($F("uri"));
                ws.onopen = function() {
                	addChatText("server","connected to chat room\n");
                }
                ws.onmessage = function(e) {
                	//addChatText("[WebSocket#onmessage] Message: '" + e.data + "'\n");
                    chat_handle_msg(e.data);
                }
                ws.onclose = function() {
                	addChatText("server","closed chat connection\n");
                    $("uri", "connect").invoke("enable");
                    $("disconnect").disable();
                    ws = null;
                    $("disconnect").style.display='none';
                    $("connect").style.display='block';
                }
                $("uri", "connect").invoke("disable");
                $("disconnect").enable();
                $("disconnect").style.display='block';
                $("connect").style.display='none';
            });

            $("sendForm").observe("submit", function(e) {
                e.stop();
                if (ws) {
                    var textField = $("textField");
                    chat_send_tweet(textField.value);
                    textField.value = "";
                    textField.focus();
                }
            });

            $("disconnect").observe("click", function(e) {
                e.stop();
                if (ws) {
                    ws.close();
                    ws = null;
                }
            });
        });
    	
    	function chat_handle_msg(msgStr) {
    		console.log("MSG: "+msgStr);
    		msg=JSON.parse(msgStr);
    		if (msg.msgType == 'chat-login') { chat_send_sessionid(msg); }
    		else if (msg.msgType == 'chat-tweet') { addChatText(msg.author, msg.text); }
            else(console.log('Error: received unhandled chat msg '+msg.msgType))
    	}
    	
    	
    	function chat_send_sessionid(msg) {
    		msg.sessionId="<s:property value='loggedUserProfile.sessionId'/>";
    		ws.send(JSON.stringify(msg));
    	}
    	
    	function chat_send_tweet(text) {
    		// TODO use a real JSOB object
    		//chatTweetMsg = new JSON();
    		//chatTweetMsg.msgType="chat-tweet";
    		JSONstr="{ 'msgType':'chat-tweet' , 'author' : '<s:property value='loggedUserProfile.username'/>', 'text' : '"+text+"'}";
    		ws.send(JSONstr);
    	}
    	
    </script>
  
	<fieldset id="chatroom" style="display:none" >
		<legend>Chat Room</legend>
		
		<form id="uriForm">
		    	<input type="hidden" id="uri" value="ws://localhost:8887" > 
		      	<input class="chat-connect-button" type="submit" id="connect" value="Enter">
		      	<input class="chat-connect-button" style="display:none" type="button" id="disconnect" value="Leave" disabled="disabled">      	
		 </form>
		      	
	  <center>
		  <div id="chat-contents" ></div>
			
		  <div>
		  	<form id="sendForm"><input type="text" id="textField" value=""  style="width:90%;"/></form>
	      </div>
	      
	      
	    </center>
	</fieldset>		
	
		
