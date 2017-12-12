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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import socket.HTTPFetcher;
import songLibrary.ArtistInfo;
import songLibrary.Library;

public class TempDisplayServlet extends BaseServlet{
	
	private String listAtstByPlaycount() {
		Library library = (Library) getServletContext().getAttribute(LIBRARY);
		TreeSet<ArtistInfo> artistsByPCount = (TreeSet<ArtistInfo>)getServletContext().getAttribute(ARTISTSBYPCOUNT);
		StringBuilder builder = new StringBuilder();
		builder.append("<table border=1 border-spacing=3px>");
		builder.append("<tr><th>Artist</th><th>Play count</th></tr>");
		for(ArtistInfo ai : artistsByPCount) {
			builder.append("<tr><td>" + ai.getName() + "</td>"
					+ "<td>" + ai.getPlaycount() + "</td></tr>");
		}	
		builder.append("</table>");
		return builder.toString();
	} 
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		HttpSession session = request.getSession();
		String action = request.getParameter("action");
		
		PrintWriter out = prepareResponse(response);
		out.println("<html><head><title>Temporary display page</title>"
				+ "</head><body>");
		if(action.equals("Abpc")) {
			out.println(listAtstByPlaycount());
		}
		out.println(footer());
	}


}
