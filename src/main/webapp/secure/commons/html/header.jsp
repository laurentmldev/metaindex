<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
  
 <c:url value="/logoutprocess" var="logoutUrl"/>
 
 <c:if test="${currentUserProfile.loggedIn == false }">
 	<META HTTP-EQUIV="Refresh" CONTENT="0;URL=https://metaindex.fr:8443/metaindex/loginform?expired">
 </c:if>

<div id="show_info_container"  style="display:none;opacity:0.0;transition: opacity 100ms ease-in-out 100ms;">
	 <div  id="info_modal" class="modal" style="display:block;z-index:1000;background:#fffa;">
		  <div id="info_dialog" class="modal-dialog" style="min-width:30vw;display:block;position:absolute;margin-top:20vh;margin-left:60vh;max-height:80vh;">		  	 
		     <div class="modal-content">		     
		       <div class="modal-header">
		            <span id="info_modal_title"> </span>
		        	<span class="scale" style="cursor:pointer" onclick="MxGuiHeader.hideInfoModal()">X</span>
		       </div>
		       <div id="info_modal_body" class="modal-body"  style="overflow:auto;max-height:50vh;"></div>
		       <div id="info_modal_footer" class="modal-footer"></div>				       
		     </div>
	     </div>
     </div>       

</div> 
<div id="show_profile_container"></div>
<s:include value="profile_modal.jsp" />
<script type="text/javascript">

	var MX_HEADER_PROFILE_POPUP_ID="profile_preferences_popup";
	let popupNode = profile_modal_createPreferencesForm();
	popupNode.id=MX_HEADER_PROFILE_POPUP_ID;
	document.getElementById("show_profile_container").appendChild(popupNode);

	
	 
	 function addCheckoutButton(containerId, amount) {

		 paypal.Buttons({
		        createOrder: function(data, actions) {
		          return actions.order.create({
		            purchase_units: [{
		              amount: {
		                value: amount,
		                currency_code: "EUR",
		                breakdown: {
		                    item_total: {
		                      currency_code: "EUR",
		                      value: amount
		                    },
		                  }
		                
		              },
		              description:"A MetaindeX Plan",
		              invoice_id: "INVOICE-123",
		              custom_id:"id-abcd",
		              
		              items: [
		      				{
		      				name: 'hat',
		      				description: 'Brown hat.',
		      				quantity: '1',
		      				unit_amount: {
		      					value : amount,
		      					currency_code : 'EUR'
		      				  }
		      				}
		      			],
		      		
		            }],
		            
		          });
		        },
		        onApprove: function(data, actions) {
		          return actions.order.capture().then(function(details) {
		            alert('Transaction completed by ' + details.payer.name.given_name);
		          });
		        }
		      }).render('#'+containerId); // Display payment options on your web page
		      
	 }
	
	var MX_HEADER_ENABLED_FEATURES_POPUP_ID="profile_features_popup";
	let featuresPopupNode=profile_modal_createFeaturesForm();
	featuresPopupNode.id=MX_HEADER_ENABLED_FEATURES_POPUP_ID;
	document.getElementById("show_profile_container").appendChild(featuresPopupNode);
	addCheckoutButton("paypal-button-container",29);
	
</script>


 <!-- Topbar -->
        <nav class="navbar navbar-expand navbar-light bg-white topbar mb-2 static-top shadow">
		
          <!-- Sidebar Toggle (Topbar) -->
          <button id="sidebarToggleTop" class="btn btn-link d-md-none rounded-circle mr-3">
            <i class="fa fa-bars"></i>
          </button>
	
		  <s:include value="./header-search.jsp"></s:include>
		
          <!-- Topbar Navbar -->
          <ul class="navbar-nav ml-auto">

			<s:include value="./header-search-xs.jsp"></s:include>

            <!-- Nav Item - Alerts -->
            <!--li class="nav-item dropdown no-arrow mx-1">
              <a class="nav-link dropdown-toggle" href="#" id="alertsDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-bell fa-fw"></i>
                
              </a>
              
              <div class="dropdown-list dropdown-menu dropdown-menu-right shadow animated--grow-in" aria-labelledby="alertsDropdown">
                <h6 class="dropdown-header">
                  Alerts Center
                </h6>
                
                <a class="dropdown-item text-center small text-gray-500" href="#">Show All Alerts</a>
              </div>
            </li-->

          

			<div id="MxGui.header.insertSpot" ></div>
			
            <div class="topbar-divider d-none d-sm-block"></div>

            <!-- Nav Item - User Information -->
            <li class="nav-item dropdown no-arrow">
              <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <span class="mr-2 d-none d-lg-inline text-gray-600 small"><s:property value='currentUserProfile.nickname'/></span>
                <img class="img-profile rounded-circle" src="public/commons/deps/fontawesome-free/svgs/regular/smile-beam.svg">
              </a>
              <!-- Dropdown - User Information -->
              <div class="dropdown-menu dropdown-menu-right shadow animated--grow-in" aria-labelledby="userDropdown">
              	<a class="dropdown-item" href="#" onclick="document.getElementById(MX_HEADER_PROFILE_POPUP_ID).toggleShowHide();">
                  <i class="fas fa-user fa-sm fa-fw mr-2 text-gray-400"></i>
                  <s:text name="Profile.preferences"></s:text>
                </a>
                <a class="dropdown-item" href="#" onclick="document.getElementById(MX_HEADER_ENABLED_FEATURES_POPUP_ID).toggleShowHide();">
                  <i class="fas fa-user fa-sm fa-fw mr-2 text-gray-400"></i>
                  <s:text name="Profile.features"></s:text>
                </a>
                <a class="dropdown-item" data-toggle="modal" data-target="#logoutModal">
                  <i class="fas fa-running fa-sm fa-fw mr-2 text-gray-400"></i>
                  <s:text name="Profile.logout"></s:text>
                </a>                
              </div>
              
            </li>

          </ul>

        </nav>
        <!-- End of Topbar -->
            
        
  <!-- Logout Modal-->
  <div class="modal fade" id="logoutModal" tabindex="-1" role="dialog" aria-labelledby="ModalLabelLogout" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="ModalLabelLogout"><s:text name="Profile.logout" /></h5>
          <button class="close" type="button" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">×</span>
          </button>
        </div>
        <div class="modal-body"><s:text name="Profile.logout_text" /></div>
        <div class="modal-footer">
          <button class="btn btn-secondary" type="button" data-dismiss="modal"><s:text name="global.cancel" /></button>
          <form action="${logoutUrl}" id="logoutform" method="post" >
          	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
          </form>
          
          <a class="btn btn-primary" href="#" onclick="document.getElementById('logoutform').submit();"><s:text name="Profile.logout" /></a>
        </div>
      </div>
    </div>
  </div>
  
  <script type="text/javascript" >
  
  MxGuiHeader.getInsertSpot=function() { 
	  return document.getElementById('MxGui.header.insertSpot');
  }
  
  MxGuiHeader.showInfoModal=function(titleTxt,bodyTxt,footerTxt) {
	  
	  let title = document.getElementById("info_modal_title");
	  title.innerHTML=titleTxt;
	  
	  let body = document.getElementById("info_modal_body");
	  body.innerHTML="";
	  if (typeof(bodyTxt)=='string') { body.innerHTML=bodyTxt; }
	  else { body.appendChild(bodyTxt); }
	  
	  let footer = document.getElementById("info_modal_footer");
	  if (footerTxt==null || footerTxt=='') { footer.style.display='none'; }
	  else { 
		  footer.style.display='block';
		  footer.innerHTML="";
		  if (typeof(footerTxt)=='string') { footer.innerHTML=footerTxt; }
		  else { footer.appendChild(footerTxt); }
	  }
	  
	  document.getElementById("show_info_container").style.opacity=0.0;
	  document.getElementById("show_info_container").style.display='block';
	  // need to give time to the transition to be loaded before applying property
	  window.setTimeout( function() {
		  document.getElementById("show_info_container").style.opacity=1.0;
		}, 110);	 
  }

  MxGuiHeader.hideInfoModal=function() {
	  
	  document.getElementById("show_info_container").style.opacity=0.0;
	  document.getElementById("show_info_container").style.display='none';
	 
  }
  </script>
