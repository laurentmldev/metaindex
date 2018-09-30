<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<c:url value="/loginprocess" var="loginUrl"/> 
<c:url value="/logoutprocess" var="logoutUrl"/> 
 
<!DOCTYPE html/>
<html>
<head>

<script src="public/deps/sockjs/sockjs.js"></script>
<script src="public/deps/stomp/stomp.js"></script>

<!--script>

var socket = new SockJS('http://localhost:8080/ws/myHandler', null, {debug:true});

console.log("Socket="+socket.url)
stompClient = Stomp.over(socket);
stompClient.connect({}, function(frame) {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/greetings', function(greeting){
        showGreeting(JSON.parse(greeting.body).content);
    });
});
</script-->

<!--  
        <script language="javascript" type="text/javascript">
            
            var wsUri = "ws://localhost:8080/myHandler";
            
            function init() {
                output = document.getElementById("output");
            }
            function send_message() {
                websocket = new WebSocket(wsUri);
                websocket.onopen = function(evt) {
                    onOpen(evt)
                };
                websocket.onmessage = function(evt) {
                    onMessage(evt)
                };
                websocket.onerror = function(evt) {
                    onError(evt)
                };
            }
            function onOpen(evt) {
                writeToScreen("Connected to Endpoint!");
                doSend(textID.value);
            }
            function onMessage(evt) {
                writeToScreen("Message Received: " + evt.data);
            }
            function onError(evt) {
                writeToScreen('ERROR: ' + evt.data + " URI: "+wsUri);
            }
            function doSend(message) {
                writeToScreen("Message Sent: " + message);
                websocket.send(message);
                //websocket.close();
            }
            function writeToScreen(message) {
                var pre = document.createElement("p");
                pre.style.wordWrap = "break-word";
                pre.innerHTML = message;
                 
                output.appendChild(pre);
            }
            window.addEventListener("load", init, false);
        </script>
        -->
        
        
        <script type="text/javascript">
			var connection = new WebSocket('ws://localhost:10440/ws/plop', 'json');
			connection.onopen = function () {
			  console.log('Connection Opened');
			};
			connection.onerror = function (error) {
			  console.log('WebSocket Error ' + error);
			};
			connection.onmessage = function (e) {
			  if(e.data.indexOf("subProtocol")==-1)
			    document.getElementById("response").innerHTML=e.data+"<br/>";
			};
			function sendMessage(msg){
			  connection.send(msg);
			}
		</script>
</head>
<body style="background:#555">
<center><h3>Testing Web-Sockets</h3></center>


        <h1 style="text-align: center;">Hello World WebSocket Client</h1>
        <br>
        <div style="text-align: center;">
            <form action="">
                <input onclick="sendMessage(document.getElementById('textID'))" value="Send" type="button">
                <input id="textID" name="message" value="Hello WebSocket!" type="text"><br>
            </form>
        </div>
        <div id="output"></div>

</body>
</html>
