package net.hydrotekz.MCAC.utils;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

import net.hydrotekz.MCAC.handlers.JsonHandler;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class RSA {

	public static PublicKey publicKey;
	public static PrivateKey privateKey;
	public static PublicKey serverKey;

	public static KeyPair generateKeys() {
		try {
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(4048, random);
			return keyGen.generateKeyPair();

		} catch (Exception e) {
			Printer.log(e);
		}
		return null;
	}

	public static String encrypt(String data, PublicKey key) {
		try {
			Cipher rsa = Cipher.getInstance("RSA");
			rsa.init(1, key);
			return DatatypeConverter.printBase64Binary(rsa.doFinal(data.getBytes()));

		} catch (Exception e) {
			Printer.log(e);
			Printer.log("Failed to encrypt: " + data);
		}
		return null;
	}

	public static String decrypt(String data, PrivateKey key) {
		try {
			Cipher rsa = Cipher.getInstance("RSA");
			rsa.init(2, key);
			return new String(rsa.doFinal(DatatypeConverter.parseBase64Binary(data)));

		} catch (BadPaddingException e){
			Printer.log(e);
			JsonHandler.regenerateKeys();
			Printer.log("Encryption keys was regenerated!");

		} catch (Exception e) {
			Printer.log(e);
			Printer.log("Failed to decrypt: " + data);
		}
		return null;
	}

	public static PrivateKey loadPrivateKey(String key64) throws Exception {
		byte[] clear = new BASE64Decoder().decodeBuffer(key64);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
		KeyFactory fact = KeyFactory.getInstance("RSA");
		PrivateKey priv = fact.generatePrivate(keySpec);
		Arrays.fill(clear, (byte)0);
		return priv;
	}

	public static PublicKey loadPublicKey(String stored) throws Exception {
		byte[] data = new BASE64Decoder().decodeBuffer(stored);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
		KeyFactory fact = KeyFactory.getInstance("RSA");
		return fact.generatePublic(spec);
	}

	public static String savePrivateKey(PrivateKey priv) throws Exception {
		KeyFactory fact = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec spec = (PKCS8EncodedKeySpec)fact.getKeySpec(priv, PKCS8EncodedKeySpec.class);
		byte[] packed = spec.getEncoded();
		String key64 = new BASE64Encoder().encode(packed);
		Arrays.fill(packed, (byte)0);
		return key64;
	}

	public static String savePublicKey(PublicKey publ) throws Exception {
		KeyFactory fact = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec spec = (X509EncodedKeySpec)fact.getKeySpec(publ, X509EncodedKeySpec.class);
		return new BASE64Encoder().encode(spec.getEncoded());
	}
}