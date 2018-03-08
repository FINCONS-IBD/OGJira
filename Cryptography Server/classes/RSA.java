package org.boris.xlloop.util;

import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import javax.crypto.Cipher;



public class RSA {
	static String encrypted ="";
	public static String getFirstPart(){
		return encrypted.substring(0, encrypted.length()/2);
	}
	
	public static String getSecondPart(){
		return encrypted.substring(encrypted.length()/2, encrypted.length());
	}
	
	public static void encrypt(String textToEncrypt) throws Exception {
		RSAPublicKey publicKey = getPublicKey();
		byte[] testoCriptato = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance("RSA");
			// encrypt the plain text using the public key
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			testoCriptato = cipher.doFinal(textToEncrypt.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		encrypted = new String(Base64.getEncoder().encode(testoCriptato));
	}
	
	private static RSAPublicKey getPublicKey() throws Exception {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		String certPath = PropertiesHelper.getConnectionConfig().getProperty("RSAPublicKeyCert");
		
		if(!certPath.isEmpty()){
			FileInputStream in = new FileInputStream(certPath + "OGzillaAuthN.cer");
			java.security.cert.Certificate c = cf.generateCertificate(in);
			in.close();

			X509Certificate t = (X509Certificate) c;

			RSAPublicKey pk = (RSAPublicKey) t.getPublicKey();
			return pk;
		}
		else {
//			logger.error("Error in reading the configuration file");
			return null;
		}
	}
}
