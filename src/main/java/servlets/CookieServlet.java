package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieServlet extends BaseServlet{

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try {
			response.setContentType("text/html; charset=gbk");
			PrintWriter pw = response.getWriter();
			//create a cookie
			Cookie myCookie = new Cookie("color1", "red");
			//set lifetime of cookie
			myCookie.setMaxAge(30);
			response.addCookie(myCookie);
			pw.println("Cookie is already created.");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
