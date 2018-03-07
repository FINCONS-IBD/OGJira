package com.fincons.proton.ogjira.communicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;

import com.fincons.proton.util.PropertiesHelper;

public class Ask_Credentials {
	private static final Logger logger = Logger.getLogger(Ask_Credentials.class.getName());
	
	public String getCredentials(String token) {
		logger.info("Called method getCredentials...");
		String protocol = "";
		String host = "";
		String port = "";
		
		protocol = PropertiesHelper.getConnectionConfig().getProperty("Token_protocol");
		host = PropertiesHelper.getConnectionConfig().getProperty("Token_host"); // prop.getProperty("host");
		port = PropertiesHelper.getConnectionConfig().getProperty("Token_port"); // prop.getProperty("port");
		
		String tokenServiceResourceLocation = protocol + "://" + host + ":" + port;
		
		logger.info("Connecting to the Token Service located to " + tokenServiceResourceLocation);
		URL url = null;
		try {
			url = new URL(tokenServiceResourceLocation + "/Token_Service_Excel/getCredential");
		} catch (MalformedURLException exception) {
			exception.printStackTrace();
		}
		HttpURLConnection httpURLConnection = null;
		OutputStreamWriter out = null;
		String credentials = "";
		logger.info("Sending of POST request to the Token Service. Service in use: " + url);
		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			out = new OutputStreamWriter(httpURLConnection.getOutputStream());
			out.write(token);
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			credentials = response.toString();
		} catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
		return credentials;
	}
}
