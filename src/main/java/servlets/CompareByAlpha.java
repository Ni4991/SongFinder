package servlets;

import java.util.Comparator;

import songLibrary.ArtistInfo;

public class CompareByAlpha implements Comparator<ArtistInfo>{

	@Override
	public int compare(ArtistInfo a1, ArtistInfo a2) {
		return a1.getName().compareTo(a2.getName());
	}
}
