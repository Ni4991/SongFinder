package servlets;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.servlet.ServletException;
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
		HttpSession session = request.getSession();
				
		String status = getParameterValue(request, STATUS);
				
		boolean statusok = status != null && status.equals(ERROR)?false:true;
		boolean redirected = status != null && status.equals(NOT_LOGGED_IN)?true:false;
		
		//output text box requesting user name
		PrintWriter out = prepareResponse(response);
		
		out.println(header("Search Page"));		
		out.println("<p>You've got a song finder in me! Search for an artist, title or tag and "
				+ "I will give you similar songs.</p></hr>");
		out.println("<form action=\"verifyuser\" method=\"post\">");
		out.println("<label>Search type: </label>" 
				+ "<select name=\"type\">"
				+ "<option value=\"Artist\">Artist</option>" 
				+ "<option value=\"Song Title\">Song Title</option>" 
				+ "<option value=\"Tag\">Tag</option>" 
				+ "</select>");
		out.println("Query:");
		out.println("<input type=\"text\" name=\"query\"/>");
		out.println("<input type=\"submit\" value=\"Submit\"/>");
		out.println("</form>");
		out.println(footer());	
	}
}
