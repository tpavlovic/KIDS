package kids_domaci;

import java.util.Map;
import java.util.concurrent.Future;

public class FileFile implements ScanningFileOrWeb{

	private ScanType type;
	private String query;
	
	public FileFile(String query) {
		this.type = ScanType.FILE;
		this.query = query;
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
		return 0;
	}
	

}
