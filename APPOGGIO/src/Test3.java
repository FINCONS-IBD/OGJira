import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Test3 {
	public static void main(String[] args) throws JSONException {
		String putTemplate = "{\"dcterms:title\": \"\","
				+ "\"jira:issueType\": {\"rdf:about\": \"http://172.25.12.108:8081/rest/jirarestresource/1.0/issueTypes/\"},"
				+ "\"jira:customField\": [{\"jira:id\": \"customfield_10000\",\"jira:name\": \"Requirement\",\"jira:value\": \"\"}],"
				+ "\"jira:issueComment\": [{\"jira:commentBody\": \"\"}],"
				+ "\"prefixes\": {"
				+ "\"dcterms\": \"http://purl.org/dc/terms/\"," + "\"foaf\": \"http://xmlns.com/foaf/0.1/\","
				+ "\"jira\": \"http://atlassian.com/ns/cm#\"," + "\"oslc\": \"http://open-services.net/ns/core#\","
				+ "\"oslc_cm\": \"http://open-services.net/ns/cm#\","
				+ "\"oslc_data\": \"http://open-services.net/ns/servicemanagement/1.0/\","
				+ "\"oslc_qm\": \"http://open-services.net/ns/qm#\","
				+ "\"oslc_rm\": \"http://open-services.net/ns/rm#\","
				+ "\"oslc_scm\": \"http://open-services.net/ns/scm#\","
				+ "\"rdf\": \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\","
				+ "\"rdfs\": \"http://www.w3.org/2000/01/rdf-schema#\"}}";
		JSONObject putRequest = new JSONObject(putTemplate);
		
		putRequest.put("dcterms:title", "TITOLO");
		JSONObject stat = putRequest.optJSONObject("jira:issueType");
		stat.put("rdf:about", (String)stat.opt("rdf:about") + 3);
		System.out.println(stat);
//		putRequest.put("jira:issueType", stat);
		JSONArray comments = putRequest.optJSONArray("jira:issueComment");
		JSONObject comment = comments.getJSONObject(0);
		System.out.println(comment);
		comment.put("jira:commentBody", "COMMENTO  CON ECLISPE");
		System.out.println(comments);
//		putRequest.put("jira:issueComment", comments);
		JSONArray customField = putRequest.optJSONArray("jira:customField");
		JSONObject req = customField.getJSONObject(0);
		System.out.println(req);
		req.put("jira:value", "4");
		System.out.println(customField);
		putRequest.put("jira:customField", customField);
		
		System.out.println(putRequest.toString());
		
	}
}
