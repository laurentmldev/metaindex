
importScripts('https://metaindex.fr:20000/webapp/public/commons/deps/pako.min.js');

var MX_WS_UPLOAD_FILE_MAX_RAW_SIZE_BYTE = 100000000;
var MX_WS_UPLOAD_MSG_SEP_MS = 50;
var MX_WS_UPLOAD_NBPACKETS_BEFORE_BREATH= 100;

function mxPerformCsvUpload(serverProcessingTaskId,csvRows,totalNbValidLines,finishCallback,fileMaxLinesForUpload) {
		
	fileMaxLinesForUpload=totalNbValidLines/100;
	if (fileMaxLinesForUpload<50) { fileMaxLinesForUpload=50; }
	if (fileMaxLinesForUpload>20000) { fileMaxLinesForUpload=20000; }
	
		// adaptive packets size
	    if (totalNbValidLines>100*fileMaxLinesForUpload) {
	    	fileMaxLinesForUpload=10*fileMaxLinesForUpload;
	    }
	    
    	let curLineNb=0;
    	let curLinesWsBuffer=[];
    	let nbLinesSent=0;
    	let msgNb=0;
    	
    	//console.log("### sendCsvContentsFunc before");
    	let sendCsvContentsFunc=function() {
    		
    		//console.log("### sendCsvContentsFunc 1");
    		if (nbLinesSent>=totalNbValidLines) {    			
    			return;
    		}
    		
    		//console.log("### sendCsvContentsFunc 2");
    		
    		let curLine=csvRows[curLineNb];
    		
    		// ignore first line (header)
    		if (curLineNb==0||curLine.length==0||curLine[0]=='#') { 
    			curLineNb++; 
    			sendCsvContentsFunc();
    			return;
    		}
    		nbLinesSent++;
    		
    		curLinesWsBuffer.push(curLine);
    		//console.log("### "+curLineNb+":"+curLine);
    		
    		if (curLineNb % fileMaxLinesForUpload==0 || nbLinesSent==totalNbValidLines) {
    			msgNb++;
    			//console.log("	### sending msg "+msgNb+" with "+curLinesWsBuffer.length+" lines ("+(nbLinesSent)+" / "+(totalNbValidLines)+")");
    			//console.log(curLinesWsBuffer);
    			
    			let strJsonArrayCsvLines=JSON.stringify(curLinesWsBuffer);
    			let bytesGzip = pako.gzip(strJsonArrayCsvLines,{ to: 'string' });    			
    			let base64BufferCsvLines = btoa(bytesGzip);    			
    			    			
    			let remainingDataToSend=base64BufferCsvLines;
    			let totalNbChunks=Math.ceil(remainingDataToSend.length/MX_WS_UPLOAD_FILE_MAX_RAW_SIZE_BYTE);
    			let curChunkNb=0;
    			
    			// msg might be divided into several raw chunks if some column contents is especially long.
    			while (remainingDataToSend.length>0) {
    				curChunkNb=curChunkNb+1;
    				let curChunktoSend=remainingDataToSend.substr(0,MX_WS_UPLOAD_FILE_MAX_RAW_SIZE_BYTE);
    				remainingDataToSend=remainingDataToSend.substr(MX_WS_UPLOAD_FILE_MAX_RAW_SIZE_BYTE);
    				
    				
    				//console.log("sending msg "+msgNb+" : CSV chunk "+curChunkNb+"/"+totalNbChunks+": "
    				//		+remainingDataToSend.length+"B remaining after that one. >> "+curChunktoSend);
    				
	    			let jsonData = { 
	    				 "compressedCsvLineStr" : curChunktoSend,
						 "processingTaskId" : serverProcessingTaskId,
						 "totalNbLines" : totalNbValidLines,
						 "totalNbChunks" : totalNbChunks,
						 "curChunkNb" : curChunkNb,
						 "msgNb" : msgNb
	    				};
	    				    			
	    			
	    			//console.log("### sending msg "+msgNb+" nbLinesSent="+nbLinesSent);
	    			//callback_NetworkEvent(MX_UPSTREAM_MSG);
	    			postMessage({"cmd":"SEND","jsonData":jsonData});
	    			//stompClient.send(mxWsPrefix+"/upload_filter_file_contents", {},
	    			//		JSON.stringify(jsonData));
	    			
	    			
    			}
    			//console.log("### nbLinesSent="+nbLinesSent);
    			
    			curLinesWsBuffer=[];
    		}
    		curLineNb++;
    		//sendCsvContentsFunc();
    		//console.log("### nbLine="+curLineNb);
    		//sendCsvContentsFunc();						 
			
			
    	}
    	
    	/*
    	while (nbLinesSent<totalNbValidLines) {
    		sendCsvContentsFunc();
    	}
    	*/
    	
    	var timer = setInterval(function() {
    			let nbIter=0;
    			while (nbLinesSent<totalNbValidLines && nbIter<MX_WS_UPLOAD_NBPACKETS_BEFORE_BREATH) {
    				nbIter++;
    				sendCsvContentsFunc();
    			}
	    		if (nbLinesSent>=totalNbValidLines) {
	    			clearInterval(timer);
	    			postMessage({"cmd":"END"});
	    		}
	    	},
	    	MX_WS_UPLOAD_MSG_SEP_MS);
    	
    	
    	
    	
    	
	}
	
onmessage = function(e) {
	let cmd=e.data.cmd;
	let params=e.data.params;
	

	if (cmd=="START") {
		
		console.log("starting upload of "+params.totalNbValidLines+" entries ...");

		
		mxPerformCsvUpload(params.serverProcessingTaskId,params.csvRows,params.totalNbValidLines,
				params.finishCallback,params.fileMaxLinesForUpload)
				
	}
  
}