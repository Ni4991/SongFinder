package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminServlet extends BaseServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		StopJettyHandler sjh = (StopJettyHandler)getServletConfig().getServletContext().getAttribute("sjh");
		Data data = (Data) getServletConfig().getServletContext().getAttribute(DATA);
		HttpSession session = request.getSession();
		String name = (String) session.getAttribute(NAME);
		String delete = getParameterValue(request, "delete");
		String shutdown = request.getParameter("shutdown");
		
		//if admin has clicked delete, delete that user from list
		if(delete != null) {
			data.deleteUser(name);
		}
		
		PrintWriter out = prepareResponse(response);
		out.println(header("Admin page"));	
		
		if(shutdown != null) {
			sjh.shutdown();
			out.println("You shutdown the server.");
		}
		
		out.println("<center>");		
		out.println(data.usersListToHtml());
		out.println("<br/><hr/>");
		
		out.println("<form action=\"admin\" method=\"post\">");
		out.println("<input type=\"submit\" name=\"shutdown\" value=\"Shutdown server\"/>");
		out.println("</form>");
		
		out.println("</center>");
		out.println(footer());
	}
}
