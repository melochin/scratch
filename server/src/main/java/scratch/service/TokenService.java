package scratch.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

@Service
public class TokenService {

	private static RSAPrivateKey privateKey;

	private static RSAPublicKey publicKey;

	@Value("${token.secret}")
	private String secret;


	public void generateKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(512, new SecureRandom(new String("chicnkaze").getBytes()));
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		save(keyPair.getPrivate().getEncoded(), "private.key");
		save(keyPair.getPublic().getEncoded(), "public.key");
	}

	private void save(byte[] bytes, String fileName) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(fileName);
		ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
		outputStream.write(bytes);
		outputStream.flush();
		outputStream.close();
	}

	//@PostConstruct
	public void init() throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
		InputStream privateIn = new ClassPathResource("private.key").getInputStream();
		InputStream publicIn = new ClassPathResource("public.key").getInputStream();

		byte[] privateBytes = getBytes(privateIn);
		byte[] publicBytes = getBytes(publicIn);



		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateBytes);
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicBytes);
		PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

		this.privateKey = (RSAPrivateKey) privateKey;
		this.publicKey = (RSAPublicKey) publicKey;
	}

	private byte[] getBytes(InputStream in) throws IOException {
		InputStream inputStream = new ObjectInputStream(in);
		byte[] buffer = new byte[1024];
		int length = inputStream.read(buffer);
		inputStream.close();
		return Arrays.copyOfRange(buffer,0, length);
	}

	public String sign(Long userId) {
		Algorithm algorithm = Algorithm.HMAC512(secret);
		// Algorithm algorithm = Algorithm.RSA256(this.publicKey, this.privateKey);
		String token = JWT.create()
				.withIssuer("admin")
				.withClaim("id", userId)
				.sign(algorithm);
		return token;
	}

	public DecodedJWT verify(String token) {

		if(token == null) {
			throw new JWTVerificationException("无效token");
		}
		Algorithm algorithm = Algorithm.HMAC512(secret);
		//Algorithm algorithm = Algorithm.RSA256(this.publicKey, this.privateKey);
		JWTVerifier verifier = JWT.require(algorithm)
				.withIssuer("admin")
				.build();
		DecodedJWT jwt = verifier.verify(token);
		return jwt;
	}


}
