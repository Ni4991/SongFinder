package songfinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * a class to parse arguments.
 * @author nina luo     
 *
 */  

public class ParseArgs {
	private String inputpath, outputpath, order;
	private int nThreads;
	private JsonObject searchObj;
	private ArrayList<String> artistsToSearch, titlesToSearch, tagsToSearch;;
	private String searchOutputpath;
	private boolean doSearch;
	
	public ParseArgs(String[] args) throws JSONException {
		inputpath = "";
		outputpath = "";
		order = "";
		nThreads = 10;
		doSearch = false;
		artistsToSearch = new ArrayList<String>();
		titlesToSearch = new ArrayList<String>();
		tagsToSearch= new ArrayList<String>();
		if(!checkArgs(args)) {
			System.out.println("bad args.");
			return;
		}
		LibraryBuilder lb = new LibraryBuilder(inputpath, nThreads, order);
		lb.build(new File(inputpath));
		lb.getWorkQueue().shutdown();
		try {
			lb.getWorkQueue().awaitTermination();
		} catch (InterruptedException e) {
			System.out.println("interrupted");
		}
		if(lb.getWorkQueue().isTerminated()) {
			lb.getLibrary().saveToFile(outputpath, order);
			if(doSearch) {
				lb.getLibrary().search(artistsToSearch, titlesToSearch, tagsToSearch, searchOutputpath);
			}
		}		
	}
	
	/**
	 * check if args are valid.
	 * @param args
	 */
	public boolean checkArgs(String[] args) {
		if(args.length < 6) {
			System.out.println("incorrect args length.");
			return false;
		}
		for(int i = 0; i < args.length - 1; i++) {
			if(args[i].equals("-searchInput")) {
				doSearch = true;
				BufferedReader reader = null;  
				String laststr = "";  
				if(new File(args[i + 1]).isFile()) {
					try {
						FileInputStream fileInputStream = new FileInputStream(args[i + 1]);  
						InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");  
						reader = new BufferedReader(inputStreamReader);  
						String tempString = null; 
						while((tempString = reader.readLine()) != null){  
							laststr += tempString;  
						}  
				        reader.close();  
						}
					catch(IOException e){  
			            e.printStackTrace();  
			        }finally{  
			            if(reader != null){  
			                try {  
			                    reader.close();  
			                } catch (IOException e) { 
			                	e.printStackTrace();  
			                }  
			            }  
			        } 
					JsonParser parser = new JsonParser();
					searchObj = (JsonObject) parser.parse(laststr);
					if(!laststr.contains("searchByArtist") && !laststr.contains("searchByTitle") && !laststr.contains("searchByTag")) {
						return false;
					}
					JsonArray arrSearchByArtist = searchObj.get("searchByArtist").getAsJsonArray();
					JsonArray arrSearchByTitle = searchObj.get("searchByTitle").getAsJsonArray();
					if(!laststr.contains("searchByTag")) {
						JsonArray arrSearchByTag = null;
						continue;
					}
					JsonArray arrSearchByTag = searchObj.get("searchByTag").getAsJsonArray();
					
					for(int j = 0; j < arrSearchByArtist.size(); j++) {
						artistsToSearch.add(arrSearchByArtist.get(j).getAsString());
					}
					for(int j = 0; j < arrSearchByTitle.size(); j++) {
						titlesToSearch.add(arrSearchByTitle.get(j).getAsString());
					} 
					for(int j = 0; j < arrSearchByTag.size(); j++) {
						tagsToSearch.add(arrSearchByTag.get(j).getAsString());
					}
				}
			}
	
			if(args[i].equals("-threads")) {
				try {
					if(Integer.parseInt(args[i + 1]) > 1 && Integer.parseInt(args[i + 1]) < 1000){
						nThreads = Integer.parseInt(args[i + 1]);
					}
				}catch(NumberFormatException e) {
					e.getMessage();
				}
			}
			if(args[i].equals("-input")) {
				inputpath = args[i + 1]; 
				if(!new File(inputpath).isDirectory()) {
					System.out.println("bad input path");
					return false;
				}
			}
			if(args[i].equals("-output")) {
				outputpath = args[i + 1];
			}
			if(args[i].equals("-order")) {
				order = args[i + 1];
				if(!(order.equals("artist") || order.equals("title") || order.equals("tag"))){
					System.out.println("bad order");
					return false;
				}
			}
			if(args[i].equals("-searchOutput")) {
				doSearch = true;
				searchOutputpath = args[i + 1];
			}
		}
		return true;
	}

	public ArrayList<String> getSearchByArtist(){
		return artistsToSearch;
	}
	
	public ArrayList<String> getSearchByTitle(){
		return titlesToSearch;
	}
	
	public ArrayList<String> getSearchByTag(){
		return tagsToSearch;
	}
	
	/**
	 * getter of input path.
	 * @return
	 */
	public String getInputpath() {
		return inputpath;
	}
	
	/**
	 * getter of output path.
	 * @return
	 */
	public String getOutputpath() {
		return outputpath;
	}
	
	/**
	 * getter of desired order.
	 * @return
	 */
	public String getOrder() {
		return order;
	}
	
	/**
	 * getter of the number of threads.
	 * @return
	 */
	public int getnThreads() {
		return nThreads;
	}
}
