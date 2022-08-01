package control;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.io.*;

public class Version {
    
	private static final String urlCheckVersion = "https://raw.githubusercontent.com/pjmeca/WordleSolver/main/resources/version";
	private static final String urlCurrVersion = "resources/version";
	
	public static String readStringFromURL(String requestURL) throws IOException
	{
	    try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
	            StandardCharsets.UTF_8.toString()))
	    {
	        scanner.useDelimiter("\\A");
	        return scanner.hasNext() ? scanner.next() : "";
	    }
	}
	
	public static String getNewestVersion() throws IOException {
		return readStringFromURL(urlCheckVersion);
    }
	
	public static String getCurrentVersion() {
		String content;
		try {
			Scanner s = new Scanner(new File(urlCurrVersion));
			//Scanner s = new Scanner(new File(Version.class.getClassLoader().getResource(urlCurrVersion).toURI()));
			content = s.useDelimiter("\\Z").next();
			s.close();
			return content; 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static boolean isNewVersionAvailable() {
		try {
			return !readStringFromURL(urlCheckVersion).equals(getCurrentVersion()+"\n"); // GitHub automatically adds \n at the end of files
		} catch (IOException e) {
			System.err.println("Error checking program version!");
			return false;
		}
	}
}
