<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript" src="public/deps/prototype.js"></script>

    <script type="text/javascript">
    
    	// our websocket
    	var elData_ws;
    	
    	// some global info about current element
    	curElementData=null;
    	curElementDatasets={};
    	curElementMetadatas={};
    	previousElementId=null;
    	
    	function connectElementDataServer(elDataServerUri) {
			//console.log("Connecting to ElementData server on "+elDataServerUri)
    		elData_ws = new WebSocket(elDataServerUri);
    		elData_ws.onopen = function() {
            	console.log("connected to WS element-data");
            }
    		elData_ws.onmessage = function(e) {
            	handleMsg(e.data);
            }
    		elData_ws.onclose = function() { 
            	console.log("closed element-data connection\n");
            	elData_ws = null;                
            }                       
		}
    	    	
    	function handleMsg(msgStr) {
    		
    		//console.log("Received a msg : "+msgStr)
    		msg=JSON.parse(msgStr);
    		
        	
    		if (msg.msgType == 'element-login') { 
    			element_send_sessionid(msg);
    			changeElement(0)
    		}
    		else if (msg.msgType == 'element-contents') {
    			hideEditModeZones();
    			if (curElementData!=null) { previousElementId=curElementData.elementId; }
    			curElementData=msg;
    			curElementDatasets={};
    			curElementMetadatas={}
    			handleElementData(msg);
    			
    		}
    		else if (msg.msgType == 'element-datasets') {
    			selected_dataset={};
    			for (var i=0;i<msg.datasets.length;i++) {
    				var curDatasetData = msg.datasets[i];    				
    				handleDatasetData(curDatasetData);
    				selected_dataset[curDatasetData.datasetId]=false;
    				curElementDatasets[curDatasetData.datasetId]=msg.datasets[i];
    				
    				// store metadatas descriptions
    				if (curDatasetData.metadatas!=null) {
	    				for (var j=0;j<curDatasetData.metadatas.length;j++) {    					
	    					var curMetadata = curDatasetData.metadatas[j];
	    					curElementMetadatas[curMetadata.metadataId]=curMetadata;        				
	    				}
	    				
	    				// add metadata contents for each columns of the dataset
	    				handleMetadadatas(curDatasetData.datasetId, curDatasetData.columnsMetadata);
    				}
    			}
			}    		
    			
    		else(console.log('Error: received unhandled elementData msg '+msg.msgType))
    	}
    	
    	function element_send_sessionid(msg) {
    		msg.sessionId="<s:property value='loggedUserProfile.sessionId'/>";
    		elData_ws.send(JSON.stringify(msg));    			
    	}
    	
        function changeElement(elementId) {
        	
            if (!window.WebSocket) {
                alert("FATAL: WebSocket not natively supported!");
            }

            JSONstr="{ 	  'msgType':'element-getcontents' , "
            			+"'sessionId' : '<s:property value='loggedUserProfile.sessionId'/>',"
						+"'elementId' : '"+elementId+"'}";
			
			//console.log("Sending msg : "+JSONstr)
			elData_ws.send(JSONstr);
			clearMultiSelection();
						
        };
    	        
        // Refresh contents of selected element in the workspace page
    	function handleElementData(elementDataMsg) {   
    		updateElementThumbnail(elementDataMsg,previousElementId);
    		updateElementDataContents(elementDataMsg);
			updateElementProperties(elementDataMsg);			    		
        }
        
    	// Refresh contents of selected element in the workspace page
    	function handleDatasetData(datasetDataMsg) {    		
    		elementContents_addDataset(datasetDataMsg);    		
			elementProperties_addDataset(datasetDataMsg);			
        }
        
    	function handleMetadadatas(datasetId, datasetColumns) {
    		
    		// for each column
    		for (var colId=0;colId<datasetColumns.length;colId++) {
    			metadatasList=datasetColumns[colId];
    			for (var idx=0;idx<metadatasList.length;idx++) {
    				withDropzoneBefore=(idx==0);
    				var metadataId=metadatasList[idx];
    				elementContents_addMetadataColumn(datasetId,colId+1,metadataId,withDropzoneBefore);
    				elementProperties_addMetadata(curElementMetadatas[metadataId]);
    			}
    		}
    		 
     	}
		
    </script>
  	
