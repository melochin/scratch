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
		// 直接用BuffredReader readLine为什么乱码？
		InputStreamReader input = new InputStreamReader(in, charset);
		int cha = 0;
		while((cha = input.read()) != -1){
			sb.append((char)cha);
		}
		
		return sb.toString();
	}
	
	
}
