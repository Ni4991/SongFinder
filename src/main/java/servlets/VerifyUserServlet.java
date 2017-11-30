package servlets;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/*
 * Servlet invoked at login.
 * Creates cookie and redirects to main ListServlet.
 */
public class VerifyUserServlet extends BaseServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//VerifyUser does not accept GET requests. Just redirect to login with error status.
		response.sendRedirect(response.encodeRedirectURL("/login?" + STATUS + "=" + ERROR));
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String name = request.getParameter(NAME);
		String type = request.getParameter(TYPE);
		String query = request.getParameter(QUERY);

		HttpSession session = request.getSession();
		session.setAttribute(NAME, name);
		session.setAttribute(TYPE, type);
		session.setAttribute(QUERY, query);
		
		//OPTION 2: Store volatile data in a single, shared object that is stored in the servlet context and
		//can therefore be accessed by all users/sessions/servlets.
		
		//map id to name and userinfo
		Data data = (Data)getServletContext().getAttribute(DATA);
		//we assume no username conflicts and provide no ability to register for our service!
		data.addUser(name);  
							
		//redirect to list
		response.sendRedirect(response.encodeRedirectURL("/list"));
	}
}
