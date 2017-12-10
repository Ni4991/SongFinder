package servlets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import concurrent.Lock;

public class Data {

	//maintain a map of name to UserInfo object
	protected HashMap<String, UserInfo> userInfo;
	private TreeMap<String, Integer> popularSearch;
	private Lock lock;

	public Data() {
		userInfo = new HashMap<String, UserInfo>();
		popularSearch = new TreeMap<String, Integer>(); 
		lock = new Lock();
	}
	
	public String usersListToHtml() {
		try {
			lock.lockRead();
			StringBuilder builder = new StringBuilder();
			builder.append("<table border=1 border-spacing=3px>");
			builder.append("<tr><td colspan=2><b>List of all users</b></td></tr>");
			for(String user: userInfo.keySet()) {
				builder.append("<b><tr><td>" + "<details><summary>"+ user + "</summary>" + "<p>search history: </p><ul>");
				for(String search : userInfo.get(user).getSearches()) {
					builder.append("<li>" + search + "</li>");
				}
				builder.append("</ul></details></td><td><form action=\"admin?delete=" + user + "\" method=\"post\"><input type=\"submit\" value=\"delete\"></form></td></tr></b>");
			}
			builder.append("</table>");
			return builder.toString();
		}finally {
			lock.unlockRead();
		}
	}
	
	public void deleteUser(String name) {
		try {
			lock.lockWrite();
			if(userInfo.containsKey(name)) {
				userInfo.remove(name);
			}
		}finally {
			lock.unlockWrite();
		}
	}
	
	public String getPopSearch() {
		try {
			lock.lockRead();
			for(String user : userInfo.keySet()) {
				for(String search : userInfo.get(user).getSearches()) {
					if (popularSearch.containsKey(search)) {
						popularSearch.put(search, popularSearch.get(search) + 1);
					} else {
						popularSearch.put(search, 1);
					}
				}
			}
			// reference: http://blog.csdn.net/xiaokui_wingfly/article/details/42964695
			List<Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(popularSearch.entrySet());  
	
			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {  
			   
				@Override
				public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
					// TODO Auto-generated method stub
					 return o2.getValue() - (o1.getValue()); // desc
				}  
			});  
			StringBuilder builder = new StringBuilder();
			builder.append("<table align=\"center\" border=1 border-spacing=3px>");
			builder.append("<thead><tr><th>Search item</th><th>Times searched</th></tr></thead><tbody>");
			for (Entry<String, Integer> mapping : list) {   
				builder.append("<tr><td>" + mapping.getKey() + "</td>"
						+ "<td>" + mapping.getValue()+ "</td></tr>");
			}  
			builder.append("</tbody></table>");
			return builder.toString();
		}finally {
			lock.unlockRead();
		}
	}
		
	/*
	 * Returns true if the user exists in the data store.
	 */
	public boolean userExists(String name) {
		try {
			lock.lockRead();
			return userInfo.containsKey(name);
		}finally {
			lock.unlockRead();
		}
		
	}
	
	/*
	 * Add a new UserInfo object for a particular user.
	 */
	public void addUser(String name) {
		lock.lockWrite();
		if(!userInfo.containsKey(name)) {
			userInfo.put(name, new UserInfo(name, lock));
		}
		lock.unlockWrite();
	}

	/*
	 * For a given user, add a new todo.
	 */
	public void add(String name, String item) {
		lock.lockWrite();
		if(!userInfo.containsKey(name)) {
			return;
		}
		userInfo.get(name).addSearch(item);
		lock.unlockWrite();
	}
	
	public void addLoginTime(String name, String loginTime) {
		lock.lockWrite();
		if(!userInfo.containsKey(name)) {
			return;
		}
		userInfo.get(name).setLoginTime(loginTime);
		lock.unlockWrite();
	}
	
	public String clear(String name) {
		try {
			lock.lockWrite();
			if(userInfo.containsKey(name)) {
				userInfo.get(name).clear();
			}
			return "<p>You have cleared your search history.</p>";
		}finally {
			lock.unlockWrite();
		}
	}
	
	public String getLoginTime(String name) {
		try {
			lock.lockRead();
			if(!userInfo.containsKey(name)) {
				return null;
			}	
			return userInfo.get(name).getLoginTime();
		}finally {
			lock.unlockRead();
		}
	}
	

	/*
	 * Returns a String containing an HTML representation of the
	 * list associated with the session identified by id.
	 */
	public String hisToHtml(String name) {
		try {
			lock.lockRead();
			if(!userInfo.containsKey(name)) {
				return null;
			}	
			return userInfo.get(name).historyToHtml();
		}finally {
			lock.unlockRead();
		}
	}
}
