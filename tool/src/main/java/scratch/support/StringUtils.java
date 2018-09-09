package scratch.support;

import java.io.*;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class StringUtils {

	/**
	 * 功能：将字符串里的占位符{}代表的变量由args替换
	 * 例子： str : "{1} {2}" args: {Hello, World!}
	 * 返回： Hello World!
	 * @param str
	 * @param args
	 * @return
	 */
	public static String build(String str, Object... args) {
		String result = new String(str);
		for(Object obj : args) {
			result = result.replaceFirst("[{]\\w+[}]", String.valueOf(obj));
		}
		return result;
	}

	public static String buildEncodeUrl(String str, Object... args) {
		String result = new String(str);
		for(Object obj : args) {
			try {
				result = result.replaceFirst("[{]\\w+[}]", URLEncoder.encode(String.valueOf(obj), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return result;
	}


	/**
	 * 判断字符串是否为null或者空字符
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}

	public static boolean isNumber(String str) {
		Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
		Matcher isNum = pattern.matcher(str);
		return isNum.matches();
	}

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
		/*InputStreamReader input = new InputStreamReader(in, charset);
		int cha = 0;
		while((cha = input.read()) != -1){
			sb.append((char)cha);
		}*/

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		while (true) {
			int count = in.read(data,0 ,data.length);
			if(count == - 1) {
				break;
			}
			outputStream.write(data, 0, count);
		}

		outputStream.flush();
		outputStream.close();
		return new String(outputStream.toByteArray(), charset);

	}
	
	
}
