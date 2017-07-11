package scratch.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class DictList extends ArrayList<Dict>{

	public Map<String, String> asMap() {
		
		Map<String, String> hashMap = new HashMap<String, String>();
		
		for(Dict dict : this) {
			hashMap.put(dict.getCode(), dict.getValue());
		}
		return hashMap;
	}
	
}
