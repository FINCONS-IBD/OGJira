package com.fincons.proton.ogjira.communicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fincons.proton.util.PropertiesHelper;


public class PUT_Sender_Jira {
	private static final String TITLE = "dcterms:title";
	private static final String STATUS_BUG = "jira:issueStatus";
	private static final String STATUS_ABOUT = "rdf:about";
	private static final String COMMENTS = "jira:issueComment";
	private static final String COMMENT = "jira:commentBody";
	private static final String CUSTOM_FIELD = "jira:customField";
	private static final String REQUIREMENT = "jira:value";
	private static final String REPORTER = "jira:reporter";
	private static final String DESCRIPTION = "dcterms:description";
	private static final String ENVIRONMENT = "jira:environment";
	private static final String COMPONENT = "jira:component";
	private static final String MEMBER_TAG = "ns2:member";
	
	public static final Logger logger = Logger.getLogger(PUT_Sender_Jira.class.getName());

	public int sendRequest(String product, String idBug, String requirement, String user, String pass, String bugStatus,
			String modifiedBy, String title) throws IOException, JSONException {
		logger.info("Preparing PUT request to Jira Server. Bug ID " + idBug);
		String protocol = "";
		String host = "";
		String port = "";
		protocol = PropertiesHelper.getConnectionConfig().getProperty("JiraProtocol");
		host = PropertiesHelper.getConnectionConfig().getProperty("JiraHost"); // prop.getProperty("host");
		port = PropertiesHelper.getConnectionConfig().getProperty("JiraPort"); // prop.getProperty("port");
		
		String serverResource = protocol + "://" + host + ":" + port;
		String authParams = user + ":" + pass;
		byte[] authByte = Base64.encodeBase64(authParams.getBytes());
		String auth = new String(authByte);
		
		String changeReq = doGetCR(serverResource, idBug, product, auth);
		
		// GET ID OF REQUIREMENT CUSTOM FIELD 
		String idRequField = getReqFieldID(changeReq);
		
		//GET DESCRIPTION AND ENVIRONMENT FOR A SINGLE BUG
		String environment = getEnvironment(changeReq);
		String description = getDescription(changeReq);
		JSONArray components = getComponents(changeReq);

		String putTemplate = "{\"dcterms:title\": \"\"," + "\"jira:issueStatus\": {\"rdf:about\": \"" + serverResource
				+ "/rest/jirarestresource/1.0/issueStates/\"}," + "\"jira:customField\": [{\"jira:id\": \""
				+ idRequField + "\",\"jira:name\": \"Requirement\",\"jira:value\": \"\"}],"
				+ "\"jira:issueComment\": [{\"jira:commentBody\": \"\"}]," + "\"jira:reporter\": \"\","
				+ "\"prefixes\": {" + "\"dcterms\": \"http://purl.org/dc/terms/\","
				+ "\"foaf\": \"http://xmlns.com/foaf/0.1/\"," + "\"jira\": \"http://atlassian.com/ns/cm#\","
				+ "\"oslc\": \"http://open-services.net/ns/core#\","
				+ "\"oslc_cm\": \"http://open-services.net/ns/cm#\","
				+ "\"oslc_data\": \"http://open-services.net/ns/servicemanagement/1.0/\","
				+ "\"oslc_qm\": \"http://open-services.net/ns/qm#\","
				+ "\"oslc_rm\": \"http://open-services.net/ns/rm#\","
				+ "\"oslc_scm\": \"http://open-services.net/ns/scm#\","
				+ "\"rdf\": \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\","
				+ "\"rdfs\": \"http://www.w3.org/2000/01/rdf-schema#\"}}";

		logger.info("Connecting to " + serverResource + "...\n");
		URL url = null;
		int status = 0;

		try {
			url = new URL(serverResource + "/rest/jirarestresource/1.0/" + product + "/changeRequests/"
					+ idBug);
			logger.info("Service in use: " + url + "\n");
		} catch (MalformedURLException exception) {
			logger.severe(exception.getMessage());
		}
		HttpURLConnection httpURLConnection = null;
		OutputStreamWriter out = null;
		try {
			logger.info("Starting of template compilation");
			JSONObject putRequest = new JSONObject(putTemplate);
			title = StringEscapeUtils.unescapeXml(title);
			putRequest.put(TITLE, title);
			putRequest.put(REPORTER, user);
			putRequest.put(DESCRIPTION, description);
			putRequest.put(ENVIRONMENT, environment);
			JSONObject stat = putRequest.optJSONObject(STATUS_BUG);
			String firstStatus = getFirstStatus(idBug, serverResource, auth);
			switch (bugStatus) {
			case "Open":
				logger.info("Case \"OPEN\". Bug status still OPEN");
				stat.put(STATUS_ABOUT, stat.optString(STATUS_ABOUT) + idBug + "/" + 1);
				break;
			case "In Progress":
				if (firstStatus.equals("Open")) {
					logger.info("Case \"IN PROGRESS\". Changing IN PROGRESS -> OPEN");
					stat.put(STATUS_ABOUT, stat.optString(STATUS_ABOUT) + idBug + "/" + 1);
					break;
				} else if (firstStatus.equals("Backlog")) {
					logger.info("Case \"IN PROGRESS\". Changing IN PROGRESS -> BACKLOG");
					stat.put(STATUS_ABOUT, stat.optString(STATUS_ABOUT) + idBug + "/" + 10100);
					break;
				} else if (firstStatus.equalsIgnoreCase("To Do")) {
					logger.info("Case \"IN PROGRESS\". Changing IN PROGRESS -> TO DO");
					stat.put(STATUS_ABOUT, stat.optString(STATUS_ABOUT) + idBug + "/" + 10000);
					break;
				}
			case "Resolved":
				logger.info("Case \"RESOLVED\". Changing RESOLVED -> REOPENED");
				stat.put(STATUS_ABOUT, stat.optString(STATUS_ABOUT) + idBug + "/" + 4);
				break;
			case "Closed":
				logger.info("Case \"CLOSED\". Changing CLOSED -> REOPENED");
				stat.put(STATUS_ABOUT, stat.optString(STATUS_ABOUT) + idBug + "/" + 4);
				break;
			case "Reopened":
				logger.info("Case \"REOPENED\". Bug status still REOPENED");
				stat.put(STATUS_ABOUT, stat.optString(STATUS_ABOUT) + idBug + "/" + 4);
				break;
			case "Done":
				if (firstStatus.equals("Backlog")) {
					logger.info("Case \"DONE\". Changing DONE -> BACKLOG");
					stat.put(STATUS_ABOUT, stat.optString(STATUS_ABOUT) + idBug + "/" + 10100);
					break;
				} else if (firstStatus.equalsIgnoreCase("To Do")) {
					logger.info("Case \"DONE\". Changing DONE -> TO DO");
					stat.put(STATUS_ABOUT, stat.optString(STATUS_ABOUT) + idBug + "/" + 10000);
					break;
				}
			case "In Review":
				logger.info("Case \"IN REVIEW\". Changing IN REVIEW -> TO DO");
				stat.put(STATUS_ABOUT, stat.optString(STATUS_ABOUT) + idBug + "/" + 10000);
				break;
			case "Selected for Development":
				logger.info("Case \"SELECT FOR DEVELOPEMENT\". Changing SELECT FOR DEVELOPEMENT -> BACKLOG");
				stat.put(STATUS_ABOUT, stat.optString(STATUS_ABOUT) + idBug + "/" + 10100);
				break;
			case "To Do":
				logger.info("Case \"TO DO\". Bug status still TO DO");
				stat.put(STATUS_ABOUT, stat.optString(STATUS_ABOUT) + idBug + "/" + 10000);
				break;
			default:
				logger.info("Case DEFAULT. No change has done. Something goes wrong.");
				break;
			}
			
			putRequest.put(STATUS_BUG, stat);
			JSONArray comments = putRequest.optJSONArray(COMMENTS);
			JSONObject comment = comments.getJSONObject(0);
			comment.put(COMMENT,
					"Requirement " + requirement + " has been modified by " + modifiedBy + ". Now the bug is open.");
			putRequest.put(COMMENTS, comments);
			JSONArray customField = putRequest.optJSONArray(CUSTOM_FIELD);
			JSONObject requir = customField.getJSONObject(0);
			requir.put(REQUIREMENT, requirement);
			putRequest.put(CUSTOM_FIELD, customField);
			putRequest.put(COMPONENT, components);

			String data = putRequest.toString(2);
			logger.info("\nTemplate compilation completed: \n" + data);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestProperty("Content-Type", "application/json");
			httpURLConnection.setRequestProperty("Accept", "application/json");
			httpURLConnection.setRequestProperty("authorization", "Basic " + auth);
			httpURLConnection.setRequestMethod("PUT");
			httpURLConnection.setDoOutput(true);
			out = new OutputStreamWriter(httpURLConnection.getOutputStream());
			out.write(data);
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

	private String getFirstStatus(String idBug, String serverResource, String auth) {
		logger.info("Get the first status from the available states list related to Bug ID: " + idBug);
		String firstState = "";
		String response = "";
		URL url = null;
		try {
			url = new URL(serverResource + "/rest/jirarestresource/1.0/issueStates/" + idBug);
			logger.info("Sending of GET request to: " + url + "\n");
		} catch (MalformedURLException exception) {
			logger.severe(exception.getMessage());
		}
		HttpURLConnection restConnection = null;
		try {
			restConnection = (HttpURLConnection) url.openConnection();
			restConnection.setRequestMethod("GET");
			restConnection.setRequestProperty("authorization", "Basic " + auth);
			int responseCode = restConnection.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(restConnection.getInputStream()));
			String inputLine;
			StringBuffer responseBuffer = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				responseBuffer.append(inputLine);
			}
			response = responseBuffer.toString();
			in.close();
			logger.info("Response OK. Status code: " + responseCode);
		} catch (IOException e) {
			logger.severe(e.getMessage());
		} finally {
			if (restConnection != null) {
				restConnection.disconnect();
			}
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(response)));
			firstState = doc.getElementsByTagName(MEMBER_TAG).item(0).getTextContent();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			logger.severe(e.getMessage());
		}
		return firstState;
	}

	private String doGetCR(String serverResource, String idBug, String proj, String auth) {
		logger.info("Get information about Bug with ID " + idBug + " frrom Jira Server at " + serverResource);
		String response = "";
		URL url = null;
		try {
			url = new URL(
					serverResource + "/rest/jirarestresource/1.0/" + proj + "/changeRequests/" + idBug);
			logger.info("Send GET request to: " + url + "\n");
		} catch (MalformedURLException exception) {
			logger.severe(exception.getMessage());
		}
		HttpURLConnection restConnection = null;
		try {
			restConnection = (HttpURLConnection) url.openConnection();
			restConnection.setRequestMethod("GET");
			restConnection.setRequestProperty("authorization", "Basic " + auth);
			restConnection.setRequestProperty("accept", "application/json");
			int responseCode = restConnection.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(restConnection.getInputStream()));
			String inputLine;
			StringBuffer responseBuffer = new StringBuffer();
			
			while ((inputLine = in.readLine()) != null) {
				responseBuffer.append(inputLine);
			}
			response = responseBuffer.toString();
			logger.info("Request OK. Status code: " + responseCode + ". Response forwarded");
			in.close();

		} catch (IOException e) {
			logger.severe(e.getMessage());
		} finally {
			if (restConnection != null) {
				restConnection.disconnect();
			}
		}
		return response;
	}

	private String getReqFieldID(String changeReq) {
		logger.info("Get ID of Requirement field from Bug information");
		String id = "";
		try {
			JSONObject jsonResp = new JSONObject(changeReq);
			JSONArray customs = jsonResp.getJSONArray("jira:customField");
			for (int i = 0; i < customs.length(); i++) {
				JSONObject temp = customs.getJSONObject(i);
				if (temp.optString("jira:name").equals("Requirement")) {
					id = temp.optString("jira:id");
				}
			}
		} catch (JSONException e) {
			logger.severe(e.getMessage());
		}
		return id;
	}

	private String getDescription(String changeReq) {
		logger.info("Get Description value from Bug information");
		String description = "";
		try {
			JSONObject jsonResp = new JSONObject(changeReq);
			description = jsonResp.optString(DESCRIPTION);
		} catch (JSONException e) {
			logger.severe(e.getMessage());
		}
		return description;
	}
	
	private String getEnvironment(String changeReq){
		logger.info("Get Environment value from Bug information");
		String environment = "";
		try {
			JSONObject jsonResp = new JSONObject(changeReq);
			environment = jsonResp.optString(ENVIRONMENT);
		} catch (JSONException e) {
			logger.severe(e.getMessage());
		}
		return environment;
	}
	
	private JSONArray getComponents(String changeReq){
		logger.info("Get Components value from Bug information");
		JSONArray components = null;
		try {
			JSONObject jsonResponse = new JSONObject(changeReq);
			components = jsonResponse.optJSONArray(COMPONENT);
		} catch (JSONException e) {
			logger.severe(e.getMessage());
		}
		return components;
	}
}