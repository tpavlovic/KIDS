package kids_domaci;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class JobForFileScanner implements Callable<ConcurrentHashMap<String, Integer>>{
	
	private List<File> files = new ArrayList<File>();
	private int count = 0;
	private String s, path;
	private String[] wordsInLine;
	private String[] keywords = DirectoryParams.keyWord;
	private ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
	
	
	public JobForFileScanner(List<File> files, String path) {
		this.files = files;
		this.path = path;
	}


	@Override
	public ConcurrentHashMap<String, Integer> call() throws Exception {
		
		FileReader f = null;
		BufferedReader br = null;
		
		for (String key : keywords) {
			for (File file : files) {
				if(!(file.canRead())) {
					continue;
				}
				f = new FileReader(file);
				br = new BufferedReader(f);
				
				while((s = br.readLine()) != null) {
					wordsInLine = s.split(" ");
						for (String words : wordsInLine) {//
							if (words.equals(key)) {
								//System.out.println("Jeste");
								count++;
							}
						}
				}
				
			}
			
			map.put(key, count);
			count = 0;
		}
		
		f.close();
		br.close();
		
		return map;
	}
	
	




}
