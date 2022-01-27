package toolbox.utils.parsers;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


/**
 * Handle parsing warnings on the fly.
 * Used to notify of some warnings without block the whole parsing progress
 * @author laurentml
 *
 */
public interface IParseWarningsHandler {
	
	public static enum PARSE_WARNING_TYPES { TRUNCATED_CONTENTS };
	
	public void handleParseWarning(String itemId, String fieldName, 
					IFieldsListParser.PARSING_FIELD_TYPE fieldType, 
					PARSE_WARNING_TYPES warningType);
	
	public void handleStop();
}
