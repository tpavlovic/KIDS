package kids_domaci;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class JobQueueImpl implements ScanningJob{
	
	ArrayList<ScanningFileOrWeb> data = new ArrayList<>();
	private final int LIMIT = 100;
	HashMap<String, Integer> mapOfResults = new HashMap<String, Integer>();

	@Override
	public void enqueue(ScanningFileOrWeb s) {
		synchronized (this) {
			while(data.size() >= LIMIT) {
				try {
					wait(); //blokiramo red jer je pun
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			data.add(s);
			notifyAll(); //javljamo da smo ubacili stavku u red
			
		}
		
	}

	@Override
	public ScanningFileOrWeb dequeue() {
		ScanningFileOrWeb result;
		
		synchronized (this) {
			while (data.isEmpty()) { //red je prazan
				try {
					wait();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			result = data.get(0);
			System.out.println("Posao je: " + result.getQuery());
			data.remove(0);
			notifyAll(); //javljamo da smo uzeli stavku iz reda
		}
		
		return result;
	}
	
	

}
