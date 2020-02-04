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

</head>

<body class="bg-gradient-primary" style="background-color:#fff;background-image:none;">

  <div class="container" style="width:100%;" >
    <!-- Outer Row -->
    <div class="row justify-content-center"  >

     	
        <div class="card o-hidden border-0  my-5">
          <div class="card-body p-0">
            <!-- Nested Row within Card Body -->
            <div class="row">
            	<div class="app-title" style="padding:0;margin:0">M<span class="app-title2">etainde</span>X<span 
            		style="text-shadow:1px 1px 1px;vertical-align: super; font-size:1.5rem;">&reg;</span>
            		<div class="app-title" style="padding:0;margin:0;font-size:3rem;text-shadow:none">The OpenSource Data Toolbox</div>
            	</div>
            	
            	<div class="app-title" 
	            	style="text-shadow:none;color:grey;font-size:0.8rem;padding:0;margin:1rem;" >
	            	<s:text name="globals.copyright"/><br/>
	            	<img src="public/commons/media/img/mx-gui-capture.jpg" class="mx_welcome_capture_img"/>
            	</div>
            	
            	
            	    
            </div>
          </div>
        </div>
        
        
         <div class="row" style="width:100vw;"
         	>
         	
  			<div class="col-sm-3" 
  				onmouseover="document.getElementById('control.body').style.display='block';"
  				onmouseout="document.getElementById('control.body').style.display='none';">  
   				<div class="card mb-3 mx_welcome_card"  >
				    <div class="card-body text-center" >
				      <p class="card-text mx_welcome_card_title">Control</p>
				      <div id="control.body"class="mx_welcome_card_body" style="display:none">
					      <ul>
					      	<li>Define data model</li>
					      	<li>Customize visualization</li>
					      	<li>Upload from CSV file</li>
					      	<li>Store and use files in embedded FTP server</li>
					      	<li>Link data items with each other</li>					      	
					      </ul>
				      </div>
				    </div>
				  </div>								 
			  </div>
			  <div class="col-sm-3" 
			  	onmouseover="document.getElementById('collab.body').style.display='block';"
			  	onmouseout="document.getElementById('collab.body').style.display='none';">  
   				<div class="card mb-3 mx_welcome_card" >
				    <div class="card-body text-center">
				      <p class="card-text mx_welcome_card_title">Collaborate</p>
				      <div id="collab.body" class="mx_welcome_card_body" style="display:none">
					      <ul>
					      	<li>All the team can create and use data</li>
					      	<li>Control who can see, edit or delete data</li>
					      	<li>Get notified when data has changed</li>
					      	<li>Trace who changed what and when</li>
					      	<li>Multi-language (French,English,Spanish, more to come ...)</li>					      	
					      </ul>
				      </div>
				    </div>
				  </div>								 
			  </div>
			  <div class="col-sm-3"
			  	onmouseover="document.getElementById('explore.body').style.display='block';"
			  	onmouseout="document.getElementById('explore.body').style.display='none';">
			  	 <div class="card mb-3 mx_welcome_card">
				    <div class="card-body text-center">
				      <p class="card-text mx_welcome_card_title">Explore</p>
				      <div id="explore.body"  class="mx_welcome_card_body" style="display:none">
				      	 <ul>
					      	<li>Powerful <a class="mx_welcome_card_body_link" href="http://lucene.org">Lucene</a> Search Queries</li>
					      	<li>Lightning-fast search with world-famous <a class="mx_welcome_card_body_link" href="http://elk.org">Elastic-Search</a> engine</li>
					      	<li>Save and Reuse your favorite queries</li>
					      </ul>
				      </div>
				    </div>
				  </div>
				  
				 
			</div>
			  <div class="col-sm-3"
			  	onmouseover="document.getElementById('compute.body').style.display='block';"
			  	onmouseout="document.getElementById('compute.body').style.display='none';">
			   	 <div class="card mb-3 mx_welcome_card">
				    <div class="card-body text-center">
				      <p class="card-text mx_welcome_card_title">Compute</p>
				      <div id="compute.body" class="mx_welcome_card_body" style="display:none">
				      		<ul>
				      			<li>Statistics graphs with world-famous <a class="mx_welcome_card_body_link" href="http://elk.org">Kibana</a></li>
				      			<li>Connection graphs with auto-generated VEGA graphs</li>
				      			<li>Integrated Kibana panels into your main visualization</li> 
				      		</ul>
				      </div>
				    </div>
				  </div>
			  </div>
  
		</div> 
        						
				
				  
				
				  				 
				
				   
	  </div>
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
