package communication;

import com.google.gson.reflect.TypeToken;
import dto.flow.FlowDefinitionDTO;
import com.google.gson.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.Buffer;
import java.util.List;

public class UserRequestsDispatcher extends StepperRequestsDispatcher{
    private static final String USER_LOGOUT = "/user/logout";
    private static final String USER_LOGIN= "/user/login?name=";
    private static final String FLOWS_DEFINITIONS = "/flow/definitions";


    private static UserRequestsDispatcher instance = new UserRequestsDispatcher();
    public static UserRequestsDispatcher getInstance() {
        return instance;
    }
    private UserRequestsDispatcher() {}

    public List<FlowDefinitionDTO> getAllFlowDefinitionsData(){
        try {
            HttpURLConnection con = getConnection(FLOWS_DEFINITIONS, "GET", "application/json");
            con.getOutputStream().flush();

            List<FlowDefinitionDTO> allFlowDefs = Utils.GSON_INSTANCE.fromJson(getResponse(con),
                                                        new TypeToken<List<FlowDefinitionDTO>>(){}.getType());
            con.disconnect();
            return allFlowDefs;
        } catch (Exception e){
            return null;
        }
    }

    public boolean logout(){
        try {
            HttpURLConnection con = getConnection(USER_LOGOUT, "DELETE", "text/plain");
            con.getOutputStream().flush();

            boolean status = Boolean.parseBoolean(getResponse(con));
            con.disconnect();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean login(String userName) {
        try {
            HttpURLConnection con = getConnection(USER_LOGIN + userName, "POST", "text/plain");
            con.getOutputStream().flush();
            try {
                cookieIDValue = Integer.parseInt(getResponse(con));
            } catch (Exception e) {
                cookieIDValue = -1;
            }
            con.disconnect();
            return cookieIDValue >= 0;
            // refactor ^^
        } catch (Exception e) {
            e.printStackTrace();
        }
       return false;
    }

    public List<FlowDefinitionDTO> getUserRoleData() {
         return null;
    }
}
