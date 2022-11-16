<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript">

//
// the page including this file shall declare a node (div) with id=MxGui.cards.insertspot
// marking where to inject new cards
 
var ITEMSVIEW_CARDS_INSERTSPOT_ID = "MxGui.cards.insertspot"

function itemsView_cards_clearItems() {
	var insertSpot = document.getElementById(ITEMSVIEW_CARDS_INSERTSPOT_ID);
	clearNodeChildren(insertSpot);
} 
function itemsView_cards_addNewItem(objDescr) {
	var insertSpot = document.getElementById(ITEMSVIEW_CARDS_INSERTSPOT_ID);
	let newCard=itemsView_cards_buildNewCard(objDescr);
	insertSpot.appendChild(newCard);
	return newCard;
}


// objDescr : shall containing following data :
//	objDescr.id
//	objDescr.name
//	objDescr.thumbnailUrl (optional)
function itemsView_cards_buildNewCard(objDescr) {
	
	
	var guiId="MxGui.card."+MxItemsView.extractId(objDescr);
	var newCard=document.getElementById("MxGui._templates_.card").cloneNode(true);
	newCard.descr=objDescr;
	newCard.id=guiId;
	
	
	// name
	let name = newCard.querySelector("._name_");
	name.innerHTML=MxItemsView.extractName(objDescr);
	newCard.getName=function() { return name.innerHTML; };// useful to get name associated to this card
	
	// anchor
	let anchor = newCard.querySelector("._anchor_");
	anchor.name="anchor-"+MxItemsView.extractId(objDescr);
	newCard.anchorName=anchor.name;
	
	// container
	let container = newCard.querySelector("._container_");
	container.id=guiId+".container";
	
	imgUrl=itemsView_extractThumbnailUrl(objDescr);
	
	if (imgUrl!=null && imgUrl.toLowerCase().match(/\.(jpeg|jpg|gif|png|tif|webp)$/)!=null) {
		let img = newCard.querySelector("._img_");
		img.src=imgUrl;
		img.style.display="block";		
	} else {
		let noimg = newCard.querySelector("._no_img_");
		noimg.style.display="block";
	}
	// footer
	let footer = newCard.querySelector("._footer_");
	footer.innerHTML=MxItemsView.extractId(objDescr);
	
	// onmouseover
	newCard.onmouseover = function(e) {
		container.classList.add('mx-item-hover');
	}
	newCard.onmouseout = function(e) {
		container.classList.remove('mx-item-hover');
	}
	
	newCard.isSelected=false;	
	newCard.select = function(e) {
		newCard.isSelected=true;		
		container.classList.add("mx-item-selected");
		container.classList.add("mx-item-visited");
		_selectedItemsMapById[MxItemsView.extractId(newCard.descr)]=newCard;
		_activeItem=newCard;				
		MxGuiDetails.populate(newCard);		
		MxGuiPerspective.activateLastChosenTab();
		
	}
	newCard.deselect = function(e) {
		newCard.isSelected=false;		
		container.classList.remove("mx-item-selected");
		MxGuiDetails.clear();
		_selectedItemsMapById[MxItemsView.extractId(newCard.descr)]=null;
		_activeItem=null;
		
		 
	}
	
	newCard.onclick = function(e) {

		if (e!=null) {
			e.stopPropagation();
			e.preventDefault();
		}
		if (newCard.isSelected) { 
			newCard.deselect(e);
			scrollTo(anchor.name);			
		}
		else {
			MxGuiDetails.clear();
			itemsView_deselectAll();
			newCard.select(e); 	
			scrollTo("page-top");
		}
	}

	
	newCard.style.display='block';
	return newCard;	
};

function itemsView_cards_deselectAll() {
	let cardsInsertSpot = document.getElementById(ITEMSVIEW_CARDS_INSERTSPOT_ID); 
	for (var curCard=cardsInsertSpot.firstChild;curCard!==null;curCard=curCard.nextElementSibling) {		
		if (typeof(curCard)!='object') { continue; }
		if (curCard.isSelected) { curCard.deselect(); }
	}
} 

function itemsView_cards_getNbItemsInView() {
	let count=0;
	var cardsInsertSpot = document.getElementById(ITEMSVIEW_CARDS_INSERTSPOT_ID); 
	for (var curCard=cardsInsertSpot.firstChild;curCard!==null;curCard=curCard.nextElementSibling) {		
		if (typeof(curCard)!='object') { continue; }
		count++;
	}
	return count;
}


function itemsView_cards_selectNext() {
	let nextCard=null;
	if (itemsView_getActiveItem()==null) { 
		nextCard=document.getElementById(ITEMSVIEW_CARDS_INSERTSPOT_ID).getElementsByClassName('card')[0];
	} else {
		nextCard=itemsView_getActiveItem().nextElementSibling;
		if (nextCard==null) { 
			nextCard=itemsView_getActiveItem().parentNode.getElementsByClassName('card')[0]; 
		}
	}
	itemsView_deselectAll();
	nextCard.select();
}
function itemsView_cards_selectPrevious() {
	if (itemsView_getActiveItem()==null) { return; }
	let nextCard=itemsView_getActiveItem().previousElementSibling;
	if (nextCard==null) { 
		nextCard=itemsView_getActiveItem().parentNode.getElementsByClassName('card')[itemsView_getActiveItem().parentNode.getElementsByClassName('card').length-1]; 
	}
	itemsView_deselectAll();
	nextCard.select(); 
}


</script>

      <div id="MxGui._templates_.card" class="card mb-4 mx-card" style="display:none;" >
		<a class="_anchor_ anchor_middle" name="" ></a>
       	 <img src="" alt="" class="card-img-top _img_" style="display:none;min-height:7rem">
       	 <span class="_no_img_" style="display:none;min-height:7rem"></span>
       	 
         <div id="_container_id_" class="card-img-overlay py-3 d-flex flex-row align-items-top justify-content-between mx-card-darker-bg _container_" 
          	style="border-radius:2px;" >
           <h6 class="m-0 small font-weight-bold _name_">_name_</h6>           
         </div>
         <div class="card-footer text-center small _footer_" style="font-size:0.5rem;" >_id_</div>                
       </div><!-- /.card -->   

<!-- ###### -->      
