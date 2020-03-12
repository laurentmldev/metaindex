package toolbox.utils;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;

import toolbox.exceptions.DataProcessException;

public interface IStreamHandler<TData> {
	
	public void handle(List<TData> d);	

}
