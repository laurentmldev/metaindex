package toolbox.utils.parsers;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;

import toolbox.exceptions.DataProcessException;

public interface IListParser<TFrom,TTo> {
	
	public class ParseException extends DataProcessException {
		public ParseException(Exception e) { super(e); }
		public ParseException(String msg) { super(msg); }
		public ParseException(String msg, Exception e) { super(msg, e); }
		public ParseException(String msg,List<String> errorDetails) { super(msg, errorDetails); }
		
	};
	
	
	List<TTo> parseAll(List<TFrom> input) throws ParseException;

	TTo parse(TFrom input) throws ParseException;
}
