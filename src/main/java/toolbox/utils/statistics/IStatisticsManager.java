package toolbox.utils.statistics;

import java.util.List;
import java.util.Set;

import toolbox.exceptions.DataProcessException;
import toolbox.utils.IPeriodicProcess;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public interface IStatisticsManager extends IPeriodicProcess {	
	
	void handleStatItem(IStatisticItem s) throws DataProcessException;
	List<IStatisticItem> getStatItemsByName(String statName);
	Set<String> getStatItemsNames();
	void start();
	void stop();
}
