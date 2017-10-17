package songfinder;

import java.util.HashSet;

/**
 * a class to hold info of a single song.
 * @author nina luo
 *
 */
public class SongInfo {
	private String artist, title, track_id;
	private HashSet<String> tags;
	
	public SongInfo(String artist, String title, HashSet<String> tags, String track_id) {
		this.artist = artist;
		this.title = title;
		this.tags = tags;
		this.track_id = track_id;
	}
	
	/**
	 * return to artist's name.
	 * @return
	 */
	public String getArtist() {
		return artist;
	}
	
	/**
	 * return to the title.
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * return to all the tags that song has.
	 * @return
	 */
	public HashSet<String> getTags() {
		return tags;
	}
	
	/**
	 * return to the track_id.
	 * @return
	 */
	public String getTrack_id() {
		return track_id;
	}
}
