<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>  
  

<script type="text/javascript">

function createChatBox(catalogId,catalogName) {
	let chatBox=document.createElement("div");
	chatBox.id="chatbox_"+catalogId;
	chatBox.classList.add("mx-chatbox");
	
	let title=document.createElement("div");
	title.classList.add("mx-chatbox-title");
	chatBox.append(title);
	title.innerHTML=catalogName;
	
	
	let contents=document.createElement("div");
	contents.classList.add("mx-chatbox-text");
	chatBox.append(contents);
	
	// input
	let inputTable=document.createElement("table");
	chatBox.append(inputTable);
	let tr=document.createElement("tr");
	inputTable.append(tr);
	
	let td=document.createElement("td");
	td.style.width="100%";
	tr.append(td);	
	let input=document.createElement("input");
	input.classList.add("mx-chatbox-input");
	input.type="text";
	td.append(input);
	input.onkeydown=function(event) {
		event.stopPropagation();
		// escape
		if (event.which==27||event.keycode==27) { chatBox.style.display='none'; }
	}	
	input.onkeypress=function(event) {
		event.stopPropagation();
		// enter
		if (event.which==13||event.keycode==13) {  
			if (input.value.length>0) {
				ws_handlers_send_chatmsg(catalogId,input.value);
				input.value="";
			}
		}
	}
	
	let td2=document.createElement("td");
	tr.append(td2);	
	let sendBtn=document.createElement("i");
	sendBtn.classList.add("fa");
	sendBtn.classList.add("fa-paper-plane");	
	sendBtn.classList.add("mx-help-icon");
	td2.append(sendBtn);
	td2.onclick=function() { 
		if (input.value.length>0) {
			ws_handlers_send_chatmsg(catalogId,catalogName,input.value);
			input.value="";
		}
	}


	let crossClose= document.createElement("i");
	crossClose.classList.add("mx-help-icon");
	crossClose.classList.add("mx-chatbox-crossclose");
	crossClose.innerHTML="x";
	chatBox.append(crossClose);
	crossClose.onclick=function(event) {
		event.stopPropagation();
		chatBox.style.display="none";
		if (getOpenedChatsList().length==0) {
			hideChatsContainer();
		}
	}
	
	document.getElementById("chatBoxes_container").append(chatBox);
	ws_handlers_get_chat_histo(catalogId);
		
	return chatBox;
	
}


function openChatWindow(catalogId,catalogName) {
	let chatBox=document.getElementById("chatbox_"+catalogId);
	if (chatBox==null) { chatBox=createChatBox(catalogId,catalogName); }
	chatBox.style.display="block";	
	return chatBox;
}

function addChatEntry(catalogId,catalogName,date,name,senderid,text) {
	
	let entry = document.createElement("div");
	entry.classList.add("mx-chatbox-entry");
	
	let dateNode = document.createElement("span");
	dateNode.classList.add("mx-chatbox-entry-date");
	let dateObj=new Date(date);
	let mm = dateObj.getMonth() + 1;
	let dd = dateObj.getDate();
	let yy = dateObj.getFullYear();
	dateNode.innerHTML=yy+"/"+mm+"/"+dd+" "+dateObj.toLocaleTimeString();
	entry.append(dateNode);

	let nameNode = document.createElement("span");
	nameNode.classList.add("mx-chatbox-entry-name");
	nameNode.innerHTML=name+":";
	entry.append(nameNode);
	nameNode.title=senderid;

	let textNode = document.createElement("span");
	textNode.classList.add("mx-chatbox-entry-text");
	textNode.innerHTML=text;
	entry.append(textNode);
	textNode.title=senderid;
	
	let chatBox = openChatWindow(catalogId,catalogName);
	
	let chatBoxTextContainer=chatBox.querySelector(".mx-chatbox-text");
	chatBoxTextContainer.append(entry);	
	// keep visible latest message
	chatBoxTextContainer.scrollTop = chatBoxTextContainer.scrollHeight;
	
}

function getOpenedChatsList() {
	let result=[];
	let chatBoxes=document.getElementById("chatBoxes_container").querySelectorAll(".mx-chatbox");
	for (var idx=0;idx<chatBoxes.length;idx++) {		
		let chatBox=chatBoxes[idx];
		if (chatBox.style.display!="none") { result.push(chatBox); }
	}
	return result;
}
function isChatsContainerVisible() {
	let chatsContainer=document.getElementById("chatBoxes_container");
	return chatsContainer.style.display!="none";
}
function showChatsContainer() {
	let chatsContainer=document.getElementById("chatBoxes_container");
	chatsContainer.style.display="flex";
}
function hideChatsContainer() {
	let chatsContainer=document.getElementById("chatBoxes_container");
	chatsContainer.style.display="none";
}


function isChatsContainerVisible() {
	let chatsContainer=document.getElementById("chatBoxes_container");
	return chatsContainer.style.display!="none";
}
function isChatCatalogsListVisible() {
	let catalogsChoiceList=document.getElementById("chat_catalogsChoice");
	return catalogsChoiceList!=null && catalogsChoiceList.style.display!="none";
}
function hideChatCatalogsList() {
	let catalogsChoiceList=document.getElementById("chat_catalogsChoice");
	if (catalogsChoiceList!=null) { catalogsChoiceList.style.display="none"; }
}
function showChatCatalogsList() {
	let catalogsChoiceList=document.getElementById("chat_catalogsChoice");
	if (catalogsChoiceList==null) {
		catalogsChoiceList=document.createElement("div");
		catalogsChoiceList.classList.add("mx-chatbox-catalogs-list");
		catalogsChoiceList.id="chat_catalogsChoice";
		
		<c:forEach items="${currentUserProfile.accessibleCatalogs}" var="catalog">
		// adding catalog ${catalog.name}
		let catalogChoice_${catalog.id}=document.createElement("div");
		catalogChoice_${catalog.id}.classList.add("mx-chatbox-catalogs-list-elem");
		catalogChoice_${catalog.id}.innerHTML="${catalog.name}";
		catalogChoice_${catalog.id}.onclick=function(event) {
			event.stopPropagation();
			openChatWindow("${catalog.id}","${catalog.name}");
			hideChatCatalogsList();
			showChatsContainer();
		}
		catalogsChoiceList.append(catalogChoice_${catalog.id});
		
		</c:forEach>
		
		document.body.append(catalogsChoiceList);	
	}
	catalogsChoiceList.style.display="block";	
}
var newChatTimer=null;
var longClick=false;
function startChatClick() {
	newChatTimer=setTimeout(function() {     		
        showChatCatalogsList();
        clearTimeout(newChatTimer);
        longClick=true;
    }, 750);
	
}
function stopChatClick() {
	clearTimeout(newChatTimer);
	newChatTimer=null;
	if (longClick==false) {
		if (isChatCatalogsListVisible()) { hideChatCatalogsList(); }
		else if (isChatsContainerVisible()) { hideChatsContainer(); }
		else if (!isChatsContainerVisible()) {
			let chatsOpen=getOpenedChatsList();
			if (chatsOpen.length==0) { showChatCatalogsList(); }
			else { showChatsContainer(); } 
		}
	}
	longClick=false;
}

var MxChat={}
MxChat.handleChatMessage=function(catalogId,message) {
	addChatEntry(catalogId,catalogId,message.timestamp,message.authorName,message.authorId,message.text);
}

</script>
<div  id="chatBoxes_container" class="mx-chatboxes-container" style="display:none">

</div>

<i style="position:fixed;right:30px;bottom:10px;font-size:2rem;color:#999;opacity:0.6;" 
	class="mx-help-icon fa fa-user-friends"	
	onmousedown="startChatClick();"
	onmouseup="stopChatClick();"
	title="<s:text name="Catalogs.chat" />" >	
	</i>

