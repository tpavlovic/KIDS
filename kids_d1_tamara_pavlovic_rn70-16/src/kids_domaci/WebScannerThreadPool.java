package kids_domaci;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScannerThreadPool {
	
	private String path, nameOfFile, newJob;
	private ScanningFileOrWeb url;
	private ExecutorService threadPool = Executors.newCachedThreadPool();
	private CompletionService<ConcurrentHashMap<String, Integer>> completionService = new ExecutorCompletionService<>(threadPool);
	private List<Future<ConcurrentHashMap<String, Integer>>> results = Collections.synchronizedList(new ArrayList<Future<ConcurrentHashMap<String, Integer>>>());
	public static List<String> listOfURLs = Collections.synchronizedList(new ArrayList<String>());
	boolean end = false;
	int counter, brojac = 0;
	private AtomicBoolean find = new AtomicBoolean(false);
	private AtomicBoolean write = new AtomicBoolean(false);
	private AtomicBoolean stopBoolean = new AtomicBoolean(false);
	private BlockingQueue<ScanningFileOrWeb> queue;
	private Boolean t = new Boolean(true);
	private Boolean fal = new Boolean(false);
	private static ScheduledExecutorService ex =  Executors.newSingleThreadScheduledExecutor();
	private ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
	private ConcurrentHashMap<String, ConcurrentHashMap<String,Integer>> mapOfAllResults;
	private  ConcurrentHashMap<String, Integer> localMap;
	
	public WebScannerThreadPool(BlockingQueue<ScanningFileOrWeb> queue) {
		super();
		this.queue = queue;
		this.localMap = new ConcurrentHashMap<String, Integer>();
		mapOfAllResults = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>();
	 localMap = new ConcurrentHashMap<String, Integer>();
	}
	

	public void addJob(ScanningFileOrWeb job) throws IOException, InterruptedException {
		System.out.println("Job za web: " + job.getQuery());
		
		if(DirectoryParams.resetHop == true) {
		counter = DirectoryParams.hopCount;
		}
		
		url = job;
		Document doc = Jsoup.connect(url.getQuery()).get();
	    String title = doc.text();
	    
	    
	    File f = new File("testFile.txt");
	    BufferedWriter bw = new BufferedWriter(new FileWriter(f));
	    bw.write(title);
	    bw.close();
	    
	    Elements links = doc.select("a");
	    	if(job.getHop() > 0) {
	    		for (Element link : links) {
	    			newJob = link.attr("abs:href");
	    			if(!(isValid(newJob))) {
	    				System.err.println("Nije validna url adresa: " + newJob);
	    				continue;
	    			}
	    			try {
	    				queue.put(new Web(newJob, job.getHop()-1));
					} catch (Exception e) {
					}
		    		System.out.println("Newjob: " + newJob);
	    		}
	    	}
	    	
	    	
	    if(listOfURLs.isEmpty()) {
	    	//threadPool.submit(new JobForWebScanner(f ,job.getQuery()));
	    	//completionService.submit(new JobForWebScanner(f ,job.getQuery()));
	 		results.add(threadPool.submit(new JobForWebScanner(f ,job.getQuery())));
	 		listOfURLs.add(job.getQuery());
	    } else if(!(listOfURLs.isEmpty())){
	    	for (String element : listOfURLs) {
				if(element.equals(job.getQuery())) {
					System.err.println("Ponovljen");
					find.set(true);
					break;
			}
			}
	    	
	    	if(find.get() == t) {
		 		find.set(false);
	    	} else if(find.get() == fal){
	    		//completionService.submit(new JobForWebScanner(f ,job.getQuery()));
		 		//results.add(completionService.take());    
	    		results.add(threadPool.submit(new JobForWebScanner(f ,job.getQuery())));
		 		listOfURLs.add(job.getQuery());
            	
	    	}
	    	
	    }
	    
	   
    
        for(String s : DirectoryParams.keyWord) {
        	for(Future<ConcurrentHashMap<String,Integer>> future : results) {
        		try {
        			brojac++;
        			if(brojac == DirectoryParams.keyWord.length) {
        				continue;
        			}
        			map.put(s, future.get().get(s));
        		} catch (Exception e) {
        			e.printStackTrace();
        			}
        		}
        }
            	
        System.out.print(job.getQuery() + "| ");
        for(Map.Entry<String, Integer> m:map.entrySet()) {
        	System.out.print(m.getKey() + " = " + m.getValue() + " ");
        	localMap.put(m.getKey(), m.getValue());
        }
            	
        System.out.println();
        mapOfAllResults.put(job.getQuery(), localMap);
        map.clear();
        		
        results.clear();
        brojac = 0;
        System.out.println("--------------------------");  
	}
	

	public void stopThredPool() {
		threadPool.shutdown();
		ex.shutdown();
	}
	
	public static void refreshUrls(List<String> listOfUrls) {
		ex.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				listOfUrls.clear();
			}
		}, 0, DirectoryParams.refreshTime, TimeUnit.MICROSECONDS);
	}
	
	
	private boolean isValid(String url) {
		try {
			Document doc = Jsoup.connect(url).get();
			return true;
			
		} catch (Exception e) {
			return false;
		}
	}
	
	
	public void setStopBoolean() {
		stopBoolean.set(true);
		this.stopThredPool();
	}
	
	
}
