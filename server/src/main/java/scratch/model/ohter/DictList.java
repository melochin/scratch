package scratch.model.ohter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import scratch.model.entity.Dict;

@SuppressWarnings("serial")
public class DictList extends ArrayList<Dict>{

	// List 转换成 map 时候的 hashCode
	private int hashCode;
	
	// List 转换的 map
	private Map<String,String> map;
	
	// 根据key获取value,主要用于页面值转换
	public String get(String key) {
		return asMap().get(key);
	}
	
	// 根据key获取value,主要用于页面值转换
	public String get(Long key) {
		return asMap().get(String.valueOf(key));
	}

	public String get(Integer key) {
		return asMap().get(String.valueOf(key));
	}
	
	/**
	 * 将List Dict转换为<String,String>Map 便于取code对应的值
	 * 说明：缓存已经生成的map，只要DictList不发生变化，就不会重新生成
	 * @return
	 */
	private Map<String, String> asMap() {
		if(isUpdated() == false) return map;
		Map<String, String> hashMap = getMap();
		map = hashMap;
		hashCode = this.hashCode();
		return map;
	}

	private boolean isUpdated() {
		return hashCode != this.hashCode() || map == null;
	}

	private Map<String, String> getMap() {
		Map<String, String> hashMap = new HashMap<String, String>();
		for(Dict dict : this) {
			hashMap.put(dict.getCode(), dict.getValue());
		}
		return hashMap;
	}

}
