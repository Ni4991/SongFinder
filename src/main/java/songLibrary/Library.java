package songLibrary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import concurrent.Lock;
import general.SongInfo;

import org.json.JSONException;  
import org.json.JSONObject;  
import org.json.JSONArray; 

/**
 * a class to hold information of all songs.
 * @author nina luo
 *
 */
public class Library {
	private String order;
	private Map<String, TreeSet<SongInfo>> byArtist, byTitle, byTag;
	private HashMap<String, SongInfo> byTrack_id;
	private HashMap<String, TreeSet<SongInfo>> byArtistForSearch;
	private HashMap<String, TreeSet<SongInfo>> byTitleForSearch;
	private HashMap<String, TreeSet<SongInfo>> byTagForSearch;
	private Lock lock;
	private JSONObject searchOutput;
	
	public Library(String order, Lock lock) {
		this.order = order;
		this.lock = lock;
		byArtist = new TreeMap<String, TreeSet<SongInfo>>();
		byTitle = new TreeMap<String, TreeSet<SongInfo>>();
		byTag = new TreeMap<String, TreeSet<SongInfo>>();
		byTrack_id = new HashMap<String, SongInfo>();
		byArtistForSearch = new HashMap<String, TreeSet<SongInfo>>();
		byTitleForSearch = new HashMap<String, TreeSet<SongInfo>>();
		byTagForSearch = new HashMap<String, TreeSet<SongInfo>>();
	}
	
	public synchronized String listToHtml(String type, String query) {
		TreeSet<SongInfo> similarSongs = null;
		System.out.println("type: " + type + ", query: " + query);
		if(type.equals("Artist")) {
			similarSongs = new TreeSet<SongInfo>(new CompareByTrack_id());
			if(byArtistForSearch.containsKey(query)) {
				for(SongInfo song : byArtistForSearch.get(query)) {
					for(String track_id : song.getSimilars()) {
						if(byTrack_id.containsKey(track_id)) {
							similarSongs.add(byTrack_id.get(track_id));
						}
					}
				}
			}	
		}
		else if(type.equals("Song Title")) {
			similarSongs = new TreeSet<SongInfo>(new CompareByTrack_id());
			if(byTitleForSearch.containsKey(query)) {
				for(SongInfo song : byTitleForSearch.get(query)) {
					for(String track_id : song.getSimilars()) {
						if(byTrack_id.containsKey(track_id)) {
							similarSongs.add(byTrack_id.get(track_id));
						}
					}
				}	
			}
		}
		else if(type.equals("Tag")) {
			similarSongs = byTag.get(query);
		}
		StringBuilder builder = new StringBuilder();
		builder.append("<table border=1 border-spacing=3px>");
//		builder.append("<tr><td colspan=2><b>" + query + "'s similar songs:</b></td></tr>");
		builder.append("<tr><th>Artist</th><th>Song Title</th></tr>");
		for(SongInfo song : similarSongs) {
			builder.append("<tr><td>" + song.getArtist() + "</td><td>" + song.getTitle() + "</td></tr>");
		}
		builder.append("</table>");
		return builder.toString();
	}
	
	/**
	 * search method.
	 * @param artistsToSearch
	 * @param titlesToSearch
	 * @param tagsToSearch
	 * @param searchOutputpath
	 * @return
	 * @throws JSONException
	 */
	public JSONObject search(ArrayList<String> artistsToSearch, ArrayList<String> titlesToSearch, ArrayList<String> tagsToSearch, String searchOutputpath) throws JSONException {
		lock.lockRead();
		searchOutput = new JSONObject();
		if(!artistsToSearch.isEmpty()) {
			searchOutput.put( "searchByArtist", searchByArtist(artistsToSearch));
		}
		if(!tagsToSearch.isEmpty()) {
			searchOutput.put("searchByTag", searchByTag(tagsToSearch));
		}
		if(!titlesToSearch.isEmpty()) {
			searchOutput.put( "searchByTitle", searchByTitle(titlesToSearch));
		}
		Path outPath = Paths.get(searchOutputpath);
		outPath.getParent().toFile().mkdirs();
		try(BufferedWriter output = Files.newBufferedWriter(outPath)){
			output.write(searchOutput.toString());
			System.out.println("writing in " + outPath);
		} catch (IOException e1) {
			e1.printStackTrace();
//			System.out.println("can't access outpath." + outPath);
		}     
		lock.unlockRead();
		return searchOutput;
	}
	
	/**
	 * add method for search purpose.
	 * @param si
	 */
	public void addForSearch(SongInfo si) {
		byTrack_id.put(si.getTrack_id(), si);
		if(byArtistForSearch.containsKey(si.getArtist())) {
			byArtistForSearch.get(si.getArtist()).add(si);
		}
		else {
			TreeSet<SongInfo> value = new TreeSet<SongInfo>(new CompareByTrack_id());
			value.add(si);
			byArtistForSearch.put(si.getArtist(), value);
		}
		if(byTitleForSearch.containsKey(si.getTitle())) {
			byTitleForSearch.get(si.getTitle()).add(si);
		}
		else {
			TreeSet<SongInfo> value = new TreeSet<SongInfo>(new CompareByTrack_id());
			value.add(si);
			byTitleForSearch.put(si.getTitle(), value);
		}
		for(String tag : si.getTags()) {
			if(byTagForSearch.containsKey(tag)) {
				byTagForSearch.get(tag).add(si);
			}
			else {
				TreeSet<SongInfo> value = new TreeSet<SongInfo>(new CompareByTrack_id());
				value.add(si);
				byTagForSearch.put(tag, value);
			}
		}
	}

	/**
	 * artist part of the search method.
	 * @param artistsToSearch
	 * @return
	 * @throws JSONException
	 */
	public JSONArray searchByArtist(ArrayList<String> artistsToSearch) throws JSONException {
		JSONArray array2 = new JSONArray();
		TreeSet<SongInfo> similarSongs;
		for(String artist : artistsToSearch) {
			JSONObject obj1 = new JSONObject();
			JSONArray array3 = new JSONArray();
			obj1.put("artist", artist);
			similarSongs = new TreeSet<SongInfo>(new CompareByTrack_id());
			if(byArtistForSearch.containsKey(artist)) {
				for(SongInfo song : byArtistForSearch.get(artist)) {
					for(String track_id : song.getSimilars()) {
						if(byTrack_id.containsKey(track_id)) {
							similarSongs.add(byTrack_id.get(track_id));
						}
					}
				}
			}
			for(SongInfo si : similarSongs) {
				if(si != null) {
					JSONObject obj2 = new JSONObject();
					obj2.put("artist", si.getArtist());
					obj2.put("trackId", si.getTrack_id());
					obj2.put("title", si.getTitle());
					array3.put(obj2);

				}
			}
			obj1.put("similars", array3);
			array2.put(obj1);
		}
		return array2;
	}
	
	/**
	 * tag part of the search method.
	 * @param tagsToSearch
	 * @return
	 * @throws JSONException
	 */
	public JSONArray searchByTag(ArrayList<String> tagsToSearch) throws JSONException {
		JSONArray array2 = new JSONArray();
		for(String tag : tagsToSearch) {
			JSONObject obj1 = new JSONObject();
			JSONArray array3 = new JSONArray();
			if(byTagForSearch.containsKey(tag)) {
				for(SongInfo song : byTagForSearch.get(tag)) {
					if(song != null) {
						JSONObject obj2 = new JSONObject();
						obj2.put("artist", song.getArtist());
						obj2.put("trackId", song.getTrack_id());
						obj2.put("title", song.getTitle());
						array3.put(obj2);
					}
				}	
			}
			obj1.put("similars", array3);
			obj1.put("tag", tag);
			array2.put(obj1);
		}
		return array2;
	}

	/**
	 * title part of the search method.
	 * @param titlesToSearch
	 * @return
	 * @throws JSONException
	 */
	public JSONArray searchByTitle(ArrayList<String> titlesToSearch) throws JSONException {
		JSONArray array2 = new JSONArray();
		TreeSet<SongInfo> similarSongs;
		for(String title : titlesToSearch) {
			JSONObject obj1 = new JSONObject();
			JSONArray array3 = new JSONArray();
			similarSongs = new TreeSet<SongInfo>(new CompareByTrack_id());
			if(byTitleForSearch.containsKey(title)) {
				for(SongInfo song : byTitleForSearch.get(title)) {
					for(String track_id : song.getSimilars()) {
						if(byTrack_id.containsKey(track_id)) {
							similarSongs.add(byTrack_id.get(track_id));
						}
					}
				}
				for(SongInfo si : similarSongs) {
					if(si != null) {
						JSONObject obj2 = new JSONObject();
						obj2.put("artist", si.getArtist());
						obj2.put("trackId", si.getTrack_id());
						obj2.put("title", si.getTitle());
						array3.put(obj2);
					}
				}	
			}
			obj1.put("similars", array3);
			obj1.put("title", title);
			array2.put(obj1);
		}
		return array2;
	}

	/**
	 * add each song object to three data structures.
	 * @param si
	 */
	public void add(SongInfo si) {
		lock.lockWrite();
		if(byArtist.containsKey(si.getArtist())) {
			byArtist.get(si.getArtist()).add(si);
		}
		else {
			TreeSet<SongInfo> value = new TreeSet<SongInfo>(new CompareByArtist());
			value.add(si);
			byArtist.put(si.getArtist(), value);
		}
		if(byTitle.containsKey(si.getTitle())) {
			byTitle.get(si.getTitle()).add(si);
		}
		else {
			TreeSet<SongInfo> value = new TreeSet<SongInfo>(new CompareByTitle());
			value.add(si);
			byTitle.put(si.getTitle(), value);
		}
		for(String tag : si.getTags()) {
			if(byTag.containsKey(tag)) {
				byTag.get(tag).add(si);
			}
			else {
				TreeSet<SongInfo> value = new TreeSet<SongInfo>(new CompareByTrack_id());
				value.add(si);
				byTag.put(tag, value);
			}
		}
		addForSearch(si);
		lock.unlockWrite();
	}
	
	/**
	 * save sorted songs to file.
	 * @param resultPath
	 * @param order
	 * @return
	 */
	public boolean saveToFile(String resultPath, String order) {
		lock.lockRead();
		Path outPath = Paths.get(resultPath);
		outPath.getParent().toFile().mkdirs();
		StringBuilder sb = new StringBuilder();
		try(BufferedWriter output = Files.newBufferedWriter(outPath)){
			if(order.equals("artist")) {
				Set<Entry<String, TreeSet<SongInfo>>> entrySet = byArtist.entrySet();
				Iterator<Entry<String, TreeSet<SongInfo>>> it = entrySet.iterator();
				while(it.hasNext()) {
					Entry<String, TreeSet<SongInfo>> me = it.next();
					TreeSet<SongInfo> value = me.getValue();
					for(SongInfo song : value) {
						sb.append(song.getArtist() + " - " + song.getTitle() + "\n");
					}   
				} 
				output.write(sb.toString());
			}     
			else if(order.equals("title")) {
				Set<Entry<String, TreeSet<SongInfo>>> entrySet = byTitle.entrySet();
				Iterator<Entry<String, TreeSet<SongInfo>>> it = entrySet.iterator();
				while(it.hasNext()) {
					Entry<String, TreeSet<SongInfo>> me = it.next();
					TreeSet<SongInfo> value = me.getValue();
					for(SongInfo song : value) {
						sb.append(song.getArtist() + " - " + song.getTitle() + "\n");
					}
				}
				output.write(sb.toString());
			}
			else if(order.equals("tag")) {
				StringBuilder sb2 = new StringBuilder();
				Set<Entry<String, TreeSet<SongInfo>>> entrySet = byTag.entrySet();
				Iterator<Entry<String, TreeSet<SongInfo>>> it = entrySet.iterator();
				while(it.hasNext()) {
					Entry<String, TreeSet<SongInfo>> me = it.next();
					String str = me.getKey();
					TreeSet<SongInfo> value = me.getValue();
					for(SongInfo song : value) {
						sb2.append(song.getTrack_id() + " ");
					}
					sb.append(str + ": " + sb2 + "\n");
					sb2.delete(0, sb2.length());
				}
				output.write(sb.toString());
			}
			lock.unlockRead();
		}catch(IOException e) {
			System.out.print(e.getMessage());	
		}
		return true;
	}
}
