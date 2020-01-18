<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
  
 <c:url value="/logoutprocess" var="logoutUrl"/>
 
<div id="show_profile_container"></div>
<s:include value="profile_modal.jsp" />
<script type="text/javascript">
	var MX_HEADER_PROFILE_POPUP_ID="profile_preferences_popup";
	
	let popupNode = profile_modal_createPreferencesForm();
	popupNode.id=MX_HEADER_PROFILE_POPUP_ID;
	document.getElementById("show_profile_container").appendChild(popupNode);

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
                <img class="img-profile rounded-circle" src="">
              </a>
              <!-- Dropdown - User Information -->
              <div class="dropdown-menu dropdown-menu-right shadow animated--grow-in" aria-labelledby="userDropdown">
              	<a class="dropdown-item" href="#" onclick="document.getElementById(MX_HEADER_PROFILE_POPUP_ID).toggleShowHide();">
                  <i class="fas fa-user fa-sm fa-fw mr-2 text-gray-400"></i>
                  <s:text name="Profile.preferences"></s:text>
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
  </script>
