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
	
	/**
	 * getter of byTrack_id
	 * @return
	 */
	public HashMap<String, SongInfo> getByT_id(){
		try {
			lock.lockRead();
			HashMap<String, SongInfo> copyHm = new HashMap<String, SongInfo>();
			for(String track_id : byTrack_id.keySet()) {
				HashSet<String> newTags = new HashSet<String>();
				for(String tg : byTrack_id.get(track_id).getTags()) {
					newTags.add(tg);
				}
				ArrayList<String> newSimi = new ArrayList<String>();
				for(String simi : byTrack_id.get(track_id).getSimilars()) {
					newSimi.add(simi);
				}	
				copyHm.put(track_id, new SongInfo(byTrack_id.get(track_id).getArtist(), byTrack_id.get(track_id).getTitle(), 
						newTags, byTrack_id.get(track_id).getTrack_id(), newSimi));
			}
			return copyHm;
		}
		finally {
			lock.unlockRead();
		}
	}
	
	/**
	 * getter for byTagForSearch
	 * @return
	 */
	public HashMap<String, TreeSet<SongInfo>> getByTaForSearch(){
		try {
			lock.lockRead();
			HashMap<String, TreeSet<SongInfo>> copyHm = new HashMap<String, TreeSet<SongInfo>>();
			for(String tag : byTagForSearch.keySet()) {
				TreeSet<SongInfo> copyTs = new TreeSet<SongInfo>(new CompareByTrack_id());
				for(SongInfo song : byTagForSearch.get(tag)) {
					HashSet<String> newTags = new HashSet<String>();
					for(String tg : song.getTags()) {
						newTags.add(tg);
					}
					ArrayList<String> newSimi = new ArrayList<String>();
					for(String simi : song.getSimilars()) {
						newSimi.add(simi);
					}
					copyTs.add(new SongInfo(song.getArtist(), song.getTitle(), newTags, song.getTrack_id(), newSimi));
				}
				copyHm.put(tag, copyTs);
			}
			return copyHm;
		}
		finally {
			lock.unlockRead();
		}
	}
	
	/**
	 * getter for byTitleForSearch
	 * @return
	 */
	public HashMap<String, TreeSet<SongInfo>> getByTiForSearch(){
		try {
			lock.lockRead();
			HashMap<String, TreeSet<SongInfo>> copyHm = new HashMap<String, TreeSet<SongInfo>>();
			for(String title : byTitleForSearch.keySet()) {
				TreeSet<SongInfo> copyTs = new TreeSet<SongInfo>(new CompareByTrack_id());
				for(SongInfo song : byTitleForSearch.get(title)) {
					HashSet<String> newTags = new HashSet<String>();
					for(String tg : song.getTags()) {
						newTags.add(tg);
					}
					ArrayList<String> newSimi = new ArrayList<String>();
					for(String simi : song.getSimilars()) {
						newSimi.add(simi);
					}
					copyTs.add(new SongInfo(song.getArtist(), song.getTitle(), newTags, song.getTrack_id(), newSimi));
				}
				copyHm.put(title, copyTs);
			}
			return copyHm;
		}
		finally {
			lock.unlockRead();
		}
	}
	
	/**
	 * getter for byArtistForSearch
	 * @return
	 */
	public HashMap<String, TreeSet<SongInfo>> getByAForSearch(){
		try {
			lock.lockRead();
			HashMap<String, TreeSet<SongInfo>> copyHm = new HashMap<String, TreeSet<SongInfo>>();
			for(String artist : byArtistForSearch.keySet()) {
				TreeSet<SongInfo> copyTs = new TreeSet<SongInfo>(new CompareByTrack_id());
				for(SongInfo song : byArtistForSearch.get(artist)) {
					HashSet<String> newTags = new HashSet<String>();
					for(String tg : song.getTags()) {
						newTags.add(tg);
					}
					ArrayList<String> newSimi = new ArrayList<String>();
					for(String simi : song.getSimilars()) {
						newSimi.add(simi);
					}
					copyTs.add(new SongInfo(song.getArtist(), song.getTitle(), newTags, song.getTrack_id(), newSimi));
				}
				copyHm.put(artist, copyTs);
			}
			return copyHm;
		}
		finally {
			lock.unlockRead();
		}
	}
	
	/**
	 * project 3 - feature search(normal, partial, case ignore)
	 * @param type
	 * @param partial
	 * @return
	 */
	public String searchToHtml(String type, String partial) {
		try {
			lock.lockRead();
			TreeSet<SongInfo> similarSongs = null;
			if(type.equals("Artist")) {
				similarSongs = searchArtist(partial);
			}
			else if(type.equals("Song Title")) {
				similarSongs = searchTitle(partial);
				
			}
			else if(type.equals("Tag")) {
				similarSongs = searchTag(partial);
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
			return builder.toString();
		}finally {
			lock.unlockRead();
		}
		
	}
	
	/**
	 * project 3 
	 * @param partial
	 * @return
	 */
	public TreeSet<SongInfo> searchTag(String partial) {
		try {
			lock.lockRead();
			TreeSet<SongInfo> similarSongs = new TreeSet<SongInfo>(new CompareByTrack_id());
			HashSet<String> containsQ = new HashSet<String>();
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
			return similarSongs;
		}finally {
			lock.unlockRead();
		}
	}
	
	/**
	 * project 3 
	 * @param partial
	 * @return
	 */
	public TreeSet<SongInfo> searchTitle(String partial) {
		try {
			lock.lockRead();
			TreeSet<SongInfo> similarSongs = new TreeSet<SongInfo>(new CompareByTrack_id());
			HashSet<String> containsQ = new HashSet<String>();
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
			return similarSongs;
		}finally {
			lock.unlockRead();
		}
	}

	/**
	 * project 3
	 * @param partial
	 * @return
	 */
	public TreeSet<SongInfo> searchArtist(String partial) {
		try {
			lock.lockRead();
			TreeSet<SongInfo> similarSongs = new TreeSet<SongInfo>(new CompareByTrack_id());
			HashSet<String> containsQ = new HashSet<String>();
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
			return similarSongs;
		}finally {
			lock.unlockRead();
		}
	}

	/**
	 * project 3 - list all alphabetically.
	 * @return
	 */
	public String htmlByAlpha() {
		try {
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
			return builder.toString();
		}finally {
			lock.unlockRead();
		}
		
	}
	
	/**
	 * project 3 - list all by play count.
	 * @return
	 */
	public String htmlByPCount() {
		try {
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
			return builder.toString();
		}finally {
			lock.unlockRead();
		}
		
	}
	
	/**
	 * add method for project 3.
	 * @param ai
	 */
	public void addArtistInfo(ArtistInfo ai) {
		lock.lockWrite();
		artistsByPCount.add(ai);
		artistsByAlpha.add(ai);
		lock.unlockWrite();
	}
	
	/**
	 * get a set of all artists.
	 * @return
	 */
	public HashSet<String> getCopyArtists() {
		try {
			lock.lockRead();
			for(String artist : byArtist.keySet()) {
				copyArtists.add(artist);
			}
			return copyArtists;
		}finally {
			lock.unlockRead();
		}
		
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
	 * add method for project 2 search.
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
	 * save sorted songs to file.
	 * @param resultPath
	 * @param order
	 * @return
	 */
	public void saveToFile(String resultPath, String order) {
		try {
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
			}
		}catch(IOException e) {
			System.out.print(e.getMessage());	
		}finally {
			lock.unlockRead();
		}
	}
}
