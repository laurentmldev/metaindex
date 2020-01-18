package toolbox.patterns.observer;

@SuppressWarnings("rawtypes")
public interface IObserver<T extends IObservable> {
	
	public void notifyChange(T observedObject) throws InterruptedException;	
	
}

