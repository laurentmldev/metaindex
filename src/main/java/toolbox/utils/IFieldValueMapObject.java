package toolbox.utils;

import java.util.Set;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public interface IFieldValueMapObject extends IIdentifiable<String>  {	
	Object getValue(String fieldName);
	void setValue(String fieldName, Object value);
	Set<String> getFieldsList();

}
