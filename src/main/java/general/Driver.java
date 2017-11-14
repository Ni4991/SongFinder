package general;

import java.io.File;
import java.io.FileNotFoundException;

import org.json.JSONException;

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
			LibraryBuilder lb = new LibraryBuilder(pa.getInputpath(), pa.getnThreads(), pa.getOrder());
			lb.build(new File(pa.getInputpath()));
			lb.getWorkQueue().shutdown();
			try {
				lb.getWorkQueue().awaitTermination();
			} catch (InterruptedException e) {
				System.out.println("interrupted");
			}
			lb.getLibrary().saveToFile(pa.getOutputpath(), pa.getOrder());
			if(pa.getDoSearch()) {
				lb.getLibrary().search(pa.getArtistsToSearch(), pa.getTitlesToSearch(), pa.getTagsToSearch(), pa.getSearchOutputpath());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}	
	}
}
