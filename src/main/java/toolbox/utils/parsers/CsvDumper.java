package toolbox.utils.parsers;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AStreamHandler;
import toolbox.utils.IFieldValueMapObject;

public class CsvDumper<T extends IFieldValueMapObject> extends AStreamHandler<T>   {

	private Log log = LogFactory.getLog(CsvDumper.class);
	
	private String _separator=";";
	private String _strMarker="\"";
	private FileOutputStream _outputstream;
	private List<String> _csvColumnsList=new ArrayList<>();

	List<String> _linesToWrite = new ArrayList<>();
	
	public CsvDumper(IUserProfileData u, 
						 String name, 
						 Long expectedNbActions,
						 List<String> csvColumnsList,
						 Date timestamp,
						 String targetFileName) throws DataProcessException { 
		super(u,name,expectedNbActions,timestamp,targetFileName,
						"Items.serverside.gencsvprocess.progress");
		
		this.setCsvColumnsList(csvColumnsList);		

		try {
			_outputstream = new FileOutputStream(this.getTargetFileName());
			String headerLine="#_id";
			for (String csvCol : this.getCsvColumnsList()) { headerLine+=this.getSeparator()+csvCol; }
			_linesToWrite.add(headerLine);
			flush();
		} catch (IOException e1) {
			e1.printStackTrace();
			this.abort();
			return;
		}
	}
	@Override
	public void beforeFirst() {

		
	}
	
	@Override
	public void handle(T d) {
		String curCsvLine=d.getId();
		Integer colIdx=0;
		for (String csvCol : this.getCsvColumnsList()) {
			curCsvLine+=this.getSeparator();
			Object val = d.getValue(csvCol);
			if (val!=null) { 
				String valStr=val.toString();
				if (valStr.contains(this.getSeparator())) { 
					if (valStr.contains(this.getStrMarker())) {
						valStr.replaceAll(this.getStrMarker(), "\\"+this.getStrMarker());
					}
					valStr=this.getStrMarker()+valStr+this.getStrMarker();  
				}
				curCsvLine+=valStr;
			}
			colIdx++;
		}
		_linesToWrite.add(curCsvLine);
	}
	
	@Override
	public void flush() throws IOException {
		
		// actually write lines in file
		for (String line : _linesToWrite) {
			_outputstream.write((line+"\n").getBytes());					
			//log.error(" ### dumping line "+line);
		}
		_linesToWrite.clear();
		_outputstream.flush();

	}
	
	@Override
	public void afterLast() throws IOException {
		_outputstream.close();
		
	}
	

	

	@Override
	public void sendErrorMessageToUser(String msg, List<String> details) {
		getActiveUser().sendGuiErrorMessage(msg, details);		
	}
	@Override
	public void sendErrorMessageToUser(String msg) {
		getActiveUser().sendGuiErrorMessage(msg);		
	}

	public List<String> getCsvColumnsList() {
		return _csvColumnsList;
	}

	public void setCsvColumnsList(List<String> csvColumnsList) {
		this._csvColumnsList = csvColumnsList;
	}

	public String getSeparator() {
		return _separator;
	}

	public void setSeparator(String separator) {
		this._separator = separator;
	}
	
	public String getStrMarker() {
		return _strMarker;
	}

	public void setStrMarker(String strMarker) {
		this._strMarker = strMarker;
	}
};
