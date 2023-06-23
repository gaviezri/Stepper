package Communication;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class StepperRequestsDispatcher {
    private static final String PORT = "8080";
    private static final String LOAD_XML = "/stepper/loadXML";
    private static final String HOST = "http://localhost:" + PORT;
    private static StepperRequestsDispatcher instance = new StepperRequestsDispatcher();
    public static StepperRequestsDispatcher getInstance() {
        return instance;
    }
    private StepperRequestsDispatcher() {}

    private HttpURLConnection getConnection(String path) throws Exception{
        URL url = new URL(HOST + path);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/xml");
        con.setDoOutput(true);
        return con;
    }

    public Map loadXML(String xmlContent){
        try {
            HttpURLConnection con = getConnection(LOAD_XML);
            con.getOutputStream().write(xmlContent.getBytes("UTF-8"));
            con.getOutputStream().flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            // change to utils.GSON
            Gson gson = new Gson();
            Map map = gson.fromJson(sb.toString(), Map.class);
            con.disconnect();
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
