package toolbox.utils;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;


public class StreamHandler<T> implements IStreamHandler<T> {
	List<T> _res = null;
	public StreamHandler(List<T> result) { _res=result;}
	@Override public void handle(List<T> d) { _res.addAll(d); }				
}

