package communication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public abstract class StepperRequestsDispatcher {
    public enum StartUpStatus {
        SUCCESS,
        FAILURE,
        ALREADY_RUNNING
    }

    protected static final String PORT = "8080";

    protected static final String HOST = "http://localhost:" + PORT + "/stepper";

    protected static int cookieIDValue;

    protected StepperRequestsDispatcher() {}

    protected HttpURLConnection getConnection(String path, String method, String contentType) throws Exception{
        URL url = new URL(HOST + path);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod(method);
        if (contentType != null){
            con.setRequestProperty("Content-Type", contentType);
        }
        if (cookieIDValue >= 0) {
            con.setRequestProperty("Cookie", "ID=" + cookieIDValue);
        }
        con.setDoOutput(true);
        return con;
    }

    protected String getBodyResponseFromConnection(HttpURLConnection con) throws Exception{
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
