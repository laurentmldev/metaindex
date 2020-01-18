package toolbox.utils;

public class BasicPair<T1,T2> implements IPair<T1,T2> {
	private T1 _first;
	private T2 _second;
	
	public BasicPair() {};
	public BasicPair(T1 v1,T2 v2) { _first=v1; _second=v2; };
	@Override
	public T1 getFirst() { return _first; }
	@Override
	public T2 getSecond() { return _second; }
	@Override
	public void setFirst(T1 v) { _first=v; }
	@Override
	public void setSecond(T2 v) { _second=v; }
}
