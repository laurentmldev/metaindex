package toolbox.patterns.observer;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

@SuppressWarnings("rawtypes")
public interface IObservable<T extends IObserver> {
		
	
	public void notifyObservers() throws InterruptedException;
	public void addObserver(T newObserver) throws InterruptedException;
	public void removeObserver(T oldObserver);
	
}

