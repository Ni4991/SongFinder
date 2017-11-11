package songfinder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
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
	private TreeMap<String, TreeSet<SongInfo>> byArtist, byTitle, byTag;
	private Lock lock;
	
	public Library(String order, Lock lock) {
		this.order = order;
		this.lock = lock;
		byArtist = new TreeMap<String, TreeSet<SongInfo>>();
		byTitle = new TreeMap<String, TreeSet<SongInfo>>();
		byTag = new TreeMap<String, TreeSet<SongInfo>>();
	}
	
	/**
	 * add each song object to three data structures.
	 * @param si
	 */
	public void add(SongInfo si) {
		lock.lockWrite();
		if(byArtist.containsKey(si.getArtist())) {
			byArtist.get(si.getArtist()).add(si);
		}
		else {
			TreeSet<SongInfo> value = new TreeSet<SongInfo>(new CompareByArtist());
			value.add(si);
			byArtist.put(si.getArtist(), value);
		}
		if(byTitle.containsKey(si.getTitle())) {
			byTitle.get(si.getTitle()).add(si);
		}
		else {
			TreeSet<SongInfo> value = new TreeSet<SongInfo>(new CompareByTitle());
			value.add(si);
			byTitle.put(si.getTitle(), value);
		}
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
		lock.unlockWrite();
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
				lock.lockWrite();
				
				Set<Entry<String, TreeSet<SongInfo>>> entrySet = byArtist.entrySet();
				Iterator<Entry<String, TreeSet<SongInfo>>> it = entrySet.iterator();
				
				while(it.hasNext()) {
					Entry<String, TreeSet<SongInfo>> me = it.next();
					String str = me.getKey();
					TreeSet<SongInfo> value = me.getValue();
					for(SongInfo song : value) {
						sb.append(song.getArtist() + " - " + song.getTitle() + "\n");
					}   
				} 
				output.write(sb.toString());
			}     
			else if(order.equals("title")) {
				lock.lockWrite();
				Set<Entry<String, TreeSet<SongInfo>>> entrySet = byTitle.entrySet();
				Iterator<Entry<String, TreeSet<SongInfo>>> it = entrySet.iterator();
				while(it.hasNext()) {
					Entry<String, TreeSet<SongInfo>> me = it.next();
					String str = me.getKey();
					TreeSet<SongInfo> value = me.getValue();
					for(SongInfo song : value) {
						sb.append(song.getArtist() + " - " + song.getTitle() + "\n");
					}
				}
				output.write(sb.toString());
			}

			else if(order.equals("tag")) {
				lock.lockWrite();
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
			lock.unlockWrite();
		}catch(IOException e) {
			System.out.print(e.getMessage());	
		}
		return true;
	}
}
