<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>



<script type="text/javascript" >
	
	thumbnailsContentsContainerId="workzone.thumbnails.container";
	
	var MAX_DISPLAY_ELEMS=20;
	var UPDATE_LOADING_THUMBNAILS_TRESHOLD=7;
	var DISPLAY_LOADPERCENT_TRESHOLD=30;
	var EXCLUDEALL_FILTER="_NONE_";
	
	var catalogws;
	
	// where did stop the previous thumbnails filtering
	// so that we can continue it from there if required
	var lastThumbnailsFilterPosition=0;
	
	var elementsSummaryById={};
	var communityNbElements=0;
	var communityNbLoadedElements=0;
	var elementsOrderedKeys = [];
	
	// the list of all elements IDs, sorted as in the GUI (alphabetically by title for example)
	var filteredThumbnailsIds = [];
	var displayedThumbnailsIds = [];

	// position of elements in the current thumbnails list, by elementid, starting from 1
	var elementsPosition=new Array();
	
	document.getElementById("workzone.thumbnails.container").minimize=function() {
		console.log("Minimizing");
		document.getElementById('workzone.thumbnails.container').classList.add('slideItemsContainer');
		document.getElementById('workzone.thumbnails.container').classList.remove('slideItemsBigContainer');
		document.getElementById('downside').classList.add('slideItemsSmallSizeContainer');
		document.getElementById('downside').classList.remove('slideItemsBigSizeContainer');
		document.getElementById('workzone.thumbnails.icon.maximize').style.display='block';
		document.getElementById('workzone.thumbnails.icon.minimize').style.display='none';
	}
	
	document.getElementById("workzone.thumbnails.container").maximize=function() {
		console.log("Maximizing");
		document.getElementById('workzone.thumbnails.container').classList.remove('slideItemsContainer');
		document.getElementById('workzone.thumbnails.container').classList.add('slideItemsBigContainer');
		document.getElementById('downside').classList.remove('slideItemsSmallSizeContainer');
		document.getElementById('downside').classList.add('slideItemsBigSizeContainer');
		document.getElementById('workzone.thumbnails.icon.maximize').style.display='none';
		document.getElementById('workzone.thumbnails.icon.minimize').style.display='block';
	}
	
	function toggleThumbnailsHide() {		
		var thumbnailsContainer = document.getElementById('downside');
		if (thumbnailsContainer.style.display=='none') { thumbnailsContainer.style.display='block'; }
		else { thumbnailsContainer.style.display='none'; }
	}
	
   	function connectCatalogContentsServer(targeturi) {
   		console.log(targeturi);
   		catalogws = new WebSocket(targeturi);
   		catalogws.onopen = function() {
               	console.log("catalog-server : connected to catalog server");
               	
               }
   		catalogws.onmessage = function(e) {
               	//addChatText("[WebSocket#onmessage] Message: '" + e.data + "'\n");
                thumbnails_handle_msg(e.data);
               }
   		catalogws.onclose = function() {
               	console.log("Catalogs server connection closed");
               }
      }

   	   // TODO
       // ws.close()
       function closeWS() {
       	catalogws.close();
        }
   	   
   	
   	function thumbnails_handle_msg(msgStr) {
   		//console.log("Received: "+msgStr);
   		msg=JSON.parse(msgStr);
   		if (msg.msgType == 'catalog-login') { 
   			catalog_send_sessionid(msg);
   			community_send_elementsSummariesRequest();   			   			
   		}
   		else if (msg.msgType == 'catalogs-list') {
   			catalogs_handle_cataloglist(msg);
   			//console.log("received catalogs list : "+msgStr)
   		}
   		else if (msg.msgType == 'catalog-summary') {
   			//console.log("### Received : "+msgStr);
   			catalogs_handle_catalogsummary(msg);   			   			
   		}
   		else if (msg.msgType == 'community-getelements_summaries') {
   			//console.log("### Received : "+msgStr);
   			communityNbElements=msg.communityNbElements ;   			
   		}
   		// add elements in the community contents list while they are loading via websockets
   		else if (msg.msgType == 'elements-summaries') {
   			//console.log("### Received "+msg.elements.length+" elements-summaries "+msgStr);
   			for (var i=0;i<msg.elements.length;i++) {
   				var curElementData = msg.elements[i];    				
   				handleElementSummary(curElementData);
   			}   				
   		}
        else(console.log('Error: received unhandled chat msg '+msg.msgType))        
   	}
   	
   	
   	function handleElementSummary(jsonMsg) {
  		
   		communityNbLoadedElements++;
		el = new ElementSummary(jsonMsg);
		elementsSummaryById[el.elementId]=el;
  			//console.log("### Adding new community element "+msg.elementId+" / "+communityNbElements);
		
		// if we received all elements data expected then we
		// hide the progress bar and 
		if (communityNbLoadedElements==communityNbElements) {
			
			//console.log("### Community elements fully loaded!");
			updateprogressbar(communityNbLoadedElements,communityNbElements, "Loaded");
			document.getElementById("thumbnails-loadprogress").style.display='none';
	   		document.getElementById("workzone.thumbnails.elementsFastSearch").style.display='block'; 
	   		elementsOrderedKeys=Object.keys(elementsSummaryById);
	   		catalog_send_catalogslistrequest();
	   		changeElement(0);   			   		
	   	} 
		// else we refresh the progress bar
		else if (communityNbLoadedElements%DISPLAY_LOADPERCENT_TRESHOLD==0) { 
			//console.log("### Updating progress bar "+Object.keys(elementsSummaryById).length+"/"+communityNbElements);
			updateprogressbar(communityNbLoadedElements,communityNbElements, "Loading");
			// update displayed number of mathing elems
			nbMatchingElemsNode=document.getElementById("workzone.thumbnails.nbMatchingElements");
			nbMatchingElemsNode.innerHTML=communityNbLoadedElements;
			if (filteredThumbnailsIds.length>1) { nbMatchingElemsNode.innerHTML+=" <s:property value="selectedCommunity.vocabulary.elementsTraduction" />";
			} else  { nbMatchingElemsNode.innerHTML+=" <s:property value="selectedCommunity.vocabulary.elementTraduction" />"; }
			
		}
		

   	}
   	function updateprogressbar(part,total,title){
   		if (title==null) { title=""; }
   		else { title = title+" : "; }
   		var value=Math.round(part*100/total);
   		if (part==null || part==0) { value=0; }
   		
   		//console.log("### Progress bar text="+text);
   		document.getElementById("thumbnails-loadprogress-bar").style.width=value+'%';
   		document.getElementById("thumbnails-loadprogress-text").innerHTML=title+value+'%';
   	}

   	function addThumbnailsContainerInitialData(insertSpot) {
   		var initialData = document.getElementById("ThumbnailsContainerInitialData").cloneNode(true);
   		initialData.id="dropping_multi_elements_parent";
   		initialData.style.display="block";
   		
		insertSpot.append(initialData);
   		
   	}
   	
   	function shouldBeThumbnailed(curEl,userSearchRegexes,catalogSearchRegexes) {
    		
   		//console.log("#### testing el "+curEl.elementId);
   		
   		// ignore templates elements, except in dedicated catalog
   		var baseElementFilter =   !curEl.isTemplate && curCatalogId!=TEMPLATES_CATALOG_ID 
			|| curEl.isTemplate && curCatalogId==TEMPLATES_CATALOG_ID;
   		
   		if (!baseElementFilter) {
   			//console.log("#### base filter Nok");
   			return false;   			
   		}
   		
   		// catalog static elements
   		matchStaticElements = false;
		staticElementsList=catalogSummaries[curCatalogId].staticElementsIds;
		for (i=0;i<staticElementsList.length;i++) {
			if (curEl.elementId==staticElementsList[i]) { matchStaticElements=true; break; } 
		}
		//console.log("	-> static match = "+matchStaticElements);
		
		var filterCatalogOK = null;
		
   		// filter from catalog searchquery only if not in hardcoded 'static' elements list
   		if (matchStaticElements) { filterCatalogOK=true; }
   		else {
	   		filterCatalogOK = catalogSearchRegexes!=EXCLUDEALL_FILTER;
	   		if (filterCatalogOK) {
		   		for (i=0;i<catalogSearchRegexes.length;i++) {
		   			var regex=catalogSearchRegexes[i];
		   			filterCatalogOK = filterCatalogOK && curEl.searchText.search(regex)!=-1;
		   		}
	   		}
	   		//console.log("	-> catalog match = "+filterCatalogOK);
   		}
   		// user filter
		var filterUserOK = userSearchRegexes!=EXCLUDEALL_FILTER;
   		if (filterUserOK) {
			for (i=0;i<userSearchRegexes.length;i++) {
	   			var regex=userSearchRegexes[i];
	   			filterUserOK = filterUserOK && curEl.searchText.search(regex)!=-1;
	   		}
   		}
   		//console.log("	-> user match = "+filterUserOK);
   		
		// global filter result
		var shouldBeThumbnailed = (filterCatalogOK || matchStaticElements) && filterUserOK;
		
		//console.log("	-> global = "+shouldBeThumbnailed);
		
		return shouldBeThumbnailed;
   	}
   	
   	function buildRegexesFromSearchQuery(searchQuery) {
   		result=[];
   		queriesList=searchQuery.split(" & ");
   		//console.log("### building regex from '"+searchQuery+"' ("+queriesList.length+")");
   		
   		for (i=0;i<queriesList.length;i++) {
   			var curPattern=queriesList[i];
   			//console.log("	: checking sub-pattern '"+curPattern+"'");
   			if (curPattern=="") {
   				//console.log("	-> EXCLUDE regex");
   				return EXCLUDEALL_FILTER; 
   			}
   			if (curPattern=="*") {
   				//console.log("	-> ignored "); 
   				continue; 
   			}
   			//console.log("	-> adding regex for "+curPattern);
	   		var curRegexp = new RegExp(curPattern,"gi");
	   		result.push(curRegexp);
   		}   
   		
   		return result;
   	}
   	
   	// clear current thumbnails list and re-populate it based on given userSearchPattern
   	function updateThumbnailsContents(userSearchPattern,clearCurrentContents) {
   		
   		if (catalogSummaries[curCatalogId]==null) {
   			console.log("catalogSummaries["+curCatalogId+"] is empty, cannot display thumbnails");
   			return;
   		}

   		// by default userSearchPattern is rather inclusive
   		// i.e. show all unless i precise what
   		if (userSearchPattern=="") { userSearchPattern="*"; }
   		
   		// remove the 'show more elements' link at the end of thumbnails 
   		loadMoreLink=document.getElementById("thumbnailsContents.template.endOfElementsList.instance");
   		if (loadMoreLink!=null) { loadMoreLink.outerHTML=""; }
   		
   		// clearCurrentContents still to be tested for displaying elements
   		// while they are loaded from WebSocket
   		if (clearCurrentContents==null) { clearCurrentContents=true; }
   		
   		var nbDynElemsNode = document.getElementById("workzone.catalogdetails.nbDynamicElements");   		
   		var nbDynElems=nbDynElemsNode.innerHTML;
   		   		
   		var nbStaticElemsNode = document.getElementById("workzone.catalogdetails.nbStaticElements");
   		var nbStaticElems=nbStaticElemsNode.innerHTML;
   		
   		//console.log("searching for "+userSearchPattern);
   		document.getElementById("thumbnails-loadprogress").style.display='block';
   		document.getElementById("workzone.thumbnails.elementsFastSearch").style.display='none';
   		updateprogressbar(0,0,"Filtering");
   		
   		// the insert spot
   		var insertSpot = document.getElementById(thumbnailsContentsContainerId);
   		
   		// clear existing list if needed (i.e. if new search)
   		if (clearCurrentContents) { 
   			insertSpot.innerHTML=""; 
   			addThumbnailsContainerInitialData(insertSpot);
   			filteredThumbnailsIds.clear();
   			displayedThumbnailsIds.clear();
   	   		elementsPosition.clear();
   	   		lastThumbnailsFilterPosition=0;
   	   		nbDynElems=0;
   	   		nbDynElemsNode.innerHTML=nbDynElems;
   	   		nbStaticElems=0;
   	   		nbStaticElemsNode.innerHTML=nbStaticElems;
   		}
   		
   		// Prepare search rexeps depending on search queries
   		var userRegexps = buildRegexesFromSearchQuery(userSearchPattern);
   		var catRegexps = buildRegexesFromSearchQuery(catalogSummaries[curCatalogId].dynamicElementsFilter);
   		
   		var nbJustAddedThumbnails=0;
   		var nbTestedElements=0;
   		
   		
   		var elIndex=lastThumbnailsFilterPosition;
   		//console.log("### updating from "+elIndex+"/"+communityNbLoadedElements);
   		
   		// for each not already filtered element check if it should be added in thumbnails   		
   		while (elIndex<communityNbLoadedElements) {
   			
   			nbTestedElements++;
   			curEl=elementsSummaryById[ elementsOrderedKeys[elIndex] ];
   			curEl.index=-1;
   			
   			//console.log(nbTestedElements+": ### testing '"+curEl.elementId+"' @ "+elIndex);   			
   			
   			if (shouldBeThumbnailed(curEl,userRegexps,catRegexps)) {
   				//console.log("	"+nbTestedElements+": YES");
   	   			if (clearCurrentContents) {
	   				filteredThumbnailsIds.push(curEl.elementId);
	   				curEl.index=filteredThumbnailsIds.length;
	   				elementsPosition[curEl.elementId]=curEl.index;
   	   				
	   				// update displayed number of matching elems
	  				nbMatchingElemsNode=document.getElementById("workzone.thumbnails.nbMatchingElements");
	   				nbMatchingElemsNode.innerHTML=" / " + filteredThumbnailsIds.length;
	   				if (filteredThumbnailsIds.length>1) { nbMatchingElemsNode.innerHTML+=" <s:property value="selectedCommunity.vocabulary.elementsTraduction" />";
	   				} else  { nbMatchingElemsNode.innerHTML+=" <s:property value="selectedCommunity.vocabulary.elementTraduction" />"; }
   	   			}								   				
   				// build and add HTML node of our new thumbnail if maX. allowed not reached yet
   				if (nbJustAddedThumbnails<MAX_DISPLAY_ELEMS) {
   					nbJustAddedThumbnails++;
	   				var thumbnailNode = curEl.makeThumbnailNode(matchStaticElements);
	   				insertSpot.append(thumbnailNode);
	   				displayedThumbnailsIds.push(curEl.elementId);
   				}
   				
   				// update statistics of displayed static and dyn. elements
   				if (matchStaticElements) { 
   					nbStaticElems++;
   	   				nbStaticElemsNode.innerHTML=nbStaticElems;
   	   				isStatic=true;
   				} else {
   					nbDynElems++;
   	   				nbDynElemsNode.innerHTML=nbDynElems;   	   				
   				}   				
   			} 
   			//else { console.log("	"+nbTestedElements+": nop"); }
   			
   			if (nbTestedElements%DISPLAY_LOADPERCENT_TRESHOLD==0) {  
   				var nbElementsToTest=communityNbElements-lastThumbnailsFilterPosition-1;
   				updateprogressbar(nbTestedElements,nbElementsToTest, "Filtering");
	   		}   
   			
   			elIndex++;
   			
   		}
   		lastThumbnailsFilterPosition+=nbJustAddedThumbnails;
   		document.getElementById("workzone.thumbnails.nbDisplayedThumbnails").innerHTML=lastThumbnailsFilterPosition;
   		
   		if (nbJustAddedThumbnails==MAX_DISPLAY_ELEMS) {
			var endOfContents = document.getElementById("thumbnailsContents.template.endOfElementsList").cloneNode(true);
			endOfContents.id+=".instance";
			endOfContents.style.display='table-cell';
			var link = endOfContents.querySelector("._link_");
			link.onclick=function(event) {
				event.stopPropagation();
				updateThumbnailsContents(document.getElementById('workzone.thumbnails.elementsFastSearch').value,false); 
			}
			insertSpot.append(endOfContents);   						
		}
   		
   		document.getElementById("thumbnails-loadprogress").style.display='none';
   		document.getElementById("workzone.thumbnails.elementsFastSearch").style.display='block';
   	}
   	
   
   function getPourcentageElemsHtml(current,total) {
	   	pourcentLoaded= (current / total) * 100;
		return "&nbsp;"+current+"&nbsp;"+"/"+"&nbsp;&nbsp;"+total+"&nbsp;("+pourcentLoaded+" %)";
   }
 	
   	function catalog_send_sessionid(msg) {
   		msg.sessionId="<s:property value='loggedUserProfile.sessionId'/>";
   		catalogws.send(JSON.stringify(msg));   		
   	}
   	
   
   	function send_request_cataloginfo(catid) {
   		JSONstr="{ 'msgType':'catalog-summary', 'catalogId' : "+catid+" }";
		catalogws.send(JSONstr);
   	}
   	
   	function community_send_elementsSummariesRequest() {
   		updateprogressbar(0,0);
   		document.getElementById("thumbnails-loadprogress").style.display='block';
   		document.getElementById("workzone.thumbnails.elementsFastSearch").style.display='none';
   		JSONstr="{ 'msgType':'community-getelements_summaries' }";
		catalogws.send(JSONstr);   		
   	}
   	
   	function catalog_send_catalogslistrequest() {
   		JSONstr="{ 'msgType':'catalogs-list' }";
		catalogws.send(JSONstr);
   	}
   	
   	function catalogs_send_selectcatalogRequest(catId) {
   		JSONstr="{ 'msgType':'catalog-select', 'catalogId' : "+catId+" }";
		catalogws.send(JSONstr);		
		curCatalogId=catId;
   	}
   	
 
</script>

<table id="thumbnailsContents.template.endOfElementsList" style="display:none">
	<tr><td><div>
		<a  href="#" class="_link_ icon icon_moreThumbnails" 
			title="<s:text name="workzone.icon.moreThumbnails" />"></a>		
	</div></td></tr>
</table>


	 				
<option id="thumbnailsContents.template.searchSelectOption" value="" style="dislpay:none" ></option>

<!-- this is a hidden element used for drag and drop visual effect -->			       
<div id="ThumbnailsContainerInitialData" class="dropping_element" 
	 style="position:absolute;top:-500px;display:none" >
 	<div class="dropping_element" >
 		<div id="dropping_multi_elements_top" class="dropping_element" ></div>
 	</div>"
</div>

<s:include value="./javascript.navigation.jsp" />