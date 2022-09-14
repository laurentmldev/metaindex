package toolbox.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import toolbox.exceptions.DataProcessException;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class SysCall {
	
	private Log log = LogFactory.getLog(SysCall.class);
	
	private String _cmd;
	private IStreamHandler<String> _linesWriter;
	
	
	public SysCall(String cmd,IStreamHandler<String> linesWriter) {
		_cmd=cmd;
		_linesWriter=linesWriter;
	}
	
	/**
	 * 
	 * @return called cammand exit value
	 * @throws IOException
	 * @throws DataProcessException
	 * @throws InterruptedException
	 */
	public int run() throws IOException, DataProcessException, InterruptedException {
		Process syscallProcess=Runtime.getRuntime().exec(_cmd);
		
		BufferedReader isStdout = new BufferedReader(new InputStreamReader(syscallProcess.getInputStream()));
		BufferedReader isStderr = new BufferedReader(new InputStreamReader(syscallProcess.getErrorStream()));
		
		
		List<String> lines = new ArrayList<>();
		
		String line;		 
		while ((line = isStderr.readLine()) !=null) {
			lines.clear();
			lines.add(line);
			_linesWriter.handle(lines);
		}
		while ((line = isStdout.readLine()) !=null) {
			lines.clear();
			lines.add(line);
			_linesWriter.handle(lines);
		}
		
		return syscallProcess.waitFor();
		
	}
	
}
