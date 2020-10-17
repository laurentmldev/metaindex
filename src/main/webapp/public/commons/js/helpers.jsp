<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  


	<script>
	
  // from https://stackoverflow.com/questions/5997450/append-to-url-and-refresh-page
	function URL_add_parameter(url, param, value){
	    var hash       = {};
	    var parser     = document.createElement('a');

	    parser.href    = url;

	    var parameters = parser.search.split(/\?|&/);

	    for(var i=0; i < parameters.length; i++) {
	        if(!parameters[i])
	            continue;

	        var ary      = parameters[i].split('=');
	        hash[ary[0]] = ary[1];
	    }

	    hash[param] = value;

	    var list = [];  
	    Object.keys(hash).forEach(function (key) {
	        list.push(key + '=' + hash[key]);
	    });

	    parser.search = '?' + list.join('&');
	    return parser.href;
	}
	</script>
	
