package scratch.support.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RequestParam {

	private Map<String, String> param;

	private String content;

	public void content(String content) {
		this.content = content;
	}

	public void content(Map content) {
		try {
			this.content = new ObjectMapper().writeValueAsString(content);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public void content(Object content) {
		try {
			this.content = new ObjectMapper().writeValueAsString(content);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public void param(String key, String value) {
		if (param == null) {
			this.param = new HashMap<String, String>();
		}
		this.param.put(key, value);
	}

	public void param(Map<String, String> map) {
		if (param == null) {
			this.param = new HashMap<String, String>();
		}
		this.param.putAll(map);
	}


	/**
	 * 默认采用content
	 * 其次使用param
	 *
	 * @return
	 */
	@Override
	public String toString() {
		if (param == null && content == null) return null;
		if (content != null) return content;
		return toParamString();
	}

	private String toParamString() {
		StringBuilder paramString = new StringBuilder();
		try {
			for (Map.Entry<String, String> entry : param.entrySet()) {
				paramString.append(URLEncoder.encode(entry.getKey(), "utf-8"))
						.append("=")
						.append(URLEncoder.encode(entry.getValue(), "utf-8"))
						.append("&");

			}
			if (paramString.length() > 1) {
				paramString.substring(0, paramString.length() - 1);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(paramString.length() == 9) return null;
		return paramString.toString();
	}
}
