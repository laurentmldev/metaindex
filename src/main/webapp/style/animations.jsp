<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>

<style type="text/css" >

@keyframes blink {
  50% {
    opacity: 0.8;
  }
}
@-webkit-keyframes blink {
  50% {
    opacity: 0.8;
  }
}
.blink {
  animation: blink 1s step-start 0s infinite;
  -webkit-animation: blink 1s step-start 0s infinite;
}


.fadeout {
  visibility: hidden;
  opacity: 0;
  transition: visibility 0s 0.2s, opacity 0.2s linear;
}

</style>
