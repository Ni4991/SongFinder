package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * a class to store style templete and invariables. 
 * @author Administrator
 *
 */
public class BaseServlet extends HttpServlet{

	public static final String LIBRARY = "library";
	public static final String TYPE = "type";
	public static final String QUERY = "query";
	public static final String SEARCH = "search";
	public static final String NAME = "name";
	public static final String UUID = "uuid";
	public static final String DATA = "data";
	public static final String ARTISTSBYALPHA = "artistsByAlpha";
	public static final String ARTISTSBYPCOUNT = "artistsByPCount";
	
	public static final String CLEAR = "clear";
	public static final String NOT_LOGGED_IN = "not_logged_in";
	public static final String STATUS = "status";
	public static final String ERROR = "error";
	public static final String NOT_SEARCHED = "not_searched";
	
	/*
	 * Prepare a response of HTML 200 - OK.
	 * Set the content type and status.
	 * Return the PrintWriter.
	 */
	protected PrintWriter prepareResponse(HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		return response.getWriter();

	}
	
	/*
	 * Return the beginning part of the HTML page.
	 */
	protected String header(String title) {
		return "<html><head><title>" + title + "</title></head><body>";		
	}
	
	/*
	 * Return the last part of the HTML page. 
	 */
	protected String footer() {
		return "</body></html>";
	}
	
	/*
	 * Given a request, return the name found in the 
	 * Cookies provided.
	 */
	protected String getCookieValue(HttpServletRequest request, String key) {
		Cookie[] cookies = request.getCookies();

		String name = null;
		
		if(cookies != null) {
			//for each cookie, if the key is name, store the value
			for(Cookie c: cookies) {
				if(c.getName().equals(key)) {
					name = c.getValue();
				}
			}
		}
		return name;
	}
	
	/*
	 * Given a request, return the value of the parameter with the
	 * provided name or null if none exists.
	 */
	protected String getParameterValue(HttpServletRequest request, String key) {
		return request.getParameter(key);
	}
}
