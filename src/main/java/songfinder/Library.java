package songfinder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * a class to hold information of all songs.
 * @author nina luo
 *
 */
public class Library {
	private String order;
	private TreeSet<SongInfo> byArtist, byTitle;
	private TreeMap<String, TreeSet<SongInfo>> byTag;
	//TODO: I would consider changing byArtist and byTitle from a treeset to something else that allows you to
	//get song info for all songs of a specific artist or title by getting the artist/title, because later if you wanted songs of specific artist
	//the way you have it now, you would have to loop through the whole set to get only songs by that artist 
	public Library(String order) {
		this.order = order;
		byArtist = new TreeSet<SongInfo>(new CompareByArtist());
		byTitle = new TreeSet<SongInfo>(new CompareByTitle());
		byTag = new TreeMap<String, TreeSet<SongInfo>>();
	}
	
	/**
	 * add each song object to three data structures.
	 * @param si
	 */
	public void add(SongInfo si) {
		byArtist.add(si);
		byTitle.add(si);
		for(String tag : si.getTags()) {
			if(byTag.containsKey(tag)) {
				byTag.get(tag).add(si);
			}
			else {
				TreeSet<SongInfo> value = new TreeSet<SongInfo>(new CompareByTrack_id());
				value.add(si);
				byTag.put(tag, value);
			}
		}
	}
	
	/**
	 * save sorted songs to file.
	 * @param resultPath
	 * @param order
	 * @return
	 */
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
					for(SongInfo song : value) {
						sb2.append(song.getTrack_id() + " ");
					}
					sb.append(str + ": " + sb2 + "\n");
					sb2.delete(0, sb2.length());
				}
				output.write(sb.toString());
			}
		}catch(IOException e) {
			System.out.print(e.getMessage());	
		}
		return true;
	}
}
