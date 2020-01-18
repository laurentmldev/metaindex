package metaindex.data.term;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.commons.internationalization.AVocabularySet;

public class TermVocabularySet extends AVocabularySet {

	private Log log = LogFactory.getLog(TermVocabularySet.class);

	private Integer _termId;
	private String _name ="Term Name";	
	
	@Override
	public Integer getId() {
		return _termId;
	}
	public void setId(Integer termID) {
		_termId=termID;		
	}
	
	@Override
	public String getName() {
		return _name;
	}
	public void setName(String name) {
		_name=name;		
	}
	
}
