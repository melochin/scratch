package scratch.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringUtils {

	/**
	 * change InputStream to String
	 * @param in
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String toString(InputStream in, String charset) throws IOException {
		
		StringBuilder sb = new StringBuilder();
		
		InputStreamReader input = new InputStreamReader(in, charset);
		BufferedReader reader = new BufferedReader(input);
		
		String temp = "";
		while((temp = reader.readLine()) != null ){
			sb.append(temp);
		}
		
		return sb.toString();
	}
	
	
}
