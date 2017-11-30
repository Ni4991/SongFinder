package general;

import java.io.File;
import java.io.FileNotFoundException;

import org.json.JSONException;

import socket.HTTPFetcher;

/**
 * main class for SongFinder lab and projects.
 * @author nina luo
 *
 */
public class Driver {
	   
	/**
	 * main method.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ParseArgs pa = new ParseArgs(args);
			if(!pa.checkArgs(args)) {
				System.out.println("bad args.");
				return;
			}
			LibraryBuilder lb = new LibraryBuilder(pa.getInputpath(), pa.getnThreads(), pa.getOrder()
					,pa.getOutputpath(), pa.getArtistsToSearch(), pa.getTitlesToSearch(), 
					pa.getTagsToSearch(), pa.getDoSearch(), pa.getSearchOutputpath());
			lb.build();	
//			String tmp = HTTPFetcher.download("ws.audioscrobbler.com", "/2.0/?method=artist.getinfo"
//					+ "&artist=黎明&api_key=9162bc3f7439ff3d4258613b75d37287&format=json");
//			String tmp2 = HTTPFetcher.download("ws.audioscrobbler.com", "/2.0/?method=artist.getinfo"
//					+ "&artist=Cher&api_key=9162bc3f7439ff3d4258613b75d37287&format=json");
//			System.out.println(tmp);
//			System.out.println(tmp2);
//			lb.getLibrary().htmlArtists();//to delete
//			System.out.println("hey success");
//			lb.getLibrary().htmlArtists();
		} catch (JSONException e) {
			e.printStackTrace();
		}	
	}
}
