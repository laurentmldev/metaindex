<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

    <script type="text/javascript">
  	
    	
      function CatalogSummary(jsonCatalogSummary) {
    			this.name=jsonCatalogSummary.catalogName;
        		this.comment=jsonCatalogSummary.catalogComment;
        		this.catalogId=jsonCatalogSummary.catalogId;
        		this.nbElements=jsonCatalogSummary.catalogNbElements;
        		this.dynamicElementsFilter=jsonCatalogSummary.dynamicElementsFilter;       
        		this.selectedElementId=jsonCatalogSummary.selectedElementId;
        		this.selected=jsonCatalogSummary.selected;
        		this.staticElementsIds=jsonCatalogSummary.staticElementsIds;
      };
      
      
      CatalogSummary.prototype.makeThumbnailNode = function() { 
    	  						
		  //document.getElementById(thumbnailsContentsContainerId).innerHTML+="<option value=\""+this.elementId+"\" >"+this.searchText+"</option>";
	  };
    		
      if (typeof exports !== "undefined" && exports !== null) {
	    exports.CatalogSummary = CatalogSummary;
	  }
	  if (typeof window !== "undefined" && window !== null) {
	    window.CatalogSummary = CatalogSummary;
	  } else if (!exports) {
	    self.CatalogSummary = CatalogSummary;
	  }
    </script>
