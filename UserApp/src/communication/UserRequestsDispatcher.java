package communication;

import dto.execution.FlowExecutionRequestDTO;
import dto.execution.progress.ExecutedFlowDetailsDTO;
import dto.execution.history.FlowsExecutionHistoryDTO;

import dto.flow.FlowDefinitionDTO;
import dto.flow.ManyFlowDefinitionsDTO;
import dto.user.roles.RolesDTO;
import javafx.util.Pair;
import stepper.flow.execution.FlowExecution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static communication.Utils.*;


public class UserRequestsDispatcher extends StepperRequestsDispatcher{

    private static UserRequestsDispatcher instance = new UserRequestsDispatcher();
    public static UserRequestsDispatcher getInstance() {
        return instance;
    }
    private UserRequestsDispatcher() {}

//    public String getBodyResponseFromConnection(HttpURLConnection connection) throws IOException {
//        StringBuilder response = new StringBuilder();
//        try {
//            BufferedReader br = null;
//            if (100 <= connection.getResponseCode() && connection.getResponseCode() <= 399){
//                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            }else {
//                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
//            }
//
//            String line;
//            while ((line = br.readLine()) != null) {
//                response.append(line);
//            }
//        }
//        catch (Exception e){
//            return e.getMessage();
//        }
//        return response.toString();
//    }

    public FlowsExecutionHistoryDTO getHistory() {
        try {
            HttpURLConnection con = getConnection(HISTORY_ENDPOINT, "GET", JSON_CONTENT_TYPE);
            FlowsExecutionHistoryDTO dto = GSON_INSTANCE.fromJson(getBodyResponseFromConnection(con), FlowsExecutionHistoryDTO.class);
            con.disconnect();
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new FlowsExecutionHistoryDTO(new Stack<FlowExecution>());
    }

    public List<FlowDefinitionDTO> getAllAccessibleFlowDefinitionsData(){
        try {
            HttpURLConnection con = getConnection(FLOW_DEFINITIONS_ENDPOINT, "GET", JSON_CONTENT_TYPE);
            String bodyContent = this.getBodyResponseFromConnection(con);
            ManyFlowDefinitionsDTO manyFlowDefinitionsDTO = GSON_INSTANCE.fromJson(bodyContent,ManyFlowDefinitionsDTO.class);
            List<FlowDefinitionDTO> flowDefinitionDTOs = manyFlowDefinitionsDTO.getFlowDefinitions();
            con.disconnect();
            return flowDefinitionDTOs;
        } catch (Exception e){
            return null;
        }
    }

    public boolean logout(){
        try {
            HttpURLConnection con = getConnection(USER_LOGOUT_ENDPOINT, "DELETE", PLAIN_TEXT_CONTENT_TYPE);
            con.getOutputStream().flush();

            boolean status = Boolean.parseBoolean(getBodyResponseFromConnection(con));
            con.disconnect();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void filterHistory(FlowsExecutionHistoryDTO.SortFilter filter){
        try {
            HttpURLConnection con = getConnection(FILTER_HISTORY_FILTER_ENDPOINT, "GET", PLAIN_TEXT_CONTENT_TYPE);
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean login(String userName) {
        try {
            HttpURLConnection con = getConnection(USER_LOGIN + userName, "POST", PLAIN_TEXT_CONTENT_TYPE);
            con.getOutputStream().flush();
            try {
                cookieIDValue = Integer.parseInt(getBodyResponseFromConnection(con));
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

    public List<Role> getUserRolesList(){
        try {
            HttpURLConnection con = getConnection(ROLES_USER_ENDPOINT, "GET", JSON_CONTENT_TYPE);
            List<Role> userRoles = GSON_INSTANCE.fromJson(getBodyResponseFromConnection(con), RolesDTO.class).getRoles();
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
            UserSystemInfo userInfo = GSON_INSTANCE.fromJson(getBodyResponseFromConnection(con),UserSystemInfo.class);
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

    public void executeFlow(String flowName, Pair<Map, Map> valName2valType) {
        try {
            HttpURLConnection con = getConnection(FLOW_EXECUTION_ENDPOINT, "POST", JSON_CONTENT_TYPE);
            con.getOutputStream().write(GSON_INSTANCE.toJson(new FlowExecutionRequestDTO(valName2valType, flowName)).getBytes());
            con.getOutputStream().flush();
            con.getResponseMessage();
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ExecutedFlowDetailsDTO getExecutionProgress() {
        try {
            HttpURLConnection con = getConnection(FLOW_EXECUTION_PROGRESS_ENDPOINT, "GET", JSON_CONTENT_TYPE);
            ExecutedFlowDetailsDTO executionProgressDTO = GSON_INSTANCE.fromJson(getBodyResponseFromConnection(con),
                    ExecutedFlowDetailsDTO.class);
            con.disconnect();
            return executionProgressDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

