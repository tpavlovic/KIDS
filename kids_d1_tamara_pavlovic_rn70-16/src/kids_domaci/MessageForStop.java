package kids_domaci;

import java.util.Map;
import java.util.concurrent.Future;

public class MessageForStop implements ScanningFileOrWeb{
	
	private String query;
	private ScanType type;

	public MessageForStop(String query) {
		this.type = ScanType.MESSAGE;
		this.query = query;
	}

	@Override
	public ScanType getType() {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return query;
	}

	@Override
	public int getHop() {
		// TODO Auto-generated method stub
		return 0;
	}

}
