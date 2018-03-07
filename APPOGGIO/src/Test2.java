import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.security.auth.login.CredentialException;

import org.json.JSONException;
import org.json.JSONObject;

public class Test2 {
	public static String getCredentials(String token) {
		String host = "";
		String port = "";

		host = "172.25.12.108";
		port = "8081";

		URL url = null;
		int status = 0;
		try {
			url = new URL("http://" + host + ":" + port + "/rest/jirarestresources/1.0/10100/changeRequests");
		} catch (MalformedURLException exception) {
			exception.printStackTrace();
		}
		HttpURLConnection httpURLConnection = null;
		OutputStreamWriter out = null;
		String credentials = "";
		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestProperty("Content-type", "application/json");
			httpURLConnection.setRequestProperty("Accept", "application/json");
			
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);

			
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

	public static void main(String[] args) throws JSONException {

		String strJson = "{\"dcterms:title\": \"DFGDF\"," + "\"jira:component\": [\"\"],"
				+ "\"jira:issuePriority\": {\"rdf:about\": \"http://172.25.12.108:8081/rest/jirarestresource/1.0/issuePriorities/3\"},"
				+ "\"jira:issueType\": {\"rdf:about\": \"http://172.25.12.108:8081/rest/jirarestresource/1.0/issueTypes/1\"},"
				+ "\"jira:customField\": [{\"jira:id\": \"customfield_10000\",\"jira:name\": \"Requirement\",\"jira:value\": \"4\"}],"
				+ "\"jira:description\": \"DFGDFAGAFGAD\","
				+ "\"jira:environment\": \"Platform: DFGDFGDF  Operating System: GDFGDFG\"," + "\"prefixes\": {"
				+ "\"dcterms\": \"http://purl.org/dc/terms/\"," + "\"foaf\": \"http://xmlns.com/foaf/0.1/\","
				+ "\"jira\": \"http://atlassian.com/ns/cm#\"," + "\"oslc\": \"http://open-services.net/ns/core#\","
				+ "\"oslc_cm\": \"http://open-services.net/ns/cm#\","
				+ "\"oslc_data\": \"http://open-services.net/ns/servicemanagement/1.0/\","
				+ "\"oslc_qm\": \"http://open-services.net/ns/qm#\","
				+ "\"oslc_rm\": \"http://open-services.net/ns/rm#\","
				+ "\"oslc_scm\": \"http://open-services.net/ns/scm#\","
				+ "\"rdf\": \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\","
				+ "\"rdfs\": \"http://www.w3.org/2000/01/rdf-schema#\"}}";

		System.out.println(strJson);

		String res = getCredentials(strJson);
		System.out.println(res);

		// System.out.println(System.getenv("CONFIG_PATH"));

	}

}
