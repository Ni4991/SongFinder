package songfinder;

import java.util.Comparator;

/**
 * a class to sort by Artist's name.
 * @author nina luo
 *
 */
public class CompareByArtist implements Comparator<SongInfo> {
	@Override
	/**
	 * Sort two songs by artist.
	 * If two songs have the same artist and title, sort by track_id.
	 * If two songs have the same artist, sort by title.
	 */
	public int compare(SongInfo s1, SongInfo s2) {
		if(s1.getArtist().equals(s2.getArtist())) {
			if(s1.getTitle().equals(s2.getTitle())) {
				return s1.getTrack_id().compareTo(s2.getTrack_id());
			}
			return s1.getTitle().compareTo(s2.getTitle());
		}
		return (s1.getArtist().compareTo(s2.getArtist()));
	}
}
