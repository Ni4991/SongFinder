package songfinder;

import java.util.Comparator;

/**
 * A class to sort by track_id.
 * @author nina luo
 *
 */
public class CompareByTrack_id implements Comparator<SongInfo> {
	@Override
	public int compare(SongInfo s1, SongInfo s2) {
		return (s1.getTrack_id().compareTo(s2.getTrack_id()));
	}
}
