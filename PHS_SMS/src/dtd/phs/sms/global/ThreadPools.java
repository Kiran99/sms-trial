package dtd.phs.sms.global;

import java.util.LinkedList;

import dtd.phs.sms.util.Logger;

public class ThreadPools {
	private static final int N_THREAD = 3;
	private static volatile ThreadPools instance = null;
	private final PoolWorker[] threads = new PoolWorker[N_THREAD];
	private final LinkedList<Runnable> queue;
	
	private ThreadPools() {
		queue = new LinkedList<Runnable>();
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
		synchronized (queue) {
			queue.add(r);
			queue.notify();
		}
	}
	public class PoolWorker extends Thread {
		public void run() {
			Runnable r;
			while (true) {
				synchronized (queue) {
					while (queue.isEmpty() ) {
						try {
							queue.wait();
						} catch (InterruptedException e) {
							
						}
					}
					r = (Runnable) queue.removeFirst();
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
