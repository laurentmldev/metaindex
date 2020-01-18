package toolbox.patterns.observer;

@SuppressWarnings("rawtypes")
public interface IObservable<T extends IObserver> {
		
	
	public void notifyObservers() throws InterruptedException;
	public void addObserver(T newObserver) throws InterruptedException;
	public void removeObserver(T oldObserver);
	
}

