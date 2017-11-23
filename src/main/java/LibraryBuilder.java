package general;

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

import concurrent.Lock;
import concurrent.WorkQueue;
import concurrent.Worker;
import songLibrary.Library;

/**
 * a class to add .json files to the song library.
 * @author nina luo
 *
 */   
public class LibraryBuilder {
	private String inputpath, outputpath, order;
	private int nThreads;
	private ArrayList<String> artistsToSearch, titlesToSearch, tagsToSearch;
	private Library library;
	private WorkQueue wq;
	private Lock lock;
	
	public LibraryBuilder() {
		inputpath = "input\\lastfm_subset";
		outputpath = "output\\servlet";
		order = "artist";
		nThreads = 3;
	}
	public LibraryBuilder (String inputpath, int nThreads, String order, String outputpath, 
			ArrayList<String> artistsToSearch, ArrayList<String> titlesToSearch, 
			ArrayList<String> tagsToSearch) {
		this.order = order;
		this.inputpath = inputpath;
		this.outputpath = outputpath;
		this.nThreads = nThreads;
		lock = new Lock();
		library = new Library(order, lock);
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
				if(fileName.toLowerCase().endsWith(".json")) {
					wq.execute(new Worker(this.getLibrary(), files[i]));
				}	
			}
		}
		getWorkQueue().shutdown();
		try {
			getWorkQueue().awaitTermination();
		} catch (InterruptedException e) {
			System.out.println("interrupted");
		}
		getLibrary().saveToFile(outputpath, order);
		getLibrary().search(artistsToSearch, titlesToSearch, tagsToSearch, outputpath);
	}
	
	/**
	 * return workqueue.
	 * @return
	 */
	public WorkQueue getWorkQueue() {
		return wq;
	}
	
	/**
	 * getter of the song library.
	 * @return
	 */
	public Library getLibrary() {
		return library;	
	}
}
