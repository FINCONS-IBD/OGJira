import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.logging.Logger;

import javax.security.auth.login.CredentialException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class Test {
	public static final Logger logger = Logger.getLogger(Test.class.getName());

	public static String getCredentials(String token) {
		String host = "";
		String port = "";

		host = "172.25.12.109";
		port = "8080";

		URL url = null;
		int status = 0;
		try {
			url = new URL("http://" + host + ":" + port + "/Token_Service/getCredential");
		} catch (MalformedURLException exception) {
			exception.printStackTrace();
		}
		HttpURLConnection httpURLConnection = null;
		OutputStreamWriter out = null;
		String credentials = "";
		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			out = new OutputStreamWriter(httpURLConnection.getOutputStream());
			out.write(token);
			out.flush();
			out.close();
			status = httpURLConnection.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			credentials = response.toString();
			System.out.println(response.toString());
		} catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
		return credentials;
	}
	
	private String getFirstStatus(String idBug, String auth) {
		String firstState = "";
		URL url = null;
		String host = "172.25.12.108";
		String port = "8081";
		// host = PropertiesHelper.getConnectionConfig().getProperty("host"); //
		// prop.getProperty("host");
		// port =
		// PropertiesHelper.getConnectionConfig().getProperty("JiraPort"); //
		// prop.getProperty("port");
		try {
			url = new URL("http://" + host + ":" + port + "/rest/jirarestresource/1.0/10100/changeRequests/" + idBug);
			logger.info("Service in use: " + url + "\n");
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
			String response = responseBuffer.toString();
			in.close();
			System.out.println(response);
			
			try {
				JSONObject jsonResp = new JSONObject(response);
				System.out.println(jsonResp);
				
				JSONArray compo =  jsonResp.optJSONArray("jira:component");
				compo.put("CICCIO");
				System.out.println(compo);
				jsonResp.put("jira:component", compo);
				System.out.println(jsonResp);
				JSONArray customs = jsonResp.getJSONArray("jira:customField");
				for(int i = 0; i < customs.length(); i++){
					JSONObject temp = customs.getJSONObject(i);
					if(temp.optString("jira:name").equals("Requirement")){
						firstState = temp.optString("jira:id");
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			/*DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
				Document doc = builder.parse(new InputSource(new StringReader(response)));
				firstState = doc.getElementsByTagName("ns2:member").item(0).getTextContent();
			} catch (ParserConfigurationException | SAXException e) {
				logger.severe(e.getMessage());
			}*/
		} catch (IOException e) {
			logger.severe(e.getMessage());
		} finally {
			if (restConnection != null) {
				restConnection.disconnect();
			}
		}
		return firstState;
	}

	public static void main(String[] args) throws JSONException {

		String authParams =  "gaetano.giordano:Atlassian2016";
		byte[] authByte = Base64.getEncoder().encode(authParams.getBytes());
		String auth = new String(authByte);
		Test t = new Test();
		System.out.println(t.getFirstStatus("PROJ-59", auth));
		
	}

}
