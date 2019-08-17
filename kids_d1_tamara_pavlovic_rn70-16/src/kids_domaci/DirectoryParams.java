package kids_domaci;

import java.util.ArrayList;

public class DirectoryParams {
	
	public static String[] keyWord = new String[10];
	public static String fileCorpus;
	public static long sleepTime;
	public static int sizeLimit;
	public static int hopCount;
	public static long refreshTime;
	public static String findFolder;
	public static String findURL;
	public static int modBreak;
	public static ScanType type;
	public static boolean resetHop = false;
	

	public static String[] getKeyWord() {
		return keyWord;
	}

	public static void setKeyWords(String[] keyWord) {
		DirectoryParams.keyWord = keyWord;
	}

	public static String getFileCorpus() {
		return fileCorpus;
	}

	public static void setFileCorpus(String fileCorpus) {
		DirectoryParams.fileCorpus = fileCorpus;
	}

	public static long getSleepTime() {
		return sleepTime;
	}

	public static void setSleepTime(int sleepTime) {
		DirectoryParams.sleepTime = sleepTime;
	}

	public static int getSizeLimit() {
		return sizeLimit;
	}

	public static void setSizeLimit(int sizeLimit) {
		DirectoryParams.sizeLimit = sizeLimit;
	}

	public static int getHopCount() {
		return hopCount;
	}

	public static void setHopCount(int hopCount) {
		DirectoryParams.hopCount = hopCount;
	}

	public static long getRefreshTime() {
		return refreshTime;
	}

	public static void setRefreshTime(int refreshTime) {
		DirectoryParams.refreshTime = refreshTime;
	}

	public static String getFindFolder() {
		return findFolder;
	}

	public static void setFindFolder(String findFolder) {
		DirectoryParams.findFolder = findFolder;
	}

	public static String getFindURL() {
		return findURL;
	}

	public static void setFindURL(String findURL) {
		DirectoryParams.findURL = findURL;
	}
	
	
	

}
