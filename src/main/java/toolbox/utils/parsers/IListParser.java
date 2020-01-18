package toolbox.utils.parsers;

import java.util.ArrayList;
import java.util.List;

public interface IListParser<TFrom,TTo> {
	
	public class ParseException extends Exception {
		List<String> _parseErrors = new ArrayList<String>();
		ParseException(String msg) { super(msg); }
		ParseException(List<String> parseErrors) { 
			super();
			_parseErrors=parseErrors;
		}
		public List<String> getParseErrors() { return  _parseErrors; }
	};
	
	
	List<TTo> parseAll(List<TFrom> input) throws ParseException;

	TTo parse(TFrom input) throws ParseException;
}
