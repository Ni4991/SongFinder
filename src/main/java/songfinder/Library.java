package songfinder;

import java.io.BufferedWriter;
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONException;  
import org.json.JSONObject;  
import org.json.JSONArray; 

/**
 * A class to hold information of all songs.
 * @author nina luo
 *
 */
public class Library {
	private String order;
	private Map<String, TreeSet<SongInfo>> byArtist, byTitle, byTag;
	private HashMap<String, SongInfo> byTrack_id;
	private HashMap<String, TreeSet<SongInfo>> linkedByArtist;
	private HashMap<String, TreeSet<SongInfo>> linkedByTitle, linkedByTag;
	private Lock lock;
	private JSONObject searchOutput;
	
	public Library(String order, Lock lock) {
		this.order = order;
		this.lock = lock;
		byArtist = new TreeMap<String, TreeSet<SongInfo>>();
		byTitle = new TreeMap<String, TreeSet<SongInfo>>();
		byTag = new TreeMap<String, TreeSet<SongInfo>>();
		byTrack_id = new HashMap<String, SongInfo>();
		linkedByArtist = new HashMap<String, TreeSet<SongInfo>>();
		linkedByTitle = new HashMap<String, TreeSet<SongInfo>>();
		linkedByTag = new HashMap<String, TreeSet<SongInfo>>();
	}
	
	public JSONObject search(ArrayList<String> artistsToSearch, ArrayList<String> titlesToSearch, ArrayList<String> tagsToSearch, String searchOutputpath) throws JSONException {
		
		searchOutput = new JSONObject();
//		System.out.println("searchByArtist" + "\n" + searchByArtist(artistsToSearch));
		
		searchOutput.put( "searchByArtist", searchByArtist(artistsToSearch));
		searchOutput.put("searchByTag", searchByTag(tagsToSearch));
//		lock.lockWrite();
//		if(!tagsToSearch.isEmpty()) {
			searchOutput.put( "searchByTitle", searchByTitle(titlesToSearch));
//		}
//		lock.unlockWrite();
		Path outPath = Paths.get(searchOutputpath);
		outPath.getParent().toFile().mkdirs();
		try(BufferedWriter output = Files.newBufferedWriter(outPath)){
			try {
				output.write(searchOutput.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}     
		
		return searchOutput;
	}
	
	public void addForSearch(SongInfo si) {
		byTrack_id.put(si.getTrack_id(), si);
		if(linkedByArtist.containsKey(si.getArtist())) {
			linkedByArtist.get(si.getArtist()).add(si);
		}
		else {
			TreeSet<SongInfo> value = new TreeSet<SongInfo>(new CompareByTrack_id());
			value.add(si);
			linkedByArtist.put(si.getArtist(), value);
		}
		if(linkedByTitle.containsKey(si.getTitle())) {
			linkedByTitle.get(si.getTitle()).add(si);
		}
		else {
			TreeSet<SongInfo> value = new TreeSet<SongInfo>(new CompareByTrack_id());
			value.add(si);
			linkedByTitle.put(si.getTitle(), value);
		}
		for(String tag : si.getTags()) {
			if(linkedByTag.containsKey(tag)) {
				linkedByTag.get(tag).add(si);
			}
			else {
				TreeSet<SongInfo> value = new TreeSet<SongInfo>(new CompareByTrack_id());
				value.add(si);
				linkedByTag.put(tag, value);
			}
		}
	}

	public JSONArray searchByArtist(ArrayList<String> artistsToSearch) throws JSONException {
		JSONArray array2 = new JSONArray();
		TreeSet<SongInfo> similarSongs;
//		System.out.println(artistsToSearch);
		for(String artist : artistsToSearch) {
			JSONObject obj1 = new JSONObject();
			JSONArray array3 = new JSONArray();
			obj1.put("artist", artist);
			similarSongs = new TreeSet<SongInfo>(new CompareByTrack_id());
			if(linkedByArtist.containsKey(artist)) {
//				System.out.println(linkedByArtist.containsKey(artist));
				for(SongInfo song : linkedByArtist.get(artist)) {
					for(String track_id : song.getSimilars()) {
						if(byTrack_id.containsKey(track_id)) {
							similarSongs.add(byTrack_id.get(track_id));
						}
					}
				}
			}
//			System.out.println(similarSongs.size());
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
//			System.out.println("印不出");
		}
//		System.out.println("aa");
		return array2;
	}
	
	public JSONArray searchByTag(ArrayList<String> tagsToSearch) throws JSONException {
		JSONArray array2 = new JSONArray();
		for(String tag : tagsToSearch) {
			JSONObject obj1 = new JSONObject();
			JSONArray array3 = new JSONArray();
			if(linkedByTag.containsKey(tag)) {
				for(SongInfo song : linkedByTag.get(tag)) {
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

	public JSONArray searchByTitle(ArrayList<String> titlesToSearch) throws JSONException {
		JSONArray array2 = new JSONArray();
		TreeSet<SongInfo> similarSongs;
		for(String title : titlesToSearch) {
			JSONObject obj1 = new JSONObject();
			JSONArray array3 = new JSONArray();
			similarSongs = new TreeSet<SongInfo>(new CompareByTrack_id());
			if(linkedByTitle.containsKey(title)) {
				for(SongInfo song : linkedByTitle.get(title)) {
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
	 * Add each song object to three data structures.
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
	 * Save sorted songs to file.
	 * @param resultPath
	 * @param order
	 * @return
	 */
	public boolean saveToFile(String resultPath, String order) {
		Path outPath = Paths.get(resultPath);
		outPath.getParent().toFile().mkdirs();
		StringBuilder sb = new StringBuilder();
		try(BufferedWriter output = Files.newBufferedWriter(outPath)){
			if(order.equals("artist")) {
				lock.lockWrite();	
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
				lock.lockWrite();
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
				lock.lockWrite();
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
			lock.unlockWrite();
		}catch(IOException e) {
			System.out.print(e.getMessage());	
		}
		return true;
	}

}