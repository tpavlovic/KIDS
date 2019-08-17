package kids_domaci;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class DirectoryCrawlerThread implements Runnable{
	
	private String nameOfFolder;
	private String prefixCorpus;
	private ConcurrentHashMap<String, Long> mapOfFileLastMod;
	private ScanningJob jobQueue;
	private BlockingQueue<ScanningFileOrWeb> queue;
	public static  List<String> listOfDirectories = Collections.synchronizedList(new ArrayList<String>());
	private List<String> listOfVisitedFolders = Collections.synchronizedList(new ArrayList<String>());
	public static AtomicBoolean crawlerLoop = new AtomicBoolean(true);
	private Boolean tr = new Boolean(true);
	private ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> mapOfAll = new ConcurrentHashMap<String, ConcurrentHashMap<String,Long>>();

	public DirectoryCrawlerThread(BlockingQueue<ScanningFileOrWeb> queue) {
		super();
		this.queue = queue;
		 mapOfFileLastMod = new ConcurrentHashMap<String, Long>();
	}

	@Override
	public void run() {
		while(crawlerLoop.get() == tr) {
			
			if(!(listOfDirectories.isEmpty())) {
				mapOfFileLastMod.clear();
				try {
					findCorpusFolders();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(DirectoryParams.sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void findCorpusFolders() throws InterruptedException {
		File file = new File("example\\" + listOfDirectories.get(0));
		listOfDirectories.remove(0);
		
		search(file);
	}
	
	
	private void searchDirectory(File directory, String fileNameToSearch) throws InterruptedException {
		
		if(directory.isDirectory()) {
			search(directory);
		}
	}
	
	
	private void search(File file) throws InterruptedException {
		
		if(!(file.exists())) {
			System.err.println("Ovaj direktorijum ne postoji!");
		}
		
		if(file.isDirectory()) {
			System.out.println("Searching directory..." + file.getAbsolutePath());
			
			if(file.canRead()) {
				for(File temp : file.listFiles()) {
					if(temp.isDirectory()) {
						if((temp.getName().startsWith("corpus_"))) {												
							queue.put(new FileFile(temp.toString()));
						}
						search(temp);
					}
				}
			}
		}
	}
	
	
	private boolean isVisited(File temp) {
		
		if(mapOfFileLastMod.isEmpty()) {
			File[] listOfFiles = temp.listFiles();
			for (File f : listOfFiles) {
				mapOfFileLastMod.put(f.getName(), f.lastModified());
			}
			mapOfAll.put(temp.getName(), mapOfFileLastMod);
			return false;
		} else if(!(mapOfFileLastMod.isEmpty())) {
			
			File[] listOfFiles = temp.listFiles();
			for (File file : listOfFiles) {
				for(Map.Entry<String, Long> second : mapOfFileLastMod.entrySet()) {
					if(!(file.getName().equals(second.getKey()))) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	public static void setBoolean() {
		crawlerLoop.set(false);
	}


}
