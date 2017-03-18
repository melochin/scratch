package scratch.support;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


@SuppressWarnings("restriction")
@Service
public class DE5Cipher implements CipherSupport {

	private static Logger log = Logger.getLogger(DE5Cipher.class);
	
	private static final String KEY = "84129745";
	
	private static final String MODE = "DES";
		
	@Override
	public String encode(String encodeStr) {
		return encode(KEY, encodeStr);
	}
	
	/**
	 * DES5加密->BASE64转码
	 */
	@Override
	public String encode(String key, String encodeStr) {
		if(encodeStr == null) return null;
		
		String result = null;
		byte[] encodeByte = null;
		
		try {
			//加密
			encodeByte = doFinal(key, Cipher.ENCRYPT_MODE, encodeStr.getBytes());
			if(encodeByte == null) return null;
			//BASE64转码
			result = new BASE64Encoder().encode(encodeByte);
			//特殊符号进行处理
			result = result.replace('/', '-');
			result = result.replace('=', '_');
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		return result;
	}

	@Override
	public String decode(String decodeStr) {
		return decode(KEY, decodeStr);
	}

	/**
	 * BASE解码->DES5解密
	 */
	@Override
	public String decode(String key, String decodeStr) {
		if(decodeStr == null) return null;
		
		String result = null;
		byte[] decodeByte = null;
		//特殊符号处理
		decodeStr = decodeStr.replace('-', '/');
		decodeStr = decodeStr.replace('_', '=');
		try{
			//BASE64解码
			decodeByte = new BASE64Decoder().decodeBuffer(decodeStr);
			if(decodeByte == null) return null;
			//初始化解密
			decodeByte = doFinal(key, Cipher.DECRYPT_MODE, decodeByte);
			result = new String(decodeByte);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		return result;
	}

	/**
	 * 执行加密解密的具体方法
	 * @param key		生成秘钥的值
	 * @param type		加密/解密
	 * @param source	处理的二进制数组
	 * @return
	 */
	private byte[] doFinal(String key, int type, byte[] source) {
		byte[] b = null;
		SecretKey secretKey = new SecretKeySpec(key.getBytes(), MODE);
		try{
			//初始化加密、解密
			Cipher cipher = Cipher.getInstance(MODE);
			cipher.init(type, secretKey);
			//进行加密、解密
			b = cipher.doFinal(source);
		} catch(Exception e){
			log.warn(e.getMessage());
		}
		return b;
	}
	
}
