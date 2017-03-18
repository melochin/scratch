package scratch.support;

public interface CipherSupport {

	String encode(String encodeStr);
	
	String encode(String key, String encodeStr);
	
	String decode(String decodeStr);
	
	String decode(String key, String decodeStr);
}
