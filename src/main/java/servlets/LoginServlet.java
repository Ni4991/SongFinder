package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/*
 * Allows a user to log in
 */
public class LoginServlet extends BaseServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{		
		Data data = (Data) getServletConfig().getServletContext().getAttribute(DATA);
		HttpSession session = request.getSession();
		
		//if user is logged in, redirect
		if(session.getAttribute(NAME) != null) {
			response.sendRedirect(response.encodeRedirectURL("/list"));
			return;
		}
				
		String status = getParameterValue(request, STATUS);
				
		boolean statusok = status != null && status.equals(ERROR)?false:true;
		boolean redirected = status != null && status.equals(NOT_LOGGED_IN)?true:false;
		
		//output text box requesting user name
		PrintWriter out = prepareResponse(response);
		
		out.println(header("Login Page"));	
		
		out.println("<style>\r\n" + 
				"body,td,th{font-family:Georgia, serif;}" +
				"h1\r\n" + 
				"{\r\n" + 
				"	background-color:#6495ed;\r\n" + 
				"}\r\n" + 
				"p\r\n" + 
				"{\r\n" + 
				"	background-color:#e0ffff;\r\n" + 
				"}\r\n" + 
				"div\r\n" + 
				"{\r\n" + 
				"	background-color:#b0c4de;\r\n" + 
				"}\r\n" + 
				"</style>");

		//if the user was redirected here as a result of an error
		if(!statusok) {
			out.println("<h3><font color=\"red\">Invalid Request to Login</font></h3>");
		} else if(redirected) {
			out.println("<h3><font color=\"red\">Log in first!</font></h3>");
		}
		out.println("<script>\r\n" + 
				"  window.fbAsyncInit = function() {\r\n" + 
				"    FB.init({\r\n" + 
				"      appId            : '269265720264443',\r\n" + 
				"      autoLogAppEvents : true,\r\n" + 
				"      xfbml            : true,\r\n" + 
				"      version          : 'v2.11'\r\n" + 
				"    });\r\n" + 
				"  };\r\n" + 
				"\r\n" + 
				"  (function(d, s, id){\r\n" + 
				"     var js, fjs = d.getElementsByTagName(s)[0];\r\n" + 
				"     if (d.getElementById(id)) {return;}\r\n" + 
				"     js = d.createElement(s); js.id = id;\r\n" + 
				"     js.src = \"https://connect.facebook.net/en_US/sdk.js\";\r\n" + 
				"     fjs.parentNode.insertBefore(js, fjs);\r\n" + 
				"   }(document, 'script', 'facebook-jssdk'));\r\n" + 
				"</script>");
		out.println("<form name=\"name\" action=\"verifyuser\" method=\"post\">");
		out.println("Username: <input type=\"text\" name=\"name\"/>");
		out.println("Password: <input type=\"password\" name=\"password\"/>");
		out.println("<input type = \"checkbox\" name = \"keep\" value = \"2\">stay logged in for 2 weeks");
		out.println("<input type=\"submit\" value=\"Login\"/>");
		out.println("</form>");
		
		out.println("<p>password is 123. username can be anything.</p>");
		
		out.println(footer());
		
	}
}