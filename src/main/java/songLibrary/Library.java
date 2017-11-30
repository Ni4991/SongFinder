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
import java.util.Scanner;
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
import servlets.ArtistInfo;
import servlets.CompareByAlpha;
import servlets.CompareByPCount;
import socket.HTTPFetcher;

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
//	private HashSet<String> allArtists;
	private TreeSet<ArtistInfo> artistsByPCount;
	
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
	//TODO:lock all methods.
	
	
	public void test() {
		System.out.println("all's size: " + byArtist.keySet().size());
		for(String artist : byArtist.keySet()) {
			System.out.println(artist);
			String page = HTTPFetcher.download("ws.audioscrobbler.com", "/2.0/?method=artist.getinfo"
					+ "&artist="+ artist +"&api_key=9162bc3f7439ff3d4258613b75d37287&format=json");
			
		}
	}
	
	public void htmlArtists() {
		lock.lockWrite();
		String apiKey = "9162bc3f7439ff3d4258613b75d37287";
		artistsByPCount = new TreeSet<ArtistInfo>(new CompareByPCount());
//		artistsByPCount = new TreeSet<ArtistInfo>();
		JsonObject toJson;
		
		for(String artist : byArtist.keySet()) {
			System.out.println(artist);
			String page = HTTPFetcher.download("ws.audioscrobbler.com", "/2.0/?method=artist.getinfo"
					+ "&artist="+ artist +"&api_key=9162bc3f7439ff3d4258613b75d37287&format=json");
			try(Scanner sc = new Scanner(page)){
				while(sc.hasNextLine()) {
					String line = sc.nextLine();
					if(line.trim().length() == 0) {
						line = sc.nextLine();
						if(line.trim().startsWith("{\"artist\"")) {
							System.out.println(line);
							JsonParser parser = new JsonParser();
							toJson = (JsonObject) parser.parse(line);
							String art = toJson.getAsJsonObject("artist").get("name").getAsString();
							JsonObject data = toJson.getAsJsonObject("artist");
							data = data.getAsJsonObject("stats");
							String playc = data.get("playcount").getAsString();
							data = toJson.getAsJsonObject("artist");
							data = data.getAsJsonObject("stats");
							String lsnrs = data.get("listeners").getAsString();
							data = toJson.getAsJsonObject("artist");
							data = data.getAsJsonObject("bio");
							String published = data.get("published").getAsString();
							data = toJson.getAsJsonObject("artist");
							data = data.getAsJsonObject("bio");
							String summary = data.get("summary").getAsString();
							data = toJson.getAsJsonObject("artist");
							data = data.getAsJsonObject("bio");
							String content = data.get("content").getAsString();
							String bio = "published: " + published + "\n" + "summary: " + summary + "\n" + "content: " + content;
						    int playcount = Integer.parseInt(playc);
						    int listeners = Integer.parseInt(lsnrs);
						    data = toJson.getAsJsonObject("artist");
						    JsonArray dat = data.getAsJsonArray("image");
						    String image = dat.get(0).getAsJsonObject().get("#text").getAsString();
//						    artistsByAlpha.add(new ArtistInfo(art, bio, image, listeners, playcount));
						    artistsByPCount.add(new ArtistInfo(art, bio, image, listeners, playcount));
	
						}
					}
				}
			}
//			System.out.println("got here1");//to delete
		}	
//		System.out.println("got here2");//to delete
		for(ArtistInfo ai : artistsByPCount) {
			System.out.println("name: " + ai.getName() + "\n" + "bio: " + ai.getBio() + "\n" 
			+ "image: " + ai.getImage() + "\n" + "listeners: " + ai.getListeners() + "\n" 
					+"playcount"+ ai.getPlaycount() + "\n");
		}
		lock.unlockWrite();
	}
	
	public String viewAllArtists() {
//		StringBuilder builder = new StringBuilder();
//		builder.append("<table border=1 border-spacing=3px>");
		String str = "Artist name";
//		builder.append("");
		for(String artist : byArtist.keySet()) {
//			builder.append(artist + "\n");
			str += artist;
		}
//		builder.append("</p>");
//		return builder.toString();
		return str;
	}
	/**
	 * convert to html representation.
	 * @param type
	 * @param query
	 * @return
	 */
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
		builder.append("<tr><th>Artist</th><th>Song Title</th></tr>");
		for(SongInfo song : similarSongs) {
			ArrayList<String> artistsToSearch = new ArrayList<String>();
			ArrayList<String> titlesToSearch = new ArrayList<String>();
			ArrayList<String> tagsToSearch = new ArrayList<String>();
			titlesToSearch.add(song.getTitle());
			builder.append("<tr><td>" + song.getArtist() + "</td>"
					+ "<td>" + song.getTitle()  + "</td></tr>");
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
		System.out.println(searchOutputpath);
		
		System.out.println(outPath.toString());
		outPath.getParent().toFile().mkdirs();
		try(BufferedWriter output = Files.newBufferedWriter(outPath)){
			output.write(searchOutput.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
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
