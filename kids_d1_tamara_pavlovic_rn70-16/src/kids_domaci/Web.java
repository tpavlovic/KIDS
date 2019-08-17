package kids_domaci;

import java.util.Map;
import java.util.concurrent.Future;

public class Web implements ScanningFileOrWeb{
	
	private ScanType type;
	private String query;
	private int hop;
	private long refresh;

	public Web(String query, int hop) {
		this.type = ScanType.WEB;
		this.query = query;
		this.hop = hop;
	}

	@Override
	public ScanType getType() {
		return type;
	}

	@Override
	public String getQuery() {
		return query;
	}

	@Override
	public int getHop() {
		// TODO Auto-generated method stub
		return hop;
	}

	
	
}
