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

	public Data() {
		userInfo = new HashMap<String, UserInfo>();
		popularSearch = new TreeMap<String, Integer>();  
	}
	
	public String getPopSearch() {
		for(String user : userInfo.keySet()) {
			for(String search : userInfo.get(user).getSearches()) {
				if (popularSearch.containsKey(search)) {
					popularSearch.put(search, popularSearch.get(search) + 1);
				} else {
					popularSearch.put(search, 1);
				}
			}
		}
		// ref: http://blog.csdn.net/xiaokui_wingfly/article/details/42964695
		List<Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(popularSearch.entrySet());  

		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {  
		   
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				// TODO Auto-generated method stub
				 return o2.getValue() - (o1.getValue()); // 降序  
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
	}
		
	/*
	 * Returns true if the user exists in the data store.
	 */
	public boolean userExists(String name) {
		return userInfo.containsKey(name);
	}
	
	/*
	 * Add a new UserInfo object for a particular user.
	 */
	public void addUser(String name) {
		if(!userInfo.containsKey(name)) {
			userInfo.put(name, new UserInfo(name));
		}
	}

	/*
	 * For a given user, add a new todo.
	 */
	public boolean add(String name, String item) {
		if(!userInfo.containsKey(name)) {
			return false;
		}
		userInfo.get(name).addSearch(item);
		return true;
	}
	
	public boolean addLoginTime(String name, String loginTime) {
		if(!userInfo.containsKey(name)) {
			return false;
		}
		userInfo.get(name).setLoginTime(loginTime);
		return true;
	}
	
	public String clear(String name) {
		if(userInfo.containsKey(name)) {
			userInfo.get(name).clear();
		}
		return "<p>You have cleared your search history.</p>";
	}
	
	public String getLoginTime(String name) {
		if(!userInfo.containsKey(name)) {
			return null;
		}	
		return userInfo.get(name).getLoginTime();
	}
	

	/*
	 * Returns a String containing an HTML representation of the
	 * list associated with the session identified by id.
	 */
	public String hisToHtml(String name) {
		if(!userInfo.containsKey(name)) {
			return null;
		}	
		return userInfo.get(name).historyToHtml();
	}


}
