package scratch.support;

public class FileUtils {

	public static String getSuffix(String fileName) {
		String splits[] = fileName.split("\\.");
		if(splits == null || splits.length < 2) {
			return "";
		}
		return splits[1];
	}
	
}
