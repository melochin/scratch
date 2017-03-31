package scratch.bilibili.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rory.regex.RegexMatch;

import scratch.bilibili.model.Video;

public class VideoSpecificReader extends ScratchReader<Video>{

	private static final String CREATE_DATE = "created_at";
	
	private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	
	@Override
	protected void read(String html, RegexMatch match, List<Video> returnList) {
		try {
			Video v = new Video();
			JsonNode tree = new ObjectMapper().readTree(html);
			System.out.println(html);
			//2017-03-29 15:16
			String createDate = tree.findValue(CREATE_DATE).asText();
			v.setCreateDate(format.parse(createDate));
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

	}

}
