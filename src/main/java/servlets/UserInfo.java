package servlets;

import java.util.ArrayList;
import java.util.UUID;


public class UserInfo {

	private String name;
	private ArrayList<String> searches;
	
	public UserInfo(String name) {
		super();
		this.name = name;
		this.searches = new ArrayList<String>();
	}
	
	
	public synchronized void addSearch(String item) {
		this.searches.add(item);
	}


	public synchronized String getName() {
		return name;
	}
	
	public synchronized String listToHtml() {
		
		StringBuilder builder = new StringBuilder();
		builder.append("<table border=1 border-spacing=3px>");
		builder.append("<tr><td colspan=2><b>" + name + "'s Search history</b></td></tr>");
		int i = 0;
		for(String search: searches) {
			builder.append("<b><tr><td>" + search + "</td></tr></b>");
		}
		builder.append("</table>");
		return builder.toString();
	}
	
	
}