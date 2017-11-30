package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import songLibrary.Library;

/**
 * a result page.
 * @author nluo
 *
 */
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

		PrintWriter out = prepareResponse(response);
		out.println("<html><head><title>Result Page</title>"
				+ "</head><body>");
		
		out.println("<p>You've got a song finder in me! Search for an artist, title or tag and "
				+ "I will give you similar songs.</p><hr/>");
		out.println("<form action=\"verifyuser\" method=\"post\"> ");
		out.println("<label>Search type: </label>" 
					+ "<select name=\"type\">"
					+ "<option value=\"Artist\">Artist</option>" 
					+ "<option value=\"Song Title\">Song Title</option>" 
					+ "<option value=\"Tag\">Tag</option>" 
					+ "</select>" );
		out.println("Query: <input type=\"text\" name=\"query\"/>");
		out.println("<input type=\"submit\" value=\"Submit\"/>");
		out.println("</form>");
		out.println("<center>");		
		out.println(library.listToHtml(type, query));
		out.println("<br/>");
		out.println("</center>");
		out.println(footer());
	}
}
