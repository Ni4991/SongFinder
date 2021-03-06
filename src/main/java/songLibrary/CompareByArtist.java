package songLibrary;

import java.util.Comparator;

import general.SongInfo;

/**
 * a class to sort by Artist's name.
 * @author nina luo
 *
 */
public class CompareByArtist implements Comparator<SongInfo> {
	@Override
	public int compare(SongInfo s1, SongInfo s2) {
		if(s1.getTitle().equals(s2.getTitle())) {
			return s1.getTrack_id().compareTo(s2.getTrack_id());
		}
		return s1.getTitle().compareTo(s2.getTitle());
	}
}
