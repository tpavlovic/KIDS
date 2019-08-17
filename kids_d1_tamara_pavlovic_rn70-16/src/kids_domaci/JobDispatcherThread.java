package kids_domaci;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class JobDispatcherThread implements Runnable{
	
	private ScanningJob jobQueue;
	private String jobWeb;
	private ScanningFileOrWeb job;
	private FileScannerThreadPool thredPool;
	private WebScannerThreadPool webThredPool;
	private static DirectoryParams params;
	private BlockingQueue<ScanningFileOrWeb> queue;
	public static AtomicBoolean dispatcherLoop = new AtomicBoolean(true);
	private Boolean tr = new Boolean(true);

	
	public JobDispatcherThread(BlockingQueue<ScanningFileOrWeb> queue) {
		this.queue = queue;
		this.thredPool = new FileScannerThreadPool();
		this.webThredPool = new WebScannerThreadPool(queue);
	}
	

	@Override
	public void run() {
		while(dispatcherLoop.get() == tr) {
			try {
				job = queue.take();
				if(job.getType().equals(ScanType.MESSAGE)) {
					webThredPool.setStopBoolean();
					dispatcherLoop.set(false);
					webThredPool.stopThredPool();
					thredPool.stopThredPool();
				} else if(job.getType().equals(ScanType.FILE)) {
					thredPool.addJob(job);
					
				} else if(job.getType().equals(ScanType.WEB)) {
					webThredPool.addJob(job);
						
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static void setBoolean() {
		dispatcherLoop.set(false);
	}

	
	

}
