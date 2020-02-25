package metaindex.data.commons.statistics;


import toolbox.utils.statistics.ASimpleStatisticsManager;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class MxStatisticsManager extends ASimpleStatisticsManager {		

	@Override
	public Integer getPeriodicProcessPeriodSec() {
		return 10;
	}
	
	@Override
	public String getName() {
		return "MetaindeX Simple Statistics Manager";
	}

	@Override
	/// Meaningless in this context of usage
	public Integer getId() { return 0; }
	
}
