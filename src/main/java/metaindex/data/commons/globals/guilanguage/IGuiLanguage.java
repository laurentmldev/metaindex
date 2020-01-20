package metaindex.data.commons.globals.guilanguage;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.utils.IIdentifiable;

public interface IGuiLanguage extends IIdentifiable<Integer> {

	void setId(Integer id);
	void setName(String name);
	
	String getShortname();
	void setShortName(String shortName);
	
}
