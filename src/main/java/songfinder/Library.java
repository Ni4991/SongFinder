package songfinder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import com.google.gson.JsonObject;

public class Library {
	private String order;
	private TreeSet<SongInfo> byArtist, byTitle;
	private TreeMap<String, TreeSet<SongInfo>> byTag;
	
	public Library(String order) {
		this.order = order;
		byArtist = new TreeSet<SongInfo>(new CompareByArtist());
		byTitle = new TreeSet<SongInfo>(new CompareByTitle());
		byTag = new TreeMap<String, TreeSet<SongInfo>>();
	}
	
	public void add(SongInfo si) {
		byArtist.add(si);
		byTitle.add(si);
		for(String tag : si.getTags()) {
			if(byTag.containsKey(tag)) {
				byTag.get(tag).add(si);
//				System.out.println(tag + "has: " + si.getTrack_id());
			}
			else {
				TreeSet<SongInfo> value = new TreeSet<SongInfo>(new CompareByTrack_id());
				value.add(si);
				byTag.put(tag, value);
				for(SongInfo song : value) {
//					System.out.println(song.getTrack_id());
				}
//				System.out.println(tag + " has: " + si.getTrack_id());
			}
		}
	}
	
	public boolean saveToFile(String resultPath, String order) {
		Path outPath = Paths.get(resultPath);
		outPath.getParent().toFile().mkdirs();
		StringBuilder sb = new StringBuilder();
		try(BufferedWriter output = Files.newBufferedWriter(outPath)){
			if(order.equals("artist")) {
				Iterator it = byArtist.iterator();
				while(it.hasNext()) {
					SongInfo si = (SongInfo)it.next();
					sb.append(si.getArtist()).append(" - ").append(si.getTitle()).append("\n");
				}
				output.write(sb.toString());
			}
			else if(order.equals("title")) {
				Iterator it = byTitle.iterator();
				while(it.hasNext()) {
					SongInfo si = (SongInfo)it.next();
					sb.append(si.getArtist()).append(" - ").append(si.getTitle()).append("\n");
				}
				output.write(sb.toString());
			}
			else if(order.equals("tag")) {
				StringBuilder sb2 = new StringBuilder();
				Set<Entry<String, TreeSet<SongInfo>>> entrySet = byTag.entrySet();
				Iterator<Entry<String, TreeSet<SongInfo>>> it = entrySet.iterator();
				
				while(it.hasNext()) {
					Entry<String, TreeSet<SongInfo>> me = it.next();
					String str = me.getKey();
					TreeSet<SongInfo> value = me.getValue();
					Iterator<SongInfo> it2 = value.iterator();
					for(SongInfo song : value) {
						
						sb2.append(song.getTrack_id() + " ");
					}
					SongInfo song = it2.next();
					sb.append(str + ": " + sb2 + "\n");
					sb2.delete(0, sb2.length());
//					sb.append(str + ": " + song.getTrack_id() + "\n");
				}
				output.write(sb.toString());
			}
		}catch(IOException e) {
			System.out.print(e.getMessage());	
		}
		return true;
	}
}
