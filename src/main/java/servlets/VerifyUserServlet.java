package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
		String pass = request.getParameter("password");
		String keep = request.getParameter("keep");
		
		//no username info in session
		if(name == null || name.trim().equals("")) {
			//maybe username info in cookies
			response.sendRedirect(response.encodeRedirectURL("/search?name="));
		}
		
		HttpSession session = request.getSession();
		session.setAttribute(NAME, name);
		
		//OPTION 2: Store volatile data in a single, shared object that is stored in the servlet context and
		//can therefore be accessed by all users/sessions/servlets.
		
		//map id to name and userinfo
		Data data = (Data)getServletContext().getAttribute(DATA);
		//we assume no username conflicts and provide no ability to register for our service!
		data.addUser(name);  
							
		//redirect to search
		if(pass.equals("123")) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
				String dateTime = dateFormat.format(new Date());  
				data.addLoginTime(name, dateTime);
			if(keep != null) {
				
				
				Cookie username = new Cookie("username", name);
				Cookie password = new Cookie("password", pass); 
				
				username.setMaxAge(2 * 7 * 24 * 3600);//2 weeks
				password.setMaxAge(2 * 7 * 24 * 3600);
				
				response.addCookie(username);
				response.addCookie(password);
			}
			response.sendRedirect(response.encodeRedirectURL("/search?name=" + name));
		}
		else {
			PrintWriter out = prepareResponse(response);
			out.println(header("Login fail"));	
			out.println("<h1>Wrong password, " + name + "!</h1>");
		}
	}
}
