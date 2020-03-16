package toolbox.utils.parsers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;

import metaindex.app.control.websockets.users.WsControllerUser.CATALOG_MODIF_TYPE;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.IDbItem;
import toolbox.database.elasticsearch.ESDataSource;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;
import toolbox.utils.IFieldValueMapObject;
import toolbox.utils.parsers.ACsvParser;

public class CsvDumper<T extends IFieldValueMapObject> extends AProcessingTask   {

	private Log log = LogFactory.getLog(Catalog.class);
	
	private Semaphore _postingDataLock = new Semaphore(1,true);	
	private Semaphore _stoppingProcessingLock = new Semaphore(1,true);
	
	private String _targetFileName="";
	private String _separator=";";
	private String _strMarker="\"";
	
	private List<String> _csvColumnsList=new ArrayList<>();

	private List<T> _dataToDump = new ArrayList<>();
	
	private FileOutputStream _outputstream; 
	
	public CsvDumper(IUserProfileData u, 
						 String name, 
						 Long expectedNbActions,
						 List<String> csvColumnsList,
						 Date timestamp,
						 String targetFileName) throws DataProcessException { 
		super(u,name);
		this.setTargetNbData(expectedNbActions);
		this.setTargetFileName(targetFileName);
		this.setCsvColumnsList(csvColumnsList);
		this.addObserver(u);
		
	}
	
	public void postDataToDump(List<T> o) throws InterruptedException {
		_postingDataLock.acquire();
		_dataToDump.addAll(o);
		addReceivedNbData(new Long(o.size()));
		//log.error("### added "+o.size()+" data to dump -> "+_dataToDump.size()+" to be dumped");
		_postingDataLock.release();
		Thread.sleep(10);
	}
	
	public void lock() throws InterruptedException { _postingDataLock.acquire(); }
	public void unlock() { _postingDataLock.release(); }
	

	
	@Override
	public void run()  { 
		
		try {
			_outputstream = new FileOutputStream(this.getTargetFileName());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			this.abort();
			return;
		}
		
		getActiveUser().sendGuiProgressMessage(
    			getId(),
    			getActiveUser().getText("Items.serverside.gencsvprocess.progress", getName()),
    			AProcessingTask.pourcentage(getProcessedNbData(),getTargetNbData()));
		
		
		while (!this.isAllDataProcessed()) {
			try { 
				// wait for having received all data to be processed 
				Thread.sleep(200);
				
				List<String> linesToWrite = new ArrayList<>(); 
				
				if (this.getProcessedNbData()==0) {
					String headerLine="#_id";
					for (String csvCol : this.getCsvColumnsList()) { headerLine+=this.getSeparator()+csvCol; }
					linesToWrite.add(headerLine);
					
				}
				_postingDataLock.acquire();
				//if (_dataToDump.size()>0) { log.error("#### dumping "+_dataToDump.size()+" lines"); }
				for (IFieldValueMapObject d : _dataToDump) {
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
					linesToWrite.add(curCsvLine);
				}				
				_dataToDump.clear();
				_postingDataLock.release();
				
				// actually write lines in file
				for (String line : linesToWrite) {
					if (!line.startsWith("#")) { addProcessedNbData(1L); }
					_outputstream.write((line+"\n").getBytes()); 
				}
				_outputstream.flush();
				// --------
				
				getActiveUser().sendGuiProgressMessage(
		    			getId(),
		    			getActiveUser().getText("Items.serverside.gencsvprocess.progress", getName()),
		    			AProcessingTask.pourcentage(getProcessedNbData(), getTargetNbData()), true /*processing continuing*/);
				
			} catch (InterruptedException | IOException e) { 
				e.printStackTrace(); 
			}
		}
		
		stop();

	}

	@Override
	/**
	 * Blocking, wait for currently posted data is finished to be processed
	 */
	public void stop() {
		
		try {
			while (!this.isAllDataProcessed()) {
				Thread.sleep(200);
			}
			_stoppingProcessingLock.acquire();
			_outputstream.close();	
			_stoppingProcessingLock.release();
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
			_stoppingProcessingLock.release();			
		}
		
		getActiveUser().sendGuiProgressMessage(
    			getId(),
    			getActiveUser().getText("Items.serverside.gencsvprocess.progress", getName()),
    			AProcessingTask.pourcentage(getProcessedNbData(), getTargetNbData()), false /*processing ended*/);
	
		getActiveUser().removeProccessingTask(this.getId());
	}
	@Override
	public void abort() {
		
		getActiveUser().sendGuiProgressMessage(
    			getId(),
    			getActiveUser().getText("Items.serverside.gencsvprocess.progress", getName()),
    			AProcessingTask.pourcentage(getProcessedNbData(), getTargetNbData()), false /*processing ended*/);
		
		getActiveUser().removeProccessingTask(this.getId());
	}
	

	public String getTargetFileName() {
		return _targetFileName;
	}
	public void setTargetFileName(String targetFileName) {
		this._targetFileName = targetFileName;
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
