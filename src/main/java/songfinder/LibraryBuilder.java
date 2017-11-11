package songfinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

/**
 * a class to add .json files to the song library.
 * @author nina luo
 *
 */
public class LibraryBuilder {
	private String inputpath, order;
	private Library library;
	private WorkQueue wq;
	private int j;
	private Lock lock;
	
	
	public LibraryBuilder (String inputpath, int nThreads, String order) {
		this.order = order;
		this.inputpath = inputpath;
		lock = new Lock();
		library = new Library(order, lock);
//		System.out.println("order=" + order);
		wq = new WorkQueue(nThreads);
		
	}
	
	
	/**
	 * a method to find file in sub directories.
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
				j++;
				if(fileName.toLowerCase().endsWith(".json")) {
					System.out.println(files[i].getPath());
					lock.lockWrite();
					wq.execute(new Worker(this.getLibrary(), files[i]));
					lock.unlockWrite();
				}	
			}
		}
		System.out.println(j);
	}
	

	public WorkQueue getWorkQueue() {
		return wq;
	}
	
	/**
	 * get collection of all songs(sorted).
	 * @return
	 */
	public Library getLibrary() {
		return library;	
	}
}