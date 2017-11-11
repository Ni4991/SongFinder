package songfinder;

import java.util.LinkedList;

public class WorkQueue{
	
	private final int nThreads;
	private final PoolWorker[] threads;
    private final LinkedList<Runnable> queue;
    private volatile boolean hasShutdown, isTerminated;
 
    public WorkQueue(int nThreads){
        this.nThreads = nThreads;
        queue = new LinkedList();
        threads = new PoolWorker[nThreads];
        hasShutdown = false;
        isTerminated = false;
 
        for(int i = 0; i < nThreads; i++) {
            threads[i] = new PoolWorker();
            threads[i].start();
        }
    }
 
    public void execute(Runnable r) {
    	if(!hasShutdown) {
    		synchronized(queue) {
            queue.addLast(r);
            queue.notify();
    		}
    	}
    }
 
    private class PoolWorker extends Thread {
    	
        public void run() {
        	Runnable r = null;
 
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
 
                // If we don't catch RuntimeException, 
                // the pool could leak threads
                if(r != null) {
                	try {
                		r.run();
                	}
                	catch (RuntimeException e) {
                    // You might want to log something here
                	}
                }
                r = null;
            }
        }
}
    
	public void shutdown() {
		hasShutdown = true;
	}

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
	
	public boolean isTerminated() {
		return isTerminated;
	}
}
