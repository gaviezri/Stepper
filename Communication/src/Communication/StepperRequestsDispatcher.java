package Communication;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class StepperRequestsDispatcher {

    protected static final String PORT = "8080";

    protected static final String HOST = "http://localhost:" + PORT + "/stepper";

    protected StepperRequestsDispatcher() {}

    protected HttpURLConnection getConnection(String path, String method, String contentType) throws Exception{
        URL url = new URL(HOST + path);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", contentType);
        con.setDoOutput(true);
        return con;
    }


}
