package metaindex.data.commons.globals.guilanguage;

import toolbox.utils.IIdentifiable;

public interface IGuiLanguage extends IIdentifiable<Integer> {

	void setId(Integer id);
	void setName(String name);
	
	String getShortname();
	void setShortName(String shortName);
	
}
