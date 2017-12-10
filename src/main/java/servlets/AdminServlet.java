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
		
		Data data = (Data) getServletConfig().getServletContext().getAttribute(DATA);
		HttpSession session = request.getSession();
		String name = (String) session.getAttribute(NAME);
		String delete = getParameterValue(request, "delete");
		
		//if user has clicked delete, delete item from list
		if(delete != null) {
			data.deleteUser(name);
		}
		
		PrintWriter out = prepareResponse(response);
		out.println(header("Admin page"));		
		out.println("<center>");		
		out.println(data.usersListToHtml());
		out.println("<br/><hr/>");
		
		out.println("</center>");
		out.println(footer());
	}

}
