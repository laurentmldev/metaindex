package toolbox.patterns.observer;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

@SuppressWarnings("rawtypes")
public interface IObserver<T extends IObservable> {
	
	public void notifyChange(T observedObject) throws InterruptedException;	
	
}

