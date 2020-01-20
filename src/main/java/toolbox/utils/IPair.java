package toolbox.utils;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public interface IPair<T1,T2> {	
	T1 getFirst();
	void setFirst(T1 v);
	T2 getSecond();
	void setSecond(T2 v);
}
