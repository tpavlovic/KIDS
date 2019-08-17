package kids_domaci;

import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Test {

	
	static String[] query = new String[2];
	static MainCliThread mct = new MainCliThread();
	
	
	public static void main(String[] args) {
		
		ScanningJob jobQueue = new JobQueueImpl();
		BlockingQueue<ScanningFileOrWeb> queue = new ArrayBlockingQueue<ScanningFileOrWeb>(1000);
		
		Thread mainThread = new Thread(new MainCliThread(queue));
		Thread directoryCrawlerThread = new Thread(new DirectoryCrawlerThread(queue));
		Thread jobDispathcher = new Thread(new JobDispatcherThread(queue));
		
		mainThread.start();
		directoryCrawlerThread.start();
		jobDispathcher.start();
		
	}
}
