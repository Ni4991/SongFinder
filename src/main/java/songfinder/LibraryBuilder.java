package songfinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
/**
 * a class to add .json files to the song library.
 * @author nina luo
 *
 */
public class LibraryBuilder {
	private Library library;
	private String inputpath, outputpath, order;
	private JsonObject songObj;
	
	public LibraryBuilder (String inputpath, String outputpath, String order) {
		library = new Library(order);
		findFile(new File(inputpath));
	}
	/**
	 * a method to find sub directories.
	 * @param dir
	 */
	public void findFile(File dir) {
		File[] files = dir.listFiles();
		for(int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()) {
				findFile(files[i]);
			}
			else {
				if(!files[i].exists()) {
					continue;
				}
				String fileName = files[i].getName();
				if(fileName.toLowerCase().endsWith(".json")) {
					try {
						FileReader fr = new FileReader(files[i]);
						BufferedReader br = new BufferedReader(fr);
						String line = br.readLine();
						JsonParser parser = new JsonParser();
						songObj = (JsonObject) parser.parse(line);
						String artist = songObj.get("artist").getAsString();
						String title = songObj.get("title").getAsString();
						String track_id = songObj.get("track_id").getAsString();
						JsonArray arr = songObj.get("tags").getAsJsonArray();
						HashSet<String> tags = new HashSet<String>();
						for(int j = 0; j < arr.size(); j++) {
							tags.add(arr.get(j).getAsJsonArray().get(0).getAsString());
						}
						SongInfo si = new SongInfo(artist, title, tags, track_id);
						library.add(si);
					} catch (FileNotFoundException e1) {
						System.out.println("file not found.");
					} catch (JsonParseException pe) {
						System.err.println("Unable to execute tests. " + pe.getMessage());
					} catch (IOException e) {
						e.getMessage();
					}
				}	
			}
		}
	}
	/**
	 * return LibraryInfo to main().
	 * @return
	 */
	public Library getLibraryInfo() {
		return library;	
	}
}
