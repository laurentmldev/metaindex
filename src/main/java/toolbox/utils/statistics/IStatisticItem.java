package toolbox.utils.statistics;

import java.util.Date;

import toolbox.utils.IIdentifiable;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public interface IStatisticItem extends IIdentifiable<String> {	
	
	Date getTimestamp();
	Object getProperty(String propName); 
	void setProperty(String propName, Object propValue);
	
}
