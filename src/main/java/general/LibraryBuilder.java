package general;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import concurrent.Fetcher;
import concurrent.Lock;
import concurrent.WorkQueue;
import concurrent.Worker;
import socket.HTTPFetcher;
import songLibrary.Library;

/**
 * a class to add .json files to the song library.
 * @author nina luo
 *
 */   
public class LibraryBuilder {
	private String inputpath, outputpath, searchOutputpath, order;
	private int nThreads;
	private ArrayList<String> artistsToSearch, titlesToSearch, tagsToSearch;
	private Library library;
	private WorkQueue wq;
	private Lock lock;
	private boolean doSearch, doSave;
	
	public LibraryBuilder() {
		inputpath = "input";
		outputpath = "output";
		order = "artist";
		nThreads = 3;
		lock = new Lock();
		library = new Library(order, lock);
		wq = new WorkQueue(nThreads);
		doSearch = false;
		doSave = true;
	}
	
	public LibraryBuilder (String inputpath, int nThreads, String order, String outputpath) {
		this.order = order;
		this.inputpath = inputpath;
		this.outputpath = outputpath;
		this.nThreads = nThreads;
		lock = new Lock();
		library = new Library(order, lock);
		wq = new WorkQueue(nThreads);
		doSearch = false;
		doSave = true;
	}
	
	public LibraryBuilder (String inputpath, int nThreads, String order, String outputpath, 
			ArrayList<String> artistsToSearch, ArrayList<String> titlesToSearch, 
			ArrayList<String> tagsToSearch, boolean doSearch, String searchoutputpath) {
		this.order = order;
		this.inputpath = inputpath;
		this.outputpath = outputpath;
		this.nThreads = nThreads;
		lock = new Lock();
		library = new Library(order, lock);
		wq = new WorkQueue(nThreads);
		this.doSearch = doSearch;
		this.artistsToSearch = artistsToSearch;
		this.titlesToSearch = titlesToSearch;
		this.tagsToSearch = tagsToSearch;
		this.searchOutputpath = searchoutputpath;
		doSave = true;
	}
	
	public void build() {
		parseFile(new File(inputpath));
		this.wq.shutdown();
		try {
			this.wq.awaitTermination();
		} catch (InterruptedException e) {
			System.out.println("interrupted");
		}
//		wq = new WorkQueue(30);//TODO: uncomment later
//		parseLastfm();
//		this.wq.shutdown();
//		try {
//			this.wq.awaitTermination();
//		} catch (InterruptedException e) {
//			System.out.println("interrupted");
//		}
		if(doSave) {
			this.library.saveToFile(outputpath, order);
		}
		if(doSearch) {
			library.search(artistsToSearch, titlesToSearch, tagsToSearch, searchOutputpath);
		}
	}
	/**
	 * 
	 */
	private void parseLastfm() {		
		for(String artist : library.getCopyArtists()) {
			try {
				artist = URLEncoder.encode(artist, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.wq.execute(new Fetcher(artist, library));
		}
	}

	/**
	 * a method to find file in sub directories.
	 * @param dir
	 */
	public void parseFile(File dir) {
		File[] files = dir.listFiles();
		for(int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()) {
				parseFile(files[i]);
			}
			else {
				if(!files[i].exists()) {
					continue;
				}
				String fileName = files[i].getName();
				if(fileName.toLowerCase().endsWith(".json")) {
					wq.execute(new Worker(library, files[i]));
				}	
			}
		}
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
