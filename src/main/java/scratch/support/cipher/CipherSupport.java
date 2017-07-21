package scratch.support.cipher;

public interface CipherSupport {

	String encode(String encodeStr);
	
	String encode(String key, String encodeStr);
	
	String encode(String key, String[] encodeStrs);
	
	String decode(String decodeStr);
	
	String decode(String key, String decodeStr);
}
