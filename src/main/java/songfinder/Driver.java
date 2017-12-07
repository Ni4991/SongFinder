package songLibrary;

import java.io.File;
import java.io.FileNotFoundException;

import org.json.JSONException;

import general.LibraryBuilder;
import general.ParseArgs;
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
			lb.build();	//TODO: uncomment before submit
//			String punk = HTTPFetcher.download("ws.audioscrobbler.com", "/2.0/?method=artist.getinfo"
//					+ "&artist=Punk%27d+Royal&api_key=9162bc3f7439ff3d4258613b75d37287&format=json");
//			System.out.print(punk);//$.artist.bio.summary
//			String robin = HTTPFetcher.download("ws.audioscrobbler.com", "/2.0/?method=artist.getinfo"
//					+ "&artist=Robin+Frederick&api_key=9162bc3f7439ff3d4258613b75d37287&format=json");
//			System.out.print(robin);//$.artist.bio.content
//			String walter = HTTPFetcher.download("ws.audioscrobbler.com", "/2.0/?method=artist.getinfo"
//					+ "&artist=Walter+Trout&api_key=9162bc3f7439ff3d4258613b75d37287&format=json");
//			System.out.print(walter);//$.artist.bio.content
		} catch (JSONException e) {
			e.printStackTrace();
		}	
	}
}
