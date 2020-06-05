<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:url value="/" var="mxurl"/>

<script type="text/javascript">

//
// the page including this file shall declare a node (div) with id=MxGui.cards.insertspot
// marking where to inject new cards
 
var CARDS_INSERTSPOT_ID = "MxGui.cards.insertspot"
var _activeCard = null;
var _selectedCardsMapById = [];


function card_ClearCards() {
	var insertSpot = document.getElementById(CARDS_INSERTSPOT_ID);
	clearNodeChildren(insertSpot);
	_activeCard = null;
	_selectedCardsMapById = [];
} 
function card_addNewCard(objDescr) {
	var insertSpot = document.getElementById(CARDS_INSERTSPOT_ID);
	let newCard=card_buildNewCard(objDescr);
	insertSpot.appendChild(newCard);
	return newCard;
}

function card_extractId(objDescr) { return objDescr.id; }
function card_extractName(objDescr) { return objDescr.name; }
function card_extractThumbnailUrl(objDescr)  { 
	let urlStr=objDescr.thumbnailUrl;
	if (objDescr.itemsUrlPrefix!=null && objDescr.itemsUrlPrefix!="" && !urlStr.startsWith('http')) {
		urlStr=objDescr.itemsUrlPrefix+"/"+urlStr;
	}
	return urlStr; 
}

// objDescr : shall containing following data :
//	objDescr.id
//	objDescr.name
//	objDescr.thumbnailUrl (optional)
function card_buildNewCard(objDescr) {
	
	
	var guiId="MxGui.card."+MxGuiCards.extractId(objDescr);
	var newCard=document.getElementById("MxGui._templates_.card").cloneNode(true);
	newCard.descr=objDescr;
	newCard.id=guiId;
	
	
	// name
	let name = newCard.querySelector("._name_");
	name.innerHTML=MxGuiCards.extractName(objDescr);
	
	// anchor
	let anchor = newCard.querySelector("._anchor_");
	anchor.name="anchor-"+MxGuiCards.extractId(objDescr);
	newCard.anchorName=anchor.name;
	
	// container
	let container = newCard.querySelector("._container_");
	container.id=guiId+".container";
	
	imgUrl=card_extractThumbnailUrl(objDescr);
	
	if (imgUrl!=null && imgUrl.match(/\.(jpeg|jpg|gif|png|tif)$/)!=null) {
		let img = newCard.querySelector("._img_");
		img.src=imgUrl;
		img.style.display="block";		
	} else {
		let noimg = newCard.querySelector("._no_img_");
		noimg.style.display="block";
	}
	// footer
	let footer = newCard.querySelector("._footer_");
	footer.innerHTML=MxGuiCards.extractId(objDescr);
	
	// onmouseover
	newCard.onmouseover = function(e) {
		container.classList.add('mx-card-lighter-bg');
	}
	newCard.onmouseout = function(e) {
		container.classList.remove('mx-card-lighter-bg');
	}
	
	newCard.isSelected=false;	
	newCard.select = function(e) {
		newCard.isSelected=true;		
		container.classList.add("mx-card-selected");
		container.classList.remove("mx-card-darker-bg");
		container.classList.add("mx-card-lighter-bg");		
		_selectedCardsMapById[MxGuiCards.extractId(newCard.descr)]=newCard;
		_activeCard=newCard;				
		MxGuiDetails.populate(newCard);									
		
	}
	newCard.deselect = function(e) {
		newCard.isSelected=false;
		container.classList.remove("mx-card-selected");
		container.classList.add("mx-card-darker-bg");	
		MxGuiDetails.clear();
		_selectedCardsMapById[MxGuiCards.extractId(newCard.descr)]=null;
		_activeCard=null;
		
		// mark cark as lighter
		container.classList.add("mx-card-lighter-bg");
		 
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
			card_deselectAll();
			newCard.select(e); 	
			scrollTo("page-top");
		}
	}

	
	newCard.style.display='block';
	return newCard;	
};

function card_deselectAll() {
	let cardsInsertSpot = document.getElementById(CARDS_INSERTSPOT_ID); 
	for (var curCard=cardsInsertSpot.firstChild;curCard!==null;curCard=curCard.nextElementSibling) {		
		if (typeof(curCard)!='object') { continue; }
		if (curCard.isSelected) { curCard.deselect(); }
	}
} 

function card_getNbCards() {
	let count=0;
	var cardsInsertSpot = document.getElementById(CARDS_INSERTSPOT_ID); 
	for (var curCard=cardsInsertSpot.firstChild;curCard!==null;curCard=curCard.nextElementSibling) {		
		if (typeof(curCard)!='object') { continue; }
		count++;
	}
	return count;
}

function card_getActiveCard() { return _activeCard; }

function card_selectNext() {
	let nextCard=null;
	if (card_getActiveCard()==null) { 
		nextCard=document.getElementById(CARDS_INSERTSPOT_ID).getElementsByClassName('card')[0];
	} else {
		nextCard=card_getActiveCard().nextElementSibling;
		if (nextCard==null) { 
			nextCard=card_getActiveCard().parentNode.getElementsByClassName('card')[0]; 
		}
	}
	card_deselectAll();
	nextCard.select();
}
function card_selectPrevious() {
	if (card_getActiveCard()==null) { return; }
	let nextCard=card_getActiveCard().previousElementSibling;
	if (nextCard==null) { 
		nextCard=card_getActiveCard().parentNode.getElementsByClassName('card')[card_getActiveCard().parentNode.getElementsByClassName('card').length-1]; 
	}
	card_deselectAll();
	nextCard.select(); 
}

// Public Interface
var MxGuiCards={}
MxGuiCards.deselectAll=card_deselectAll;
// expect structure with at least fields 'id','name','thumbnailUrl', 
MxGuiCards.addNewCard=card_addNewCard;
MxGuiCards.clearCards=card_ClearCards;
MxGuiCards.getActiveCard=card_getActiveCard;
MxGuiCards.selectNext=card_selectNext;
MxGuiCards.selectPrevious=card_selectPrevious;
MxGuiCards.extractName=card_extractName;
MxGuiCards.extractId=card_extractId;
MxGuiCards.getNbCards=card_getNbCards;

</script>

      <div id="MxGui._templates_.card" class="card mb-4" style="display:none;" >
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
