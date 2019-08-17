package kids_domaci;

import java.util.Map;
import java.util.concurrent.Future;

public interface ScanningFileOrWeb {
	
	ScanType getType();
	String getQuery();
	int getHop();

}
