<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
  

<script type="text/javascript">

var PLANS_COLORS=["#eee" ,"#bceaff",  "#e4ffad","#ffaaaa", "#aaa"];


function addPlanCheckoutBreakDown(id,name,checkoutBreakdownMsg) {
	
	let checkoutBreakDownContainer=document.getElementById("checkout-breakdown-container");
	let breakdownEntries=checkoutBreakdownMsg.breakdownEntries;
	let totalcost=checkoutBreakdownMsg.totalCost;
	let tax=0;
	let discount=0;
	let productCost=0;
	
	let table = document.createElement("table");
	table.classList.add("table");
	table.classList.add("table-striped");
	table.style="width:50%";
	checkoutBreakDownContainer.append(table);
	

	for (var entryId in breakdownEntries) {
		let breakDownEntry = breakdownEntries[entryId];
		
		if (breakDownEntry.type=="discount" && breakDownEntry.cost==0) { continue; }
		let rowPlan = document.createElement("tr");
		table.append(rowPlan);	
		let planNameCell=document.createElement("td");
		planNameCell.innerHTML=breakDownEntry.title;
		rowPlan.append(planNameCell);
		let planCostCell=document.createElement("td");
		planCostCell.innerHTML=breakDownEntry.cost+" "+breakDownEntry.unit;
		if (breakDownEntry.type=="totalht" || breakDownEntry.type=="total") { 
			planNameCell.style="font-weight:bold";
			planCostCell.style="font-weight:bold";
		}		
		if (breakDownEntry.type=="tax") { tax=breakDownEntry.cost; }
		if (breakDownEntry.type=="discount") { discount=-breakDownEntry.cost; }
		if (breakDownEntry.type=="product") { productCost=breakDownEntry.cost; }
		rowPlan.append(planCostCell);
	}
	
	return {totalCost:totalcost,
			tax:tax,
			discount:discount,
				product:{
					name:name,
					cost:productCost
				}};
	
}

function makePlanPresentationRow(id,name,nbCatalogs,nbDocs,nbBytes,yearlyCostEuros,currentPlanId,popupWindow,
				availableForPurchase, curPlanEndDateStr) {
	
	if (availableForPurchase==false && id!=currentPlanId) { return null; }
	let popupElement = popupWindow.querySelector(".modal-body");
	let colorIdx=id%PLANS_COLORS.length - 1;
	//console.log("### plan "+name+" : id="+id+" colorIdx="+colorIdx+" color="+PLANS_COLORS[colorIdx]);	
	let rowColor=PLANS_COLORS[colorIdx];
		
	let row=document.createElement("tr");
	row.style="font-size:1.2rem";
	// name
	let colName = document.createElement("td");
	colName.innerHTML=name;
	colName.style="background-color:"+rowColor+";color:#111;";
	if (id==currentPlanId) { colName.innerHTML+=" <div style='font-size:0.8rem;color:#999'><s:text name="Profile.plans.currentPlan"/></div>"; }	
	row.append(colName);
	
	if (id==currentPlanId) { row.classList.add("mx-table-rounded-row-selected"); }
	else if (availableForPurchase==true && yearlyCostEuros>0)  {
	
		row.onmouseenter=function(event) { row.classList.add("mx-table-rounded-row-hover"); }
		row.onmouseleave=function(event) { row.classList.remove("mx-table-rounded-row-hover"); }
		
		row.onclick=function(e) {
			function checkoutFailureCallback(msg) {
				footer_showAlert(ERROR, msg);
			}
			
	 		function checkoutSuccessCallback(method,breakdownCheckout) {
	 			
	 			clearNodeChildren(popupElement);
	 			popupElement.innerHTML="<s:text name="Profile.plans.confirmingPayment" />";
	 			function onPlanUpdateRefusedByServer(msg) {
	 				footer_showAlert(ERROR,msg);
	 			}
	 			function onPlanUpdateConfirmedByServer(msg) {
	 				popupWindow.hide();
	 				if (msg.isSuccess==false) {
	 					 footer_showAlert(ERROR, "<s:text name="Profile.plans.paymentNotConfirmedByServer" /> '<b>"+breakdownCheckout.transactionId+"</b>'",
	 							 	null,
	 							 	3600000);
	 					 return;
	 				 }
	 				
	 				footer_showAlert(SUCCESS, "<s:text name="Profile.plans.upgradeSuccess" /> "+breakdownCheckout.product.name+"."
	 								+ " <s:text name="Profile.plans.upgradeSuccessRefreshing" />",null,30000);
	 				
	 				let timerReloadPage = setInterval(function() { 
	 					clearInterval(timerReloadPage); 
	 					redirectToPage(""); 
	 				}, 3000);
	 				  
	 			}
	 			// request server confirmation
	 			ws_handlers_requestPlanUpdateConfirmPayment(id,
	 					breakdownCheckout.transactionId,
	 					breakdownCheckout.totalCost,
	 					breakdownCheckout.paymentMethod,
	 					breakdownCheckout.paymentDetails,
	 					onPlanUpdateConfirmedByServer,
	 					onPlanUpdateRefusedByServer)
				
				
			}
			
			
			function onCheckoutBreakDownDetails(checkoutBreakDownMsg) {
				clearNodeChildren(document.getElementById("checkout-breakdown-container"));			
				breakdownCheckout=addPlanCheckoutBreakDown(id,name,checkoutBreakDownMsg);
				clearNodeChildren(document.getElementById("checkout-button-container"));
				addCheckoutButton("checkout-button-container",
										checkoutBreakDownMsg.transactionId,
										breakdownCheckout,
										checkoutSuccessCallback,
										checkoutFailureCallback);
				scrollTo("checkout-footer-container");
			}
			let checkoutTitle=document.getElementById("checkout-breakdown-title");
			checkoutTitle.style.display='block';
			ws_handlers_requestPlanUpdate(id,onCheckoutBreakDownDetails);
		}
	}
	
	// nb Catalogs
	let colNbcat = document.createElement("td");
	colNbcat.innerHTML="<span style='color:#111;'>"+nbCatalogs+"</span> <span style='font-size:1rem'>catalog(s)</span>";
	colNbcat.style="background-color:"+rowColor
	row.append(colNbcat);
	
	// nb docs
	let colNbDocs = document.createElement("td");
	colNbDocs.innerHTML="<span style='color:#111;'>"+nbDocs+"</span>  <span style='font-size:1rem'>documents</span>";
	colNbDocs.style="background-color:"+rowColor;
	row.append(colNbDocs);
	
	// drive usage quota
	let colDriveSpaceUsage = document.createElement("td");
	colDriveSpaceUsage.innerHTML="<span style='color:#111;'>"+nbBytes/1000000+"</span> <span style='font-size:1rem'>MB</span>";
	colDriveSpaceUsage.style="background-color:"+rowColor;
	row.append(colDriveSpaceUsage);
	
	// cost
	let cost = document.createElement("td");
	if (yearlyCostEuros==0) {
		cost.innerHTML="<span style='color:#111;'><s:text name="Profile.plans.free" /></span>";	
	} else {
		cost.innerHTML="<span style='color:#111;'>"+yearlyCostEuros+"€</span><span style='font-size:1rem;margin-left:0.3rem'><s:text name="Profile.plans.withoutTaxPerYear" /></span>";
	}	
	cost.style="background-color:"+rowColor;
	row.append(cost);
	
	// end date (for current plan)
	let endDate = document.createElement("td");
	endDate.style="background-color:"+rowColor+";";
	if (id==currentPlanId && yearlyCostEuros>0) { 
		endDate.innerHTML="<s:text name='Profile.plans.endsOn' /><br/>"+curPlanEndDateStr; 
		endDate.style="background-color:"+rowColor+";font-size:0.8rem;";
	}  
	
	row.append(endDate);
	
	return row;
}

function plans_modal_addHeaderMenu() {
	
	let plansPopupNode=MxGuiPopups.newBlankPopup("<s:text name="Profile.plans" />","<s:text name="global.cancel" />","80vw","90vh","rgba(255, 255, 255,1)");
	let bodynode = plansPopupNode.querySelector(".modal-body");
	
	plansPopupNode.id=MX_HEADER_PLANS_POPUP_ID;
	document.getElementById("show_profile_container").appendChild(plansPopupNode);
	
	let header=document.createElement("div");
	header.style="width:100%;text-align:center";
	bodynode.append(header);
	let headerLine=document.createElement("div");
	headerLine.innerHTML+="<s:text name="Profile.plans.headerLine" />";
	headerLine.style="font-size:1.5rem;margin:1rem;font-weight:bold";
	header.append(headerLine);
	
	
	// other plans available
	let plansTable = document.createElement("table");
	plansTable.classList.add("mx-table-rounded-rows");
	
	
	let hrow = document.createElement("tr");
	plansTable.append(hrow);
	let hName=document.createElement("th");
	hName.innerHTML="<s:text name="Profile.plans.planName" />";
	hrow.append(hName);
	let hNbCat=document.createElement("th");
	hNbCat.innerHTML="<s:text name="Profile.plans.quotaNbCatalogs" />";
	hrow.append(hNbCat);
	let hNbDocs=document.createElement("th");
	hNbDocs.innerHTML="<s:text name="Profile.plans.quotaNbDocs" />";
	hrow.append(hNbDocs);
	let hSpace=document.createElement("th");
	hSpace.innerHTML="<s:text name="Profile.plans.quotaDriveSpacePerCatalog" />";
	hrow.append(hSpace);
	let hCost=document.createElement("th");
	hCost.innerHTML="<s:text name="Profile.plans.cost" />"
	hrow.append(hCost);
	let hAction=document.createElement("th");
	hAction.innerHTML=""
	hrow.append(hAction);
	
	bodynode.appendChild(plansTable);
		
	let curPlanId=<s:property value="currentUserProfile.plan.id"/>;
	let nodePres_curPlan = makePlanPresentationRow(
						curPlanId,
						"<s:property value="currentUserProfile.plan.name"/>",
						<s:property value="currentUserProfile.plan.quotaCatalogsCreated"/>,
						<s:property value="currentUserProfile.plan.quotaNbDocsPerCatalog"/>,
						<s:property value="currentUserProfile.plan.quotaDriveBytesPerCatalog"/>,
						<s:property value="currentUserProfile.plan.yearlyCostEuros"/>,
						<s:property value="currentUserProfile.plan.id"/>,
						plansPopupNode,
						<s:property value="currentUserProfile.plan.availableForPurchase"/>,
						"<s:property value="currentUserProfile.planEndDateStr"/>"
		);

		plansTable.appendChild(nodePres_curPlan); 
	

	<c:forEach items="${plansList}" var="item">
	   if (${item.id}!=curPlanId && ${item.availableForPurchase}==true) {
		   
			let nodePres_plan${item.id} = makePlanPresentationRow(
								${item.id},
								"${item.name}",
								${item.quotaCatalogsCreated},
								${item.quotaNbDocsPerCatalog},
								${item.quotaDriveBytesPerCatalog},
								${item.yearlyCostEuros},
								<s:property value="currentUserProfile.plan.id"/>,
								plansPopupNode,
								${item.availableForPurchase},
								"<s:property value="currentUserProfile.planStartDateStr"/>"
				);
		
			plansTable.appendChild(nodePres_plan${item.id});
			
	   }
	</c:forEach>
	
	let checkoutContainer = document.createElement("div");
	bodynode.append(checkoutContainer);
	let center = document.createElement("center");
	checkoutContainer.append(center);
	
	let checkoutBreakDownTitle = document.createElement("div");
	checkoutBreakDownTitle.id="checkout-breakdown-title";
	center.append(checkoutBreakDownTitle);
	checkoutBreakDownTitle.style="font-weight:bold;color:#339;font-size:1.6rem;margin-top=3rem;margin-bottom=1rem;";
	checkoutBreakDownTitle.style.display='none';
	checkoutBreakDownTitle.innerHTML="<hr><s:text name="Profile.plans.checkoutDetails" />"
	
	let checkoutBreakDownContainer = document.createElement("div");
	checkoutBreakDownContainer.id="checkout-breakdown-container";
	center.append(checkoutBreakDownContainer);
	
	let checkoutButtonContainer = document.createElement("div");
	checkoutButtonContainer.id="checkout-button-container";
	center.append(checkoutButtonContainer);
	
	let checkoutFooterContainer = document.createElement("div");
	checkoutFooterContainer.id="checkout-footer-container";
	checkoutFooterContainer.innerHTML="";
	checkoutFooterContainer.style="margin-top:10rem;";
	center.append(checkoutFooterContainer);
}
  </script>
