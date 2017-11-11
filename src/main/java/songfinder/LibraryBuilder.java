package songfinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;

/**
 * A class to add .json files to the song library.
 * @author nina luo
 *
 */   
public class LibraryBuilder {
	private String inputpath, order;
	private Library library;
	private WorkQueue wq;
	private Lock lock;
	
	public LibraryBuilder (String inputpath, int nThreads, String order) {
		this.order = order;
		this.inputpath = inputpath;
		lock = new Lock();
		library = new Library(order, lock);
		wq = new WorkQueue(nThreads);
		
	}
	
	/**
	 * A method to find file in sub directories.
	 * @param dir
	 */
	public void build(File dir) {
		File[] files = dir.listFiles();
		for(int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()) {
				build(files[i]);
			}
			else {
				if(!files[i].exists()) {
					continue;
				}
				String fileName = files[i].getName();
				if(fileName.toLowerCase().endsWith(".json")) {
					lock.lockWrite();
					wq.execute(new Worker(this.getLibrary(), files[i]));
					lock.unlockWrite();
				}	
			}
		}
	}
	
	/**
	 * Return workqueue.
	 * @return
	 */
	public WorkQueue getWorkQueue() {
		return wq;
	}
	
	/**
	 * Get collection of all songs(sorted).
	 * @return
	 */
	public Library getLibrary() {
		return library;	
	}
}
