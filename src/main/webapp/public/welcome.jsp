<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<c:url value="/" var="mxurl"/>
<c:url value="/loginprocess" var="loginUrl"/>

<!DOCTYPE html>
<html lang="en">

<head>

  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="">
  <meta name="author" content="">

  
  <title>MetaindeX - Welcome</title>

  <!-- Custom fonts for this template-->
  <link href="${mxurl}public/commons/deps/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">

  <!-- Custom styles for this template-->
  <s:include value="/public/commons/style/style_global.jsp" />  							 

	<script type="text/javascript">
	/*
	speedMs=250;
	// Show an element
	var show = function (elem) {

		// Get the natural height of the element
		var getHeight = function () {
			elem.style.display = 'block'; // Make it visible
			var height = elem.scrollHeight + 'px'; // Get it's height
			elem.style.display = ''; //  Hide it again
			return height;
		};

		var height = getHeight(); // Get the natural height
		elem.classList.add('is-visible'); // Make the element visible
		elem.style.height = height; // Update the max-height

		// Once the transition is complete, remove the inline max-height so the content can scale responsively
		window.setTimeout(function () {
			elem.style.height = '';
		}, speedMs);

	};

	// Hide an element
	var hide = function (elem) {

		// Give the element a height to change from
		elem.style.height = elem.scrollHeight + 'px';

		// Set the height back to 0
		window.setTimeout(function () {
			elem.style.height = '0';
		}, 1);

		// When the transition is complete, hide it
		window.setTimeout(function () {
			elem.classList.remove('is-visible');
		}, speedMs);

	};

	// Toggle element visibility
	var toggle = function (elem, timing) {

		// If the element is visible, hide it
		if (elem.classList.contains('is-visible')) {
			hide(elem);
			return;
		}

		// Otherwise, show it
		show(elem);

	};
*/
	</script>
</head>

<body class="bg-gradient-primary" style="background-color:#fff;background-image:none;" >

	
	<img src="public/commons/media/img/mx-gui-capture.jpg" class="mx_welcome_capture_img" />
	<div class="up-right">
		<a href="loginform"  class="btn btn-sm btn-info mx_welcome_signin_btn mx_welcome_livedemo_btn scale " >Sign In</a>
		<a href="/demo"  class="btn btn-sm btn-info mx_welcome_livedemo_btn scale" >Try Live Demo !</a>
	</div>
		 		
  <div class="container" style="width:100%;"  >
    
    <div class="row justify-content-center"  >
     	 
        <div class="card o-hidden border-0  my-5" style="width:100%" >
          <div class="card-body p-0">            
            <div class="row">
            	
            	<div class="app-title" style="font-size:6vw;padding:0;margin:0;width:100%"><span class="scale-color-white">M</span><span class="app-title2 scale-color-white" >etainde</span><span class="scale-color-white">X</span>
            		<div class="app-title" style="padding:0;margin:0;font-size:2vw;text-shadow:none">The OpenSource Data Toolbox</div>
            			
            	</div>
            	
        	    	    
            </div>
          </div>
        </div>
        
        <div style="padding-left:6rem;padding-right:6rem;"><center>
         <div class="row" style="width:100vw;">
         	
  			<div class="col-sm-3">  
   				<div class="card mb-3 mx_welcome_card">    					
				    <div class="card-body " 
				    	onmouseenter="//show(document.getElementById('control.body'));"
  						onmouseleave="//hide(document.getElementById('control.body'));">
				      <p class="card-text mx_welcome_card_title text-center">Control</p>
				      <div id="control.body" class="mx_welcome_card_body" >
					      <ul class="mx_welcome_card_ul fa-ul">
					      	<li class="fa-li fa fa-check scale-color-blue"><span class="mx_welcome_card_li_contents">Define data model</span></li>
					      	<li class="fa-li fa fa-check scale-color-blue"><span class="mx_welcome_card_li_contents">Customize visualization</span></li>
					      	<li class="fa-li fa fa-check scale-color-blue"><span class="mx_welcome_card_li_contents">Import contents CSV file</span></li>
					      	<li class="fa-li fa fa-check scale-color-blue"><span class="mx_welcome_card_li_contents">Embedded storage server</span></li>
					      	<li class="fa-li fa fa-check scale-color-blue"><span class="mx_welcome_card_li_contents">Link data items with each other</span></li>					      	
					      </ul>
				      </div>
				    </div>
				  </div>								 
			  </div>
			  <div class="col-sm-3">  
   				<div class="card mb-3 mx_welcome_card">    					
				    <div class="card-body "
				    	onmouseenter="//show(document.getElementById('collab.body'));"
  						onmouseleave="//hide(document.getElementById('collab.body'));">
				      <p class="card-text mx_welcome_card_title text-center">Collaborate</p>
				      <div id="collab.body" class="mx_welcome_card_body" >
					      <ul class="mx_welcome_card_ul fa-ul">
					      	<li class="fa-li fa fa-check scale-color-blue"><span class="mx_welcome_card_li_contents">All the team can create and use data</span></li>
					      	<li class="fa-li fa fa-check scale-color-blue"><span class="mx_welcome_card_li_contents">Control who can see, edit or delete data</span></li>
					      	<li class="fa-li fa fa-check scale-color-blue"><span class="mx_welcome_card_li_contents">Get notified when data has changed</span></li>
					      	<li class="fa-li fa fa-check scale-color-blue"><span class="mx_welcome_card_li_contents">Trace who changed what and when</span></li>
					      	<li class="fa-li fa fa-check scale-color-blue"><span class="mx_welcome_card_li_contents">Multi-language (French,English,Spanish, more to come ...)</span></li>					      	
					      </ul>
				      </div>
				    </div>
				  </div>								 
			  </div>
			  <div class="col-sm-3">			  	
			  	 <div class="card mb-3 mx_welcome_card" >
				    <div class="card-body "
				    	onmouseenter="//show(document.getElementById('explore.body'));"
  						onmouseleave="//hide(document.getElementById('explore.body'));">
				      <p class="card-text mx_welcome_card_title text-center">Explore</p>
				      <div id="explore.body" class="mx_welcome_card_body" >
					      <ul class="mx_welcome_card_ul fa-ul">
					      	<li class="fa-li fa fa-check scale-color-blue"><span class="mx_welcome_card_li_contents">Powerful <a class="mx_welcome_card_body_link" href="http://lucene.org">Lucene</a> Search Queries</span></li>
					      	<li class="fa-li fa fa-check scale-color-blue"><span class="mx_welcome_card_li_contents">Lightning-fast search with world-famous <a class="mx_welcome_card_body_link" href="http://elk.org">Elastic-Search</a> engine</span></li>
					      	<li class="fa-li fa fa-check scale-color-blue"><span class="mx_welcome_card_li_contents">Save and combine queries</span></li>
					      </ul>
				      </div>
				    </div>
				  </div>
				  
				 
			</div>
			  <div class="col-sm-3">			  	
			   	 <div class="card mb-3 mx_welcome_card" >
				    <div class="card-body "
				    	onmouseenter="//show(document.getElementById('compute.body'));"
  						onmouseleave="//hide(document.getElementById('compute.body'));">
				      <p class="card-text mx_welcome_card_title text-center">Compute</p>
				      <div id="compute.body" class="mx_welcome_card_body" >
					      <ul class="mx_welcome_card_ul fa-ul">
				      			<li class="fa-li fa fa-check scale-color-blue"><span class="mx_welcome_card_li_contents">Statistics graphs with world-famous <a class="mx_welcome_card_body_link" href="http://elk.org">Kibana</a></span></li>
				      			<li class="fa-li fa fa-check scale-color-blue"><span class="mx_welcome_card_li_contents">Connection graphs with auto-generated VEGA graphs</span></li>
				      			<li class="fa-li fa fa-check scale-color-blue"><span class="mx_welcome_card_li_contents">Integrated Kibana panels into your main visualization</span></li> 
				      		</ul>
				      </div>
				    </div>
				  </div>
			  </div>
  
		</div> 
        
         				 
				
				   
	  </div>
	  </center></div>
	  <div class="app-title mx-copyright"  ><s:text name="globals.copyright"/></div>
	</div>


  <!-- Bootstrap core JavaScript-->
  <script src="${mxurl}public/commons/deps/jquery/jquery.min.js"></script>
  <script src="${mxurl}public/commons/deps/bootstrap/js/bootstrap.bundle.min.js"></script>

  <!-- Core plugin JavaScript-->
  <script src="${mxurl}public/commons/deps/jquery-easing/jquery.easing.min.js"></script>

  <!-- Custom scripts for all pages-->
  <script src="${mxurl}public/commons/js/sb-admin-2.min.js"></script>

</body>

</html>
