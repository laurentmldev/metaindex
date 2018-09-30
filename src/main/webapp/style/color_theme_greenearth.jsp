<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<c:url value="/media/img/" var="imgdir"/> 
<style type="text/css" >
@charset "UTF-8";

html {
background : #3d664f;
color : #bbb;
}


a,a:link {
color:#ccc;
}

a:hover {
color:#ddd;
}

a:active {
color:#fff;
}

.fieldtitle {
color:#c7d1cb;
}

.usermessages {
background: rgba(0, 0, 0, 0.2);
}

.negative {
	color: #222; 
}

.positive {
color:#ccdbd1;
}

.volumeText {
color: grey;
}

.shadowcard {
		background: rgba(58, 80, 67, 0.5);
}

/* ------------ Menu Bar ------------ */

#cssmainmenu ul,
#cssmainmenu li,
#cssmainmenu a {
  list-style: none;
  margin: 0;
  padding: 0;
  border: 0;
  line-height: 1;
}
#cssmainmenu {
  border: 1px solid #3A5043;
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  -ms-border-radius: 5px;
  -o-border-radius: 5px;
  border-radius: 5px;
  width: auto;
}
#cssmainmenu ul {
  zoom: 1;
  background: #bbe2c7;
  background: -moz-linear-gradient(top, #568f6f 0%, #bbe2c7 100%);
  background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #568f6f), color-stop(100%, #bbe2c7));
  background: -webkit-linear-gradient(top, #568f6f 0%, #bbe2c7 100%);
  background: -o-linear-gradient(top, #568f6f 0%, #bbe2c7 100%);
  background: -ms-linear-gradient(top, #568f6f 0%, #bbe2c7 100%);
  background: linear-gradient(top, #568f6f 0%, #bbe2c7 100%);
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='@top-color', endColorstr='@bottom-color', GradientType=0);
  padding: 5px 10px;
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  -ms-border-radius: 5px;
  -o-border-radius: 5px;
  border-radius: 5px;
}
#cssmainmenu ul:before {
  content: '';
  display: block;
}
#cssmainmenu ul:after {
  content: '';
  display: table;
  clear: both;
}
#cssmainmenu li {
  float: left;
  margin: 0 5px 0 0;
  border: 1px solid transparent;
}

#cssmainmenu a {
-moz-box-shadow:;
  -webkit-box-shadow:;
  box-shadow:;
}

#cssmainmenu li a {
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  -ms-border-radius: 5px;
  -o-border-radius: 5px;
  border-radius: 5px;
  padding: 8px 15px 9px 15px;
  display: block;
  text-decoration: none;
  text-shadow: 1px 1px 1px #445f4d;
  border: 1px solid transparent;
  font-size: 1.5em;
  color:#deede3;
}
#cssmainmenu li.active {
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  -ms-border-radius: 5px;
  -o-border-radius: 5px;
  border-radius: 5px;
  border: 1px solid #445f4d;
  background: #91ba9e;  
}
#cssmainmenu li.active a {
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  -ms-border-radius: 5px;
  -o-border-radius: 5px;
  border-radius: 5px;
  display: block;
  background: background: #91ba9e;
  border: 1px solid #445f4d;
  -moz-box-shadow: 1px 2px 4px #445f4d;
  -webkit-box-shadow: 1px 2px 4px #445f4d;
  box-shadow: inset 1px 2px 4px #445f4d;
  
}
#cssmainmenu li:hover {
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  -ms-border-radius: 5px;
  -o-border-radius: 5px;
  border-radius: 5px;
  border: 1px solid #445f4d;
  background: #91ba9e;
}
#cssmainmenu li:hover a {
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  -ms-border-radius: 5px;
  -o-border-radius: 5px;
  border-radius: 5px;
  display: block;
  background: #91ba9e;
  border: 1px solid #445f4d;
}
</style>
