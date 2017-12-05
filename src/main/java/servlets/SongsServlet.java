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
		
		Library library = (Library) getServletContext().getAttribute(LIBRARY);
				
		HttpSession session = request.getSession();
		String name = (String) session.getAttribute(NAME);
		
		PrintWriter out = prepareResponse(response);
	
		
		out.println("<h1>Hello, " + name + "!</h1>");
		out.println("<p>You've got a song finder in me! Search for an artist, title or tag and "
				+ "I will give you similar songs.</p><hr/>");
		
		out.println("<form action=\"list\" method=\"post\">");
		out.println("<label>Search type: </label>" 
				+ "<select name=\"type2\">"
				+ "<option value=\"Artist\">Artist</option>" 
				+ "<option value=\"Song Title\">Song Title</option>" 
				+ "<option value=\"Tag\">Tag</option>" 
				+ "</select>");
		out.println("partial/caseignore search: <input type=\"text\" name=\"partial\"/>");
		out.println("<input type=\"submit\" value=\"Search\"/>");
		out.println("</form>");
		
		if(showHistory != null && showHistory.equals("yes")) {
			out.println(data.listToHtml(name));
		}
		out.println("<center>");
	
		if(partial != null) {
			if(name != null) {
				data.add(name, partial);
			}
			out.println(library.searchToHtml(type2, partial));
		}
		if(viewtype != null) {
			if(viewtype.equals("alphabetically")) {
				out.println(library.htmlByAlpha());
			}
			if(viewtype.equals("byplaycount")) {
				out.println(library.htmlByPCount());
			}
		}
		out.println("<br/>");
		out.println("</center>");
		out.println("<form action=\"logout\" method=\"post\">");
		out.println("<input type=\"submit\" value=\"Logout\"/>");
		out.println("</form>");
		
		out.println(footer());
		//reference: http://blog.csdn.net/koself/article/details/8534119
		if(pageSize != null) {
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
