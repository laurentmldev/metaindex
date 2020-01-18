
// Metaindex Local Version
var METAINDEX_VERSION = "1.0beta";
var EDIT_MODE_ENABLED=0;
var QUIZZ_MODE_ENABLED=0;
var DEFAULT_CATALOG_FILE_NAME="catalogue_inp.csv";

// Default fields values
var DEFAULT_FILTER_SEARCH="textSearch";
var DEFAULT_FILTER_TITLE="Title";
var DEFAULT_FILTER_AUTHOR="Author";
var DEFAULT_FILTER_DATE="Date";
var DEFAULT_FILTER_MUSEUM="Museum";
var DEFAULT_FILTER_TYPE="Type";
var DEFAULT_FILTER_CENTURY="century"
var DEFAULT_FILTER_COUNTRY="country";
var DEFAULT_FILTER_COMMENTS="Comments";
var DEFAULT_FILTER_KEYWORDS="Keywords";

// Max amount of thumbnails before it get too long
var MAX_THUMBNAILS=350;

var CSVfileName;
var catalogName;
var catalogDirName;
var file_contents;
var CSVrows;
var SelectionIDs;
var curSelectionID;
var filterChanged;
var previousSelectionId;

//  Current application state
// STATE_QUIZZ|STATE_DISPLAY_INFO|STATE_EDIT_INFO
var appMode="STATE_QUIZZ"; 
var curCSVlineNb;
var curMessages = "";
var globalMessages = "";
var randomActive=true;
var quizzModeActive=false;
var navHisto = new Array();


var refreshIconRotateActive=false;
var refreshIconRotationDeg=0;

function setRandom(active)
{
	if (active != undefined)
	{
		document.getElementById("random_checkbox").checked=active;
	}
	else { active = document.getElementById("random_checkbox").checked; }	
	randomActive = active;
}

function setQuizzMode(active)
{
	if (active != undefined)
	{
		document.getElementById("quizz_checkbox").checked=active;
	}
	else { active = document.getElementById("quizz_checkbox").checked; }	
	quizzModeActive = active;
	switchAppMode();
}


function startup()
{

	// Check for the various File API support.
	if (window.File && window.FileReader && window.FileList && window.Blob) {
	  // Great success! All the File APIs are supported.
	} 
	else 
	{
	  alert('The File APIs are not fully supported in this browser.');
	}

	document.getElementById("main_table").style.display="none";
	
	document.getElementById("search_field").value=DEFAULT_FILTER_SEARCH;
	document.getElementById("title_filter").value=DEFAULT_FILTER_TITLE;
	document.getElementById("author_filter").value=DEFAULT_FILTER_AUTHOR;
	//document.getElementById("date_filter").value=DEFAULT_FILTER_DATE;
	document.getElementById("museum_filter").value=DEFAULT_FILTER_MUSEUM;
	document.getElementById("type_filter").value=DEFAULT_FILTER_TYPE;
	document.getElementById("century_filter").value=DEFAULT_FILTER_CENTURY;
	document.getElementById("country_filter").value=DEFAULT_FILTER_COUNTRY;
	document.getElementById("comments_filter").value=DEFAULT_FILTER_COMMENTS;
	document.getElementById("keywords_filter").value=DEFAULT_FILTER_KEYWORDS;
	setRandom(randomActive);
	setQuizzMode(quizzModeActive);
	if (!QUIZZ_MODE_ENABLED) { document.getElementById("quizz_switch").style.display='none';}
	if (!EDIT_MODE_ENABLED) 
	{ 
		document.getElementById("deletePic_button").style.display='none'; 
		document.getElementById("downloadCSV_button").style.display='none';
	}
	document.getElementById("quizz_showInfoPic").src="media/quizz_show_info.png";
	document.getElementById("quizz_showInfoPic").onmouseover=function(){document.getElementById('quizz_showInfoPic').src='media/quizz_show_info_ON.png';}
	document.getElementById("quizz_showInfoPic").onmouseout=function(){document.getElementById('quizz_showInfoPic').src='media/quizz_show_info.png';}
	document.getElementById("quizz_showInfoPic").onclick="switchAppMode('STATE_DISPLAY_INFO')";

	
	// declare event manager for the Fullscreen command
	(function () {
	    var viewFullScreen = document.getElementById("fullscreen");
	    if (viewFullScreen) {
		viewFullScreen.addEventListener("click", function () {
		    var docElm = document.documentElement;
		    if (docElm.requestFullscreen) {
			docElm.requestFullscreen();
		    }
		    else if (docElm.msRequestFullscreen) {
			docElm.msRequestFullscreen();
		    }
		    else if (docElm.mozRequestFullScreen) {
			docElm.mozRequestFullScreen();
		    }
		    else if (docElm.webkitRequestFullScreen) {
			docElm.webkitRequestFullScreen();
		    }
		}, false);
	    }
	
	})();	
}


function switchAppMode(requestedMode){

	//alert('switch mode '+requestedMode);
	
  // Filter and check State to aply
  if (requestedMode == undefined) 
  {
  	  if (appMode=="STATE_DISPLAY_INFO" && quizzModeActive==true) { requestedMode="STATE_QUIZZ"; }
  	  else (requestedMode="STATE_DISPLAY_INFO");
  } 
  
  // Apply States
  if (requestedMode=="STATE_QUIZZ")
  {
  	   document.getElementById("info_panel").style.display='none';
  	   document.getElementById("edit_panel").style.display='none'; 
  	   document.getElementById("quizz_showInfoPic").src="media/quizz_show_info.png";
  	   document.getElementById("quizz_showInfoPic").onmouseover=function(){document.getElementById('quizz_showInfoPic').src='media/quizz_show_info_ON.png';}
  	   document.getElementById("quizz_showInfoPic").onmouseout=function(){document.getElementById('quizz_showInfoPic').src='media/quizz_show_info.png';}
  	   
  	   document.getElementById("quizz_showInfoPic").onclick=function(){ switchAppMode('STATE_DISPLAY_INFO')};
	   appMode="STATE_QUIZZ";
	   
  }
  else if (requestedMode=="STATE_DISPLAY_INFO")
  {
  	   document.getElementById("info_panel").style.display='block';
  	   document.getElementById("edit_panel").style.display='none';
  	   document.getElementById("quizz_showInfoPic").src="media/quizz_hide_info.png";
  	   document.getElementById("quizz_showInfoPic").onmouseover=function(){document.getElementById('quizz_showInfoPic').src='media/quizz_hide_info_ON.png';}
  	   document.getElementById("quizz_showInfoPic").onmouseout=function(){document.getElementById('quizz_showInfoPic').src='media/quizz_hide_info.png';}
  	   document.getElementById("quizz_showInfoPic").onclick=function(){ switchAppMode('STATE_QUIZZ')};
	   appMode="STATE_DISPLAY_INFO";
	   
  }
  else if (requestedMode=="STATE_EDIT_INFO" && EDIT_MODE_ENABLED)
  {
  	  document.getElementById("edit_panel").style.display='block';
  	  document.getElementById("info_panel").style.display='none';
  	  document.getElementById("quizz_showInfoPic").src="media/quizz_hide_info.png";
  	  document.getElementById("quizz_showInfoPic").onmouseover=function(){document.getElementById('quizz_showInfoPic').src='media/quizz_hide_info_ON.png';}
  	  document.getElementById("quizz_showInfoPic").onmouseout=function(){document.getElementById('quizz_showInfoPic').src='media/quizz_hide_info.png';}
  	  document.getElementById("quizz_showInfoPic").onclick=function(){ switchAppMode('STATE_QUIZZ')};
	  appMode="STATE_EDIT_INFO";
  }
 
  if (quizzModeActive==true) 
  { 
  	document.getElementById("quizz_answer_area").style.display='block'; 
  	document.getElementById("quizz_showInfoPic").style.display='block'; 
  }
  else 
  { 
  	document.getElementById("quizz_answer_area").style.display='none'; 
  	document.getElementById("quizz_showInfoPic").style.display='none'; 
  }
}



// return 1 if OK, 0 if not
function checkFilter(filterTxt, testedValue)
{
	if (filterTxt.length == 0) { return 1; }
	
	// don't check default filter
	if (filterTxt == DEFAULT_FILTER_TITLE
		|| filterTxt == DEFAULT_FILTER_AUTHOR
		|| filterTxt == DEFAULT_FILTER_DATE
		|| filterTxt == DEFAULT_FILTER_MUSEUM
		|| filterTxt == DEFAULT_FILTER_TYPE
		|| filterTxt == DEFAULT_FILTER_CENTURY
		|| filterTxt == DEFAULT_FILTER_COUNTRY
		|| filterTxt == DEFAULT_FILTER_COMMENTS
		|| filterTxt == DEFAULT_FILTER_KEYWORDS
		)
	{ return 1 };
	
	var searchPattern=filterTxt;
	regexp = new RegExp(searchPattern,"gi");

	return (testedValue.search(regexp) != -1);
}

function gotoCSVLine(requiredCSVLine)
{
	if (requiredCSVLine <= 0 || requiredCSVLine > CSVrows.length) { return 0; }
	
	// search for the requiested Item
	for (thumbnailID=0;thumbnailID<SelectionIDs.length-1;thumbnailID++)
	{
		var rowID=SelectionIDs[thumbnailID];
		if (rowID == requiredCSVLine-1) { Next(thumbnailID); return 1;}
	}	

	// if not found, display a message
	globalMessages+="<br/>ID '"+requiredCSVLine+"' not in current selection.";
	Next(curSelectionID);

}

function removeCurrentItem()
{
	CSVrows[curCSVlineNb] = "# REMOVED # " + CSVrows[curCSVlineNb];
	applyFilters();
}

function buildSelection()
{
	
	document.getElementById("thumbnails_area").style.display='block';
	document.getElementById("thumbnails_hidden_area").style.display='none';
	document.body.style.cursor = 'wait';
	var searchResult='<center><table>';
	
	for (var thumbnailID=0;thumbnailID<SelectionIDs.length;thumbnailID++)
	{

		var curID=SelectionIDs[thumbnailID];
		var row = CSVrows[curID];
		var cells =row.split(";");
		var picSrc=cells[0];
		
		searchResult+="<tr><table><tr><td><td style=\"padding:2px\"><center><img id=\"thumbnail_"+(curID+1)+"\" src=\""+catalogDirName+"/"+picSrc
		  +"\" style=\"border:none;max-width:45px;max-height:45px\" onclick=\"Next("+thumbnailID+");\" "
		  +" onmouseover=\"document.getElementById('thumbnail_"+(curID+1)+"').style.setProperty('box-shadow','0 0 15px #BBBBBB');"
		  	+"location.href = '#';\""
		  +" onmouseout=\"document.getElementById('thumbnail_"+(curID+1)+"').style.setProperty('box-shadow','none');\""
		  +"/></center></td>"
		  +"<td id='thumbnailID_"+(curID+1)+"' style=\"font-size:0.7em;text-align:right\">"+(curID+1)+"</td></td></tr></table></tr>"
	}
	
			
	  searchResult+="</center></table>";
	  document.getElementById("thumbnails_area").innerHTML=searchResult;
	  document.body.style.cursor = 'default';
}

function setActiveSelection()
{

	var thumbnailElementId=curCSVlineNb+1;
	var element = document.getElementById("thumbnail_"+thumbnailElementId);
	if (typeof(element) == 'undefined' || element == null) { return; }
	//alert('set position thumbnail_'+thumbnailElementId);
	element.style.setProperty("border", "dotted 2px white");
	
	element = document.getElementById("thumbnailID_"+thumbnailElementId);
	element.style.setProperty("font-weight","bold");
	
	locationId=curSelectionID;
	if (curSelectionID>2) { locationId=curSelectionID-2;}
	location.href = "#";
	location.href = "#thumbnail_"+(SelectionIDs[locationId]+1);
}

function Next(id) 
{
  //alert('next('+id+')');                                                                       
    curMessages="";
    var previousCSVlineNb=undefined;
    
    // check that we are within the range
    if (SelectionIDs.length == 0) { return 0; }
    if (id != undefined && (id < 0 || id >= SelectionIDs.length)) { return 0; } 
    
    // save previous id
    previousSelectionId = curSelectionID;	  
    previousCSVlineNb = curCSVlineNb;
 	  
    // get a proper pic ID
     if (id==undefined)
     {
 	  // random access
 	  if (randomActive)
 	  {
		  curSelectionID=Math.floor((Math.random() * (SelectionIDs.length)));
		  //alert("SelectionIndex="+newSelectionIDindex+" => curCSVlineNb="+curCSVlineNb+" (previousSelectionId="+previousSelectionId+")");
		  while (curSelectionID == previousSelectionId && SelectionIDs.length > 1) 
		  { 
			  curSelectionID=Math.floor((Math.random() * (SelectionIDs.length)));
			  //alert("BAD badSelectionIndex="+badIndex+" SelectionIndex="+newSelectionIDindex+" => curCSVlineNb="+curCSVlineNb+" (previousSelectionId="+previousSelectionId+")");
		  } 
 	  }
 	  // sequential access
 	  else 
 	  {
 	  	  curSelectionID++;
 	  	  if (curSelectionID>=SelectionIDs.length) {curSelectionID=0;}
 	  }
     }
     else 
     {    
     	     curSelectionID=id;
     	     
     }
     
     curCSVlineNb=SelectionIDs[curSelectionID];

     // clean the previous selected thumbnail
     if (previousCSVlineNb != undefined) 
     {
		var prevSelectedElement = document.getElementById("thumbnail_"+(previousCSVlineNb+1));
		if (typeof(prevSelectedElement) != 'undefined' && prevSelectedElement != null)
		{
			prevSelectedElement.style.setProperty("border", "none");		
			prevSelectedElement = document.getElementById("thumbnailID_"+(previousCSVlineNb+1));
			prevSelectedElement.style.setProperty("font-weight","normal");
		}
     }
     //alert('final index='+newSelectionIDindex +" => picId="+curCSVlineNb );
 	  	  
     // check that row is not empty
     if (CSVrows[curCSVlineNb].match(/^\s*$/)) 
     { 
    	 globalMessages+="<br/>Removed empty row "+curCSVlineNb+" from catalog.";
 	 CSVrows.splice(curCSVlineNb,1);
 	 curCSVlineNb = previousSelectionId;
    	 return Next();
    	 
     }
     
    // From Here we have identified the Pic we want to display
    
    // update the navigation history (for the back arrow)
    if (previousSelectionId != undefined) { navHisto.push(previousSelectionId); }

    // empty previous answer
    document.getElementById("quizz_answer_area").value="";

    if (quizzModeActive==true) { switchAppMode("STATE_QUIZZ"); }
    else { switchAppMode("STATE_DISPLAY_INFO"); }
    
    var table = document.createElement("table"); 
    
    var cells = CSVrows[curCSVlineNb].split(";");
    var img_root_path = catalogDirName;

    var picSrc=cells[0];
    var title=cells[1];
    if (title == undefined) { title = ""; }
    var author=cells[2];
    if (author == undefined) { author = ""; }
    var date=cells[3];
    if (date == undefined) { date = ""; }
    var museum=cells[4];
    if (museum == undefined) { museum = ""; }
    var type=cells[5];
    if (type == undefined || type.length == 0) { type = "other"; }
    var century=cells[6];
    if (century == undefined || century.length == 0) { century = "other"; }
    var country=cells[7];
    if (country == undefined || country.length == 0) { country = "other"; }
    var comments=cells[8];
    if (comments == undefined) { comments = ""; }
    var keywords=cells[9];
    if (keywords == undefined) { keywords = ""; }
		
    	      
	    // populate HTML contents
	    document.getElementById("pic").src = img_root_path+"/"+picSrc;
	    document.getElementById("title_edit").value = title;
	    document.getElementById("title").innerHTML = title;
	    document.getElementById("author_edit").value = author;
	    document.getElementById("author").innerHTML = author;
	    document.getElementById("date_edit").value = date;
	    document.getElementById("date").innerHTML = date;
	    document.getElementById("museum_edit").value = museum;
	    document.getElementById("museum").innerHTML = museum;
	    
	    document.getElementById("type_edit").value = type;
	    var index = document.getElementById("type_edit").selectedIndex;
	    document.getElementById("type").innerHTML = 
    	    document.getElementById("type_edit").options[index].text;
	    	
	    document.getElementById("century_edit").value = century;
	    index = document.getElementById("century_edit").selectedIndex;	
	    document.getElementById("century").innerHTML = 
	    	document.getElementById("century_edit").options[index].text;
	    	
	    document.getElementById("country_edit").value = country;
	    index = document.getElementById("country_edit").selectedIndex;	
	    document.getElementById("country").innerHTML =
	    	document.getElementById("country_edit").options[index].text;
	    	
	    document.getElementById("comments_edit").value = comments; 
	    document.getElementById("comments").innerHTML = comments; 
	    
	    document.getElementById("keywords_edit").value = keywords;
	    var keywordsTab = keywords.split(",");
	    keywordsHTML="";
	    for (i = 0; i < keywordsTab.length; i++) {
		    curKeyword = keywordsTab[i];
		    keywordsHTML+="<a href=\"#\" onclick=\"document.getElementById('keywords_filter').value='"+curKeyword+"';"
		    			+"applyFilters();"
		    			+"document.getElementById('filtres_OFF').click();"
		    			+"\" >"+curKeyword+"</a> ";    
	    } 
	    document.getElementById("keywords").innerHTML =keywordsHTML;
	    
	    document.getElementById("picID").innerHTML = "Line "+(curCSVlineNb+1)+" of CSV Catalog";
	    document.getElementById("picRootPath").innerHTML = img_root_path+"/";
	    document.getElementById("picRootPath_edit").innerHTML = img_root_path+"/";
	    document.getElementById("picPath").innerHTML = picSrc;
	    document.getElementById("picPath_edit").value = picSrc;
	    
	    // handle thumbnails
	    setActiveSelection();
	    //var delayms=SelectionIDs.length*2;
	    //setTimeout(setActiveSelection,delayms+200);

    return id;
}

function applyFilters()
{
	SelectionIDs=new Array();
	var totalCount=0;

	document.getElementById("nbSelectionIds").innerHTML = "no entry found";
	document.getElementById("nbSelectionIds").style.display='block';
	document.getElementById("totalCount").innerHTML = "0";
	document.getElementById("goto_field").value = "";
	
	document.getElementById("thumbnails_area").style.display='none';
	document.getElementById("thumbnails_hidden_area").style.display='block';
	document.getElementById("thumbnails_area").innerHTML="";
	
	// filters values
	var search_field = document.getElementById("search_field").value;
	if (search_field == DEFAULT_FILTER_SEARCH) {search_field="";}
	var title_filter = document.getElementById("title_filter").value;
	var author_filter = document.getElementById("author_filter").value;
	var museum_filter = document.getElementById("museum_filter").value;
	var comments_filter = document.getElementById("comments_filter").value;
	var type_filter = document.getElementById("type_filter").value;
	var century_filter = document.getElementById("century_filter").value;
	var country_filter = document.getElementById("country_filter").value;
	var keywords_filter = document.getElementById("keywords_filter").value;
	
	document.body.style.cursor = 'wait';
	refreshIconRotateActive=true;refreshIcon_Rotate();
	
	for (var curID=0;curID<CSVrows.length-1;curID++)
	{
		// ignore comments and empty lines
		if (CSVrows[curID][0] == '#') { continue; }
		if (CSVrows[curID] == "") { continue; }
		
		totalCount++;
		document.getElementById("totalCount").innerHTML = totalCount;
		
		// retrieve cur row values
		var curCells = CSVrows[curID].split(";");
		var picSrc=curCells[0];
		var title=curCells[1];
		if (title == undefined) { title = ""; }
		var author=curCells[2];
		if (author == undefined) { author = ""; }
		var date=curCells[3];
		if (date == undefined) { date = ""; }
		var museum=curCells[4];
		if (museum == undefined) { museum = ""; }
		var type=curCells[5];
		if (type == undefined || type.length == 0) { type = "other"; }
		var century=curCells[6];
		if (century == undefined || century.length == 0) { century = "other"; }
		var country=curCells[7];
		if (country == undefined || country.length == 0) { country = "other"; }
		var comments=curCells[8];
		if (comments == undefined) { comments = ""; }
		var keywords=curCells[9];
		if (keywords == undefined) { keywords = ""; }
		
		// check search
		var search_ok = (search_field.length == 0)
				|| checkFilter(search_field,title) 
				|| checkFilter(search_field,author) 
				|| checkFilter(search_field,date)
				|| checkFilter(search_field,museum)
				|| checkFilter(search_field,comments)
				|| checkFilter(search_field,type)
				|| checkFilter(search_field,century)
				|| checkFilter(search_field,country)	
				|| checkFilter(search_field,comments)
				|| checkFilter(search_field,keywords)
				;
		//check filters
		var filter_ok = checkFilter(title_filter,title) 
				&& checkFilter(author_filter,author) 
				&& checkFilter(museum_filter,museum)
				&& checkFilter(comments_filter,comments)
				&& checkFilter(type_filter,type)
				&& checkFilter(century_filter,century)
				&& checkFilter(country_filter,country)
				&& checkFilter(keywords_filter,keywords)
				;
		// if search and filters validate the current pic
		// then we add it into our selection;
		if (search_ok && filter_ok) 
		{ 
			SelectionIDs.push(curID);
			document.getElementById("nbSelectionIds").innerHTML = SelectionIDs.length + " Entries";
		}
	}
	
	if (search_field.length > 0) { curMessages+="<br/>Searched for '"+search_field+"'";}
	if (title_filter.length > 0 && title_filter != DEFAULT_FILTER_TITLE) { curMessages+="<br/>Filtered Title ~ '"+title_filter+"'"; }
	if (author_filter.length > 0 && author_filter != DEFAULT_FILTER_AUTHOR) { curMessages+="<br/>Filtered Author ~ '"+author_filter+"'"; }
	if (museum_filter.length > 0 && museum_filter != DEFAULT_FILTER_MUSEUM) { curMessages+="<br/>Filtered Museum ~ '"+museum_filter+"'"; }
	if (comments_filter.length > 0 && comments_filter != DEFAULT_FILTER_COMMENTS) { curMessages+="<br/>Filtered Comments ~ '"+comments_filter+"'"; }
	if (type_filter.length > 0 && type_filter != DEFAULT_FILTER_TYPE) { curMessages+="<br/>Filtered Type ~ '"+type_filter+"'"; }
	if (century_filter.length > 0 && century_filter != DEFAULT_FILTER_CENTURY) { curMessages+="<br/>Filtered Century ~ '"+century_filter+"'"; }
	if (country_filter.length > 0 && country_filter != DEFAULT_FILTER_COUNTRY) { curMessages+="<br/>Filtered Country ~ '"+country_filter+"'"; }
	if (keywords_filter.length > 0 && keywords_filter != DEFAULT_FILTER_KEYWORDS) { curMessages+="<br/>Filtered Keywords ~ '"+keywords_filter+"'"; }
	if (SelectionIDs.length==0) { globalMessages+="<br/>/!\\ Sorry, no result found /!\\"; }
    	
	if (SelectionIDs.length <= MAX_THUMBNAILS) { buildSelection(); }

	document.getElementById("message_panel").innerHTML=curMessages + globalMessages;

	globalMessages="";
	curMessages="";

	// go to (first) next one
	Next();

	document.body.style.cursor = 'default';
	refreshIconRotateActive=false;	
}


function Load() {

    CSVfile = document.getElementById("fileLoad");
    
    CSVfile = document.getElementById("fileLoad");
    // Remember de CSV file name for later download
    // by the user
    CSVfileName = CSVfile.value.split("\\");
    CSVfileName = CSVfileName[CSVfileName.length-1];
    
    // removing previous timestamp if found
    CSVfileName = CSVfileName.replace(/_\d+_\d+_\d+__\d+-\d+-\d+/,"");
    
    catalogName = CSVfileName.replace(/\.csv/i,"");
    catalogDirName = catalogName+"_data";
    document.getElementById("CSVname").innerHTML=catalogName;
    
    

    var regex = /^([a-zA-Z0-9\s_\\.\-:])+(.csv|.txt)$/;
    if (regex.test(CSVfile.value.toLowerCase())) {
        if (typeof (FileReader) != "undefined") {
            var reader = new FileReader();
            reader.onload = function (e) 
            { 
            	file_contents = e; 
            	CSVrows = file_contents.target.result.split("\n");
            	applyFilters();
            }
            reader.readAsText(CSVfile.files[0]);
            previousSelectionId=0;   
            switchAppMode("STATE_QUIZZ");
        } else {
            alert("This browser does not support HTML5.");
        }
        
        document.getElementById("main_table").style.display="block";
        document.getElementById("csv").style.display="none";
    } else {
    	    alert("File name extension should be 'csv' or 'txt' : '"+CSVfileName+"'.");
    	    document.getElementById("fileLoad").click();
    }
    
}


function getTimestampStr() 
{
	var date = new Date;
	var seconds = date.getSeconds().toString();
	var minutes = date.getMinutes().toString();
	var hour = date.getHours().toString();
	if (seconds==0) { seconds="00";}
	if (minutes==0) { minutes="00";}
	if (hour==0) { hour="00";}
	if (seconds<10) { seconds="0"+seconds;}
	if (minutes<10) { minutes="0"+minutes;}
	if (hour<10) { minutes="0"+minutes;}
		
	var year = date.getFullYear().toString();
	var month = (date.getMonth()+1).toString(); 
	var day = date.getDate().toString();
	if(day<10){day="0"+day;} 
	if(month<10){month="0"+month;}
    
	return year+"/"+month+"/"+day+" "+hour+":"+minutes+":"+seconds;
}


function getTimestampCompact() 
{
	var date = new Date;
	var seconds = date.getSeconds().toString();
	var minutes = date.getMinutes().toString();
	var hour = date.getHours().toString();
	if (seconds==0) { seconds="00";}
	if (minutes==0) { minutes="00";}
	if (hour==0) { hour="00";}
	if (seconds<10) { seconds="0"+seconds;}
	if (minutes<10) { minutes="0"+minutes;}
	if (hour<10) { minutes="0"+minutes;}
		
	var year = date.getFullYear().toString();
	var month = (date.getMonth()+1).toString(); 
	var day = date.getDate().toString();
	if(day<10){day="0"+day;} 
	if(month<10){month="0"+month;} 
    
	return year+"_"+month+"_"+day+"__"+hour+"-"+minutes+"-"+seconds;
}

function downloadCSV()
{
	document.body.style.cursor = 'wait';
    
	var headerLine1 = "# Catalog File for MetaIndex v"+METAINDEX_VERSION+" - Generated on "+getTimestampStr();
	var headerLine2 = "# PictureName;Title;Autor;Date;Museum;Type;Century;Origin Country;Comments";
	textToWrite=headerLine1+"\n"+headerLine2+"\n";
	
	// ignore previous header line one if found
	var pos=0;
	var searchPattern="# Catalog File for Quiz";
	var regexp = new RegExp(searchPattern);
	var line=CSVrows[pos];
	if (line.search(regexp) != -1) { pos++ }
	
	// ignore previous header line two if found
	searchPattern=headerLine2;
	regexp = new RegExp(searchPattern);
	line=CSVrows[pos];
	if (line.search(regexp) != -1) { pos++ }
		
	// add all the data rows
	for (pos;pos<CSVrows.length;pos++) {
		
		textToWrite += CSVrows[pos] + "\n";		
	}
	
	// build a clever file name with timestamp
	
	// adding new timestamp
	CSVfileName = CSVfileName.replace(/(.*)\.csv/,"$1_"+getTimestampCompact()+".csv");	
	
	// finalize download
	document.body.style.cursor = 'default';
	var textFileAsBlob = new Blob([textToWrite], {encoding:"UTF-8",type:'text/csv;charset=UTF-8'});

	var downloadLink = document.createElement("a");
	downloadLink.download = CSVfileName;
	downloadLink.innerHTML = "Download File";
	if (window.webkitURL != null)
	{
		// Chrome allows the link to be clicked
		// without actually adding it to the DOM.
		downloadLink.href = window.webkitURL.createObjectURL(textFileAsBlob);
	}
	else
	{
		// Firefox requires the link to be added to the DOM
		// before it can be clicked.
		downloadLink.href = window.URL.createObjectURL(textFileAsBlob);
		downloadLink.onclick = destroyClickedElement;
		downloadLink.style.display = "none";
		document.body.appendChild(downloadLink);
	}

	downloadLink.click();
}

function destroyClickedElement(event)
{
	document.body.removeChild(event.target);
}


function updateCSV()
{
    if (document.getElementById("title_edit").value.length == 0
    	   	 && document.getElementById("author_edit").value == ""
    		&& document.getElementById("date_edit").value == ""
    		&& document.getElementById("museum_edit").value == ""
    		&& document.getElementById("type_edit").value == ""
    		&& document.getElementById("century_edit").value == ""
    		&& document.getElementById("country_edit").value == ""
    		&& document.getElementById("comments_edit").value == ""
    		&& document.getElementById("keywords_edit").value == "")
    {
    	    alert("Cowardly refusing to update empty fields, sorry.");
    }
    else
    {
      CSVrows[curCSVlineNb]=document.getElementById("picPath_edit").value +";"
    		+ document.getElementById("title_edit").value + ";"
    		+ document.getElementById("author_edit").value + ";"
    		+ document.getElementById("date_edit").value + ";"
    		+ document.getElementById("museum_edit").value + ";"
    		+ document.getElementById("type_edit").value + ";"
    		+ document.getElementById("century_edit").value + ";"
    		+ document.getElementById("country_edit").value + ";"
    		+ document.getElementById("comments_edit").value + ";"
    		+ document.getElementById("keywords_edit").value + ";";  
    }
}


function editCSV()
{
    switchAppMode("STATE_EDIT_INFO");
}


function checkInnerHTMLNotEmpty(elementId,defaultText)
{	
	if (document.getElementById(elementId).value == "") 
	{ 
		document.getElementById(elementId).value = defaultText;
	}	
}

// --------- IHM Advanced Functions -----------

function refreshIcon_Rotate()
{
	if (refreshIconRotateActive==false) { return; }

	refreshIconRotateActive=true;
	
	var delayms=25;
	
	var angleStepDeg=5;
	refreshIconRotationDeg=refreshIconRotationDeg + angleStepDeg;
	
	var rotate = "rotate("+refreshIconRotationDeg+"deg)";
	document.getElementById("refeshSelection_icon").style.webkitTransform = rotate;
	document.getElementById("refeshSelection_icon").style.MozTransform = rotate;
	document.getElementById("refeshSelection_icon").style.msTransform = rotate;
	document.getElementById("refeshSelection_icon").style.OTransform = rotate;
	document.getElementById("refeshSelection_icon").style.transform = rotate;
	
	// program next rotation
	setTimeout(refreshIcon_Rotate,delayms);	
}
