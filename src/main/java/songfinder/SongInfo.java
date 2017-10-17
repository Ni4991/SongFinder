package songfinder;

import java.util.HashSet;

public class SongInfo {
	private String artist, title, track_id;
	private HashSet<String> tags;
	
	public SongInfo(String artist, String title, HashSet<String> tags, String track_id) {
		this.artist = artist;
		this.title = title;
		this.tags = tags;
		this.track_id = track_id;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public String getTitle() {
		return title;
	}
	
	public HashSet<String> getTags() {
		return tags;
	}
	
	public String getTrack_id() {
		return track_id;
	}
}
