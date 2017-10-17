package songfinder;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Main class for SongFinder lab and projects.
 * @author nina luo
 *
 */
public class Driver {
	private static boolean flag;
	/**
	 * Main method.
	 * @param args
	 */
	public static void main(String[] args) {
		flag = false;
		String inputpath = "", outputpath = "", order = "";
		if(args.length != 6) {
			System.out.println("invalid input");
			return;
		}
		for(int i = 0; i < 5; i++) {
			if(args[i].equals("-input")) {
				flag = true;
				inputpath = args[i + 1]; 
				File file = new File(inputpath);
				if(!file.isDirectory()) {
					return;
				}
			}
			if(args[i].equals("-output")) {
				flag = true;
				outputpath = args[i + 1];
			}
			if(args[i].equals("-order")) {
				flag = true;
				order = args[i + 1];
				if(!(order.equals("artist") || order.equals("title") || order.equals("tag"))){
					return;
				}
			}
		}
		if(flag == false) {
			System.out.println("nono");
			System.exit(1);
		}
		LibraryBuilder lb = new LibraryBuilder(inputpath, outputpath, order);
		
		lb.getLibraryInfo().saveToFile(outputpath, order);
	}
}

