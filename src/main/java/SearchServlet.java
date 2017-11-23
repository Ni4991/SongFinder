package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class SearchServlet extends BaseServlet{
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{		
		HttpSession session = request.getSession();
		
		//if user is logged in, redirect
		if(session.getAttribute(QUERY) != null) {
			response.sendRedirect(response.encodeRedirectURL("/list"));
			return;
		}
				
		String status = getParameterValue(request, STATUS);
		boolean statusok = status != null && status.equals(ERROR)?false:true;
		boolean redirected = status != null && status.equals(NOT_SEARCHED)?true:false;
		
		//output text box requesting user name
		PrintWriter out = prepareResponse(response);
		
		out.println(header("Search Page"));		
		out.println("<p>Welcome to song finder! Search for an artist, "
				+ "title or tag and we will show you similar songs.</p>");
		//if the user was redirected here as a result of an error
//		if(!statusok) {
//			out.println("<h3><font color=\"red\">Invalid Request to Search</font></h3>");
//		} 
//		else if(redirected) {
//			out.println("<h3><font color=\"red\">Search first!</font></h3>");
//		}

		out.println("<form action=\"list\" method=\"post\">");
		out.println("<label>Search type: </label>" 
				+ "<select name=\"type\">"
				+ "<option value=\"Artist\">Artist</option>" 
				+ "<option value=\"Song Title\">Song Title</option>" 
				+ "<option value=\"Tag\">Tag</option>" 
				+ "</select>");
		out.println("<input type=\"text\" name=\"query\"/>");
		out.println("<input type=\"submit\" value=\"Submit\"/>");
		out.println("</form>");
		out.println(footer());
		
	}
}
