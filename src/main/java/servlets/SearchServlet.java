package servlets;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import general.LibraryBuilder;
import socket.HTTPFetcher;
import songLibrary.CompareByTrack_id;
import songLibrary.Library;

/*
 * Allows a user to log in
 */
public class SearchServlet extends BaseServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{		
		Data data = (Data) getServletConfig().getServletContext().getAttribute(DATA);
		String name = request.getParameter("name");
		Library library = (Library) getServletContext().getAttribute(LIBRARY);
		
		HttpSession session = request.getSession();
		
		String uname = "";
		String passwd = "";
		if(name == null || name.trim().equals("")) {
			//maybe username info in cookies
			Cookie[] allCookies = request.getCookies();
			int i = 0;
			if(allCookies != null) {
				for(i = 0; i < allCookies.length; i++) {
					Cookie tmp = allCookies[i];
					if(tmp.getName().equals("username")) {
						uname = tmp.getValue();
					}else if(tmp.getName().equals("password")) {
						passwd = tmp.getValue();
					}
				}
			}
			if(!uname.equals("") && passwd.equals("123")) {
				response.sendRedirect("/verifyuser?name=" + uname + "&pass=" + passwd);
				return;
			}
		}
		
		PrintWriter out = prepareResponse(response);
		
		out.println(header("Search Page"));		
		out.println("<style>\r\n" + 
				"body {TEXT-ALIGN: center;}"+
				"#table1\r\n" + 
				"{\r\n" + 
				"	font-family:\"Trebuchet MS\", Arial, Helvetica, sans-serif;\r\n" + 
				"	width:100%;\r\n" + 
				"	border-collapse:collapse;\r\n" + 
				"}\r\n" + 
				"#table1 td, #table1 th \r\n" + 
				"{\r\n" + 
				"	font-size:1em;\r\n" + 
				"	border:1px solid #98bf21;\r\n" + 
				"	padding:3px 7px 2px 7px;\r\n" + 
				"}\r\n" + 
				"#table1 th \r\n" + 
				"{\r\n" + 
				"	font-size:1.1em;\r\n" + 
				"	text-align:left;\r\n" + 
				"	padding-top:5px;\r\n" + 
				"	padding-bottom:4px;\r\n" + 
				"	background-color:#A7C942;\r\n" + 
				"	color:#ffffff;\r\n" + 
				"}\r\n" + 
				"#table1 tr.alt td \r\n" + 
				"{\r\n" + 
				"	color:#000000;\r\n" + 
				"	background-color:#EAF2D3;\r\n" + 
				"}"+
				"ul.a {list-style-type:circle;}" +
				"body,td,th{font-family:Georgia, serif;}" +
				"h1\r\n" + 
				"{\r\n" + 
				"	background-color:#6495ed;\r\n" + 
				"}\r\n" + 
				"p\r\n" + 
				"{\r\n" + 
				"	background-color:#e0ffff;\r\n" + 
				"}\r\n" + 
				"div\r\n" + 
				"{\r\n" + 
				"	background-color:#b0c4de;\r\n" + 
				"}\r\n" + 
				"</style>");
		
		if(name == null|| name.trim().equals("")) {
			out.println("<h1>Hello, guest!</h1>");
		}else {
			out.println("<h1>Hello, " + name + "!</h1>");
			out.println("<p>Your last login time was: " + data.getLoginTime(name) + "</p>");
		}
		
		out.println("<div class=\"logo\"><a href=\"#\" title=\"logo\">"
				+ "<img src=http://s1.sinaimg.cn/large/003wnHkdzy7gudQgA0w50&690 />" + "</a></div>");
		out.println("<p>You've got a song finder in me! Search for an artist, title or tag and "
				+ "I will give you similar songs.</p><hr/>");
		
		out.println("<form action=\"list\" method=\"post\">");
		out.println("<label>Search type: </label>" 
				+ "<select name=\"type2\">"
				+ "<option value=\"Artist\">Artist</option>" 
				+ "<option value=\"Song Title\">Song Title</option>" 
				+ "<option value=\"Tag\">Tag</option>" 
				+ "</select>");
		out.println("search: <input type=\"text\" name=\"partial\" placeholder=\"partial/caseignore..\"/>");
		//you'll see a private search checkbox only if you are logged in
		if(name != null && !name.trim().equals("")) {
			out.println("<input type=\"checkbox\" name=\"private\" value=\"private\" /> private search<br/>");
		}
		out.println(" Display<input type=\"text\" name=\"pageSize\" placeholder=\"default:display all\"/>results per page.");
		out.println("<input type=\"submit\" value=\"Search\"/>");
		out.println("</form>");
		
		out.println("<form action=\"list\" method=\"post\">");
		out.println("<input type=\"hidden\" name = \"popular\" value=\"yes\"/>");
		out.println("View popular search<input type=\"submit\" value=\"Go!\"/>");
		out.println("</form>");
		
		out.println("<form action=\"list\" method=\"post\">");
		out.println("<label>View all artists...</label>" 
				+ "<select name=\"viewtype\">"
				+ "<option value=\"\"></option>" 
				+ "<option value=\"alphabetically\">alphabetically</option>" 
				+ "<option value=\"byplaycount\">by play count</option>" 
				+ "</select>");
		out.println("Display<input type=\"text\" name=\"pageSize\" placeholder=\"default:display all\"/>results per page.");
		out.println("<input type=\"submit\" value=\"Go\"/>");
		out.println("</form>");	
		
		out.println("<form action=\"list\" method=\"post\">");
		out.println("<input type=\"hidden\" name = \"history\" value=\"yes\"/>");
		if(name != null && !name.trim().equals("")) {
			out.println("View search history<input type=\"submit\" value=\"Go!\"/>");
		}
		out.println("</form>");
		
		out.println("<form action=\"logout\" method=\"post\">");
		out.println("<input type=\"submit\" value=\"Logout\"/>");
		out.println("</form>");
		
		out.println(footer());	
	}
}
