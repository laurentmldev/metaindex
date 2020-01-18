package metaindex.websockets.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import toolbox.utils.IPair;
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;

public class WsMsgFileUpload_request {

	private Integer _clientFileId = 0;
	private List<String> _csvFieldsList= new ArrayList<String>();
	private Integer _totalNbEntries = 0;
	
	
	public Integer getClientFileId() {
		return _clientFileId;
	}

	public void setClientFileId(Integer clientFileId) {
		this._clientFileId = clientFileId;
	}

	public List<String> getCsvFieldsList() {
		return _csvFieldsList;
	}

	public void setCsvFieldsList(List<String> csvMapping) {
		this._csvFieldsList = csvMapping;
	}

	public Integer getTotalNbEntries() {
		return _totalNbEntries;
	}

	public void setTotalNbEntries(Integer totalNbEntries) {
		this._totalNbEntries = totalNbEntries;
	}

	
	
}
