package concurrent;

import java.io.IOException;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import servlets.ArtistInfo;
import socket.HTTPFetcher;
import songLibrary.Library;

public class Fetcher implements Runnable{
	
	private String artist;
	private Library library;
	private String page;
//	private String apiKey = "9162bc3f7439ff3d4258613b75d37287";
	
	public Fetcher(String artist, Library library) {
		this.artist = artist;
		this.library = library;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String page = HTTPFetcher.download("ws.audioscrobbler.com", "/2.0/?method=artist.getinfo"
				+ "&artist="+ artist +"&api_key=9162bc3f7439ff3d4258613b75d37287&format=json");
//		System.out.println(artist);//test
		try(Scanner sc = new Scanner(page)){
		while(sc.hasNextLine()) {
			String line = sc.nextLine();
			if(line.trim().length() == 0) {
				line = sc.nextLine();
				if(line.trim().startsWith("{\"artist\"")) {
					JsonParser parser = new JsonParser();
					JsonObject toJson = (JsonObject) parser.parse(line);
					String art = toJson.getAsJsonObject("artist").get("name").getAsString();
					JsonObject data = toJson.getAsJsonObject("artist");
					data = data.getAsJsonObject("stats");
					String playc = data.get("playcount").getAsString();
					data = toJson.getAsJsonObject("artist");
					data = data.getAsJsonObject("stats");
					String lsnrs = data.get("listeners").getAsString();
					data = toJson.getAsJsonObject("artist");
					data = data.getAsJsonObject("bio");
					String published = data.get("published").getAsString();
					data = toJson.getAsJsonObject("artist");
					data = data.getAsJsonObject("bio");
					String summary = data.get("summary").getAsString();
					data = toJson.getAsJsonObject("artist");
					data = data.getAsJsonObject("bio");
					String content = data.get("content").getAsString();
					String bio = "published: " + published + "\n" + "summary: " + summary + "\n" + "content: " + content;
				    int playcount = Integer.parseInt(playc);
				    int listeners = Integer.parseInt(lsnrs);
				    data = toJson.getAsJsonObject("artist");
				    JsonArray dat = data.getAsJsonArray("image");
				    String image = dat.get(0).getAsJsonObject().get("#text").getAsString();
				    library.addArtistInfo(new ArtistInfo(art, bio, image, listeners, playcount));
					}
				}
			}
		} catch (JsonParseException pe) {
			System.err.println(artist + " Unable to execute tests. " + pe.getMessage());
		}
	}
}
