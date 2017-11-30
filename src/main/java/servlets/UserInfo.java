package servlets;

import java.util.ArrayList;
import java.util.UUID;

/**
 * will implement later.
 * @author nluo
 *
 */
public class UserInfo {

	private String name;
	private ArrayList<String> searches;
	
	public UserInfo(String name) {
		super();
		this.name = name;
		this.searches = new ArrayList<String>();
	}
	
	
	public synchronized void addSearch(String search) {
		this.searches.add(search);
	}

	public synchronized void clear() {
		this.searches.clear();
	}


	public synchronized String getName() {
		return name;
	}
	
	public synchronized String historyToHtml() {
		
		StringBuilder builder = new StringBuilder();
		builder.append("<table border=1 border-spacing=3px>");
		builder.append("<tr><td colspan=2><b>" + name + "'s search history!</b></td></tr>");
		int i = 0;
		for(String search: searches) {
			builder.append("<b><tr><td>" + search + i++ + "</td></tr></b>");
		}
		builder.append("</table>");
		builder.append("<form action=\"list?clear=\" method=\"post\"><input type=\"submit\" value=\"Clear\"></form>");
		return builder.toString();
	}
}
