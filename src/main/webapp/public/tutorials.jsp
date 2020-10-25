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
 
function deselectAll() {
	let buttons=document.querySelectorAll(".mx-tuto-button");
	for (var idx=0;idx<buttons.length;idx++) {
		buttons[idx].classList.remove("mx-tuto-button-selected");
	}
}
function activateTuto(buttonObj,tutoid,tutoTitle,tutoDesc) {
	document.getElementById("tuto-title").innerHTML=tutoTitle;
	document.getElementById("tuto-desc").innerHTML=tutoDesc;
	document.getElementById("tuto-video-source").src=TUTO_VIDEO_BASEURI+tutoid+".m4v";
	document.getElementById("tuto-video").load();
	document.getElementById("tuto-contents").style.display="block";	
	deselectAll();
	buttonObj.classList.add("mx-tuto-button-selected");
	
	
}
</script>
  
  
</head>


<body id="" 
	onload="document.getElementById('default-tuto').onclick();
		document.getElementById('tuto-video').stop();"
	>


	 <nav class="navbar navbar-expand navbar-light bg-white topbar static-top mx_welcome_navbar" 
	 		style="background:#aaa;height:5rem">
				<div class="app-title" style="font-size:4vw;padding:0;margin:0;width:100%;text-align:center;">
			 		<span class="scale-color-white">M</span><span class="app-title2 scale-color-white" style="color:white;">etainde</span><span class="scale-color-white">X</span> 
			 		Tutorials           		            			
            	</div>	 	   			 	
		 			
	 </nav>
  
  <!-- Page Wrapper -->
  <div id="wrapper">

    <ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">
      <div style="height:70vh;overflow:auto;" >
		<div id="default-tuto" class="mx-tuto-button" 
			onclick="activateTuto(this,'tuto1',
			'<s:text name="tutorials.tuto1.title"/>', 
			'<s:text name="tutorials.tuto1.desc"/>');" >
			Tuto 1
			<div class="mx-tuto-button-descr"><s:text name="tutorials.tuto1.title"/></div>
		</div>
		
		<div class="mx-tuto-button" 
			onclick="activateTuto(this,'tuto2',
			'<s:text name="tutorials.tuto2.title"/>', 
			'<s:text name="tutorials.tuto2.desc"/>');" >
			Tuto 2
			<div class="mx-tuto-button-descr"><s:text name="tutorials.tuto2.title"/></div>
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
           	  
	 		  
			  <div id="empty-placeholder" style="color:#aaa;dwidth:100%" ><center>
				 	  
						
			<table id="tuto-contents" class="table" style="display:none;margin-top:1rem;">
 		<tr><td><center>
 			<h4 id="tuto-title" ></h4>
 			<div  id="tuto-desc"></div>
 			<div>
 				 <video id="tuto-video" width="800" height="530" style="border:none;background:" controls>
					  <source  id="tuto-video-source" src="" type="video/mp4">
					  
					   <!-- track default
				           kind="captions"
				           srclang="en"
				           src="/public/commons/media/video/tuto/tuto1_en.vtt" /-->
						Your browser does not support the video tag.
					</video> 
 			</div>
 	</center>	</td></tr>
 	
 	</table>
					  
				  </div>
			  </center></div>
	   		 
	   	   </div></div>
   		 
    	</div><!-- End of Cards Container -->
      </div>
      </div><!-- End of Main content -->
          
    </div><!-- End of Contents Wrapper -->
  
  </div><!-- End of Page Wrapper -->
  
  
</body>

</html>
