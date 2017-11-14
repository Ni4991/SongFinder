package songfinder;

import java.util.LinkedList;

/**
 * a threadpool class.
 * @author nina luo
 *
 */
public class WorkQueue{
	
	private final int nThreads;
	private final PoolWorker[] threads;
	private final LinkedList<Runnable> queue;
	private volatile boolean hasShutdown, isTerminated;
 
	public WorkQueue(int nThreads){
        this.nThreads = nThreads;
        queue = new LinkedList<Runnable>();
        threads = new PoolWorker[nThreads];
        hasShutdown = false;
        isTerminated = false;
 
        for(int i = 0; i < nThreads; i++) {
            threads[i] = new PoolWorker();
            threads[i].start();
        }
    }
 
    /**
     * execute tasks.
     * @param r
     */
    public void execute(Runnable r) {
    	if(!hasShutdown) {
    		synchronized(queue) {
            queue.addLast(r);
            queue.notify();
    		}
    	}
    }
 
    /**
     * inner class of WorkQueue.
     * @author nina luo
     *
     */
    private class PoolWorker extends Thread {
        public void run() {
        	Runnable r;
 
        	while(true) {
                synchronized(queue) {
                    while (queue.isEmpty() && !hasShutdown) {
                        try{
                            queue.wait();
                        }
                        catch (InterruptedException ignored){
                             
                        }
                    }
                    if(queue.isEmpty() && hasShutdown) {
                    	break;
                    }
                    r = (Runnable) queue.removeFirst();
                }  
                try {
                	r.run();
                }
                catch (RuntimeException e) {
                   // log
                }
            }
        }
    }
    
    /**
     * shutdown workqueue. Do not allow to take new tasks.
     */
	public void shutdown() {
		hasShutdown = true;
	}

	/**
	 * wait for running threads to die.
	 * @throws InterruptedException
	 */
	public void awaitTermination() throws InterruptedException {
		synchronized(queue) {
			queue.notifyAll();
		}
		for(PoolWorker thread: this.threads) {
			try {
				thread.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		isTerminated = true;
	}
	
	/**
	 * get if the workqueue is terminated.
	 * @return
	 */
	public boolean isTerminated() {
		return isTerminated;
	}
}
