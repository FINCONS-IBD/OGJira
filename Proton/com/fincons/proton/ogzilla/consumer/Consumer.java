package com.fincons.proton.ogzilla.consumer;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.fincons.proton.ogzilla.communicator.Ask_Credentials;
import com.fincons.proton.ogzilla.communicator.PUT_Sender_Bugzilla;
import com.fincons.proton.ogzilla.communicator.PUT_Sender_Jira;
import com.ibm.hrl.proton.adapters.configuration.IOutputAdapterConfiguration;
import com.ibm.hrl.proton.adapters.connector.IOutputConnector;
import com.ibm.hrl.proton.adapters.formatters.ITextFormatter;
import com.ibm.hrl.proton.adapters.formatters.JSONFormatter;
import com.ibm.hrl.proton.adapters.interfaces.AbstractOutputAdapter;
import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.inout.ConsumerMetadata;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;

public class Consumer extends AbstractOutputAdapter {
	ITextFormatter textFormatter;
	PUT_Sender_Bugzilla senderBugzilla;
	Ask_Credentials cred;
	PUT_Sender_Jira senderJira;

	public Consumer(ConsumerMetadata consumerMetadata, IOutputConnector serverConnector,
			EventMetadataFacade eventMetadata, EepFacade eep) throws AdapterException {
		super(consumerMetadata, serverConnector, eventMetadata);
		textFormatter = new JSONFormatter(consumerMetadata.getConsumerProperties(), eventMetadata, eep);
		senderBugzilla = new PUT_Sender_Bugzilla();
		cred = new Ask_Credentials();
		senderJira = new PUT_Sender_Jira();
	}

	@Override
	public void writeObject(IDataObject instance) throws AdapterException {
		String jsonBug = textFormatter.formatInstance(instance).toString();
		System.out.println("\n");
		System.out.println("\nDATA ARRIVED: \n" + jsonBug);
		String user = "";
		String pass = "";
		String credentials = "";
		try {
			JSONObject bug = new JSONObject(jsonBug);
			String product = bug.optString("productId");
			System.out.println("\nPRODUCT:	" + product);
			String id = bug.optString("identifier");
			System.out.println("IDENTIFIER:	" + id);
			String req = bug.optString("requirement");
			System.out.println("REQUIREMENT:	" + req);
			String token = bug.optString("token");
			String modifiedBy = bug.optString("modifiedBy");
			System.out.println("MODIFIED BY:	" + modifiedBy);
			String title = bug.optString("title");
			System.out.println("TITLE:		" + title);
			String state = bug.optString("status");
			System.out.println("STATUS:		" + state);
			String server = bug.optString("server");
			System.out.println("SERVER:		" + server + "\n");
			credentials = cred.getCredentials(token);
			if (credentials == null) {
				logger.warning("Empty response. No credential available\n");
			} else {
				JSONObject credJson = new JSONObject(credentials);
				user = credJson.optString("Username");
				pass = credJson.optString("Password");
				if (server.equals("Bugzilla")) {
					int statusCode = senderBugzilla.sendRequest(product, id, req, user, pass);
					if (statusCode == 204) {
						logger.info("REQUEST DONE, REQUIREMENT " + req + " HAS BEEN MODOFIED BY " + modifiedBy
								+ "! BUG ID: " + id +"\n");
					} else {
						logger.info("REQUEST FAILED FOR BUG ID: " + id + "!\n");
					}
				} else if (server.equals("Jira")) {
					int statusCode = senderJira.sendRequest(product, id, req, user, pass, state, modifiedBy, title);
					if (statusCode == 200) {
						logger.info("\nREQUEST DONE, REQUIREMENT " + req + " HAS BEEN MODOFIED BY " + modifiedBy
								+ "! BUG ID: " + id + "\n");
					} else {
						logger.info("\nREQUEST FAILED FOR BUG ID: " + id + "!\n");
					}
				}
			}

		} catch (JSONException e) {
			logger.warning("JSONException " + e.getMessage());
		} catch (IOException e) {
			logger.warning("IOException " + e.getMessage());
		}

	}

	@Override
	public void initializeAdapter() throws AdapterException {
		super.initialize();
	}

	@Override
	public void shutdownAdapter() throws AdapterException {
		super.shutdown();
	}

	@Override
	public IOutputAdapterConfiguration createConfiguration(ConsumerMetadata consumerMetadata) {
		return new ConsumerConfiguration();
	}

}
