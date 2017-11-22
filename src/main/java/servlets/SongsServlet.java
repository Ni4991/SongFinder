package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import songLibrary.Library;


public class SongsServlet extends BaseServlet{
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Library library = (Library) getServletContext().getAttribute(LIBRARY);
		Data data = (Data) getServletContext().getAttribute(DATA);
		
		HttpSession session = request.getSession();
		String query = (String) session.getAttribute(QUERY);
		String name = (String) session.getAttribute(NAME);
		String type = (String) session.getAttribute(TYPE);

		//user is not logged in, redirect to login page
		if(name == null || !data.userExists(name)) {
			response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + NOT_LOGGED_IN));
			return;
		}
		
		//if user has entered a new item add it to the list
		
		
		
		PrintWriter out = prepareResponse(response);
		out.println(header("Search Result"));	
		out.println("<p>type: " + type + ", query: " + query + ", name:" + name + "</p>");//delete later
		out.println("<form action=\"list\" method=\"post\"> ");
		out.println("<label>Search type: </label>" 
					+ "<select name=\"type\">"
					+ "<option value=\"Artist\">Artist</option>" 
					+ "<option value=\"Song Title\">Song Title</option>" 
					+ "<option value=\"Tag\">Tag</option>" 
					+ "</select>" );
		out.println("<input type=\"text\" name=\"query\"/>");
		out.println("<input type=\"submit\" value=\"Submit\"/>");
		out.println("</form>");
		out.println("<center>");		
		out.println(library.listToHtml(type, name));
		out.println("<br/><hr/>");
		out.println("</center>");
		out.println(footer());
		
	}
}
