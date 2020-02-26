package metaindex.data.commons.statistics;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import metaindex.data.userprofile.IUserProfileData;
import toolbox.utils.IIdentifiable;
import toolbox.utils.statistics.IStatisticItem;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public abstract class AMxStatisticItem implements IStatisticItem {

	private Map<String,Object> _properties = new HashMap<>();
	
	protected String hashString(String toHash) {
		if (toHash==null || toHash.length()==0) { return ""; }
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder.encode(toHash);
	}
	
	public AMxStatisticItem(IUserProfileData u) {
		// hash the user id to ensure no nominal statistic is possible
		this.setProperty("session", hashString(u.getHttpSessionId()));
		this.setProperty("timestamp", new Date()); 
	}
	
	@Override
	public String getId() { return ""; } // not relevant for this class

	@Override
	public Date getTimestamp() { return new Date(); }

	@Override
	public Object getProperty(String propName) {
		return _properties.get(propName);
	}

	@Override
	public void setProperty(String propName, Object propValue) {
		_properties.put(propName, propValue);
	}	
	
}
