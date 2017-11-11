package songfinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class Worker implements Runnable {

	private Library library;
	private File file;
	private JsonObject songObj;
	
	public Worker(Library library, File file) {
		this.library = library;
		this.file = file;
	}
	
	@Override
	public void run() {
		try {
			FileReader fr = new FileReader(file);
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
