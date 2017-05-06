package scratch.service.reader;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserStatusReader extends ScratchReader<Boolean>{
	
	{
		readerUrl = "https://api.bilibili.com/x/v2/history?callback=ajax&pn=1&ps=100&jsonp=jsonp";
	}
	
	private final static String STATUS = "status"; 

	@Override
	protected void read(String html, List<Boolean> returnList) {
		
		System.out.println(html);
		
		try {
			JsonNode tree = new ObjectMapper().readTree(html);
			Boolean status = tree.findValue(STATUS).asBoolean();
			returnList.add(status);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
