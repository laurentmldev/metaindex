package metaindex.app.control.websockets.items;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import metaindex.data.filter.IFilter;
import metaindex.app.Globals;
import metaindex.app.control.websockets.items.messages.*;
import metaindex.app.control.websockets.commons.AMxWSController;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.IDbSearchResult.SORTING_ORDER;
import toolbox.database.elasticsearch.ESDownloadProcess;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.BasicPair;
import toolbox.utils.IPair;
import toolbox.utils.IStreamHandler;
import toolbox.utils.StrTools;
import toolbox.utils.SysCall;
import toolbox.utils.filetools.FileSystemUtils;

@Controller
public class WsControllerItemsGenerateQuiz extends AMxWSController {
	
	private Log log = LogFactory.getLog(WsControllerItemsCsvDownload.class);
	
	private static final String PYTHON_SCRIPT_CSV2QUIZ = "csv2quiz/csv2quiz.py";
	 
	@Autowired
	public WsControllerItemsGenerateQuiz(SimpMessageSendingOperations messageSender) {
		super(messageSender);		
	}		
	
    @MessageMapping("/generate_quiz_request")
    @SubscribeMapping ("/user/queue/generate_quiz_response")    				    
    public void handleGenerateQuizRequest( SimpMessageHeaderAccessor headerAccessor, 
    											WsMsgGenerateQuiz_request requestMsg) {
    	Date now = new Date();
    	WsMsgGenerateQuiz_answer answer = new  WsMsgGenerateQuiz_answer(requestMsg);
    	
    	try {
	    	
	    	IUserProfileData user = getUserProfile(headerAccessor);
	    	
	    	// 0- store JSON file in tmp location
	    	String quizJsonConfigFileNamesPath="json_config_"+user.getId()+"_"+requestMsg.getRequestId()+".json";
	    	String quizJsonConfigFsPath=Globals.Get().getWebappsTmpFsPath()+quizJsonConfigFileNamesPath;
	    	FileOutputStream _outputstream = new FileOutputStream(quizJsonConfigFsPath);	    	
	    	_outputstream.write(requestMsg.getJsonQuizConfig().getBytes());
	    	_outputstream.close();
	    	
	    	// 1- generate corresponding CSV file
	    	String timestamp = StrTools.Timestamp(new Date());
    		String csvFileBasename=user.getCurrentCatalog().getName()+"-extract_"+timestamp+".csv";
    		String csvFileFsPath=Globals.Get().getWebappsTmpFsPath()+csvFileBasename;
    		    		
	    	ESDownloadProcess procTask = Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
					.getNewCsvExtractProcessor(
		    			user, 
		    			user.getCurrentCatalog(),
		    			 user.getText("Items.downloadItems.csv.extracting"),
		    			 csvFileFsPath,
		    			 requestMsg.getTermNamesList(),
		    			 new Long(requestMsg.getSize()),
		    			 new Long(requestMsg.getFromIdx()),
		    			 requestMsg.getQuery(),
		    			 WsControllerItemsCsvDownload.buildFilters(user, requestMsg),
		    			 WsControllerItemsCsvDownload.buildSortOrfer(user, requestMsg),
		    			 now);
	    	procTask.setShallSendUserProgressMsgs(false);
    		procTask.start();
    		// wait for end of processing
    		procTask.stop();
    		//String csvFileUrl="http://metaindex.fr/metaindex/downloads/xyzazertutj2454RHHF433a";
    		
    		Boolean success = procTask.isDataGenerated();
    		if (success!=true) {
    			answer.setIsSuccess(false);    	    	
	    		answer.setRejectMessage(user.getText("Items.server.quiz.unableToGenerateCsv"));	    		
	    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),
	    				"/queue/generate_quiz_response", 
						answer);
	    		// created files (if any) will be deleted after 10min by MxTmpFolderMMonitor 
	    		return;
    		}

			
	    	// 2- run python quiz generator script
    		String targetQuizFileBaseName="quiz_"+user.getCurrentCatalog().getName()+"_user"+user.getId();
    		if (requestMsg.getAsJson()) { targetQuizFileBaseName=targetQuizFileBaseName+".json"; } 
    		else { targetQuizFileBaseName=targetQuizFileBaseName+".qcm"; }
    		String targetQuizFileFullPath=Globals.Get().getWebappsTmpFsPath()+"/"+targetQuizFileBaseName;
    		
    		// erase existing result if any
    		File targetQuizFile = new File(targetQuizFileFullPath);    		
    		if (targetQuizFile.exists()) {
    			if (targetQuizFile.isDirectory()) { FileUtils.deleteDirectory(targetQuizFile); } 
    			else {	targetQuizFile.delete(); }    			
    		}
	    	String syscommand =
	    			"python3 "
	    			+Globals.Get().getToolboxFsPath()+PYTHON_SCRIPT_CSV2QUIZ
	    			+" -f -d "
	    			+" "+requestMsg.getNbQuestions()
	    			+" "+quizJsonConfigFsPath
	    			+" "+csvFileFsPath
	    			+" --output "+targetQuizFileFullPath
	    			+" --imgpath "+user.getCurrentCatalog().getLocalFsFilesPath();
			
			class LinesStdoutWriter implements IStreamHandler<String> {
							
				Integer _procId;
				Pattern progressTitlePattern = Pattern.compile("^PROGRESS: ([^:]+): ([0-9.]+)%");
				String _currentTitle="";
				Float _previousProgressMsg=-2.0F;
				public LinesStdoutWriter(Integer procId) {
					_procId=procId;
					
					
				}
				@Override
				public void handle(List<String> d) throws DataProcessException {
					for (String line : d) {
						if (line.startsWith("[")) {
							// skip info messages
							//user.sendGuiInfoMessage(line.replace("[", "").replace("]", ""));
						} else if (line.startsWith("ERROR")) {
							user.sendGuiErrorMessage(line);
						}
						else if (line.startsWith("WARNING")) {
							user.sendGuiWarningMessage(line);
						}
						else if (line.startsWith("PROGRESS")) {
							Matcher m = progressTitlePattern.matcher(line);
							//log.info("### -> "+line);
							if (!m.find()) {
								user.sendGuiErrorMessage("Quiz processing too returned invalid progress message");
								return;
							}
							String titleStr=m.group(1);
							if (!titleStr.equals(_currentTitle) ) {
								_currentTitle=titleStr;
								_previousProgressMsg=0.0F;
							}
							String pourcentageValueStr=m.group(2);
							Float pourcentageValue=Float.valueOf(pourcentageValueStr);
							if (pourcentageValue-_previousProgressMsg>=1.0F) {
								user.sendGuiProgressMessage(_procId,titleStr,pourcentageValue);
								//log.info("### "+titleStr+" : "+pourcentageValue+"%");
								_previousProgressMsg=pourcentageValue;
							}
						}
					}
				}				
			}
			log.info("Gen. Quiz Command: "+syscommand);
			SysCall pythonCall = new SysCall(syscommand,new LinesStdoutWriter(requestMsg.getRequestId()));
			try { 
				int exitStatus = pythonCall.run();
				if (exitStatus != 0) {
					if (targetQuizFile.exists()) {
		    			if (targetQuizFile.isDirectory()) { FileUtils.deleteDirectory(targetQuizFile); } 
		    			else {	targetQuizFile.delete(); }    			
		    		}
					throw new DataProcessException("python csv2quiz called returned exit status "+exitStatus);					
				}				
			} catch (IOException | InterruptedException | DataProcessException e) { 
				e.printStackTrace();
				if (targetQuizFile.exists()) {
	    			if (targetQuizFile.isDirectory()) { FileUtils.deleteDirectory(targetQuizFile); } 
	    			else {	targetQuizFile.delete(); }    			
	    		}
				answer.setIsSuccess(false);    	    	
	    		answer.setRejectMessage(user.getText("Items.server.quiz.unableToGenerateCsv"));	    		
	    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),
	    				"/queue/generate_quiz_response", 
						answer);
	    		// created files (if any) will be deleted after 10min by MxTmpFolderMMonitor
	    		return;
			}
			
	    	
	    	// 3- provide generated QCM file for download
			String quizFileUrl=Globals.Get().getWebAppsTmpUrl()+targetQuizFileBaseName;
			String quizFileFsPath=Globals.Get().getWebappsTmpFsPath()+targetQuizFileBaseName;
	    	answer.setQuizFileName(targetQuizFileBaseName);
	    	answer.setQuizFileUrl(quizFileUrl);
	    	answer.setQuizFileSizeMB(new Double(FileSystemUtils.GetTotalSizeBytes(quizFileFsPath)/1000000.0));
	    	answer.setIsSuccess(true);
    		this.messageSender.convertAndSendToUser(
    				headerAccessor.getUser().getName(),
    				"/queue/generate_quiz_response", 
    				answer);
    		//Globals.GetStatsMgr().handleStatItem(new CsvDownloadMxStat(user,user.getCurrentCatalog()));
    		log.info("generated quiz file '"+targetQuizFileBaseName+"' ("+answer.getQuizFileSizeMB()+"MB)");
	    	
	    } catch (DataProcessException | MessagingException | IOException e) 
		{
			log.error("Unable to process generate_quiz from '"+headerAccessor.getUser().getName()+"' : "+e);
			e.printStackTrace();
			
			answer.setIsSuccess(false);
			answer.setRejectMessage("Unable to genrate quiz file:"+e.getMessage());
			this.messageSender.convertAndSendToUser(
    				headerAccessor.getUser().getName(),
    				"/queue/generate_quiz_response", 
    				answer);
		}   
    }
    
    
}
