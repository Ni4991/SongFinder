package songfinder;

import java.io.File;
import java.io.FileNotFoundException;

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
		ParseArgs pa = new ParseArgs(args);
//		String inputpath = pa.getInputpath();
//		String order = pa.getOrder();
//		String outputpath = pa.getOutputpath();
//		int nThreads = pa.getnThreads();
//		LibraryBuilder lb = new LibraryBuilder(inputpath, nThreads, order);
//		lb.build(new File(inputpath));
//		lb.getWorkQueue().shutdown();
//		try {
//			lb.getWorkQueue().awaitTermination();
//		} catch (InterruptedException e) {
//			System.out.println("interrupted");
//		}
//		lb.getLibrary().saveToFile(outputpath, order);
	}
}