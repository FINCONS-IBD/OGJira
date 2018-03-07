package com.fincons.token.utils;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

/**
 * An util AES Encryption/Decryption Class with secret server side symmetric key
 * 
 * @author leonardo.straniero
 *
 */
public class AESencrp {

	final static Logger logger = Logger.getLogger(AESencrp.class);

	public static String encrypt(String decryptedData, String keyString) throws Exception {
		logger.info("Start AES encryption...");
		String encryptedText;
		try {
			Base64.Encoder encoder = Base64.getEncoder();
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			byte[] plainTextByte = decryptedData.getBytes();
			byte[] secret = keyString.getBytes();
			Key sec = new SecretKeySpec(secret, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, sec);
			byte[] encryptedByte = cipher.doFinal(plainTextByte);
			encryptedText = encoder.encodeToString(encryptedByte);
		} catch (Exception e) {
			logger.error("AES Encryption problems!", e);
			return null;
		}
		logger.info("AES Encryption OK");
		return encryptedText;
	}
}