package songfinder;

import java.io.File;
import java.io.FileNotFoundException;

import org.json.JSONException;

/**
 * Main class for SongFinder lab and projects.
 * @author nina luo
 *
 */
public class Driver {
	   
	/**
	 * Main method.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ParseArgs pa = new ParseArgs(args);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
