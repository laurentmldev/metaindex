<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">

<head>

  <title>MetaindeX</title>
 
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="MetaindeX - Opensource Cataloger App">
  <meta name="author" content="Editions du Tilleul - Laurent ML">
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">

  
  <title>MetaindeX</title>
  <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
  <link rel="icon" type="image/svg" href="${webAppBaseUrl}/public/commons/media/img/favicon.png">
 
  <!-- Custom fonts for this template-->
  <link href="${webAppBaseUrl}/public/commons/deps/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">

  <!-- Custom styles for this template-->
  <s:include value="/public/commons/style/style_global.jsp" />  							 
  <s:include value="/public/commons/js/helpers.jsp" />
  
  <script type="text/javascript"> 
 
var TUTO_VIDEO_BASEURI="/webapp/public/commons/media/video/tuto/";
var tuto_contents=null;
var tuto_title=null;
var tuto_desc=null;
var tuto_links=null;
var tuto_links_contents=null;

var tuto_video=null;
var tuto_video_src=null;
var tuto_video_captions=null;

var language_shortname = "${current_guilanguage}";
if (language_shortname.length==0) { language_shortname="en"; }
language_shortname=language_shortname.toLowerCase();

var origin="welcome";
if ("${param.origin}"!="") { origin="${param.origin}"; }

function setVars() {

	tuto_contents=document.getElementById("tuto-contents");
	tuto_title=document.getElementById("tuto-title");
	tuto_desc=document.getElementById("tuto-desc");
	tuto_links=document.getElementById("tuto-links");
	tuto_links_contents=document.getElementById("tuto-links-contents");

	tuto_video=document.getElementById("tuto-video");
	tuto_video_src=document.getElementById("tuto-video-source");
	tuto_video_captions=document.getElementById("tuto-video-captions");
		
	if ("<s:property value="currentUserProfile.name" />".length!=0) {
		document.getElementById("language_buttons").style.display='none';	
	}
}

function deselectAll() {
	let buttons=document.querySelectorAll(".mx-tuto-button");
	for (var idx=0;idx<buttons.length;idx++) {
		buttons[idx].classList.remove("mx-tuto-button-selected");
	}
}
function activateTuto(buttonObj,tutoid,tutoTitle,tutoDesc,tutoLinks) {
	
	tuto_title.innerHTML=tutoTitle;
	tuto_desc.innerHTML=tutoDesc;
	tuto_video_src.src=TUTO_VIDEO_BASEURI+tutoid+".m4v";
	tuto_video_captions.src=TUTO_VIDEO_BASEURI+tutoid+"-desc-"+language_shortname+".vtt";
	tuto_video_captions.srclang=language_shortname;
	tuto_video.load();
	tuto_contents.style.display="block";	
	deselectAll();
	buttonObj.classList.add("mx-tuto-button-selected");
	
	if (tutoLinks.length>0) {
		tuto_links_contents.innerHTML=tutoLinks;
		tuto_links.style.display='block';
	} else {
		tuto_links.style.display='none';
	}	
	
}

function goBack() { window.location.href=origin; }
</script>
  
  
  <style>
  video::cue{
  	  /*background:#f8f9fc;*/ 
  	  background:black;
  	  color:white;
      font-size:1.4rem;
      padding:1rem;
    } 
  </style>
</head>


<body id="" 
	onload="
		setVars();
		document.getElementById('default-tuto').onclick();
		//document.getElementById('tuto-video').stop();"
	>


	 <nav class="navbar navbar-expand navbar-light bg-white topbar static-top mx_welcome_navbar" 
	 		style="background:#aaa;height:5rem">
	 			<div id="language_buttons">
            		<table style="margin-left:1rem;width:10vw;"><tr>
            		<td><a onclick="window.location.href=URL_add_parameter(location.href, 'language', 'en');"><img src="${webAppBaseUrl}/public/commons/media/img/flags/UK.png" class="mx-lang-flag scale" /></a></td>
            		<td><a onclick="window.location.href=URL_add_parameter(location.href, 'language', 'fr');"><img src="${webAppBaseUrl}/public/commons/media/img/flags/France.png" class="mx-lang-flag scale"/></a></td>
            		</tr></table>
            	</div>
				<div class="app-title" style="font-size:4vw;padding:0;margin:0;width:100%;text-align:center;">
			 		<span class="scale-color-white">M</span><span class="app-title2 scale-color-white" style="color:white;">etainde</span><span class="scale-color-white">X</span> 
			 		<s:text name="tutorials.title" />           		            			
            	</div>	 	 
            	
            	<a href="#" class="btn btn-primary btn-user btn-block scale" 
	        		style="max-width:10%;height:3rem;background:#999;border:none;padding:0;padding:0;padding-top:0.8rem;" 
		  	onclick="goBack();">
	                   <s:text name="globals.goback" />
	                 </a>  			 	
		 			
	 </nav>
  
  <!-- Page Wrapper -->
  <div id="wrapper">

    <ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">
      <div style="height:70vh;overflow:auto;" >
		<div id="default-tuto" class="mx-tuto-button" 
			onclick="activateTuto(this,'tuto1',
			'<s:text name="tutorials.tuto1.title"/>', 
			'<s:text name="tutorials.tuto1.desc"/>',
			'<s:text name="tutorials.tuto1.links"/>');" >
			
			<div class="mx-tuto-button-title"><b>1</b> <s:text name="tutorials.tuto1.title"/></div>
		</div>
		
		<div class="mx-tuto-button" 
			onclick="activateTuto(this,'tuto2',
			'<s:text name="tutorials.tuto2.title"/>', 
			'<s:text name="tutorials.tuto2.desc"/>',
			'<s:text name="tutorials.tuto2.links"/>');" >
			
			<div class="mx-tuto-button-title"><b>2</b> <s:text name="tutorials.tuto2.title"/></div>
		</div>
		
		<div class="mx-tuto-button" 
			onclick="activateTuto(this,'tuto3',
			'<s:text name="tutorials.tuto3.title"/>', 
			'<s:text name="tutorials.tuto3.desc"/>',
			'<s:text name="tutorials.tuto3.links"/>');" >
			
			<div class="mx-tuto-button-title"><b>3</b> <s:text name="tutorials.tuto3.title"/></div>
		</div>
		
		<div class="mx-tuto-button" 
			onclick="activateTuto(this,'tuto4',
			'<s:text name="tutorials.tuto4.title"/>', 
			'<s:text name="tutorials.tuto4.desc"/>',
			'<s:text name="tutorials.tuto4.links"/>');" >
			
			<div class="mx-tuto-button-title"><b>4</b> <s:text name="tutorials.tuto4.title"/></div>
		</div>
		
		<div class="mx-tuto-button" 
			onclick="activateTuto(this,'tuto5',
			'<s:text name="tutorials.tuto5.title"/>', 
			'<s:text name="tutorials.tuto5.desc"/>',
			'<s:text name="tutorials.tuto5.links"/>');" >
			
			<div class="mx-tuto-button-title"><b>5</b> <s:text name="tutorials.tuto5.title"/></div>
		</div>
				
		<div class="mx-tuto-button" 
			onclick="activateTuto(this,'tuto6',
			'<s:text name="tutorials.tuto6.title"/>', 
			'<s:text name="tutorials.tuto6.desc"/>',
			'<s:text name="tutorials.tuto6.links"/>');" >
			
			<div class="mx-tuto-button-title"><b>6</b> <s:text name="tutorials.tuto6.title"/></div>
		</div>
				
        </div>
      <hr class="sidebar-divider d-none d-md-block" style="margin-top:2rem;"/>
	  


	<div class="sidebar-brand-text counter-text app-title " 
		style="text-align:center;width:100%;color:#ccc;font-size:0.8rem;padding:0;margin:0;line-height:0.5;">
		MetaindeX	
	</div>
	
    </ul>

    <!-- Content Wrapper -->
    <div id="content-wrapper" class="d-flex flex-column">

      <!-- Main Content -->
      <div id="content">

        
        <!-- Cards Container -->
        
      <div id="details-wrapper">       
        <div id="cards-content-wrapper" class="container-fluid" >
          
          <div class="row">
            <div class="col-lg-12" >
           	  
	 		<center>
				 	  
						
			<table id="tuto-contents" class="table" style="display:none;margin-top:1rem;">
 		<tr><td><center>
 			<h3 id="tuto-title" ></h3>
 			<h5  id="tuto-desc"></h5>
 			<div style="font-size:1rem">
 					<a href="#tuto-links"><s:text name="tutorials.seeUsefulLinksBelow" /></a>
 					<span style="font-weight:bold;margin-left:3rem;color:#66a;"><s:text name="tutorials.enableSubtitles"/></span>
 			</div>
 			<div>
 				 <video id="tuto-video" width="1000" height="550" style="border:none;background:" controls>
					  <source  id="tuto-video-source" src="" type="video/mp4">					  
					   <track  id="tuto-video-captions" default  kind="captions" srclang="" src="" /-->
					   Your browser does not support the video tag.
					</video> 
 			</div>
 	</center>	</td></tr>
 	
 	</table>
					  
				  </div>
				  
				  <div id="tuto-links" style="display:none;margin-left:5rem;">
				  		<h5><s:text name="tutorials.usefullinks" /></h5>
				  		<div id="tuto-links-contents" ></div> 
				  </div>
			  </center></div>
	   		 
	   	   </div></div>
   		 
   		  
  <footer class="sticky-footer bg-white">
        <div class="container my-auto">
          <div class="mx-copyright text-center my-auto">
            <span><b><s:text name="globals.copyright"/></b> - MetaindeX <s:text name="mxRunMode"/> v<s:property value="mxVersion"/><br/><s:property value="mxFooterInfo"/></span>
          </div>        
        </div>
       
       </footer>
    	</div><!-- End of Cards Container -->
      </div>
      </div><!-- End of Main content -->
          
    </div><!-- End of Contents Wrapper -->
  
  </div><!-- End of Page Wrapper -->
   
  </footer>
   
  
</body>

</html>
