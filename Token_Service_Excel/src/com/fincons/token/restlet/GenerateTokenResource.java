package com.fincons.token.restlet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.apache.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.fincons.token.utils.AESencrp;
import com.fincons.token.utils.DateUtil;
import com.fincons.token.utils.RSACryptService;

public class GenerateTokenResource extends ServerResource {
	final static Logger logger = Logger.getLogger(GenerateTokenResource.class);
	RSACryptService cryptService = new RSACryptService();

	@Post
	public Representation generateToken(String parameters) throws Exception {
		logger.info("Called generate Token method with parameters: " + parameters);
		JSONObject jsonResponse = new JSONObject();

		int status = 500;
		String jsonReqStr = "";
		jsonReqStr = new String(cryptService.decrypt(parameters));
		try {
			JSONObject jsonReq = new JSONObject(jsonReqStr);
			JSONObject credentials = jsonReq.optJSONObject("Credential");
			String expTimeStr = jsonReq.optString("Expiration time");
			String timeStampReqStr = jsonReq.optString("TimeStamp");
			
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date expTimeReq = df.parse(expTimeStr);
			Date timeStampReq = df.parse(timeStampReqStr);
			logger.info("Timestamp request " + df.format(timeStampReq));
			
			String secretKey = jsonReq.optString("SecretKey");
			
			Date newTimeStamp = DateUtil.GetUTCdatetimeAsDate();
			logger.info("New Timestamp generated " + df.format(newTimeStamp));
			logger.info("Expiration time of the request: " + df.format(expTimeReq));

			if (newTimeStamp.before(expTimeReq)) { 
				logger.info("Time validation OK");
				
				JSONObject tokenJSON = new JSONObject();
				tokenJSON.put("TimeStamp", expTimeReq);
				tokenJSON.put("NewTimeStamp", newTimeStamp);
				tokenJSON.put("Credential", credentials);
				String tokenStr = tokenJSON.toString();
				
				logger.info("New Token generation OK");
				
				String token = new String(Base64.encodeBase64((cryptService.encrypt(tokenStr))));
				
				token = AESencrp.encrypt(token, secretKey);
				jsonResponse.put("Token", token);
				logger.info("Json response " + jsonResponse.toString(2));
				status = 202;
			} else {
				logger.error("The request time expired. Session Timeout!");
				getResponse().setStatus(new Status(403));// Forbidden
				return new JsonRepresentation(jsonResponse);
			}

		} catch (JSONException e) {
			logger.error("JSON Exception", e);
			// e.printStackTrace();
			getResponse().setStatus(new Status(406));// Not Acceptable
			return new JsonRepresentation(jsonResponse);
		} catch (Exception e){
			logger.error("Exception", e);
			getResponse().setStatus(new Status(500));// Not Acceptable
			return new JsonRepresentation(jsonResponse);
		}
		getResponse().setStatus(new Status(status));
		return new JsonRepresentation(jsonResponse);

	}
}
