package servlets;

import java.util.ArrayList;
import java.util.UUID;

import concurrent.Lock;


public class UserInfo {

	private String name;
	private ArrayList<String> searches;
	private String loginTime;
	
	public UserInfo(String name) {
		super();
		this.name = name;
		this.searches = new ArrayList<String>();
	}
	
	public ArrayList<String> getSearches(){
		return this.searches;
	}
	
	public void clear() {
		searches.clear();
	}
	
	public void addSearch(String item) {
		this.searches.add(item);
	}
	
	public void setLoginTime(String dateTime) {
		this.loginTime = dateTime;
	}

	public String getLoginTime() {
		return loginTime;
	}
	
	public String getName() {
		return name;
	}
	
	public String historyToHtml() {
		StringBuilder builder = new StringBuilder();
		builder.append("<table border=1 border-spacing=3px>");
		builder.append("<tr><td colspan=2><b>" + name + "'s Search history</b></td></tr>");
		for(String search: searches) {
			builder.append("<b><tr><td>" + search + "</td></tr></b>");
		}
		builder.append("</table>");
		return builder.toString();
	}
}