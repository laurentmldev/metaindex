package metaindex.websockets.users;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public interface IWsUserGuiMessage  {
	
	public enum MESSAGE_TYPE { TEXT, PROGRESS };
	
	public MESSAGE_TYPE getMsgType();

}