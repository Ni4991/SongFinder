package general;

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
 * a class to parse arguments and more.
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
	}
	
	public String getSearchOutputpath() {
		return searchOutputpath;
	}
	
	public ArrayList<String> getTagsToSearch(){
		return tagsToSearch;
	}
	
	public ArrayList<String> getTitlesToSearch(){
		return titlesToSearch;
	}
	
	public ArrayList<String> getArtistsToSearch(){
		return artistsToSearch;
	}
	
	public boolean getDoSearch() {
		return doSearch;
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
						FileInputStream fis = new FileInputStream(args[i + 1]);  
						InputStreamReader isr = new InputStreamReader(fis, "UTF-8");  
						reader = new BufferedReader(isr);  
						String str = null; 
						while((str = reader.readLine()) != null){  
							laststr += str;
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
					if(laststr.contains("searchByArtist")) {
						JsonArray arrSearchByArtist = searchObj.get("searchByArtist").getAsJsonArray();
						for(int j = 0; j < arrSearchByArtist.size(); j++) {
						artistsToSearch.add(arrSearchByArtist.get(j).getAsString());
						}
					}
					if(laststr.contains("searchByTitle")) {
						JsonArray arrSearchByTitle = searchObj.get("searchByTitle").getAsJsonArray();
						for(int j = 0; j < arrSearchByTitle.size(); j++) {
							titlesToSearch.add(arrSearchByTitle.get(j).getAsString());
						} 
					}
					if(laststr.contains("searchByTag")) {
						JsonArray arrSearchByTag = searchObj.get("searchByTag").getAsJsonArray();
						for(int j = 0; j < arrSearchByTag.size(); j++) {
						tagsToSearch.add(arrSearchByTag.get(j).getAsString());
						}
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

	/**
	 * getter of all artists the query wants to search.
	 * @return
	 */
	public ArrayList<String> getSearchByArtist(){
		return artistsToSearch;
	}
	
	/**
	 * getter of all titles the query wants to search.
	 * @return
	 */
	public ArrayList<String> getSearchByTitle(){
		return titlesToSearch;
	}
	
	/**
	 * getter of all tags the query wants to search.
	 * @return
	 */
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
