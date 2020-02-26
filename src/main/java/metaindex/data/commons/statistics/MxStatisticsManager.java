package metaindex.data.commons.statistics;


import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.commons.globals.Globals;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.statistics.ASimpleStatisticsManager;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class MxStatisticsManager extends ASimpleStatisticsManager {		

	private Log log = LogFactory.getLog(MxStatisticsManager.class);
	
	@Override
	public Integer getPeriodicProcessPeriodSec() {
		return new Integer(Globals.GetMxProperty("mx.statistics.update_period_sec").replaceAll(" ", ""));
	}
	
	@Override
	public String getName() {
		return "MetaindeX Statistics Manager";
	}

	@Override
	/// Meaningless in this context of usage
	public Integer getId() { return 0; }
	
	@Override
	/**
	 * Send email to server admin with current statistics
	 */
	public void doPeriodicProcess() throws DataProcessException {
		
		log.info(this.getDetailsStr());
		try {
			String msgBodyHtml="<br/><h3>MetaindeX Statistics Report - "+new Date()+"</h3><br/><br/>"
				+this.getDetailsStr().replaceAll("\n", "<br/>");
			Globals.Get().sendEmail(Globals.GetMxProperty("mx.mailer.admin_recipient"), 
								"Server Statistics for "+Globals.GetMxProperty("mx.host"), 
								msgBodyHtml);
		} catch (Exception e) {
			log.error("Unable to send statistics email : "+e.getMessage());
			//e.printStackTrace();
		}
		
		super.doPeriodicProcess();				
	}
}
