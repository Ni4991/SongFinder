package servlets;

import java.util.Comparator;

public class CompareByPCount implements Comparator<ArtistInfo>{

	@Override
	public int compare(ArtistInfo a1, ArtistInfo a2) {
		return a1.getPlaycount() - a2.getPlaycount();
	}

}
