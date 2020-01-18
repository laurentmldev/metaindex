<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>


<style type="text/css" >

#wrapper {
	margin-right: auto;
    margin-left: auto;
    width: 100%;
}

#leftside {
text-align:center;
float:left;
width: 15%;
vertical-align: top;
padding:10px;
margin-top:100px;
}

#centerpanel {
float:left;
width:65%;
height:400px;
text-align: center;
vertical-align: middle;
padding-left:4px;
padding-right:4px;

}



#rightside {
text-align:left;
float:right;
width:15%;
min-height:150px;
vertical-align: top;
padding:10px;
margin-top:100px;
}
#downside,footer {
	position: fixed;
	clear: both;
    bottom: 0px;
    left:0px;
    right:0px;
 	text-align:center;
}

#downside {
margin-top:20px;
margin-bottom:10px;
padding:5px;
text-align:center;

}

#downside fieldset {
overflow:auto;
max-width:80%;

}
#downside fieldset.menushadowcard {
-webkit-box-shadow: inset -2px 0px 7px rgba(0, 0, 0, 0.8);
box-shadow: inset -2px 0px 7px rgba(0, 0, 0, 0.8);
border:none;
min-width:350px;
min-height:50px;
}

#chatroom {
padding:3px;
width:160px;
height:200px;
position: fixed;
clear: both;
bottom: 5px;
right: 5px;
text-align:center;
}

#chat-contents {
width:97%;
height:130px;
overflow:auto;


}

.chat-entry {
margin:7px;
text-align:left;
color:white;
padding:3px;
}

.chat-entry legend {
}
</style>
