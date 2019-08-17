package kids_domaci;

import java.beans.FeatureDescriptor;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.omg.CORBA.portable.ValueInputStream;

public class FileScannerThreadPool {
	
	private String path;
	private ExecutorService threadPool = Executors.newCachedThreadPool();
	private CompletionService<ConcurrentHashMap<String, Integer>> completionService = new ExecutorCompletionService<>(threadPool);
	private List<Future<ConcurrentHashMap<String, Integer>>> results = Collections.synchronizedList(new ArrayList<Future<ConcurrentHashMap<String, Integer>>>());
	private ConcurrentHashMap<String, CopyOnWriteArrayList<Future<HashMap<String, Integer>>>> mapOfMaps = new ConcurrentHashMap<String, CopyOnWriteArrayList<Future<HashMap<String,Integer>>>>();
	private ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
	int sumAll = 0;
	int brojac = 0;
	
	public void addJob(ScanningFileOrWeb job) throws InterruptedException{
		System.out.println("Job: " + job.getQuery());
		
		File folder = new File(job.getQuery());
		File[] files = folder.listFiles();
			
		List<File> fileForSending = new CopyOnWriteArrayList<File>();
		
		long sum = 0;
		for(File f:files) {
			
			fileForSending.add(f);
			sum += f.length();
			
			if(sum > DirectoryParams.sizeLimit) {
				results.add(threadPool.submit(new JobForFileScanner((List<File>) ((CopyOnWriteArrayList)fileForSending).clone() ,job.getQuery())));
				sum = 0;
				fileForSending.clear();
			}
		}
		
		if(!(fileForSending.isEmpty())) {
			results.add(threadPool.submit(new JobForFileScanner((List<File>) ((CopyOnWriteArrayList)fileForSending).clone() ,job.getQuery())));
			sum = 0;
			fileForSending.clear();
		}
		
		
		for(String s : DirectoryParams.keyWord) {
			for(Future<ConcurrentHashMap<String,Integer>> future : results) {
				try {
					sumAll += future.get().get(s);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			brojac++;
			if(brojac == DirectoryParams.keyWord.length) {
				continue;
			}
			map.put(s, sumAll);
			sumAll = 0;
		}
		
		System.out.print(job.getQuery() + "| ");
		for(Map.Entry<String, Integer> m:map.entrySet()) {
			System.out.print(m.getKey() + " = " + m.getValue() + " ");
		}
		System.out.println();
		
		results.clear();
		brojac = 0;
		
		System.out.println("--------------------------");
	}
	

	public void stopThredPool() {
		threadPool.shutdown();
	}
	
	

}
