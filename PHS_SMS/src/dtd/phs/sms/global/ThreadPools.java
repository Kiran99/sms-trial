package dtd.phs.sms.global;

import java.util.Stack;

import dtd.phs.sms.util.Logger;

public class ThreadPools {
	private static final int N_THREAD = 3;
	private static volatile ThreadPools instance = null;
	private final PoolWorker[] threads = new PoolWorker[N_THREAD];
	private final Stack<Runnable> jobsStack;
	
	private ThreadPools() {
		jobsStack = new Stack<Runnable>();
		for(int i = 0; i < N_THREAD ; i++) {
			threads[i] = new PoolWorker();
			threads[i].start();
		}
	}
	
	public static ThreadPools getInstance() {
		if ( instance == null ) {
			instance = new ThreadPools();
		}
		return instance;
	}
	
	public void add(Runnable r) {
		synchronized (jobsStack) {
			jobsStack.add(r);
			jobsStack.notify();
		}
	}
	
	public class PoolWorker extends Thread {
		public void run() {
			Runnable r;
			while (true) {
				synchronized (jobsStack) {
					while (jobsStack.isEmpty() ) {
						try {
							jobsStack.wait();
							break;
						} catch (InterruptedException e) {
							
						}
					}
					r = (Runnable) jobsStack.pop();
				}
				
				try {
					r.run();
				} catch (RuntimeException e) {
					Logger.logException(e);
				}
			}
		}
	}

}
