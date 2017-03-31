package scratch.bilibili.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.rory.regex.RegexMatch;

public abstract class ScratchReader<T> {

	protected abstract void read(String html, RegexMatch match, List<T> returnList);
	
	
	public List<T> read(String url) {
		RegexMatch regexMatch;
		List<T> t = new ArrayList<T>();
		try {
			regexMatch = new RegexMatch(new URL(url));
			read(regexMatch.getSourceText(), regexMatch, t);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return t;
	}
	
}
