package scratch.service.reader;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import scratch.support.regex.RegexMatch;

@Service
public class VideoPageReader extends ScratchReader<Long>{

	@Override
	protected void read(String html, RegexMatch match, List<Long> returnList) {
		try {
			//ȥ�����ò��ֵ�����
			html = html.substring(9, html.length()-2);
			//���ı�ת����JsonNode
			ObjectMapper m = new ObjectMapper();
			JsonNode tree = m.readTree(html);
			JsonNode page = tree.findValue("page");
			if(page == null) return;
			long count = page.findValue("count").asLong();
			long size = page.findValue("size").asLong();
			if(count == 0 || size == 0) return;
			int pageCount = (int) Math.ceil((double)count / (double)size);
			if(pageCount == 0) return;
			returnList.add(count);
			returnList.add((long) pageCount);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
