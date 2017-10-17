package songfinder;

import java.util.Comparator;

public class CompareByTitle implements Comparator<SongInfo> {
	@Override
	public int compare(SongInfo s1, SongInfo s2) {
		if(s1.getTitle().equals(s2.getTitle())) {
			if(s1.getArtist().equals(s2.getArtist())) {
				return s1.getTrack_id().compareTo(s2.getTrack_id());
			}
			return s1.getArtist().compareTo(s2.getArtist());
		}
		return (s1.getTitle().compareTo(s2.getTitle()));
	}
}
