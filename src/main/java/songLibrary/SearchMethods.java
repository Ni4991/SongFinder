package songLibrary;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import concurrent.Lock;
import general.SongInfo;

public class SearchMethods {
	
	private Lock lock;
	private Library library;
	
	public SearchMethods(Library library, Lock lock) {
		this.lock = lock;
		this.library = library;
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
		try{
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
			return searchOutput;
		}finally {
			lock.unlockRead();
		}
	}
	
	

	/**
	 * artist part of the search method.
	 * @param artistsToSearch
	 * @return
	 * @throws JSONException
	 */
	public JSONArray searchByArtist(ArrayList<String> artistsToSearch) throws JSONException {
		try {
			lock.lockRead();
			JSONArray array2 = new JSONArray();
			TreeSet<SongInfo> similarSongs;
			for(String artist : artistsToSearch) {
				JSONObject obj1 = new JSONObject();
				JSONArray array3 = new JSONArray();
				obj1.put("artist", artist);
				similarSongs = new TreeSet<SongInfo>(new CompareByTrack_id());
				//library.getByAForSearch() and library.getByT_id() are deep copied down to songinfo.
				if(library.getByAForSearch().containsKey(artist)) {
					for(SongInfo song : library.getByAForSearch().get(artist)) {
						for(String track_id : song.getSimilars()) {
							if(library.getByT_id().containsKey(track_id)) {
								similarSongs.add(library.getByT_id().get(track_id));
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
		}finally {
			lock.unlockRead();
		}
	}
	
	/**
	 * tag part of the search method.
	 * @param tagsToSearch
	 * @return
	 * @throws JSONException
	 */
	public JSONArray searchByTag(ArrayList<String> tagsToSearch) throws JSONException {
		try {
			lock.lockRead();
			JSONArray array2 = new JSONArray();
			for(String tag : tagsToSearch) {
				JSONObject obj1 = new JSONObject();
				JSONArray array3 = new JSONArray();
				if(library.getByTaForSearch().containsKey(tag)) {
					for(SongInfo song : library.getByTaForSearch().get(tag)) {
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
		}finally {
			lock.unlockRead();
		}
	}

	/**
	 * title part of the search method.
	 * @param titlesToSearch
	 * @return
	 * @throws JSONException
	 */
	public JSONArray searchByTitle(ArrayList<String> titlesToSearch) throws JSONException {
		try {
			lock.lockRead();
			JSONArray array2 = new JSONArray();
			TreeSet<SongInfo> similarSongs;
			for(String title : titlesToSearch) {
				JSONObject obj1 = new JSONObject();
				JSONArray array3 = new JSONArray();
				similarSongs = new TreeSet<SongInfo>(new CompareByTrack_id());
				if(library.getByTiForSearch().containsKey(title)) {
					for(SongInfo song : library.getByTiForSearch().get(title)) {
						for(String track_id : song.getSimilars()) {
							if(library.getByT_id().containsKey(track_id)) {
								similarSongs.add(library.getByT_id().get(track_id));
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
		finally {
			lock.unlockRead();
		}
	}
}
