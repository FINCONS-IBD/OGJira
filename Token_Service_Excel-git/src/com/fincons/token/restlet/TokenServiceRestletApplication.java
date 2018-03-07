package com.fincons.token.restlet;

import org.apache.log4j.Logger;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import freemarker.template.Configuration;

public class TokenServiceRestletApplication extends Application {

	private Configuration configuration;
	final static Logger logger = Logger.getLogger(TokenServiceRestletApplication.class);

	@Override
	public synchronized Restlet createInboundRoot() {
		logger.trace("Called the createInboundRoot restlet method...");

		Router router_free = new Router(getContext());
		
		router_free.attach("/generateToken", GenerateTokenResource.class);
		router_free.attach("/getCredential", GenerateCredentialResource.class);

		logger.info("Created and returned the Restlet Context with url-resource association...");

		return router_free;
	}

	public Configuration getConfiguration() {
		logger.info("Restlet Configuration loaded...");
		return configuration;
	}

}