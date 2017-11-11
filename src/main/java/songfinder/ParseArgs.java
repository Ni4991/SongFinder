package songfinder;

import java.io.File;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * a class to parse arguments.
 * @author nina luo     
 *
 */  

public class ParseArgs {
	private String inputpath, outputpath, order;
	private int nThreads;

	public ParseArgs(String[] args) {
		inputpath = "";
		outputpath = "";
		order = "";
		nThreads = 10;
		if(!checkArgs(args)) {
			System.out.println("bad args.");
			return;
		}
		LibraryBuilder lb = new LibraryBuilder(inputpath, nThreads, order);
		lb.build(new File(inputpath));
		lb.getWorkQueue().shutdown();
		try {
			lb.getWorkQueue().awaitTermination();
		} catch (InterruptedException e) {
			System.out.println("interrupted");
		}
		if(lb.getWorkQueue().isTerminated()) {
			lb.getLibrary().saveToFile(outputpath, order);
		}
		
	}
	
	/**
	 * check if args are valid.
	 * @param args
	 */
	public boolean checkArgs(String[] args) {
		if(args.length != 6 && args.length != 8) {
			System.out.println("incorrect args length.");
			return false;
		}
		for(int i = 0; i < args.length - 1; i++) {
			if(args[i].equals("-threads")) {
				try {
					if(Integer.parseInt(args[i + 1]) > 1 && Integer.parseInt(args[i + 1]) < 1000){
						nThreads = Integer.parseInt(args[i + 1]);
					}
				}catch(NumberFormatException e) {
					e.getMessage();
				}
			}
			if(args[i].equals("-input")) {
				inputpath = args[i + 1]; 
				if(!new File(inputpath).isDirectory()) {
					System.out.println("bad input path");
					return false;
				}
			}
			if(args[i].equals("-output")) {
				outputpath = args[i + 1];
			}
			if(args[i].equals("-order")) {
				order = args[i + 1];
				if(!(order.equals("artist") || order.equals("title") || order.equals("tag"))){
					System.out.println("bad order");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * getter of input path.
	 * @return
	 */
	public String getInputpath() {
		return inputpath;
	}
	
	/**
	 * getter of output path.
	 * @return
	 */
	public String getOutputpath() {
		return outputpath;
	}
	
	/**
	 * getter of desired order.
	 * @return
	 */
	public String getOrder() {
		return order;
	}
	
	/**
	 * getter of the number of threads.
	 * @return
	 */
	public int getnThreads() {
		return nThreads;
	}
}
