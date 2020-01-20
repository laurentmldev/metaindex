package toolbox.utils.parsers;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

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
