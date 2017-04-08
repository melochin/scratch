package scratch.bilibili.service.reader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rory.regex.RegexMatch;

import scratch.bilibili.model.Video;
import scratch.bilibili.model.VideoType;

/**http://api.bilibili.com/archive_rank/getarchiverankbypartion
 * ?callback=callback&type=jsonp&_=1490537389048&tid=82&pn=1
 */
@Service
public class VideoReader extends ScratchReader<Video>{

	private final static String VEDIO_URL = "http://www.bilibili.com/video/av";
	private final static String PARENT_NODE = "data";
	private final static String VEDIOS = "archives";
	private final static String TITLE = "title";
	private final static String PIC ="pic";
	private final static String AV = "aid";
	private final static String TYPE ="tid"; 
	private final static String UPLOADER = "author";
	private final static String CREATE_TIME = "create";
	private final static String PLAY = "play";
	private final static String DURATION = "duration";
	private final static String DESC = "description";
	
	private final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	@Override
	protected void read(String html, RegexMatch match, List<Video> returnList) {
		try{
			format.setLenient(false);
			//ȥ�����ò��ֵ�����
			html = html.substring(9, html.length()-2);
			//���ı�ת����JsonNode
			ObjectMapper m = new ObjectMapper();
			JsonNode tree = m.readTree(html);
			JsonNode videos = tree.findValue(PARENT_NODE)
					.findValue(VEDIOS);
			if(videos == null) return;
			for(JsonNode v : videos) {
				//��ȡ������Ϣ
				Long avid = v.findValue(AV).asLong();
				if(avid == 0) continue;
				String avUrl = VEDIO_URL + avid;
				Long tid = v.findValue(TYPE).asLong();
				String title = v.findValue(TITLE).asText();
				String pic = v.findValue(PIC).asText();
				String create = v.findValue(CREATE_TIME).asText();
				Integer play = v.findValue(PLAY).asInt();
				Integer duration = v.findValue(DURATION).asInt();
				String description = v.findValue(DESC).asText();
				Date createTime = null;
				try{
					createTime = format.parse(create);
				}catch (Exception e) {
					createTime = null;
				}
				String uploader = v.findValue(UPLOADER).asText();
				//�������
				Video video = new Video();
				video.setAvid(avid);
				video.setUrl(avUrl);
				video.setType(new VideoType(tid));
				video.setTitle(title);
				video.setPicUrl(pic);
				video.setUploader(uploader);
				video.setCreateDate(createTime);
				video.setUpdateDate(new Date());
				video.setPlay(play);
				video.setDuration(duration);
				video.setDescription(description);
				//�����б�
				returnList.add(video);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
