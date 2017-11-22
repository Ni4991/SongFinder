

package servlets;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import general.LibraryBuilder;


public class SearchSongServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		Server server = new Server(8120);
        
        ServletContextHandler servhandler = new ServletContextHandler(ServletContextHandler.SESSIONS);        
        server.setHandler(servhandler);
 
        servhandler.addEventListener(new ServletContextListener() {

        	public void contextDestroyed(ServletContextEvent sce) {
				// TODO Auto-generated method stub
				
			}
        		
			public void contextInitialized(ServletContextEvent sce) {
				LibraryBuilder lb = new LibraryBuilder("input\\lastfm_subset", 2, "tag");
				lb.build(new File("input\\lastfm_subset"));
				lb.getWorkQueue().shutdown();
				try {
					lb.getWorkQueue().awaitTermination();
				} catch (InterruptedException e) {
					System.out.println("interrupted");
				}
				sce.getServletContext().setAttribute(BaseServlet.LIBRARY, lb.getLibrary());
				sce.getServletContext().setAttribute(BaseServlet.DATA, new Data());
			}
        	
        });
        servhandler.addServlet(LoginServlet.class, "/login");
        servhandler.addServlet(VerifyUserServlet.class, "/verifyuser");
//        servhandler.addServlet(SearchServlet.class, "/search");
        servhandler.addServlet(SongsServlet.class, "/list");

        //set the list of handlers for the server
        server.setHandler(servhandler);
        
        //start the server
        server.start();
        server.join();

	}
}


