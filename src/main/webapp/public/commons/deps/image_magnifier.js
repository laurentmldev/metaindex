
function stop_magnify(imgID) {
	document.getElementById(imgID+".magnifier").remove();
}

//code originally from W3School  how to
//https://www.w3schools.com/howto/howto_js_image_magnifier_glass.asp
function magnify(imgID, zoom) {
	
  	
  // bw: border width
  let img, glass, glass_width, glass_height, bw;
  img = document.getElementById(imgID);
  bw = 1;

  /* Create magnifier glass: */
  glass = document.createElement("DIV");
  glass.id=imgID+".magnifier";
  
  let glass_style=
	   "position: absolute;"
	  +"border: "+bw+"px dashed #000;"
	  +"border-radius: 50%;"
	  +"cursor: none;"
	  +"width: 200px;"
	  +"height: 200px;";
  
  glass.setAttribute("style", glass_style);

  /* Insert magnifier glass: */
  img.parentElement.insertBefore(glass, img);

  /* Set background properties for the magnifier glass: */
  glass.style.backgroundImage = "url('" + img.src + "')";
  glass.style.backgroundRepeat = "no-repeat";
  glass.style.backgroundSize = (img.width * zoom) + "px " + (img.height * zoom) + "px";
  
  
  glass_width = glass.offsetWidth / 2;
  glass_height = glass.offsetHeight / 2;
  
  //console.log("maginfying img "+imgID+" x"+zoom+" : w="+w+" h="+h);

  /* Execute a function when someone moves the magnifier glass over the image: */
  glass.addEventListener("mousemove", moveMagnifier);
  img.addEventListener("mousemove", moveMagnifier);

  /*and also for touch screens:*/
  glass.addEventListener("touchmove", moveMagnifier);
  img.addEventListener("touchmove", moveMagnifier);
  
  function moveMagnifier(e) {
    var pos, focusPosition_x, focusPosition_y;
    /* Prevent any other actions that may occur when moving over the image */
    e.preventDefault();
    /* Get the cursor's x and y positions relative to top left of the image: */
    pos = getCursorPos(e);
    focusPosition_x = pos.x;
    focusPosition_y = pos.y;
    
    //console.log("pos: x="+x+" y="+y);
    
    /* Prevent the magnifier glass from being positioned outside the image: */    
    if (focusPosition_x > img.width - (glass_width / zoom)) {focusPosition_x = img.width - (glass_width / zoom);}
    if (focusPosition_x < glass_width / zoom) {focusPosition_x = glass_width / zoom;}
    if (focusPosition_y > img.height - (glass_height / zoom)) {focusPosition_y = img.height - (glass_height / zoom);}
    if (focusPosition_y < glass_height / zoom) {focusPosition_y = glass_height / zoom;}
    
    /* Set the position of the magnifier glass: */
    glass.style.left = (focusPosition_x - glass_width) + "px";
    glass.style.top = (focusPosition_y + glass_height / 4) + "px";
        
    //console.log("glass: left="+glass.style.left+" top="+glass.style.top);
    
    /* Display what the magnifier glass "sees": */
    glass.style.backgroundPosition = "-" + ((focusPosition_x * zoom) - glass_width + bw) + "px -" + ((focusPosition_y * zoom) - glass_height + bw) + "px";
  }

  function getCursorPos(e) {
    var a, x = 0, y = 0;
    e = e || window.event;
    /* Get the x and y positions of the image: */
    a = img.getBoundingClientRect();
    /* Calculate the cursor's x and y coordinates, relative to the image: */
    x = e.pageX - a.left;
    y = e.pageY - a.top;
    /* Consider any page scrolling: */
    x = x - window.pageXOffset;
    y = y - window.pageYOffset;
    return {x : x, y : y};
  }
}
