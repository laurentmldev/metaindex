package metaindex.app.control.catalogdrive;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


public interface ICatalogDrive {
	public void start();
	public void stop();			
	public Integer getPort();	
}