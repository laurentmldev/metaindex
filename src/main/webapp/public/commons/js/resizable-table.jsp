<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

  <script type="text/javascript">
  function setTableResizable(table) {
	  
	  let curCol = null;
	  let nxtCol = null;
	  let  pageX = null;
	  let nxtColWidth = null;
	  let curColWidth = null;
	  
	  let row = table.getElementsByTagName('tr')[0];
	  cols = row ? row.children : null;
	  if (!cols) return;
	  
	  //table.style.overflow = 'hidden';
	  
	  let tableHeight = table.offsetHeight;
	  
	  for (var i=0;i<cols.length;i++) {
		  var div= createDiv(tableHeight);
		  cols[i].appendChild(div);
		  cols[i].style.position='relative';
		  setListeners(div);
	  }
	  
	  function setListeners(div) {
		  var pageX,curCol,nxtCol,curColWidth,nxtColWidth;
		  
		  div.addEventListener('mousedown',function(e) {
			  
			  curCol = e.target.parentElement;
			  nxtCol = curCol.nextElementSibling;
			  console.log("nxtCol="+nxtCol);
			  pageX= e.pageX;
			  
			  let padding = paddingDiff(curCol);
			  curColWidth = curCol.offsetWidth - padding;
			  if (nxtCol) {
				  nxtColWidth = nxtCol.offsetWidth - padding;
			  }
		  });
		  
		  div.addEventListener('mouseover',function(e) {
			  e.target.style.borderRight = '3px solid pink';
		  });
		  
		  div.addEventListener('mouseout',function(e) {
			  if (!curCol) { e.target.style.borderRight = ''; }  
		  });
		  
		  document.addEventListener('mousemove',function(e) {
			  
			 if (curCol) {
				 
				 let diffX = e.pageX - pageX;
				 if (nxtCol) {
				
					 nxtCol.style.width = (nxtColWidth - (diffX))+'px';
					 
				 }
				 curCol.style.width = (curColWidth + (diffX))+'px';
				 //console.log("curCol="+curCol+" width="+curCol.style.width);
			 } 
		  });
		  document.addEventListener('mouseup',function(e) {
			  if (curCol) {
				  curCol = null;
				  nxtCol = null;
				  pageX = null;
				  nxtColWidth = null;
				  curColWidth = null;
				  e.target.style.borderRight = '';
			  }
		  });
		  
	  }
	  
	  
	  function createDiv(height) {
		  var div = document.createElement('div');
		  div.style.top = 0;
		  div.style.right = 0;
		  div.style.width = '10px';
		  div.style.position = 'absolute';
		  div.style.cursor = 'col-resize';
		  div.style.userSelect = 'none';
		  div.style.height = height + 'px';
		  return div;
	  }
	  
	  function paddingDiff(col) {
		  if (getStyleVal(col,'box-sizing') == 'border-box') {
			  return 0;
		  }
		  
		  var padLeft = getStyleVal(col,'padding-left');
		  var padRight = getStyleVal(col,'padding-right');
		  return (parseInt(padLeft) - parseInt(padRight));
	  }

	  function getStyleVal(elm,css) {
		  return (window.getComputedStyle(elm,null).getPropertyValue(css));
	  }
  }
  </script>
