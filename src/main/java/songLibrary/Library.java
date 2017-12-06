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
	private TreeSet<ArtistInfo> artistsByPCount, artistsByAlpha;
	private HashSet<String> copyArtists;
	
	private Lock lock;
	
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
		artistsByPCount = new TreeSet<ArtistInfo>(new CompareByPCount());
		artistsByAlpha = new TreeSet<ArtistInfo>(new CompareByAlpha());
		copyArtists = new HashSet<String>();
	}
	
	public String searchToHtml(String type, String partial) {
		lock.lockRead();
		TreeSet<SongInfo> similarSongs = null;
		HashSet<String> containsQ = null;
		if(type.equals("Artist")) {
			similarSongs = new TreeSet<SongInfo>(new CompareByTrack_id());
			containsQ = new HashSet<String>();
			for(String art : byArtistForSearch.keySet()) {
				if(art.toLowerCase().contains(partial.toLowerCase())) {
					containsQ.add(art);
				}
			}
			for(String art : containsQ){
				for(SongInfo song : byArtistForSearch.get(art)) {
					ArrayList<String> newSimilars = new ArrayList<String>();
					for(String simi : song.getSimilars()) {
						newSimilars.add(simi);
					}
					for(String track_id : song.getSimilars()) {
						if(byTrack_id.containsKey(track_id)) {
							SongInfo src = byTrack_id.get(track_id);
							HashSet<String> newTags = new HashSet<String>();
							for(String tag : src.getTags()) {
								newTags.add(tag);
							}
							ArrayList<String> newSimi = new ArrayList<String>();
							for(String simi : song.getSimilars()) {
								newSimi.add(simi);
							}
							SongInfo sinfo = new SongInfo(src.getArtist(), src.getTitle(), newTags, src.getTrack_id(), newSimi);
							similarSongs.add(sinfo);
						}
					}
				}
			}
		}
		else if(type.equals("Song Title")) {
			similarSongs = new TreeSet<SongInfo>(new CompareByTrack_id());
			containsQ = new HashSet<String>();
			for(String titl : byTitleForSearch.keySet()) {
				if(titl.toLowerCase().contains(partial.toLowerCase())) {
					containsQ.add(titl);
				}
			}
			for(String titl : containsQ){
				for(SongInfo song : byTitleForSearch.get(titl)) {
					ArrayList<String> newSimilars = new ArrayList<String>();
					for(String simi : song.getSimilars()) {
						newSimilars.add(simi);
					}
					for(String track_id : newSimilars) {
						if(byTrack_id.containsKey(track_id)) {
							if(byTrack_id.containsKey(track_id)) {
								SongInfo src = byTrack_id.get(track_id);
								HashSet<String> newTags = new HashSet<String>();
								for(String tag : src.getTags()) {
									newTags.add(tag);
								}
								ArrayList<String> newSimi = new ArrayList<String>();
								for(String simi : song.getSimilars()) {
									newSimi.add(simi);
								}
								SongInfo sinfo = new SongInfo(src.getArtist(), src.getTitle(), newTags, src.getTrack_id(), newSimi);
								similarSongs.add(sinfo);
							}
						}
					}
				}
			}
		}
		else if(type.equals("Tag")) {
			similarSongs = new TreeSet<SongInfo>(new CompareByTrack_id());
			containsQ = new HashSet<String>();
			for(String tg : byTag.keySet()) {
				if(tg.toLowerCase().contains(partial.toLowerCase())) {
					containsQ.add(tg);
				}
			}
			for(String tg : containsQ){
				for(SongInfo song : byTag.get(tg)) {
					ArrayList<String> newSimilars = new ArrayList<String>();
					for(String simi : song.getSimilars()) {
						newSimilars.add(simi);
					}
					for(String track_id : newSimilars) {
						if(byTrack_id.containsKey(track_id)) {
							SongInfo src = byTrack_id.get(track_id);
							HashSet<String> newTags = new HashSet<String>();
							for(String tag : src.getTags()) {
								newTags.add(tag);
							}
							ArrayList<String> newSimi = new ArrayList<String>();
							for(String simi : song.getSimilars()) {
								newSimi.add(simi);
							}
							SongInfo sinfo = new SongInfo(src.getArtist(), src.getTitle(), newTags, src.getTrack_id(), newSimi);
							similarSongs.add(sinfo);
						}
					}
				}
			}
		}
		StringBuilder builder = new StringBuilder();
		builder.append("<table id = \"table1\" align=\"center\" border=1 border-spacing=3px>");
		builder.append("<thead><tr><th>Artist</th><th>Song title</th></tr></thead><tbody id = \"table2\">");
		int i = 0;
		String s1 = "<tr>";
		String s2 = "<tr class=\"alt\">";
		for(SongInfo si : similarSongs) {
			SongInfo song = new SongInfo(si.getArtist(), si.getTitle(), null, si.getTrack_id(), null);
			ArrayList<String> newSimilars = new ArrayList<String>();
			for(String simi : si.getSimilars()) {
				newSimilars.add(simi);
			}
			ArrayList<String> titlesToSearch = new ArrayList<String>();
			titlesToSearch.add(song.getTitle());
			String img = "";
			for(ArtistInfo ainfo : artistsByAlpha) {
				if(ainfo.getName().equals(song.getArtist())) {
					img = ainfo.getImage();
					break;
				}
			} 
			if(i % 2 == 0) {
				builder.append(s1);
			}
			else if((i % 2 != 0)) {
				builder.append(s2);
			}
			builder.append("<td>" + song.getArtist() + "</td>"
					+ "<td><details>\r\n" + 
					"    <summary>"+ song.getTitle() +"</summary>\r\n" + 
					"        <p>details: </p>\r\n" + 
					"        <ul>\r\n" + 
					"			 <li><img src=" + img + "alt=\"img\" />" +
					"            <li>artist name: " + song.getArtist() + "</li>\r\n" + 
					"            <li>title: "+ song.getTitle() + "</li>\r\n" + 
					"            <li>similar songs: " + newSimilars + "</li>\r\n" + 
					"        </ul>\r\n" + 
					"    </details> " + "</td></tr>");
			i++;
		}
		builder.append("</tbody></table>");
		builder.append("<span id=\"spanPre\">Previous</span> <span id=\"spanNext\"> Next</span> "
				+ " Page <span id=\"spanPageNum\"></span> of <span id=\"spanTotalPage\"></span> Pages");
		lock.unlockRead();
		return builder.toString();
	}
	
	public String htmlByAlpha() {
		lock.lockRead();
		StringBuilder builder = new StringBuilder();
		builder.append("<table id = \"table1\" align=\"center\" border=1 border-spacing=3px>");
		builder.append("<thead><tr><th>Artist</th><th>Play count</th></tr></thead><tbody id = \"table2\">");
		int i = 0;
		String s1 = "<tr>";
		String s2 = "<tr class=\"alt\">";
		if(i % 2 == 0) {
			builder.append(s1);
		}
		else if((i % 2 != 0)) {
			builder.append(s2);
		}
		for(ArtistInfo ainfo : artistsByAlpha) {
			ArtistInfo ai = new ArtistInfo(ainfo.getName(), ainfo.getBio(), ainfo.getImage(), ainfo.getListeners(), ainfo.getPlaycount());
			builder.append("<td><details>\r\n" + 
					"    <summary>"+ ai.getName() +"</summary>\r\n" + 
					"        <p>details: </p>\r\n" + 
					"        <ul>\r\n" + 
					"		 	 <li><img src=\"" + ai.getImage() + "\"alt=\"img\" />" +
					"            <li>artist name: " + ai.getName() + "</li>\r\n" + 
					"            <li>play count: "+ ai.getPlaycount() + "</li>\r\n" + 
					"            <li>listeners: " + ai.getListeners() + "</li>\r\n" + 
					"            <li>bio: " + ai.getBio() + "</li>\r\n" + 
					"        </ul>\r\n" + 
					"    </details> " + "</td><td>" + ai.getPlaycount() + "</td>"
					+ "</tr>");
			i++;
		}
		builder.append("</tbody></table>");
		builder.append("<span id=\"spanPre\">Previous</span> <span id=\"spanNext\"> Next</span> "
				+ " Page <span id=\"spanPageNum\"></span> of <span id=\"spanTotalPage\"></span> Pages");
		lock.unlockRead();
		return builder.toString();
	}
	
	public String htmlByPCount() {
		lock.lockRead();
		StringBuilder builder = new StringBuilder();
		builder.append("<table id = \"table1\" align=\"center\" border=1 border-spacing=3px>");
		builder.append("<thead><tr><th>Play count</th><th>Artist</th></tr></thead><tbody id = \"table2\">");
		int i = 0;
		String s1 = "<tr>";
		String s2 = "<tr class=\"alt\">";
		if(i % 2 == 0) {
			builder.append(s1);
		}
		else if((i % 2 != 0)) {
			builder.append(s2);
		}
		for(ArtistInfo ainfo : artistsByPCount) {
			ArtistInfo ai = new ArtistInfo(ainfo.getName(), ainfo.getBio(), ainfo.getImage(), ainfo.getListeners(), ainfo.getPlaycount());
			builder.append("<td>" + ai.getPlaycount() + "</td>"
					+ "<td><details>\r\n" + 
					"    <summary>"+ ai.getName() +"</summary>\r\n" + 
					"        <p>details: </p>\r\n" + 
					"        <ul>\r\n" + 
					"		 	 <li><img src=\"" + ai.getImage() + "\"alt=\"img\" />" +
					"            <li>artist name: " + ai.getName() + "</li>\r\n" + 
					"            <li>play count: "+ ai.getPlaycount() + "</li>\r\n" + 
					"            <li>listeners: " + ai.getListeners() + "</li>\r\n" + 
					"            <li>bio: " + ai.getBio() + "</li>\r\n" + 
					"        </ul>\r\n" + 
					"    </details> " + "</td></tr>");
			i++;
		}
		builder.append("</tbody></table>");
		builder.append("<span id=\"spanPre\">Previous</span> <span id=\"spanNext\"> Next</span> "
				+ " Page <span id=\"spanPageNum\"></span> of <span id=\"spanTotalPage\"></span> Pages");
		lock.unlockRead();
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
		JSONObject searchOutput = new JSONObject();
		if(!artistsToSearch.isEmpty()) {
			ArrayList<String> aToSearch = new ArrayList<String>();
			for(String artist : artistsToSearch) {
				aToSearch.add(artist);
			}
			searchOutput.put("searchByArtist", searchByArtist(aToSearch));
		}
		if(!tagsToSearch.isEmpty()) {
			ArrayList<String> tgToSearch = new ArrayList<String>();
			for(String tag : tagsToSearch) {
				tgToSearch.add(tag);
			}
			searchOutput.put("searchByTag", searchByTag(tgToSearch));
		}
		if(!titlesToSearch.isEmpty()) {
			ArrayList<String> tToSearch = new ArrayList<String>();
			for(String t : titlesToSearch) {
				tToSearch.add(t);
			}
			searchOutput.put( "searchByTitle", searchByTitle(tToSearch));
		}
		Path outPath = Paths.get(searchOutputpath);
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
		lock.lockWrite();
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
		lock.unlockWrite();
	}

	/**
	 * artist part of the search method.
	 * @param artistsToSearch
	 * @return
	 * @throws JSONException
	 */
	private JSONArray searchByArtist(ArrayList<String> artistsToSearch) throws JSONException {
		JSONArray array2 = new JSONArray();
		for(String artist : artistsToSearch) {
			JSONObject obj1 = new JSONObject();
			JSONArray array3 = new JSONArray();
			obj1.put("artist", artist);
			TreeSet<SongInfo> similarSongs = new TreeSet<SongInfo>(new CompareByTrack_id());
			if(byArtistForSearch.containsKey(artist)) {
				for(SongInfo song : byArtistForSearch.get(artist)) {
					ArrayList<String> newSimilars = new ArrayList<String>();
					for(String simi : song.getSimilars()) {
						newSimilars.add(simi);
					}
					for(String track_id : newSimilars) {
						if(byTrack_id.containsKey(track_id)) {
							SongInfo src = byTrack_id.get(track_id);
							HashSet<String> newTags = new HashSet<String>();
							for(String tg : src.getTags()) {
								newTags.add(tg);
							}
							ArrayList<String> newSimi = new ArrayList<String>();
							for(String simi : song.getSimilars()) {
								newSimi.add(simi);
							}
							SongInfo sinfo = new SongInfo(src.getArtist(), src.getTitle(), newTags, src.getTrack_id(), newSimi);
							similarSongs.add(sinfo);
						}
					}
				}
			}
			for(SongInfo song : similarSongs) {
				SongInfo si = new SongInfo(song.getArtist(), song.getTitle(), null, song.getTrack_id(), null);
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
	private JSONArray searchByTag(ArrayList<String> tagsToSearch) throws JSONException {
		JSONArray array2 = new JSONArray();
		for(String tag : tagsToSearch) {
			JSONObject obj1 = new JSONObject();
			JSONArray array3 = new JSONArray();
			if(byTagForSearch.containsKey(tag)) {
				for(SongInfo song : byTagForSearch.get(tag)) {
					SongInfo si = new SongInfo(song.getArtist(), song.getTitle(), null, song.getTrack_id(), null);
					if(song != null) {
						JSONObject obj2 = new JSONObject();
						obj2.put("artist", si.getArtist());
						obj2.put("trackId", si.getTrack_id());
						obj2.put("title", si.getTitle());
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
	private JSONArray searchByTitle(ArrayList<String> titlesToSearch) throws JSONException {
		JSONArray array2 = new JSONArray();
		for(String title : titlesToSearch) {
			JSONObject obj1 = new JSONObject();
			JSONArray array3 = new JSONArray();
			TreeSet<SongInfo> similarSongs = new TreeSet<SongInfo>(new CompareByTrack_id());
			if(byTitleForSearch.containsKey(title)) {
				for(SongInfo song : byTitleForSearch.get(title)) {
					ArrayList<String> newSimilars = new ArrayList<String>();
					for(String simi : song.getSimilars()) {
						newSimilars.add(simi);
					}
					for(String track_id : newSimilars) {
						if(byTrack_id.containsKey(track_id)) {
							SongInfo src = byTrack_id.get(track_id);
							HashSet<String> newTags = new HashSet<String>();
							for(String tg : src.getTags()) {
								newTags.add(tg);
							}
							ArrayList<String> newSimi = new ArrayList<String>();
							for(String simi : song.getSimilars()) {
								newSimi.add(simi);
							}
							SongInfo sinfo = new SongInfo(src.getArtist(), src.getTitle(), newTags, src.getTrack_id(), newSimi);
							similarSongs.add(sinfo);
						}
					}
				}
				for(SongInfo song : similarSongs) {
					SongInfo si = new SongInfo(song.getArtist(), song.getTitle(), null, song.getTrack_id(), null);
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

	public void addArtistInfo(ArtistInfo ai) {
		lock.lockWrite();
		artistsByPCount.add(ai);
		artistsByAlpha.add(ai);
		lock.unlockWrite();
	}
	
	public HashSet<String> getCopyArtists() {
		lock.lockRead();
		for(String artist : byArtist.keySet()) {
			copyArtists.add(artist);
		}
		lock.unlockRead();
		return copyArtists;
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
			
		}catch(IOException e) {
			System.out.print(e.getMessage());	
		}
		lock.unlockRead();
		return true;
	}
}
