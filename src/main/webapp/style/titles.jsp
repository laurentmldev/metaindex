<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>

<style type="text/css" >



.negative {
	text-shadow: -1px -1px 0px rgba(255,255,255,0.3), 
				 1px 1px 0px rgba(0,0,0,0.8);
	opacity:0.4; 
	padding:10px;
	margin:2px;
}

.positive {
text-shadow: 2px 3px 3px #292929;
color: 1px lightgrey;
}

.volumeText {
text-shadow: 	0 1px 0 #ccc,
				0 2px 0 #c9c9c9,
				0 3px 0 #bbb,
				0 6px 1px rgba(0,0,0,.1),
				0 0 5px rgba(0,0,0,.1),
				0 1px 3px rgba(0,0,0,.3),
				0 3px 5px rgba(0,0,0,.2),
				0 5px 10px rgba(0,0,0,.25),
				0 10px 10px rgba(0,0,0,.2),
				0 15px 15px rgba(0,0,0,.15);
}


.fieldtitle {
font-size:1.0em;
}
    
</style>
