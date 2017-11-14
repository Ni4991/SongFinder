package concurrent;

import java.util.HashMap;
import java.util.Map;

/**
 * A read/write lock that allows multiple readers, disallows multiple writers, and allows a writer to 
 * acquire a read lock while holding the write lock. 
 * 
 * A writer may also aquire a second write lock.
 * 
 * A reader may not upgrade to a write lock.
 * @author nina luo
 * 
 */
public class Lock {

	private Map<Thread, Integer> readingThreads, writingThreads;

	/**
	 * Construct a new ReentrantLock.
	 */
	public Lock() {
		readingThreads = new HashMap<Thread, Integer>();
		writingThreads = new HashMap<Thread, Integer>();
	}

	/**
	 * Returns true if the invoking thread holds a read lock.
	 * @return
	 */
	public synchronized boolean hasRead() {
		return readingThreads.containsKey(Thread.currentThread());
	}

	/**
	 * Returns true if the invoking thread holds a write lock.
	 * @return
	 */
	public synchronized boolean hasWrite() {
		return writingThreads.containsKey(Thread.currentThread());
	}

	/**
	 * Non-blocking method that attempts to acquire the read lock.
	 * Returns true if successful.
	 * @return
	 */
	public synchronized boolean tryLockRead() {
		if(writingThreads.isEmpty() || this.hasWrite()) {
			if(!readingThreads.containsKey(Thread.currentThread())) {
				readingThreads.put(Thread.currentThread(), 1);
			} else {
				readingThreads.put(Thread.currentThread(), readingThreads.get(Thread.currentThread()) + 1);
			} 
			return true;
		}
		return false;
	}

	/**
	 * Non-blocking method that attempts to acquire the write lock.
	 * Returns true if successful.
	 * @return
	 */	
	public synchronized boolean tryLockWrite() {
		if(this.hasWrite() || writingThreads.isEmpty() && readingThreads.isEmpty()) {
			if(!writingThreads.containsKey(Thread.currentThread())) {
				writingThreads.put(Thread.currentThread(), 1);
			 } else {
				 writingThreads.put(Thread.currentThread(), writingThreads.get(Thread.currentThread()) + 1);
			 }
			return true;
		}
		return false;
	 }

	 /**
	  * Blocking method that will return only when the read lock has been 
	  * acquired.
	  */	 
	 public synchronized void lockRead() {
		 while(!tryLockRead()){
			 try {
				 wait();
			 } catch (InterruptedException e) {
				 e.printStackTrace();
			 }
		 }
	}

	 /**
	  * Releases the read lock held by the calling thread. Other threads may continue
	  * to hold a read lock.
	  */
	 public synchronized void unlockRead() throws IllegalMonitorStateException {
		 if(!readingThreads.containsKey(Thread.currentThread())) throw new IllegalMonitorStateException();
		 readingThreads.put(Thread.currentThread(), readingThreads.get(Thread.currentThread()) - 1);
		 if(readingThreads.get(Thread.currentThread()) == 0) readingThreads.remove(Thread.currentThread());
		 this.notifyAll();
	 }

	/**
	 * Blocking method that will return only when the write lock has been 
	 * acquired.
	 */
	public synchronized void lockWrite() {
		while(!tryLockWrite()){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Releases the write lock held by the calling thread. The calling thread may continue to hold
	 * a read lock.
	 */
	public synchronized void unlockWrite() throws IllegalMonitorStateException {
		 if(!writingThreads.containsKey(Thread.currentThread())) throw new IllegalMonitorStateException();
		 writingThreads.put(Thread.currentThread(), writingThreads.get(Thread.currentThread()) - 1);
		 if(writingThreads.get(Thread.currentThread()) == 0) writingThreads.remove(Thread.currentThread());
		 this.notifyAll();
	}
}
