package servlets;

import java.util.ArrayList;
import java.util.UUID;

import concurrent.Lock;


public class UserInfo {

	private String name;
	private ArrayList<String> searches;
	private String loginTime;
	private Lock lock;
	
	public UserInfo(String name, Lock lock) {
		super();
		this.name = name;
		this.searches = new ArrayList<String>();
		this.lock = lock;
	}
	
	public ArrayList<String> getSearches(){
		try {
			lock.lockRead();
			ArrayList<String> copy = new ArrayList<String>();
			for(String search : searches) {
				copy.add(search);
			}
			return copy;
		}finally {
			lock.unlockRead();
		}
	}
	
	public void clear() {
		lock.lockWrite();
		searches.clear();
		lock.unlockWrite();
	}
	
	public void addSearch(String item) {
		lock.lockWrite();
		this.searches.add(item);
		lock.unlockWrite();
	}
	
	public void setLoginTime(String dateTime) {
		lock.lockWrite();
		this.loginTime = dateTime;
		lock.unlockWrite();
	}

	public String getLoginTime() {
		try {
			lock.lockRead();
			return loginTime;
		}finally {
			lock.unlockRead();
		}
		
	}
	
	public String getName() {
		try {
			lock.lockRead();
			return name;
		}finally {
			lock.unlockRead();
		}
	}
	
	public String historyToHtml() {
		try {
			lock.lockRead();
			StringBuilder builder = new StringBuilder();
			builder.append("<table border=1 border-spacing=3px>");
			builder.append("<tr><td colspan=2><b>" + name + "'s Search history</b></td></tr>");
			for(String search: searches) {
				builder.append("<b><tr><td>" + search + "</td></tr></b>");
			}
			builder.append("</table>");
			return builder.toString();
		}finally {
			lock.unlockRead();
		}
	}
}