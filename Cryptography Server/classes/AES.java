package org.boris.xlloop.util;

import java.util.Base64;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;

import javax.crypto.Cipher;

public class AES {	
	static String entire = "";
	static String decrypted = "";
	
	public static String getFirstPart(){
		return decrypted.substring(0, decrypted.length()/2);
	}
	
	public static String getSecondPart(){
		return decrypted.substring(decrypted.length()/2, decrypted.length());
	}
	
	public static void save(String s){
		entire += s;
	}
	
	public static String get(){
		return entire;
	}
	
	public static String delete(){
		entire = "";
		return entire;
	}

	public static void decrypt(String secretKey) throws Exception {
		Base64.Decoder decoder = Base64.getDecoder();
		System.out.println("ENTIRE STRING: " + entire);
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		byte[] encryptedTextByte = decoder.decode(entire);
		byte[] secret = secretKey.getBytes();
		Key sec = new SecretKeySpec(secret, "AES");
		cipher.init(Cipher.DECRYPT_MODE, sec);
		byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
		String decryptedText = new String((decryptedByte));
		System.out.println("DECRYPTED TOKEN: " + decryptedText);
		decrypted =  decryptedText;
	}
}
