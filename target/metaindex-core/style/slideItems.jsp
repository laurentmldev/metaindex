<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>

<style type="text/css" >

.listitem {
padding:3px;
}
.selectedItem {
  	border-radius: 6px;
  	border : 1px solid ;
 }

.cell {
vertical-align: middle;

}

.slideItemsBigContainer {
position:relative;
text-align: center;
vertical-align:middle;
overflow: auto;
float:left;
max-height:500px;
border:none;

}


.slideItemsBigContainer table {
	float:left;
	vertical-align:middle;

}


.slideItemsContainer {
position:relative;
max-width:600px;
max-height:120px;
text-align: center;
vertical-align:middle;
overflow: auto;
display:table-cell;


}

.slideItemsContainer table {
	clear:both;
	display:table-cell;
	vertical-align:middle;

	
}
.slideItemContent {
	position: static;
	border:none;
	max-width:150px;
	text-align:center;
	overflow: auto;
	text-overflow : ellipsis;
	-webkit-box-shadow: 0 3px 2px rgba(0, 0, 0, 0.7);
	box-shadow: 0 3px 2px rgba(0, 0, 0, 0.7); 

}
.slideItemContent, .dropping_element {
    min-width:60px;
    height:70px;
    border-radius:5px;
}

.dropping_element {
    top:-9px;
    left:-8px;
}

.slideItem:hover,.slideItemContent:hover {
cursor: pointer;
}

.selectedSlideItem,.multiSelectedSlideItem {
	border:none;
}

.catalogThumbnail {
	max-width:40px;
	max-height: 50px;
	box-shadow: 2px 1px 2px rgba(0, 0, 0, 1.0);	 
}

    
</style>
