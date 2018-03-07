package com.fincons.token.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;

import javax.crypto.Cipher;

import org.apache.tomcat.util.codec.binary.Base64;

import org.apache.log4j.Logger;

import java.security.interfaces.RSAPublicKey;

public class RSACryptService {
	final static Logger logger = Logger.getLogger(RSACryptService.class);

	public byte[] encrypt(String textToEncrypt) throws Exception {
		logger.info("Start RSA encryption of the new Token...");
		RSAPublicKey publicKey = getPublicKey();
		byte[] testoCriptato = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance("RSA");
			// encrypt the plain text using the public key
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			testoCriptato = cipher.doFinal(textToEncrypt.getBytes());
		} catch (Exception e) {
			logger.error("RSA encryption problems: ", e);
			return null;
		}
		logger.info("RSA encryption OK");
		return testoCriptato;
	}

	public byte[] decrypt(String textToDecrypt) throws Exception {
		logger.info("Start RSA decryption of the parameters...");
		byte[] testoDecriptato = null;
		RSAPrivateCrtKey privateKey = getPrivateKey();
		byte[] toDecrypt = Base64.decodeBase64(textToDecrypt);
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance("RSA");
			// decrypt the text using the private key
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			testoDecriptato = cipher.doFinal(toDecrypt);

		} catch (Exception ex) {
			logger.error("RSA decryption problems: ", ex);
			return null;
		}
		logger.info("RSA decryption OK");
		return testoDecriptato;
	}

	private RSAPublicKey getPublicKey() throws Exception {
		logger.info("Get public key from the certificate");
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		String certPath = PropertiesHelper.getProps().getProperty("RSAPublicKeyCert");

		if (!certPath.isEmpty()) {
			FileInputStream in = new FileInputStream(certPath + "OGzillaAuthN.cer");
			java.security.cert.Certificate c = cf.generateCertificate(in);
			in.close();

			X509Certificate t = (X509Certificate) c;

			RSAPublicKey pk = null;
			try {
				pk = (RSAPublicKey) t.getPublicKey();
			} catch (NullPointerException e) {
				logger.error("Public Key not found. Check certificate parameters", e);
				e.printStackTrace();
				return null;
			}
			logger.info("Public Key reading OK");
			return pk;
		} else {
			logger.error("Error in reading the configuration file");
			return null;
		}
	}

	private RSAPrivateCrtKey getPrivateKey() throws Exception {
		logger.info("Get private key from the certificate");
		KeyStore keystore = KeyStore.getInstance("JKS");
		logger.info("Get parameters from the configuration");
		String keystorePath = PropertiesHelper.getProps().getProperty("KeyStorePath");
		String keystoreAlias = PropertiesHelper.getProps().getProperty("KeyStoreAlias");
		String keystorePass = PropertiesHelper.getProps().getProperty("KeyStorePass");

		// MAKE SURE THAT "CONFIG.PROPERTIES" FILE HAS BEEN READ
		if ((!keystorePath.isEmpty()) && (!keystoreAlias.isEmpty()) && (!keystorePass.isEmpty())) {
			RSAPrivateCrtKey privateKey = null;
			try {
				logger.info("Parameters reading OK");
				File file = new File(keystorePath + "ogzillakeystore.jks");
				logger.debug("Search file " + keystorePath + "ogzillakeystore.jks");
				InputStream stream = new FileInputStream(file);

				keystore.load(stream, keystorePass.toCharArray());

				

				privateKey = (RSAPrivateCrtKey) keystore.getKey(keystoreAlias, keystorePass.toCharArray());
			} catch (NullPointerException e) {
				logger.error("Private Key not found. Check certificate parameters", e);
				e.printStackTrace();
				return null;
			} catch(FileNotFoundException ex){
				logger.error("File not found.", ex);
				ex.printStackTrace();
				return null;
			}
			logger.info("Private Key reading OK");
			return privateKey;
		} else {
			logger.error("Error in reading the configuration file");
			return null;
		}
	}
}
