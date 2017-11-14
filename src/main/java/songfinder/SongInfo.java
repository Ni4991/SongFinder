package songfinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * a class to hold info of a single song.
 * @author nina luo
 *
 */
public class SongInfo {
	private String artist, title, track_id;
	private HashSet<String> tags;
	private ArrayList<String> similars;
	
	public SongInfo(String artist, String title, HashSet<String> tags, String track_id, ArrayList<String> similars) {
		this.artist = artist;
		this.title = title;
		this.tags = tags;
		this.track_id = track_id;
		this.similars = similars;
	}
	
	/**
	 * getter of all similar songs.
	 * @return
	 */
	public ArrayList<String> getSimilars() {
		return similars;
	}
	
	/**
	 * return artist's name.
	 * @return
	 */
	public String getArtist() {
		return artist;
	}
	
	/**
	 * return the title.
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * return all the tags that song has.
	 * @return
	 */
	public HashSet<String> getTags() {
		return tags;
	}
	
	/**
	 * return the track_id.
	 * @return
	 */
	public String getTrack_id() {
		return track_id;
	}
}
