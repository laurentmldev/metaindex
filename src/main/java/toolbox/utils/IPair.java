package toolbox.utils;

public interface IPair<T1,T2> {	
	T1 getFirst();
	void setFirst(T1 v);
	T2 getSecond();
	void setSecond(T2 v);
}
