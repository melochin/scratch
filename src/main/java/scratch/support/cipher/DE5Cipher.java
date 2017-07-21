package scratch.support.cipher;

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
	
	private static final String DIVIDER = "&";		
	
	@Override
	public String encode(String encodeStr) {
		return encode(KEY, encodeStr);
	}
	
	/**
	 * DES5����->BASE64ת��
	 */
	@Override
	public String encode(String key, String encodeStr) {
		if(encodeStr == null) return null;
		
		String result = null;
		byte[] encodeByte = null;
		
		try {
			//����
			encodeByte = doFinal(key, Cipher.ENCRYPT_MODE, encodeStr.getBytes());
			if(encodeByte == null) return null;
			//BASE64ת��
			result = new BASE64Encoder().encode(encodeByte);
			//������Ž��д���
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
	 * BASE����->DES5����
	 */
	@Override
	public String decode(String key, String decodeStr) {
		if(decodeStr == null) return null;
		
		String result = null;
		byte[] decodeByte = null;
		//������Ŵ���
		decodeStr = decodeStr.replace('-', '/');
		decodeStr = decodeStr.replace('_', '=');
		try{
			//BASE64����
			decodeByte = new BASE64Decoder().decodeBuffer(decodeStr);
			if(decodeByte == null) return null;
			//��ʼ������
			decodeByte = doFinal(key, Cipher.DECRYPT_MODE, decodeByte);
			result = new String(decodeByte);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		return result;
	}

	/**
	 * ִ�м��ܽ��ܵľ��巽��
	 * @param key		������Կ��ֵ
	 * @param type		����/����
	 * @param source	����Ķ���������
	 * @return
	 */
	private byte[] doFinal(String key, int type, byte[] source) {
		byte[] b = null;
		SecretKey secretKey = new SecretKeySpec(key.getBytes(), MODE);
		try{
			//��ʼ�����ܡ�����
			Cipher cipher = Cipher.getInstance(MODE);
			cipher.init(type, secretKey);
			//���м��ܡ�����
			b = cipher.doFinal(source);
		} catch(Exception e){
			log.warn(e.getMessage());
		}
		return b;
	}

	@Override
	public String encode(String key, String[] encodeStrs) {
		StringBuilder encodeStr = new StringBuilder();
		boolean first = true;
		for(String s: encodeStrs) {
			if(first) {
				encodeStr.append(s);
				first = false;
			} else {
				encodeStr.append(DIVIDER + s);
			}
		}
		return encode(key, encodeStr.toString());
	}
	
}
