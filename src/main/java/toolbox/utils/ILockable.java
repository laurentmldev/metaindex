package toolbox.utils;

public interface ILockable {	
	void acquireLock() throws InterruptedException;
	void releaseLock();
}
