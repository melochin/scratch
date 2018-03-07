package scratch.support;

import org.springframework.util.StringUtils;

public class NumberUtils {

	
	/**
	 * 去除所有非数字的字符，将仅剩的数字字符转换为数字
	 * @param numberStr
	 * @return
	 */
	public static Integer toInteger(String numberStr) {
		
		Integer num = null;
		
		String number = numberStr.replaceAll("[^0-9]", "");
		
		if(StringUtils.isEmpty(number)) {
			return num;
		}
		
		try{
			num = Integer.valueOf(number);
		} catch (Exception e) {
			num = null;
		}
		
		return num;
	}
	
}
