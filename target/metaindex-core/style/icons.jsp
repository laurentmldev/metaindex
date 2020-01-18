<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
 
<c:url value="/media/img/icons/gui/${selected_guitheme}" var="guiIconsUrl" />



<style type="text/css" >

.icon {
	float:left;
	width:30px;
	height:30px;
	background-size:30px 30px;
	background-repeat: no-repeat;
	display:block;
	
}
.icon:hover {
	/*background-size:32px 32px;
	width:32px;
	height:32px;*/
}

.bigicon {
	float:left;
	width:40px;
	height:40px;
	background-size:40px 40px;
	background-repeat: no-repeat;
	display:block;
	
}
.smallicon {
	float:left;
	width:20px;
	height:20px;
	background-size:20px 20px;
	background-repeat: no-repeat;
	display:block;
	
}

.tinyicon {
	float:left;
	width:15px;
	height:15px;
	background-size:15px 15px;
	background-repeat: no-repeat;
	display:block;
	
}

.icon_createElement {
	background-image: url('${guiIconsUrl}/icon-star.png');
}
.icon_createElement_active, .icon_createElement:hover {
	background-image: url('${guiIconsUrl}/icon-star.hover.png');
}

.icon_modify {
	background-image: url('${guiIconsUrl}/icon-pencil.png');
}
.icon_modify_active {
	background-image: url('${guiIconsUrl}/icon-pencil-active.png');
}
.icon_modify:hover {
	background-image: url('${guiIconsUrl}/icon-pencil-active.png');
	opacity:0.8;
}
.icon_modify_active:hover  {
	background-image: url('${guiIconsUrl}/icon-pencil.hover.png');
	opacity:0.8;
}
.icon_addElement {
	background-image: url('${guiIconsUrl}/icon-plus.png');
}
.icon_addElement:hover {
	background-image: url('${guiIconsUrl}/icon-plus.hover.png');
}


.icon_removeElement {
	background-image: url('${guiIconsUrl}/icon-minus.png');
}
.icon_removeElement:hover {
	background-image: url('${guiIconsUrl}/icon-minus.hover.png');
}
.icon_deleteElement {
	background-image: url('${guiIconsUrl}/icon-x.png');
}
.icon_deleteElement:hover {
	background-image: url('${guiIconsUrl}/icon-x.hover.png');
}
.icon_maximize {
	background-image: url('${guiIconsUrl}/icon-maximize.png');
}
.icon_maximize:hover {
	background-image: url('${guiIconsUrl}/icon-maximize.hover.png');
}
.icon_minimize {
	background-image: url('${guiIconsUrl}/icon-minimize.png');
}
.icon_minimize:hover {
	background-image: url('${guiIconsUrl}/icon-minimize.hover.png');
}
.icon_gridview {
	background-image: url('${guiIconsUrl}/icon-gridview.png');
}
.icon_gridview:hover {
	background-image: url('${guiIconsUrl}/icon-gridview.hover.png');
}
.icon_slideview {
	background-image: url('${guiIconsUrl}/icon-slideview.png');
}
.icon_slideview:hover {
	background-image: url('${guiIconsUrl}/icon-slideview.hover.png');
}
</style>
