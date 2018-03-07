package com.fincons.token.restlet;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.fincons.token.utils.RSACryptService;

public class GenerateCredentialResource extends ServerResource{
	final static Logger logger = Logger.getLogger(GenerateTokenResource.class);
	
	@Post
	public Representation getCredential(String token){
		logger.info("Start get Credential from the Token...");
		JSONObject jsonResponse = new JSONObject();
		int status = 500;
		try {
			RSACryptService rsa = new RSACryptService();
			logger.info("Decrypting token to get user info");
			String strInfo = new String(rsa.decrypt(token));
			JSONObject jsonInfo = new JSONObject(strInfo);
			jsonResponse = jsonInfo.optJSONObject("Credential");
			status = 202;
		} catch (Exception e) {
			logger.error("Internal server error", e);
			getResponse().setStatus(new Status(406));// Not Acceptable
			return new JsonRepresentation(jsonResponse);
		}
		getResponse().setStatus(new Status(status));
		logger.info("Credentials retrieved");
		return new JsonRepresentation(jsonResponse);
	}
}
