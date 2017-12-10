

package servlets;

import java.io.File;
import java.util.Scanner;
import java.util.TreeSet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import general.LibraryBuilder;
import socket.HTTPFetcher;
import songLibrary.Library;

/**
 * a server for song finder.
 * @author nluo
 *
 */
public class SearchSongServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		Server server = new Server(8120);
		
		StopJettyHandler sjh = new StopJettyHandler(server);
        
        ServletContextHandler servhandler = new ServletContextHandler(ServletContextHandler.SESSIONS);        
        server.setHandler(servhandler);
 
        servhandler.addEventListener(new ServletContextListener() {

        	public void contextDestroyed(ServletContextEvent sce) {
				// TODO Auto-generated method stub
				
			}
        		
			public void contextInitialized(ServletContextEvent sce) {
				LibraryBuilder lb = new LibraryBuilder();
				lb.build();
				Library library = lb.getLibrary();
				sce.getServletContext().setAttribute(BaseServlet.LIBRARY, library);
				sce.getServletContext().setAttribute(BaseServlet.DATA, new Data());
				sce.getServletContext().setAttribute("sjh", new StopJettyHandler(server));
			}
        });
        
        servhandler.addServlet(LoginServlet.class, "/login");
        servhandler.addServlet(VerifyUserServlet.class, "/verifyuser"); 
        servhandler.addServlet(SearchServlet.class, "/search");
        servhandler.addServlet(SongsServlet.class, "/list");
        servhandler.addServlet(AdminServlet.class, "/admin");
        servhandler.addServlet(LogoutServlet.class, "/logout");

        //set the list of handlers for the server
        server.setHandler(servhandler);
        //start the server
        server.start();
        server.join();
        
        
        	
        
        
			
        
	}
}


