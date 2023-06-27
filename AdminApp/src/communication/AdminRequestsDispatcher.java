package communication;

import dto.execution.history.FlowsExecutionHistoryDTO;
import dto.flow.FlowNamesDTO;
import dto.flow.LoadDataDTO;
import dto.roles.map.RolesMapDTO;
import dto.statistics.StatisticsDTO;
import dto.user.roles.RolesDTO;
import dto.user.system.info.UsersSystemInfoDTO;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static communication.Utils.*;


public class AdminRequestsDispatcher extends StepperRequestsDispatcher{



    private static AdminRequestsDispatcher instance = new AdminRequestsDispatcher();
    public static AdminRequestsDispatcher getInstance() {
        return instance;
    }
    private AdminRequestsDispatcher() {}


    public LoadDataDTO loadXML(String xmlContent){
        try {
            HttpURLConnection con = getConnection(LOAD_XML_ENDPOINT, "POST", XML_CONTENT_TYPE);
            con.getOutputStream().write(xmlContent.getBytes("UTF-8"));
            con.getOutputStream().flush();

            LoadDataDTO dto = GSON_INSTANCE.fromJson(getBodyResponseFromConnection(con), LoadDataDTO.class);
            con.disconnect();
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Role> getRolesMap() {
        try {
            HttpURLConnection con = getConnection(ROLES_ENDPOINT, "GET", JSON_CONTENT_TYPE);
            Map<String, Role> roleMap = GSON_INSTANCE.fromJson(getBodyResponseFromConnection(con), RolesMapDTO.class).getRolesMap();
            con.disconnect();
            return roleMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new LinkedHashMap<>();
    }

    public StatisticsDTO getStatisticsDTO(){
        try {
            HttpURLConnection con = getConnection(STATISTICS_ENDPOINT, "GET", JSON_CONTENT_TYPE);
            con.getOutputStream().flush();

            StatisticsDTO dto = GSON_INSTANCE.fromJson(getBodyResponseFromConnection(con), StatisticsDTO.class);
            con.disconnect();
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<UserSystemInfo> getOnlineUsers() {
        try {
            HttpURLConnection con = getConnection(USER_INFO_ALL_ENDPOINT, "GET", JSON_CONTENT_TYPE);
            List<UserSystemInfo> users = GSON_INSTANCE.fromJson(getBodyResponseFromConnection(con), UsersSystemInfoDTO.class).getUsersSystemInfo();
            con.disconnect();
            return users;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public FlowsExecutionHistoryDTO getHistoryDTO(){
        try {
            HttpURLConnection con = getConnection(HISTORY_ENDPOINT, "GET", JSON_CONTENT_TYPE);

            FlowsExecutionHistoryDTO dto = GSON_INSTANCE.fromJson(getBodyResponseFromConnection(con), FlowsExecutionHistoryDTO.class);
            con.disconnect();
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    synchronized public FlowNamesDTO getFlowDefinitionNames() {
        try {
            HttpURLConnection con = getConnection(FLOW_NAMES_ENDPOINT, "GET", JSON_CONTENT_TYPE);

            FlowNamesDTO flowNames = GSON_INSTANCE.fromJson(getBodyResponseFromConnection(con), FlowNamesDTO.class);
            return flowNames;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Role> postRoles(List<Role> newRole) {
        try {
            HttpURLConnection con = getConnection(ROLES_ENDPOINT, "POST", JSON_CONTENT_TYPE);
            OutputStream os = con.getOutputStream();
            os.write(GSON_INSTANCE.toJson(new RolesDTO(newRole)).getBytes("UTF-8"));
            RolesMapDTO rolesMapDTO = GSON_INSTANCE.fromJson(getBodyResponseFromConnection(con), RolesMapDTO.class);
            return rolesMapDTO.getRolesMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new LinkedHashMap<>();
    }

    synchronized public List<Role> getRoles() {
        try {
            HttpURLConnection con = getConnection(ROLES_ENDPOINT, "GET", null);
            List<Role> roles = GSON_INSTANCE.fromJson(getBodyResponseFromConnection(con), RolesDTO.class).getRoles();
            con.disconnect();
            return roles;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public StartUpStatus getIsAdminOnline() {
        try {
            HttpURLConnection con = getConnection(ADMIN_STATUS_ENDPOINT, "GET", null);
            boolean result = GSON_INSTANCE.fromJson(getBodyResponseFromConnection(con), Boolean.class);
            con.disconnect();
            return result ? StartUpStatus.ALREADY_RUNNING : StartUpStatus.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StartUpStatus.FAILURE;
    }


    public void putLogoutAdmin() {
        try {
            HttpURLConnection con = getConnection(ADMIN_LOGOUT_ENDPOINT, "PUT", null);
            getBodyResponseFromConnection(con);
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putUsers(List<UserSystemInfo> modifiedUsers) {
        try {
            HttpURLConnection con = getConnection(ROLES_USER_ENDPOINT, "PUT", JSON_CONTENT_TYPE);
            OutputStream os = con.getOutputStream();
            os.write(GSON_INSTANCE.toJson(new UsersSystemInfoDTO(modifiedUsers)).getBytes("UTF-8"));
            getBodyResponseFromConnection(con);
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
