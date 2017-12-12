package servlets;

import java.util.Comparator;

import songLibrary.ArtistInfo;

public class CompareByPCount implements Comparator<ArtistInfo>{

	@Override
	public int compare(ArtistInfo a1, ArtistInfo a2) {
		return a2.getPlaycount() - a1.getPlaycount();
	}

}
