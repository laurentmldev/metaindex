<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript">

//
// the page including this file shall declare a node (div) with id=MxGui.cards.insertspot
// marking where to inject new cards
 
var ITEMSVIEW_CARDS_INSERTSPOT_ID = "MxGui.cards.insertspot"
var ITEMSVIEW_CARDS_AREA_ID = ITEMSVIEW_CARDS_INSERTSPOT_ID;

function itemsView_cards_getItemsInsertSpot() {
	return document.getElementById(ITEMSVIEW_CARDS_INSERTSPOT_ID);	
} 

function itemsView_cards_clearItems() {
	var insertSpot = itemsView_cards_getItemsInsertSpot();
	clearNodeChildren(insertSpot);
} 
function itemsView_cards_addNewItem(objDescr) {
	var insertSpot = itemsView_cards_getItemsInsertSpot();
	let newCard=itemsView_cards_buildNewCard(objDescr);
	insertSpot.appendChild(newCard);
	return newCard;
}


function itemsView_cards_updateFieldValue(itemId,fieldName,newValue,fieldTermDesc) {
	let guiId="MxGui.card."+itemId;
	let curCard=document.getElementById(guiId);
	
	function errorCb(errMsg) { footer_showAlert(ERROR,errMsg); }
	function successCb(resp) {
		objDescr=resp.items[0];
		let newCard = itemsView_cards_buildNewCard(objDescr);
		curCard.replaceWith(newCard);
		newCard.select();
		

	}
	
	// need little time to let database updating internally new value	
	let timer = setInterval(function() {
		console.log("updating");
		clearInterval(timer); 
		ws_handlers_requestItemById(itemId,successCb,errorCb); 
	}, 1000);
	
	
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
	newCard.dbid=MxItemsView.extractId(objDescr);
	
	
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
		container.classList.remove("mx-item-visited");
		container.classList.add("mx-item-selected");
		_activeItem=newCard;					
		MxGuiDetails.populate(newCard);		
		MxGuiPerspective.activateLastChosenTab();
		itemsView_saveSelectionContext();
		
	}
	newCard.deselect = function(e) {
		newCard.isSelected=false;		
		container.classList.remove("mx-item-selected");
		container.classList.add("mx-item-visited");
		if (_activeItem!=null && _activeItem.dbid==newCard.dbid) { 
			_activeItem=null;
			MxGuiDetails.clear();			
		}						 
	}
	
	newCard.onclick = function(e) {

		if (e!=null) {
			e.stopPropagation();
			e.preventDefault();
		}
		if (newCard.isSelected) { 
			newCard.deselect(e);
			scrollTo(anchor.name);	
			if (_lastActiveItemId==newCard.dbid) {
				_lastActiveItemId=null;
			}
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
	let cardsInsertSpot = itemsView_cards_getItemsInsertSpot(); 
	for (var curCard=cardsInsertSpot.firstChild;curCard!==null;curCard=curCard.nextElementSibling) {		
		if (typeof(curCard)!='object') { continue; }
		if (curCard.isSelected) { curCard.deselect(); }
	}
} 

function itemsView_cards_getNbItemsInView() {
	let count=0;
	var cardsInsertSpot = itemsView_cards_getItemsInsertSpot(); 
	for (var curCard=cardsInsertSpot.firstChild;curCard!==null;curCard=curCard.nextElementSibling) {		
		if (typeof(curCard)!='object') { continue; }
		count++;
	}
	return count;
}


function itemsView_cards_selectNext() {
	let nextCard=null;
	if (itemsView_getActiveItem()==null) { 
		nextCard=itemsView_cards_getItemsInsertSpot().getElementsByClassName('card')[0];
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
	else {
		let nextCard=itemsView_getActiveItem().previousElementSibling;
		if (nextCard==null) { 
			nextCard=itemsView_getActiveItem()
				.parentNode.getElementsByClassName('card')[itemsView_getActiveItem()
				.parentNode.getElementsByClassName('card').length-1]; 
		}
		itemsView_deselectAll();
		nextCard.select();
	}
	 
}


</script>

      <div id="MxGui._templates_.card" class="card mb-4 mx-card" style="display:none;" >
		<a class="_anchor_ anchor_middle" name="" ></a>
       	 <img src="" alt="" class="card-img-top _img_" style="display:none;min-height:7rem;max-height:80%">
       	 <span class="_no_img_" style="display:none;min-height:7rem"></span>
       	 
         <div id="_container_id_" class="card-img-overlay py-3 d-flex flex-row align-items-top justify-content-between mx-card-darker-bg _container_" 
          	style="border-radius:2px;" >
           <h6 class="m-0 small font-weight-bold _name_">_name_</h6>           
         </div>
         <div class="card-footer text-center small _footer_" style="font-size:0.4rem;" >_id_</div>                
       </div><!-- /.card -->   

<!-- ###### -->      
