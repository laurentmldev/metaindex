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
        		this.isTemplate=jsonElementSummary.template;
        		this.isTemplated=jsonElementSummary.templated;
        		this.templateLoadError=jsonElementSummary.templateLoadError;
        		this.isSelected=jsonElementSummary.selected;
      };
      
      ElementSummary.prototype.hasThumbnail = function() {
    	  return this.thumbnailUrl != "";    	  
      }
    
      ElementSummary.prototype.makeThumbnailNode = function(isStatic) { 
    	    	 
    	  
    	// in event handlers, 'this' is overriden by the 'this' of the function
    	var curElementId=this.elementId;
    	var curElementName=this.name;
    	var curElementComment=this.comment;
    	
		var newNode=document.getElementById("_thumbnailsContents.template.elementSummary_").cloneNode(true);
		newNode.id="thumbnailsContents.element.table."+curElementId;
		newNode.style.display='table-cell';
		
		// fieldset
		var fieldset = newNode.querySelector("._fieldset_");
		fieldset.id+=curElementId;
		fieldset.title=curElementComment;		
		if (this.hasThumbnail()) { fieldset.title=curElementName; }
		fieldset.classList.add("slideItemContent");			
		if (this.isTemplate) { fieldset.classList.add("templateSlideItem"); }	
		if (this.templateLoadError) { fieldset.classList.add("errorTemplateSlideItem"); }
		if (isStatic==false) { fieldset.classList.add("dynamicSlideItem"); }
		
		if (curElementData!=null && curElementId==curElementData.elementId) {
			if (this.templateLoadError) { fieldset.classList.add("selectedErrorTemplateSlideItem"); }
			else { fieldset.classList.add("selectedSlideItem"); }
			if (this.isTemplate) { fieldset.classList.add("selectedTemplateSlideItem"); }	
		}
		
		
		fieldset.onclick=function(event) {			
			event.stopPropagation();
			var elData = elementsSummaryById[curElementId];
			var curElIndex=elData.index;
  	 		if (event.shiftKey && (event.ctrlKey||event.metaKey)) {
  	 			selectRange(lastSelectedPos,curElIndex);
  	 		}
  	 		else if (event.ctrlKey||event.metaKey) {
  	 		 	switchMultiselection(curElementId);
  	 		}
  	 		else if (event.shiftKey) {
  	 			selectNewRange(lastSelectedPos,curElIndex);
  	 		}
  	 		else { 
  	 			this.isSelected=false;
				changeElement(curElementId);
				this.classList.remove('selectedSlideItem');
				this.classList.remove('selectedErrorTemplateSlideItem');
				this.classList.remove('selectedTemplateSlideItem');
			}
			lastSelectedPos=curElIndex;	
		};
		
		fieldset.onmouseover=function(e) { document.getElementById('workzone.thumbnails.icons.'+curElementId).style.display='block'; }
		fieldset.onmouseout=function(e) { document.getElementById('workzone.thumbnails.icons.'+curElementId).style.display='none'; }
		fieldset.ondragstart=function(event) { 			
			event.stopPropagation();
			handleDragStartElement(curElementId,curElementName,this,event); 
		}
		fieldset.ondragend=function(event) { handleDragEndElement(this,event); }
		
		// legend
		var legendspan = fieldset.querySelector("._legend_");
		legendspan.id+=curElementId;
		
		
		// to be activated correspondending to current catalog selection
		// icon add
		var iconAddElement = legendspan.querySelector("._add_element_to_catalog_");
		iconAddElement.id+=curElementId;
		iconAddElement.onclick=function(event) {
			event.stopPropagation();
			document.getElementById('workzone.addStaticElement.elementName').innerHTML=curElementName;
			document.getElementById('workzone.addStaticElement.form.elementId').value=curElementId;
			document.getElementById('workzone.addStaticElement.form.modal').style.display='table';
		}
		
		if (isStatic==true) {
		// icon remove
		//we can only remove from catalog static elements 
			var iconRemoveElement = legendspan.querySelector("._remove_element_from_catalog_");
			
			iconRemoveElement.id+=curElementId;
			iconRemoveElement.style.display='block';
			iconRemoveElement.onclick=function(event) {
				event.stopPropagation();
	 			document.getElementById('workzone.removeStaticElement.elementName').innerHTML=curElementName;
	 			document.getElementById('workzone.removeStaticElement.form.elementId').value=curElementId;
	 			document.getElementById('workzone.removeStaticElement.catalogName').innerHTML='<s:property value="selectedCommunity.selectedCatalog.name"/>';
	 			document.getElementById('workzone.removeStaticElement.form.catalogId').value=<s:property value="selectedCommunity.selectedCatalog.catalogId"/>;
	 			document.getElementById('workzone.removeStaticElement.form.modal').style.display='table';
			}
		}
       
		
		// icon delete
		var iconDeleteElement = legendspan.querySelector("._delete_element_");
		iconDeleteElement.id+=curElementId;
		iconDeleteElement.style.display='block';
		iconDeleteElement.onclick=function(event) {
			event.stopPropagation();
			document.getElementById('workzone.deleteElement.form.formElementId').value=curElementId;
			document.getElementById('workzone.deleteElement.form.displayElementId').innerHTML=curElementId;
			document.getElementById('workzone.deleteElement.form.elementname').innerHTML=curElementName;
			document.getElementById('workzone.deleteElement.form.elementcomment').innerHTML=curElementComment;									 							
			document.getElementById('workzone.deleteElement.form.modal').style.display='table';
		}
				 		

		// thumbnail | name
		if (this.thumbnailUrl != "") {
			var thumbnail = fieldset.querySelector("._thumbnail_");
			thumbnail.style.display='block';
			thumbnail.src=this.thumbnailUrl;
			thumbnail.title=curElementName;
			thumbnail.alt=curElementName;
			thumbnail.onclick=function(event) {
				if (!event.shiftKey && !event.ctrlKey && !event.metaKey) {
					changeElement(curElementId);					
				}
			}
		} else {
			var text = fieldset.querySelector("._text_");
			text.style.display='block';
			text.title=curElementName;
			text.onclick=function(event) {				
				if (!event.shiftKey && !event.ctrlKey && !event.metaKey) {
					changeElement(curElementId);					
				}
			}
			text.innerHTML=curElementName;
		}
		
		// elementId
		var elementId=fieldset.querySelector("._elementId_");
		elementId.innerHTML=curElementId;
		
		// isTemplated mark
		if (this.isTemplated) {
			var isTemplated=fieldset.querySelector("._isTemplated_");
			isTemplated.style.display='block;';
		}
		  		  
    	  return newNode;	
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

    <table id="_thumbnailsContents.template.elementSummary_" style="display:none" >
    	<tr><td>
    	  
    		<fieldset id="workzone.thumbnails." class="_fieldset_" draggable="true" >
    			<legend>&nbsp;
    				<span id="workzone.thumbnails.icons." class="_legend_" >
						 <a  href="#" id="workzone.thumbnails.icon.addElement."
						 	title="<s:text name="workzone.icon.addElement" /> <s:property value="selectedCommunity.vocabulary.catalogTraduction" />" 
						 	class="_add_element_to_catalog_ tinyicon icon_addElement"></a>
						 	
						  <a  href="#" id="workzone.thumbnails.icon.removeElement." style="display:none"
						 	title="<s:text name="workzone.icon.removeElement" /> <s:property value="selectedCommunity.vocabulary.catalogTraduction" />" 
						 	class="_remove_element_from_catalog_ tinyicon icon_removeElement"></a>
						 	
						  <a  href="#" id="workzone.thumbnails.icon.deleteElement." style="display:none"
						 	title="<s:text name="workzone.icon.deleteElement" /> <s:property value="selectedCommunity.vocabulary.catalogTraduction" />" 
						 	class="_delete_element_ tinyicon icon_deleteElement"></a>
					</span>
				</legend>
				<center>
				<div style="height:100%;" >
			 		 		
			 		<img src="" style="display:none" class="catalogThumbnail _thumbnail_"  title=""	/>							
		  		     
		  		    
		  		    <div style="height:80%;align-items: center;justify-content: center;"
			  		    	class="clickable _text_" title="comment" >
					</div>
			  			
			  				
			 	</div>
			 	</center>
			 	<span class="comment slideItemElementId _elementId_"></span>
			 	<span class="slideItemTemplateAnchor _isTemplated_" style="display:none"><s:text name="workzone.slideitem.templateAnchorText"/></span>
    		</fieldset>
	    </td></tr>	  
    </table>
