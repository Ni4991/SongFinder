package songLibrary;

import java.util.Comparator;

import general.SongInfo;

/**
 * a class to sort by title.
 * @author nina luo
 *
 */
public class CompareByTitle implements Comparator<SongInfo> {
	@Override
	public int compare(SongInfo s1, SongInfo s2) {
		if(s1.getArtist().equals(s2.getArtist())) {
			return s1.getTrack_id().compareTo(s2.getTrack_id());
		}
		return s1.getArtist().compareTo(s2.getArtist());
	}
}
