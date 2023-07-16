package stepper.communication;

import communication.StepperRequestsDispatcher;
import dto.flow.LoadDataDTO;
import javafx.util.Pair;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static communication.Utils.GSON_INSTANCE;
import static communication.Utils.JSON_CONTENT_TYPE;

public class EngineRequestsDispatcher extends StepperRequestsDispatcher {
    private static EngineRequestsDispatcher instance = new EngineRequestsDispatcher();
    public static EngineRequestsDispatcher getInstance() {
        return instance;
    }

    private EngineRequestsDispatcher(){};

    protected HttpURLConnection getConnection(String protocol, String resource, String address, String method, String contentType, String body) throws Exception{
        URL url = new URL(protocol + "://" + address + resource);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod(method);

        if (contentType != null){
            con.setRequestProperty("Content-Type", contentType);
        }

        if (body != null){
            byte[] requestBodyBytes = body.getBytes(StandardCharsets.UTF_8);
            OutputStream outputStream = con.getOutputStream();
            outputStream.write(requestBodyBytes);
//            con.getOutputStream().flush();   in a other place Gal used this -> if problem occurs uncomment this
            outputStream.close();
        }

        con.setDoOutput(true);
        return con;
    }

    public Pair<String,Number> createHTTPRequestAndReturnOutput(String protocol, String resource, String address, String method, String contentType, String body) throws Exception {
        HttpURLConnection con = getConnection(protocol, resource, address, method, JSON_CONTENT_TYPE, body);
        return new Pair<String,Number>(getBodyResponseFromConnection(con),con.getResponseCode());
    }
}
