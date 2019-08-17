package kids_domaci;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainCliThread implements Runnable{
	
	//skip =
	int mod = 0, skip = 0;
	String keyWords, fileCorpusPrefix;
	String strSsleepTime, strSizeLimit, strHopCount, strUrlRefreshTime;
	long sleepTime;
	int sizeLimit, hopCount, urlRefreshTime;
	ArrayList<String> result = new ArrayList<String>();
	private ScanningJob jobQueue;
	private BlockingQueue<ScanningFileOrWeb> queue;
	private AtomicBoolean mainLoop = new AtomicBoolean(true);
	private Boolean tr = new Boolean(true);
	
	public MainCliThread(BlockingQueue<ScanningFileOrWeb> queue) {
		this.queue = queue;
	}
	
	public MainCliThread() {
	}
	
	@Override
	public void run() {
		try {
			loadFromFile("fileProperties.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("\nEnter the command: ");
		Scanner sc = new Scanner(System.in);
		String command;
		while(mainLoop.get() == tr) {
			WebScannerThreadPool.refreshUrls(WebScannerThreadPool.listOfURLs);
			
			command = sc.nextLine();
			String[] query = command.split(" ", 0);
			
			switch (query[0]) {
			case "ad":
				if(query.length != 2) {
					System.err.println("Komanda nema dva argumenta!");
				} else {
					DirectoryCrawlerThread.listOfDirectories.add(query[1]);
					DirectoryParams.modBreak = 1;
				}
				break;
			case "aw": 
				if(query.length != 2) {
					System.err.println("Komanda nema dva argumenta!");
				} else {
					
					if(isValid(query[1])) {
						try {
							queue.put(new Web(query[1], DirectoryParams.hopCount));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						System.err.println("Url nije validan!");
					}
				}
				DirectoryParams.resetHop = true;
				break;
			case "stop":
				try {
					queue.put(new MessageForStop("stop"));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				DirectoryCrawlerThread.setBoolean();
				mainLoop.set(false);
				sc.close();
				break;
			default:
				if(query.length != 2) {
					System.err.println("Komanda nema dva agrumenta!");
				} else {
					System.err.println("Komanda ne postoji");
				}
				break;
			}
		}
		
	}
	
	public void loadFromFile(String string) throws Exception{
		
		File file = new File(string);
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		if(!file.exists()) {
			System.out.println(file + "doesn't exist");
		}
		
		//Deklarise jedan ili vise objekta. Resurs je objekat koji mora biti zatvoren na kraju 
		//programa. Ovim s eosigurava da ce svaki resurs biti zatvoren.
		try(FileReader f = new FileReader(file)){
			StringBuffer sb = new StringBuffer();
			
			while(f.ready()) { //Govori nam da je spreman za citanje
				char c = (char) f.read(); //cita jedan karakter, kada dodje do kraja vraca -1
				
				if(c == '=') {
					mod = 1;
					skip = 1;
				}
				
				if(c == '\n') {
					result.add(sb.toString());
					sb = new StringBuffer();
					mod = 0;
				} else {
					if(mod == 1) {
						if(skip == 1) {
							skip = 0;
							continue;
						}
						sb.append(c);
					}
				}
			}
			
			if(sb.length() > 0) {
				result.add(sb.toString());
			}
			
			f.close();
			
		}
		
		//keywords
		keyWords = result.get(0);
		String[] arrkeyWords = keyWords.split(",", 0);
		DirectoryParams.keyWord = arrkeyWords;
		//file_corpus_prefix
		fileCorpusPrefix = result.get(1);
		DirectoryParams.fileCorpus = fileCorpusPrefix;
		//dir_crawler_sleep_time
		strSsleepTime = result.get(2);
		sleepTime = convertStringtoNumber(strSsleepTime);
		DirectoryParams.sleepTime = sleepTime;
		//file_scanning_size_limit
		strSizeLimit = result.get(3);
		sizeLimit = convertStringtoNumber(strSizeLimit);
		DirectoryParams.sizeLimit = sizeLimit;
		//hop_count
		strHopCount = result.get(4);
		hopCount = convertStringtoNumber(strHopCount);
		DirectoryParams.hopCount = hopCount;
		//url_refresh_time
		strUrlRefreshTime = result.get(5);
		urlRefreshTime = convertStringtoNumber(strUrlRefreshTime);
		DirectoryParams.refreshTime = urlRefreshTime;
		
		System.out.println("Kljucne reci su: " + DirectoryParams.keyWord[0] + ", " + DirectoryParams.keyWord[1] + ", " + DirectoryParams.keyWord[2]);//arrkeyWords[0] + ", " + arrkeyWords[1] + ", " + arrkeyWords[2]);
		System.out.print("Prefix: " + DirectoryParams.fileCorpus);
		System.out.println("Sleep time: " + DirectoryParams.sleepTime);
		System.out.println("Size limit: " + DirectoryParams.sizeLimit);
		System.out.println("Hop count: " + DirectoryParams.hopCount);
		System.out.println("Refresh time: " + DirectoryParams.refreshTime);
		
		br.close();
	}
	
	
	//metoda koja prosledjen string konvertuje u broj
	private int convertStringtoNumber(String strings) {
		int result = 0, counter = 1, number;
		
		for (int i = strings.length()-2; i >= 0; i--) {
			char c = strings.charAt(i);
			number = (int) (c - '0');
			result += (number * counter);
			counter *= 10;
		}
		
		return result;
		
	}
	
	private boolean isValid(String url) {
		try {
			Document doc = Jsoup.connect(url).get();
			return true;
			
		} catch (Exception e) {
			return false;
		}
	}


	public String getKeyWords() {
		return keyWords;
	}


	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}


	public String getFileCorpusPrefix() {
		return fileCorpusPrefix;
	}


	public void setFileCorpusPrefix(String fileCorpusPrefix) {
		this.fileCorpusPrefix = fileCorpusPrefix;
	}


	public long getSleepTime() {
		return sleepTime;
	}


	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}


	public int getSizeLimit() {
		return sizeLimit;
	}


	public void setSizeLimit(int sizeLimit) {
		this.sizeLimit = sizeLimit;
	}


	public int getHopCount() {
		return hopCount;
	}


	public void setHopCount(int hopCount) {
		this.hopCount = hopCount;
	}


	public int getUrlRefreshTime() {
		return urlRefreshTime;
	}


	public void setUrlRefreshTime(int urlRefreshTime) {
		this.urlRefreshTime = urlRefreshTime;
	}

	
	
	

}
