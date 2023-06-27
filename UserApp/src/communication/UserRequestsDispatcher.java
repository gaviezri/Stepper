package communication;

import com.google.gson.reflect.TypeToken;
import dto.flow.FlowDefinitionDTO;
import dto.user.roles.RolesDTO;

import java.net.HttpURLConnection;
import java.util.List;

import static communication.Utils.*;


public class UserRequestsDispatcher extends StepperRequestsDispatcher{

    private static UserRequestsDispatcher instance = new UserRequestsDispatcher();
    public static UserRequestsDispatcher getInstance() {
        return instance;
    }
    private UserRequestsDispatcher() {}

    public List<FlowDefinitionDTO> getAllAccessibleFlowDefinitionsData(){
        try {
            HttpURLConnection con = getConnection(FLOW_DEFINITIONS_ENDPOINT, "GET", JSON_CONTENT_TYPE);
            con.getOutputStream().flush();

            List<FlowDefinitionDTO> allAccessibleFlowDefs = GSON_INSTANCE.fromJson(getBodyResponseFromConnectio(con),
                                                        new TypeToken<List<FlowDefinitionDTO>>(){}.getType());
            con.disconnect();
            return allAccessibleFlowDefs;
        } catch (Exception e){
            return null;
        }
    }

    public boolean logout(){
        try {
            HttpURLConnection con = getConnection(USER_LOGOUT_ENDPOINT, "DELETE", PLAIN_TEXT_CONTENT_TYPE);
            con.getOutputStream().flush();

            boolean status = Boolean.parseBoolean(getBodyResponseFromConnectio(con));
            con.disconnect();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean login(String userName) {
        try {
            HttpURLConnection con = getConnection(USER_LOGIN + userName, "POST", PLAIN_TEXT_CONTENT_TYPE);
            con.getOutputStream().flush();
            try {
                cookieIDValue = Integer.parseInt(getBodyResponseFromConnectio(con));
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
    public List getUserRolesList(){
        try {
            HttpURLConnection con = getConnection(ROLES_USER_ENDPOINT, "GET", JSON_CONTENT_TYPE);
            List userRoles = GSON_INSTANCE.fromJson(getBodyResponseFromConnectio(con), RolesDTO.class).getRoles();
            con.disconnect();
            return userRoles;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public UserSystemInfo getUsersCurrentInfo(String userName){
        try {
            HttpURLConnection con = getConnection(USER_STATUS_ENDPOINT + "?name=" + userName, "GET", JSON_CONTENT_TYPE);
            UserSystemInfo userInfo = GSON_INSTANCE.fromJson(getBodyResponseFromConnectio(con),UserSystemInfo.class);
            con.disconnect();
            return userInfo; //could be null if username not in system!
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public StartUpStatus pingServer() {
        try {
            HttpURLConnection con = getConnection(USER_STATUS_ENDPOINT, "GET", PLAIN_TEXT_CONTENT_TYPE);
            con.getResponseMessage();
            con.disconnect();
            return StartUpStatus.SUCCESS;
        } catch (Exception e) {
            return StartUpStatus.FAILURE;
        }
    }
}

