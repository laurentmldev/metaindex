package metaindex.app.control.websockets.commons;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import metaindex.app.Globals;
import metaindex.app.control.websockets.commons.AMxWSController;
import toolbox.utils.IRunnable;

@Controller
public class WsControllerHeartbeat extends AMxWSController implements IRunnable {
	
	private final Integer HEARTBEAT_DURATION_SEC=3;
	private Log log = LogFactory.getLog(WsControllerHeartbeat.class);
	private Boolean _shouldBeRun=true;
	private RunnerThread _thread = null;
	private WsMsgHeartbeat_answer heartbeatMsg = new WsMsgHeartbeat_answer();
	
	private class RunnerThread extends Thread {		
		WsControllerHeartbeat _processingToBeRun;
		public RunnerThread(WsControllerHeartbeat p) { 
			super("HeartbeatSender");
			_processingToBeRun=p;
		}
		@Override
		public void run() { _processingToBeRun.run(); }
	}
		
	@Autowired
	public WsControllerHeartbeat(SimpMessageSendingOperations messageSender) {
		super(messageSender);
		_thread=new RunnerThread(this);
		// automatically start when instanciated by Spring framework (autowired)
		_thread.start();
	}		
		
    @SendTo( "/queue/heartbeat")
    public void sendHearbeat() throws Exception {
    	heartbeatMsg.incrCount();
    	heartbeatMsg.setApplicationStatus(Globals.Get().getApplicationStatus());
		this.messageSender.convertAndSend("/queue/heartbeat", heartbeatMsg);
        	
    }

	@Override
	public void run() {
		while(_shouldBeRun) {
			try {
				Thread.sleep(HEARTBEAT_DURATION_SEC*1000);
				sendHearbeat();
			} catch (Exception e) {
				e.printStackTrace();
				_shouldBeRun=false;
			}
		}		
	}

	@Override
	public Boolean isRunning() {
		return _thread.isAlive();
	}

    
}
