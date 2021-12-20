package util;

import java.io.InputStream;
import java.util.Scanner;

public class FileReader {
	static String result;
	public static String getContents(String filename) throws Exception {
		try (InputStream in = FileReader.class.getResourceAsStream(filename);
				Scanner scanner = new Scanner(in)) 
		{
			result = scanner.useDelimiter("\\A").next();
		}
		catch(Exception e) {
			System.err.println("Could not read files!");
		}
		return result;
		
	}
	
}
