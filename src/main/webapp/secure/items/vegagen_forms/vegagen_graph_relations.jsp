<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<c:url value="/" var="mxurl"/>

<script src="${mxurl}public/commons/deps/vegagen/vegagen_relations-graph_docs.js"></script>

<script type="text/javascript" >

// Stats
function left_buildNewRelationsGraphForm(catalogDescr) {
	let onValidFormCallback=function(fieldsValues) {
		
		let myGraph = new VegagenRelGraphDocs();
			myGraph.setNodesDefinition(catalogDescr.name,
										fieldsValues["form_vegagraph_refsFieldName"],
										fieldsValues["form_vegagraph_labelsFieldName"],
										fieldsValues["form_vegagraph_groupsFieldName"],
										fieldsValues["form_vegagraph_nodesFilterQuery"],
										fieldsValues["form_vegagraph_linksFilterExpr"]
										
			);		
		let vegaGraphSourceCode = myGraph.getVegaCode();
		copyToClipBoard(vegaGraphSourceCode);		
		footer_showAlert(SUCCESS, "Vega-Graph copied to clipboard, just past it in the Kibana 'Vega graph' editor!");
	}
	
	// definition of the fields in the form
	let fieldsList=[];
	
	// build list of fields by type ('REFERENCE' VS others)  
	let referenceFieldsList=[];
	let nonReferenceFieldsList=[];
	let sortedTermsNames = Object.keys(catalogDescr.terms).sort();
	for (var i=0;i<sortedTermsNames.length;i++) {
		let curFieldName=sortedTermsNames[i];
		let curTermDesc=catalogDescr.terms[curFieldName];
		if (curTermDesc.datatype=='REFERENCE') {
			referenceFieldsList.push(curTermDesc.name);
		}
		else (nonReferenceFieldsList.push(curTermDesc.name));				
	}

	// form input for references field 
	let refsFieldNameFormDef={ 	
			id:"form_vegagraph_refsFieldName", 
			termId:"refsFieldName", 
			important:"false",
			title:"Reference Field",	
			type:"dropdown",
			defaultValue:referenceFieldsList[0],
			values:referenceFieldsList
	};	
	fieldsList.push(refsFieldNameFormDef);
		
	// form input for nodes label field name
	let labelsPlaceHolder="";
	for (var refFiedIdx in nonReferenceFieldsList) {
		if (labelsPlaceHolder.length>0) { labelsPlaceHolder+= "+','+"; }
		labelsPlaceHolder+=nonReferenceFieldsList[refFiedIdx];
	}
	let labelsFieldNameFormDef={ 	
			id:"form_vegagraph_labelsFieldName", 
			termId:"labelsFieldName", 
			important:"false",
			title:"Nodes Labels Field",	
			type:"text",
			defaultValue:"",
			placeholder:labelsPlaceHolder,
			values:nonReferenceFieldsList
	};
	fieldsList.push(labelsFieldNameFormDef);
	
	// form input for nodes group field name
	let groupsFieldNameFormDef={ 	
			id:"form_vegagraph_groupsFieldName", 
			termId:"groupsFieldName", 
			important:"false",
			title:"Nodes Groups Field",	
			type:"dropdown",
			defaultValue:nonReferenceFieldsList[0],
			values:nonReferenceFieldsList
	};
	fieldsList.push(groupsFieldNameFormDef);
	
	// form input for nodes filter query
	let nodesFilterQueryFormDef={ 	
			id:"form_vegagraph_nodesFilterQuery", 
			termId:"nodesFilterQuery", 
			important:"false",
			title:"Nodes Filter Query",	
			type:"text",
			defaultValue:"",
			placeholder:"filter nodes"
	};
	fieldsList.push(nodesFilterQueryFormDef);
	
	// form input for links filter expression
	let linksFilterExprFormDef={ 	
			id:"form_vegagraph_linksFilterExpr", 
			termId:"linksFilterExpr", 
			important:"false",
			title:"Links Filter Expression",	
			type:"text",
			defaultValue:"",
			placeholder:"ex: target.name=='Nounours'"
	};
	fieldsList.push(linksFilterExprFormDef);
	
	
	
	// generate form
	let popupForm=MxGuiPopups.newMultiInputsPopup("<s:text name="Items.graphsGenerator"></s:text>",
			fieldsList,onValidFormCallback);
	
	return popupForm;
}

</script>


