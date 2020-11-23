<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 

<!--------------- IMAGEURL -------------->		  
 <script type="text/javascript" >


 function nextPic(rootNode, pageNumber,goReverse) {	 
   if (goReverse==null) { goReverse=false; }
   
   let pics = rootNode.querySelectorAll("._caroussel_img_");
   let nextOne=false;
   let found=false;
   
   if (goReverse==false) {
	   for (i = 0; i < pics.length; i++) {
		 let pic=pics[i];
		 if (pic.style.display!="none") {
			 pic.style.display="none";
			 let nextIndex=i+1;
			 if (nextIndex>=pics.length) { nextIndex=0; }
			 pics[nextIndex].style.display="block";
			 found=true;
			 pageNumber.innerHTML=(nextIndex+1)+"/"+pics.length;
			 break;
		 }  
	   }
   } else {
	   for (i = pics.length-1; i >= 0 ; i--) {
	     let pic=pics[i];
		 if (pic.style.display!="none") {
			 pic.style.display="none";
			 let nextIndex=i-1;
			 if (nextIndex<0) { nextIndex=pics.length-1; }
			 pics[nextIndex].style.display="block";
			 found=true;
			 pageNumber.innerHTML=(nextIndex+1)+"/"+pics.length;
			 break;
		 }     
	   }
   }
   if (found==false) {
	   pics[0].style.display="block";	
	   pageNumber.innerHTML="1/"+pics.length;
   }   
 }


 function _buildImgUrl(fieldValue,catalogItemsUrlPrefix) {
	 var regexAbsoluteUrl = /^http/;
 	 let imgUrl=fieldValue;
 	 if (imgUrl=="") { return ""; }
 	 
 	 // add prefix from Catalog params, if given URL is relative
 	 if (imgUrl!="" && imgUrl!=null 
 			 && catalogItemsUrlPrefix!="" && catalogItemsUrlPrefix!=null 
 			 && regexAbsoluteUrl.test(imgUrl)==0) {
 	 	imgUrl=catalogItemsUrlPrefix+"/"+imgUrl; 
 	 }
 	 
 	 //return encodeURI(imgUrl);
 	return imgUrl;
 }
 function _commons_perspective_build_readonly_field_image_url(catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,fieldVisuDesc,termDesc,fieldValue) {
	 
	 let fieldNode=null;

	 if (termDesc.isMultiEnum==true) {
		 fieldNode=document.getElementById("_commons_perspectives_multifield_readonly_template_image_url").cloneNode(true);	 	 
	 } else {
		 fieldNode=document.getElementById("_commons_perspectives_field_readonly_template_image_url").cloneNode(true);	 	 	 
	 }
	 fieldNode.id="";
 	 fieldNode.style.display="block";
 	 
 	 // title
 	 let title = fieldNode.querySelector("._title_");
 	 if (fieldVisuDesc.showTitle==true) { title.innerHTML=mx_helpers_getTermName(termDesc, catalogDesc)+": "; }
 	 else { title.style.display='none'; }
 	 
 	 // value
 	 let pageNumber = fieldNode.querySelector("._page_number_");
 	 let valueNode = fieldNode.querySelector("._value_");
 	if (termDesc.isMultiEnum==true) {
 		let urlsList=fieldValue.split(",");
 		
		for (var urlidx in urlsList) {
			 let url=urlsList[urlidx];
			 let imgNode = document.createElement("img");
			 imgNode.classList.add("_caroussel_img_");
			 imgNode.classList.add("w3-animate-opacity");
			 imgNode.classList.add("mx-perspective-field-img-size-"+fieldVisuDesc.size);
			 imgNode.src=_buildImgUrl(url,catalogDesc.itemsUrlPrefix);
			 imgNode.title=imgNode.src;
			 imgNode.onclick=function() { window.open(imgNode.src,'_blank'); }
			 if (urlidx>0) { imgNode.style.display="none"; }
			 else {  pageNumber.innerHTML="1/"+urlsList.length; }
			 valueNode.append(imgNode);
		 }	 	 
	 } else {
		 valueNode.classList.add("mx-perspective-field-img-size-"+fieldVisuDesc.size);
	 	 let imgUrl=_buildImgUrl(fieldValue,catalogDesc.itemsUrlPrefix);	 	 
	 	 valueNode.src=imgUrl;
	 	 valueNode.title=imgUrl;	 	 	 	 
	 }
 	 
 	 // prev/next (for multi only)
 	 if (termDesc.isMultiEnum==true) {
 		 let prevButton=fieldNode.querySelector("._prev_");
 		 prevButton.onclick=function() { nextPic(valueNode,pageNumber,true); }
 		 let nextButton=fieldNode.querySelector("._next_");
 		 nextButton.onclick=function() { nextPic(valueNode,pageNumber); }
 		 
 	 }
 	 fieldContainerNode.appendChild(fieldNode);
  }

</script>


<div style="display:none;" class="mx-perspective-field" id="_commons_perspectives_field_readonly_template_image_url"  >
	<table style="height:100%;width:100%" >	
		<tr><td ><img class="_value_ mx-perspective-field-img " src="" onclick="window.open(this.src,'_blank');" /></td></tr>
		<tr><td style="text-align:center" class="mx-perspective-field-title"><span class="_title_"></span></td></tr>
	</table>	               
</div>


<div style="display:none;" class="mx-perspective-field" id="_commons_perspectives_multifield_readonly_template_image_url"  >
	<table style="height:100%;width:100%" >	
		<tr><td >
			<div class="_value_ mx-perspective-field-img" >   <center>
                       
             </center></div>
			
		</td></tr>
		<tr>
			<td style="text-align:center" class="mx-perspective-field-title">
				<center>
			<div style="font-size:1.3rem">
				<i class="_prev_ mx-help-icon far fa-arrow-alt-circle-left" ></i>
				<span class="_page_number_" style="font-size:0.8rem"></span>
				<i class="_next_ mx-help-icon far fa-arrow-alt-circle-right" ></i>
			</div></center>
			
				<div class="_title_"></div>
		</td></tr>
	</table>	               
</div>


