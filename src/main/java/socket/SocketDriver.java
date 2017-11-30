package socket;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import general.LibraryBuilder;
import general.SongInfo;
import servlets.ArtistInfo;
import servlets.SearchServlet;
import songLibrary.Library;

public class SocketDriver {
	
	private static String str;
	
	public String getStr() {
		return str;
	}

	public static String getByPlaycount(TreeMap<Integer,String> playCount) {
		StringBuilder builder = new StringBuilder();
		builder.append("<table border=1 border-spacing=3px>");
		builder.append("<tr><th>Artist</th><th>Play count</th></tr>");
		Set<Entry<Integer,String>> entrySet = playCount.entrySet();
		Iterator<Entry<Integer, String>> it = entrySet.iterator();
		while(it.hasNext()) {
			Entry<Integer, String> me = it.next();
			String arti = me.getValue();
			int coun = me.getKey(); 
			builder.append("<tr><td>" + arti + "</td>"
					+ "<td>" + coun + "</td></tr>");
		} 	
		builder.append("</table>");
		str = builder.toString();
		return builder.toString();
	}

	public static void main(String[] args) {	
	}
}
