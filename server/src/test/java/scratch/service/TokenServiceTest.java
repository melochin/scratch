package scratch.service;

import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.*;

public class TokenServiceTest {

	private TokenService tokenService = new TokenService();

	@Test
	public void key() throws Exception {
		tokenService.generateKey();
	}

	@Test
	public void init() throws ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		tokenService.init();
	}

	@Test
	public void generate() throws ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
		tokenService.init();
		String token = tokenService.sign(new Long(1));
		tokenService.verify(token);
	}
}