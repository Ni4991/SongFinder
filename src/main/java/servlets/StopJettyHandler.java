package servlets;

import org.eclipse.jetty.server.Server;

public class StopJettyHandler {
	
	private Server server;

	public StopJettyHandler(Server server) {
		// TODO Auto-generated constructor stub
		this.server = server;
	}
	
	public void shutdown() {
		// Stop the server.
		//reference: http://www.petervannes.nl/files/084d1067451c4f9a56f9b865984f803d-52.php
    	new Thread()
		{
		   public void run() { 
		     try {
		        System.out.println("Shutting down the server...");
				server.stop();
				 System.out.println("Server has stopped.");
		     } catch (Exception e) {
		    	  System.out.println("Error when stopping Jetty server: " + e.getMessage());
		     }
		    }
		}.start();
	}
}
