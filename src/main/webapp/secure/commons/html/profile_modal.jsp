<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
  
  
  
  <script type="text/javascript" >

//fieldsList: [ { id:"xxx",type:"text",title:"xxx",defaultValue:"xxx", important='false' },
//	 { id:"xxx",type:"dropdown",defaultValue:"xxx", values:[{text:'xxx',value:'xxx'}], important='true' }]

 function profile_modal_createPreferencesForm() {
  let header_preferences_fieldsList=[	  
	  { id:"email",type:"text",title:"<s:text name="Profile.email" />", defaultValue:"<s:property value='currentUserProfile.name'/>", important:'true',disabled:"true" },
	  { id:"nickName",type:"text",title:"<s:text name="Profile.nickname" />", defaultValue:"<s:property value='currentUserProfile.nickName'/>", important:'true' },	  	  		
  ];
  
  let languages = { id:"language",
		  			type:"dropdown",
		  			title:"<s:text name="Profile.language" />", 
		  			defaultValue:"<s:property value='currentUserProfile.guiLanguageId'/>",
		  			important:'false'
  };
  languages_values=[];
	<s:iterator value="currentUserProfile.guiLanguages" var="curLanguage">		
	languages_values.push({ text:"<s:property value="#curLanguage.name"/>", value:"<s:property value="#curLanguage.id"/>" });
	</s:iterator>	
	languages["values"]=languages_values;
	header_preferences_fieldsList.push(languages);
	

  
  let color_themes = { 	id:"color_scheme",type:"dropdown",
		  				title:"<s:text name="Profile.scheme" />", 
		  				defaultValue:"<s:property value='currentUserProfile.guiThemeId'/>", important:'false'
		  	};
  color_themes_values=[];
  <s:iterator value="currentUserProfile.guiThemes" var="curTheme">		
  	color_themes_values.push({ text:"<s:property value="#curTheme.name"/>", value:"<s:property value="#curTheme.id"/>" });
  </s:iterator>	
  color_themes["values"]=color_themes_values;
  header_preferences_fieldsList.push(color_themes);
  
  
  
  let header_preferences_onValidFormCallback = function(fieldsList) {
	  
	  let onSuccessCallback=function() {
		  footer_showAlert(INFO, "Refreshing panorama ...");
		  redirectToPage("${mxCurPage}");		  
	  }
	  let onErrorCallback=function(errorMsg) {
		  footer_showAlert(ERROR, "Sorry, unable to update your preferences. "+errorMsg);
	  }
	  
	  MxApi.requestSetUserPreferences({
		  	userId:<s:property value='currentUserProfile.id'/>,
		  	nickName:fieldsList.nickName,
		  	languageId:fieldsList.language,
		  	themeId:fieldsList.color_scheme,
		  	successCallback:onSuccessCallback,
		  	errorCallback:onErrorCallback
	  });
  }
  let popupForm=MxGuiPopups.newMultiInputsPopup(
		  "<s:text name="Profile.preferences"></s:text>",
		  header_preferences_fieldsList,header_preferences_onValidFormCallback);
  
  return popupForm;
  
}
 
 function profile_modal_createFeaturesForm() {
	 
	 let popupForm=MxGuiPopups.newBlankPopup("Features and Pricing");
	 let bodynode = popupForm.querySelector(".modal-body");
	 let paypalButtonContainer = document.createElement("div");
	 paypalButtonContainer.id="paypal-button-container";
	 bodynode.append(paypalButtonContainer);
	 return popupForm;
	 
 
 }
  </script>
