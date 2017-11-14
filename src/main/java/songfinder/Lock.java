package songfinder;

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

	private Map<Thread, Integer> readingThreads;
	private int writeAccesses;
	private int writeRequests;
	private Thread writingThread = null;

	/**
	 * Construct a new ReentrantLock.
	 */
	public Lock() {
		readingThreads = new HashMap<Thread, Integer>();
		writeAccesses = 0;
		writeRequests = 0;
		writingThread = null;
	}

	/**
	 * Returns true if the invoking thread holds a read lock.
	 * @return
	 */
	public synchronized boolean hasRead() {
		return readingThreads.containsKey(Thread.currentThread());
	}
	
	/**
	 * Returns true if some threads hold a read lock.
	 * @return
	 */
	public synchronized boolean hasReader() {
		return readingThreads.size() > 0;
	}

	/**
	 * Returns true if the invoking thread holds a write lock.
	 * @return
	 */
	public synchronized boolean hasWrite() {
		return Thread.currentThread() == writingThread;
	}
	
	/**
	 * Returns true if some threads hold a write lock.
	 * @return
	 */
	public synchronized boolean hasWriter() {
		return writingThread != null;
	}

	/**
	 * Non-blocking method that attempts to acquire the read lock.
	 * Returns true if successful.
	 * @return
	 */
	public synchronized boolean tryLockRead() {
        if (!hasWrite()) { 
            if (!canRead(Thread.currentThread())) {
                return false;
            }
        }
        readingThreads.put(Thread.currentThread(), (getReadCount(Thread.currentThread()) + 1));
        return true;
	}

	/**
	 * Non-blocking method that attempts to acquire the write lock.
	 * Returns true if successful.
	 * @return
	 */	
	public synchronized boolean tryLockWrite() {
		writeRequests++;
		if (!hasWrite()) { 
			if ((!canWrite(Thread.currentThread()) || hasReader())) {
				return false;
			}
		}
		writeAccesses++;
		writingThread = Thread.currentThread();
		return true;
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
		 Thread t1 = Thread.currentThread();
	        if(!hasRead()){
	            throw new IllegalMonitorStateException();
	        }
	        int accessCount = getReadCount(t1);
	        if(accessCount == 1){
	            readingThreads.remove(t1);
	        } else {
	            readingThreads.put(t1, (accessCount - 1));
	        }
	        notifyAll();
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
		if(!hasWrite()){
			throw new IllegalMonitorStateException();
		}
		writeAccesses--;
		if(writeAccesses == 0){
			writingThread = null;
		}
		notifyAll();
	}
	 
	/**
	 * Determine whether the thread can hold a read lock.
	 * @param t1
	 * @return
	 */
	private boolean canRead(Thread t1){
		if(hasWrite()) {
			return true;
		}
		if(hasWriter()) {
			return false;
		}
		if(hasRead()) {
			return true;
		}
		if(hasWriteRequests()) {
			return false;
		}
		return true;
	}
	 
	/**
	 * Determine whether the thread can hold a write lock.
	 * @param t1
	 * @return
	 */
	private boolean canWrite(Thread t1){
		if(hasReader()) {
			return false;
		}
		if(writingThread == null) {
			return true;
		}
		if(!hasWrite()) {
			return false;
		}
		return true;
	}
	 
	/**
	 * Get the number of read locks a thread holds.
	 * @param t1
	 * @return
	 */
	private int getReadCount(Thread t1){
		Integer count = readingThreads.get(t1);
		if(count == null) {
			return 0;
		}
		return count.intValue();
	}

	/**
	 * Determine whether there are threads waiting to acquire a write lock.
	 * @return
	 */
	private boolean hasWriteRequests(){
		return writeRequests > 0;
	}
}
