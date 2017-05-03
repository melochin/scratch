package scratch.support.regex;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scratch.service.ScratchCookie;

public class SratchUnity {


	//�Ƿ�ƥ��
	public static boolean isMatcher(String regex, String str){
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		return m.find();
	}
	
	//�������
	public static List<String> matcher(String regex, String str) {
		List<String> list = new ArrayList<String>();

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while (m.find()) {
			list.add(m.group());
		}

		return list;
	}

	public static String matcherOne(String regex, String str) {
		List<String> list = new ArrayList<String>();
		list = matcher(regex, str);
		if (list.size() <= 0)
			return "";
		else
			return list.get(0);
	}

	public static String getHtml(String url, ScratchCookie cookie) throws MalformedURLException, IOException{
		URLConnection uc = getUc(url, cookie);
		
		InputStreamReader input = new InputStreamReader(uc.getInputStream(),"UTF-8");
		BufferedReader reader = new BufferedReader(input);

		String temp = null;
		StringBuffer html = new StringBuffer();
		
		while((temp = reader.readLine()) != null ){
			html.append(temp);
		}
		
		return html.toString();
	}
	
	public static URLConnection getUc(String url, ScratchCookie cookie) throws IOException{
		URL u = new URL(url);
		
		URLConnection uc = u.openConnection();
		if(cookie == null){
			return uc;
		}
		
		Map<String,String> cookieMap = cookie.loadCookie();
		if(cookieMap == null){
			return uc;
		}
		
		for(String key:cookieMap.keySet()){
			uc.setRequestProperty(key, cookieMap.get(key));
		}
		return uc;
	}
	
	public static boolean download(String url, String path, ScratchCookie cookie){
		try {
			URLConnection uc = SratchUnity.getUc(url, cookie);
			uc.setConnectTimeout(3000);
			System.out.println(url + ":��ʼ����");
			//ConnectException timeout
			DataInputStream dis = new DataInputStream(uc.getInputStream());
		    //����һ���µ��ļ�
		    FileOutputStream fos = new FileOutputStream(new File(path));
		    byte[] buffer = new byte[1024];
		    int length;
		    //��ʼ�������
		    //java.net.SocketTimeoutException: Read timed out
		    while((length = dis.read(buffer) )> 0){
		    	fos.write(buffer,0,length);
		    }
		    dis.close();
		    fos.flush();
		    fos.close();
//		    System.out.println(pathName + "�������");
		}catch(SocketException e){
//			System.out.println(pathName + "���ӳ�ʱ");
			System.out.println(e.toString());
			return false;
		}catch (IOException e) {
//			System.out.println(pathName + "���ӳ�ʱ");
			System.out.println(e.toString());
			return false;
		}
		return true;
	}

}
