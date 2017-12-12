package songfinder;

import org.json.JSONException;

import general.LibraryBuilder;
import general.ParseArgs;
import socket.HTTPFetcher;
import songLibrary.ArtistInfo;

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
			
		} catch (JSONException e) {
			e.printStackTrace();
		}	
	}
}
