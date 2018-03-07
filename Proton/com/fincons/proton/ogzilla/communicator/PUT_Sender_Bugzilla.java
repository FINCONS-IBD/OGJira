package com.fincons.proton.ogzilla.communicator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;

import com.fincons.proton.util.PropertiesHelper;

public class PUT_Sender_Bugzilla {
	public static final Logger logger = Logger.getLogger(PUT_Sender_Bugzilla.class.getName());
	
	public int sendRequest(String product, String idBug, String requirement, String user, String pass)
			throws IOException {
		String host = "";
		String port = "";

		host = PropertiesHelper.getConnectionConfig().getProperty("BugzillaHost"); // prop.getProperty("host");
		port = PropertiesHelper.getConnectionConfig().getProperty("BugzillaPort"); // prop.getProperty("port");
		logger.info("Connect to " + host + " at port number " + port + "\n");

		URL url = null;
		int status = 0;
		String authParams = user + ":" + pass;
		byte[] authByte = Base64.encodeBase64(authParams.getBytes());
		String auth = new String(authByte);
		try {
			url = new URL("http://" + host + ":" + port + "/oslc4bugzilla/services/" + product + "/changeRequests/"
					+ idBug + "/updateForRequirementeMod");
			logger.info("Service in use: " + url + "\n");
		} catch (MalformedURLException exception) {
			exception.printStackTrace();
		}
		HttpURLConnection httpURLConnection = null;
		OutputStreamWriter out = null;
		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpURLConnection.setRequestProperty("Authorization", "Basic " + auth);
			httpURLConnection.setRequestMethod("PUT");
			httpURLConnection.setDoOutput(true);
			out = new OutputStreamWriter(httpURLConnection.getOutputStream());
			out.write("requirement=" + requirement);
			out.flush();
			out.close();
			status = httpURLConnection.getResponseCode();
		} catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
		return status;
	}
}