<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

    <script type="text/javascript">
  		
    	
      function ElementSummary(jsonElementSummary) {
    			this.name=jsonElementSummary.elementName;
        		this.comment=jsonElementSummary.elementComment;
        		this.elementId=jsonElementSummary.elementId;
        		this.thumbnailUrl=jsonElementSummary.thumbnailUrl;  
        		this.searchText=jsonElementSummary.searchText;
        		this.index=-1;
        		this.isStatic=jsonElementSummary.static;
        		this.isTemplate=jsonElementSummary.template;
        		this.isTemplated=jsonElementSummary.templated;
        		this.templateLoadError=jsonElementSummary.templateLoadError;
        		this.isSelected=jsonElementSummary.selected;
      };
      
      ElementSummary.prototype.hasThumbnail = function() {
    	  return this.thumbnailUrl != "";    	  
      }
      ElementSummary.prototype.addInCatalogSearch = function() {
	      						
		  return "<option value=\""+this.elementId+"\" >"+this.searchText+"</option>";
	  }
	  
      ElementSummary.prototype.addInCatalogContents = function() { 
    	  
    	  htmlStr="\n<table ><tr><td>";
    	  
	    	  htmlStr+= "\n  <fieldset id=\"workzone.catalogcontents."+this.elementId+"\" ";
	    	 if (this.hasThumbnail()) { htmlStr+="\n	title=\""+this.name+"\" "; }
	    	  else { htmlStr+="\n	title=\""+this.comment+"\" "; }
	    	 
	    	  // classes 
	    	  htmlStr+="\n	class=\"  ";
	    	  htmlStr+="slideItemContent "; 
	    	  if (!this.isStatic) { htmlStr+="dynamicSlideItem "; }	
	    	  if (this.isTemplate) { htmlStr+="templateSlideItem "; }	
	    	  if (this.templateLoadError) { htmlStr+="errorTemplateSlideItem "; }
	    	  if (this.isSelected && ! this.templateLoadError) { htmlStr+="selectedSlideItem "; }
	    	  if (this.isSelected && this.templateLoadError) { htmlStr+="selectedErrorTemplateSlideItem "; }
	    	  if (this.isSelected && this.isTemplate) { htmlStr+="selectedTemplateSlideItem "; }	
	          htmlStr+="\" ";
	          
	          htmlStr+="\n	onclick=\""
      	 		+"event.stopPropagation();"
      	 		+"if (event.shiftKey && (event.ctrlKey||event.metaKey)) {"
      	 			+"selectRange(lastSelectedPos,"+this.index+");"
      	 		+"}"
      	 		+"else if (event.ctrlKey||event.metaKey) {"				        	 						
      	 		 +"switchMultiselection("+this.elementId+");"
      	 		+"}"
      	 		+"else if (event.shiftKey) {"
      	 			+"selectNewRange(lastSelectedPos,"+this.index+");"
      	 		+"}"
      	 		+"else {" 
					+"document.getElementById('workzone.chooseelement.form.elementId').setAttribute('value','"+this.elementId+"');"
					+"document.getElementById('workzone.chooseelement.form').submit();"
				+"}"
				+"lastSelectedPos="+this.index+"\" ";

					htmlStr+="\n	onmouseover=\"document.getElementById('workzone.catalogcontents.icons."+this.elementId+"').style.display='block';\" ";
					htmlStr+="\n	onmouseout=\"document.getElementById('workzone.catalogcontents.icons."+this.elementId+"').style.display='none';\" ";		
					htmlStr+="\n	draggable=\"true\" ";
					htmlStr+="\n	ondragstart=\"event.stopPropagation();handleDragStartElement('"+this.elementId+"','"+this.name+"',this,event);\" ";
					htmlStr+="\n	ondragend=\"handleDragEndElement(this,event);\" ";
			
	  		  	htmlStr+=">\n";
	  		  
	  		  // this space is used here to force the height of the legend so that it does not move 
				//	when we mouse over
					 htmlStr+="\n <legend>&nbsp";
						 htmlStr+="\n  <span id=\"workzone.catalogcontents.icons."+this.elementId+"\" >";
						 htmlStr+="\n	<a  href=\"#\" id=\"workzone.catalogcontents.icon.addElement\" ";
						 htmlStr+="\n		title=\"<s:text name="workzone.icon.addElement" /> <s:property value="selectedCommunity.vocabulary.catalogTraduction" />\""; 
						 htmlStr+="\n		class=\"tinyicon icon_addElement\" ";
						 htmlStr+="\n		onclick=\"event.stopPropagation();"
						 +"		 document.getElementById('workzone.addStaticElement.elementName').innerHTML='"+this.name+"';"
						 +"		 document.getElementById('workzone.addStaticElement.form.elementId').value='"+this.elementId+"';"
						 +"		 document.getElementById('workzone.addStaticElement.form.modal').style.display='table';\">"
						 +"\n	</a>"
					 						 
					 						 
			 			//we can only remove from catalog static elements 
			 			
			 			if (this.isStatic) {
				 			htmlStr+="\n	<a  href=\"#\" id=\"workzone.catalogcontents.icon.removeElement."+this.elementId+"\" "; 
				 			htmlStr+="\n		title=\"<s:text name="workzone.icon.removeElement" /> <s:property value="selectedCommunity.vocabulary.catalogTraduction" />\" " ;
				 			htmlStr+="\n		class=\"tinyicon icon_removeElement\" ";
				 			htmlStr+="\n		onclick=\"event.stopPropagation();"
				 			+"		 document.getElementById('workzone.removeStaticElement.elementName').innerHTML='"+this.name+"';"
				 			+"	 document.getElementById('workzone.removeStaticElement.form.elementId').value='"+this.elementId+"';"
				 			+"	 document.getElementById('workzone.removeStaticElement.catalogName').innerHTML='<s:property value="selectedCommunity.selectedCatalog.name"/>';"
				 			+"	 document.getElementById('workzone.removeStaticElement.form.catalogId').value='<s:property value="selectedCommunity.selectedCatalog.catalogId"/>';"
				 			+"	 document.getElementById('workzone.removeStaticElement.form.modal').style.display='table';\">"
				 			+"\n	</a>";
			 			}
		 												        		
		 				htmlStr+="\n	<a  href=\"#\" ";  
		 				htmlStr+="\n		title=\"<s:text name="workzone.icon.deleteElement" /> <s:property value="selectedCommunity.vocabulary.elementTraduction" />\" "; 
		 				htmlStr+="\n		class=\"tinyicon icon_deleteElement\" ";
		 				htmlStr+="\n		onclick=\"	event.stopPropagation();"
		 				+"	document.getElementById('workzone.deleteElement.form.formElementId').value='"+this.elementId+"';"
		 				+"	document.getElementById('workzone.deleteElement.form.displayElementId').innerHTML='"+this.elementId+"';"
		 				+"	document.getElementById('workzone.deleteElement.form.elementname').innerHTML='"+this.name+"';"
		 				+"	document.getElementById('workzone.deleteElement.form.elementcomment').innerHTML='"+this.comment+"';"									 							
		 				+"	document.getElementById('workzone.deleteElement.form.modal').style.display='table';\">"
		 				+"\n	</a>"
		 							 					 				
		 				htmlStr+="\n  </span>"
			 		htmlStr+="\n </legend>" 
	  		  	// This is needed to allow a Drag start on the empty part of the item -->
			 					 		
			 		htmlStr+="\n<div style=\"height:100%;\" >";
			 		 			 		
			 		if (this.thumbnailUrl != "") {			 			
		  		    	htmlStr+="\n	<img src=\""+this.thumbnailUrl+"\" class=\"catalogThumbnail\""
							+"\n	title=\""+this.name+"\""
							+"\n	alt=\""+this.name+"\""
							+"\n	onclick=\"if (!event.shiftKey && !event.ctrlKey && !event.metaKey) {"
										+"\n			document.getElementById('workzone.chooseelement.form.elementId').setAttribute('value','"+this.elementId+"');"
									 	+"\n			document.getElementById('workzone.chooseelement.form').submit();"
							+"\n		 }\""
		
							+"\n	/>";							
		  		    } 
		  		    else {
		  		    	htmlStr+="<div style=\"height:80%;align-items: center;justify-content: center;\"  ";
			  		    
			  		  	htmlStr+="\n	class=\"clickable\" "
								+"\n	title=\""+this.comment+"\" "  
								+"\n	onclick=\"if (!event.shiftKey && !event.ctrlKey && !event.metaKey) {"
											+"document.getElementById('workzone.chooseelement.form.elementId').setAttribute('value','"+this.elementId+"');"
											+"document.getElementById('workzone.chooseelement.form').submit();"
										 +"}\" "	
									+">";
			  			htmlStr+=this.name;
			  			htmlStr+="\n</div>";
				  		  	
		  		    	}
			 	htmlStr+="</div>";
			 		
		  		  
		  		  htmlStr+="<span class=\"comment slideItemElementId\">"+this.elementId+"</span>";
		  		if (this.isTemplated) {
					htmlStr+="<span class=\"slideItemTemplateAnchor\"><s:text name="workzone.slideitem.templateAnchorText"/></span>";
		  		}
		  		
		  		
					
				
	  		  htmlStr+="\n</fieldset>";
    	  htmlStr+="\n</td></tr></table>\n"
    	  //console.log("ADDING : "+htmlStr);
    	  return htmlStr;	
	  };
    		
      if (typeof exports !== "undefined" && exports !== null) {
	    exports.ElementSummary = ElementSummary;
	  }

	  if (typeof window !== "undefined" && window !== null) {
	    window.ElementSummary = ElementSummary;
	  } else if (!exports) {
	    self.ElementSummary = ElementSummary;
	  }
    </script>
