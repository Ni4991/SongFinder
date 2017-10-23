package songfinder;

import java.io.File;

/**
 * a class to parse arguments.
 * @author nina luo
 *
 */

public class ParseArgs {
	private String inputpath, outputpath, order;
	//TODO: consider having constructor that initializes the data members to empty strings. and putting this functionality in different method to check if args are valid
	//TODO: maybe add getters for each data member and can call those when you need that info in other classes
	//TODO: include a way to let users know why program exited, even a simple sysout statement is enough for now 
	public ParseArgs(String[] args) {
		if(args.length != 6) {
			return;
		}
		for(int i = 0; i < 5; i++) {
			if(args[i].equals("-input")) {
				inputpath = args[i + 1]; 
				File file = new File(inputpath);
				if(!file.isDirectory()) {
					return;
				}
			}
			if(args[i].equals("-output")) {
				outputpath = args[i + 1];
			}
			if(args[i].equals("-order")) {
				order = args[i + 1];
				if(!(order.equals("artist") || order.equals("title") || order.equals("tag"))){
					return;
				}
			}
		}
		LibraryBuilder lb = new LibraryBuilder(inputpath, outputpath, order);
		lb.getLibraryInfo().saveToFile(outputpath, order);
	}
}
