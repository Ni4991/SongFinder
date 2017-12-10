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
		Data data = (Data) getServletConfig().getServletContext().getAttribute(DATA);
		
		String partial = request.getParameter("partial");
		String type2 = request.getParameter("type2");
		String viewtype = request.getParameter("viewtype");
		String pageSize = request.getParameter("pageSize");
		String showHistory = request.getParameter("history");
		String pri = request.getParameter("private");
		String clearHis = request.getParameter("clear");
		String home = request.getParameter("home");
		String popular = request.getParameter("popular");
		
		
		
		Library library = (Library) getServletContext().getAttribute(LIBRARY);
				
		HttpSession session = request.getSession();
		String name = (String) session.getAttribute(NAME);
		
		
		
		if(home != null && home.equals("yes")) {
			if(name == null|| name.trim().equals("")) {
				response.sendRedirect(response.encodeRedirectURL("/search?name="));
			}else {
				response.sendRedirect(response.encodeRedirectURL("/search?name=" + name));
			}
		}
		
		PrintWriter out = prepareResponse(response);
	
		if(clearHis != null && clearHis.equals("yes")) {
			out.println(data.clear(name));
		}
		out.println("<style>\r\n" + 
				"body {TEXT-ALIGN: center;}"+
				"#table1\r\n" + 
				"{\r\n" + 
				"	font-family:\"Trebuchet MS\", Arial, Helvetica, sans-serif;\r\n" + 
				"	width:100%;\r\n" + 
				"	border-collapse:collapse;\r\n" + 
				"}\r\n" + 
				"#table1 td, #table1 th \r\n" + 
				"{\r\n" + 
				"	font-size:1em;\r\n" + 
				"	border:1px solid #98bf21;\r\n" + 
				"	padding:3px 7px 2px 7px;\r\n" + 
				"}\r\n" + 
				"#table1 th \r\n" + 
				"{\r\n" + 
				"	font-size:1.1em;\r\n" + 
				"	text-align:left;\r\n" + 
				"	padding-top:5px;\r\n" + 
				"	padding-bottom:4px;\r\n" + 
				"	background-color:#A7C942;\r\n" + 
				"	color:#ffffff;\r\n" + 
				"}\r\n" + 
				"#table1 tr.alt td \r\n" + 
				"{\r\n" + 
				"	color:#000000;\r\n" + 
				"	background-color:#EAF2D3;\r\n" + 
				"}"+
				"ul.a {list-style-type:circle;}" +
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
		
		if(name == null|| name.trim().equals("")) {
			out.println("<h1>Hello, guest!</h1>");
		}else {
			out.println("<h1>Hello, " + name + "!</h1>");
			out.println("<p>Your last login time was: " + data.getLoginTime(name) + "</p>");
		}
		out.println("<p>You've got a song finder in me! Search for an artist, title or tag and "
				+ "I will give you similar songs.</p><hr/>");
		
		if(popular != null && popular.equals("yes")) {
			out.println(data.getPopSearch());
		}
		
		out.println("<form action=\"list\" method=\"post\">");
		out.println("<label>Search type: </label>" 
				+ "<select name=\"type2\">"
				+ "<option value=\"Artist\">Artist</option>" 
				+ "<option value=\"Song Title\">Song Title</option>" 
				+ "<option value=\"Tag\">Tag</option>" 
				+ "</select>");
		out.println("search: <input type=\"text\" name=\"partial\" placeholder=\"partial/caseignore..\"/>");
		//you'll see a private search checkbox only if you are logged in
		if(name != null && !name.trim().equals("")) {
			out.println("<input type=\"checkbox\" name=\"private\" value=\"private\" /> private search<br/>");
		}
		out.println(" Display<input type=\"text\" name=\"pageSize\" placeholder=\"default:display all\"/>results per page.");
		out.println("<input type=\"submit\" value=\"Search\"/>");
		out.println("</form>");
		
		if(showHistory != null && showHistory.equals("yes")) {
			out.println(data.hisToHtml(name));
			out.println("<form action=\"list\" method=\"post\">");
			out.println("<input type=\"hidden\" name = \"clear\" value=\"yes\"/>");
			out.println("<input type=\"submit\" value=\"Clear history\"/>");
			out.println("</form>");
		}
		
		out.println("<center>");
	
		if(partial != null) {
			if(name != null) {
				if(pri == null) {
					data.add(name, partial);
				}
			}
			if(pageSize != null && pageSize.trim().length() > 0) {
				out.println("<span id=\"spanPre\">Previous</span> <span id=\"spanNext\"> Next</span>" + 
					"Page <span id=\"spanPageNum\"></span> of <span id=\"spanTotalPage\"></span> Pages");
			}
			out.println(library.searchToHtml(type2, partial));
		}
		if(viewtype != null) {
			if(pageSize != null && pageSize.trim().length() > 0) {
				out.println("<span id=\"spanPre\">Previous</span> <span id=\"spanNext\"> Next</span>" + 
					"Page <span id=\"spanPageNum\"></span> of <span id=\"spanTotalPage\"></span> Pages");
			}
			if(viewtype.equals("alphabetically")) {
				out.println(library.htmlByAlpha());
			}
			if(viewtype.equals("byplaycount")) {
				out.println(library.htmlByPCount());
			}
		}
		out.println("<br/>");
		out.println("</center>");
		
		out.println("<form action=\"list\" method=\"post\">");
		out.println("<input type=\"hidden\" name=\"popular\" value=\"yes\"/>");
		out.println("View popular search<input type=\"submit\" value=\"Go!\"/>");
		out.println("</form>");
		
		if(name != null && !name.trim().equals("")) {
			out.println("<form action=\"list\" method=\"post\">");
			out.println("<input type=\"hidden\" name = \"home\" value=\"yes\"/>");
			out.println("<input type=\"submit\" value=\"Go to search main page\"/>");
			out.println("</form>");
		}
		
		out.println("<form action=\"logout\" method=\"post\">");
		out.println("<input type=\"submit\" value=\"Logout\"/>");
		out.println("</form>");
		
		out.println(footer());
		//reference: http://blog.csdn.net/koself/article/details/8534119
		if(pageSize != null && pageSize.trim().length() > 0) {
			out.println("<script>\r\n" + 
					"     var theTable = document.getElementById(\"table2\");\r\n" + 
					"     var totalPage = document.getElementById(\"spanTotalPage\");\r\n" + 
					"     var pageNum = document.getElementById(\"spanPageNum\");\r\n" + 
			
					"     var spanPre = document.getElementById(\"spanPre\");\r\n" + 
					"     var spanNext = document.getElementById(\"spanNext\");\r\n" + 
					"     var spanFirst = document.getElementById(\"spanFirst\");\r\n" + 
					"     var spanLast = document.getElementById(\"spanLast\");\r\n" + 
			
					"     var numberRowsInTable = theTable.rows.length;\r\n" + 
					"     var pageSize =" + Integer.parseInt(pageSize) + ";" +
					"     var page = 1;\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"     //next\r\n" + 
					"     function next() {\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"         hideTable();\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"         currentRow = pageSize * page;\r\n" + 
					"         maxRow = currentRow + pageSize;\r\n" + 
					"         if (maxRow > numberRowsInTable) maxRow = numberRowsInTable;\r\n" + 
					"         for (var i = currentRow; i < maxRow; i++) {\r\n" + 
					"             theTable.rows[i].style.display = '';\r\n" + 
					"         }\r\n" + 
					"         page++;\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"         if (maxRow == numberRowsInTable) { nextText(); lastText(); }\r\n" + 
					"         showPage();\r\n" + 
					"         preLink();\r\n" + 
					"         firstLink();\r\n" + 
					"     }\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"     //previous\r\n" + 
					"     function pre() {\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"         hideTable();\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"         page--;\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"         currentRow = pageSize * page;\r\n" + 
					"         maxRow = currentRow - pageSize;\r\n" + 
					"         if (currentRow > numberRowsInTable) currentRow = numberRowsInTable;\r\n" + 
					"         for (var i = maxRow; i < currentRow; i++) {\r\n" + 
					"             theTable.rows[i].style.display = '';\r\n" + 
					"         }\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"         if (maxRow == 0) { preText(); firstText(); }\r\n" + 
					"         showPage();\r\n" + 
					"         nextLink();\r\n" + 
					"         lastLink();\r\n" + 
					"     }\r\n" + 
					"\r\n" + 
					"\r\n" + 
				
					"     function hideTable() {\r\n" + 
					"         for (var i = 0; i < numberRowsInTable; i++) {\r\n" + 
					"             theTable.rows[i].style.display = 'none';\r\n" + 
					"         }\r\n" + 
					"     }\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"     function showPage() {\r\n" + 
					"         pageNum.innerHTML = page;\r\n" + 
					"     }\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"     //total\r\n" + 
					"     function pageCount() {\r\n" + 
					"         var count = 0;\r\n" + 
					"         if (numberRowsInTable % pageSize != 0) count = 1;\r\n" + 
					"         return parseInt(numberRowsInTable / pageSize) + count;\r\n" + 
					"     }\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"     //link\r\n" + 
					"     function preLink() { spanPre.innerHTML = \"<a href='javascript:pre();'>Previous</a>\"; }\r\n" + 
					"     function preText() { spanPre.innerHTML = \"Previous\"; }\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"     function nextLink() { spanNext.innerHTML = \"<a href='javascript:next();'>next</a>\"; }\r\n" + 
					"     function nextText() { spanNext.innerHTML = \"Next\"; }\r\n" + 
					"\r\n" + 
					"     //hide \r\n" + 
					"     function hide() {\r\n" + 
					"         for (var i = pageSize; i < numberRowsInTable; i++) {\r\n" + 
					"             theTable.rows[i].style.display = 'none';\r\n" + 
					"         }\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"         totalPage.innerHTML = pageCount();\r\n" + 
					"         pageNum.innerHTML = '1';\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"         nextLink();\r\n" + 
					"         lastLink();\r\n" + 
					"     }\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"     hide();\r\n" + 
					"</script>\r\n" + 
					"");

		}
			}
}
