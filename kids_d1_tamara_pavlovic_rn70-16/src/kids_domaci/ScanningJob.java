package kids_domaci;

import java.util.Map;
import java.util.concurrent.Future;

public interface ScanningJob {
	
	void enqueue(ScanningFileOrWeb s);
	ScanningFileOrWeb dequeue();

}
