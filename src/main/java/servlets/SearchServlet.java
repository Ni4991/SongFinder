package servlets;
import java.io.IOException;
import java.io.PrintWriter;
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
import socket.SocketDriver;
import songLibrary.CompareByTrack_id;
import songLibrary.Library;

/*
 * Allows a user to log in
 */
public class SearchServlet extends BaseServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{		
		String name = request.getParameter("name");
		Library library = (Library) getServletContext().getAttribute(LIBRARY);
		
		HttpSession session = request.getSession();
		
       
		//output text box requesting user name
		PrintWriter out = prepareResponse(response);
		
		out.println(header("Search Page"));		
		out.println("<h1>Hello, " + name + "!</h1>");
		out.println("<div class=\"logo\"><a href=\"#\" title=\"logo\">"
				+ "<img src=http://chuantu.biz/t6/166/1512412633x-1404758501.png />" + "</a></div>");
		out.println("<p>You've got a song finder in me! Search for an artist, title or tag and "
				+ "I will give you similar songs.</p><hr/>");
		
		out.println("<form action=\"list\" method=\"post\">");
		out.println("<label>Search type: </label>" 
				+ "<select name=\"type2\">"
				+ "<option value=\"Artist\">Artist</option>" 
				+ "<option value=\"Song Title\">Song Title</option>" 
				+ "<option value=\"Tag\">Tag</option>" 
				+ "</select>");
		out.println("partial/caseignore search: <input type=\"text\" name=\"partial\"/>");
		out.println("<input type=\"submit\" value=\"Search\"/>");
		out.println("</form>");
		
		out.println("<form action=\"list\" method=\"post\">");
		out.println("<input type=\"hidden\" name = \"history\" value=\"yes\"/>");
		out.println("View search history<input type=\"submit\" value=\"Go!\"/>");
		out.println("</form>");
		
		out.println("<form action=\"list\" method=\"post\">");
		out.println("<label>View all artists...</label>" 
				+ "<select name=\"viewtype\">"
				+ "<option value=\"\"></option>" 
				+ "<option value=\"alphabetically\">alphabetically</option>" 
				+ "<option value=\"byplaycount\">by play count</option>" 
				+ "</select>");
		out.println("Display<input type=\"text\" name=\"pageSize\"/>results per page.");
		out.println("<input type=\"submit\" value=\"Go!\"/>");
		out.println("</form>");	
		
		out.println("<form action=\"logout\" method=\"post\">");
		out.println("<input type=\"submit\" value=\"Logout\"/>");
		out.println("</form>");

		out.println(footer());	
	}
}
