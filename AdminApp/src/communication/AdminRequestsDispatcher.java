package communication;

import dto.execution.history.FlowsExecutionHistoryDTO;
import dto.flow.FlowNamesDTO;
import dto.flow.LoadDataDTO;
import dto.statistics.StatisticsDTO;
import dto.user.roles.RolesDTO;
import dto.user.system.info.UsersSystemInfoDTO;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import static communication.Utils.GSON_INSTANCE;



public class AdminRequestsDispatcher extends StepperRequestsDispatcher{



    public enum StartUpStatus {
        SUCCESS,
        FAILURE,
        ALREADY_RUNNING
    }
    private static final String LOAD_XML = "/loadXML";
    private static final String STATISTICS = "/statistics";
    private static final String HISTORY = "/history";
    private static final String ADMIN = "/admin";
    private static final String USER = "/user";
    private static final String ADMIN_STATUS = ADMIN + "/status";
    private static final String ADMIN_LOGOUT = ADMIN + "/logout";
    private static final String FLOW_NAMES = "/flows/names";
    private static final String USERS_INFO_ALL = USER + "/info/all";
    private static final String ROLES = "/roles";

    private static AdminRequestsDispatcher instance = new AdminRequestsDispatcher();
    public static AdminRequestsDispatcher getInstance() {
        return instance;
    }
    private AdminRequestsDispatcher() {}



    public LoadDataDTO loadXML(String xmlContent){
        try {
            HttpURLConnection con = getConnection(LOAD_XML, "POST", "application/xml");
            con.getOutputStream().write(xmlContent.getBytes("UTF-8"));
            con.getOutputStream().flush();

            LoadDataDTO dto = GSON_INSTANCE.fromJson(getResponse(con), LoadDataDTO.class);
            con.disconnect();
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public StatisticsDTO getStatisticsDTO(){
        try {
            HttpURLConnection con = getConnection(STATISTICS, "GET", "application/json");
            con.getOutputStream().flush();

            StatisticsDTO dto = GSON_INSTANCE.fromJson(getResponse(con), StatisticsDTO.class);
            con.disconnect();
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<UserSystemInfo> getOnlineUsers() {
        try {
            HttpURLConnection con = getConnection(USERS_INFO_ALL, "GET", "application/json");


            List<UserSystemInfo> users = GSON_INSTANCE.fromJson(getResponse(con), UsersSystemInfoDTO.class).getUsersSystemInfo();
            con.disconnect();
            return users;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public FlowsExecutionHistoryDTO getHistoryDTO(){
        try {
            HttpURLConnection con = getConnection(HISTORY, "GET", "application/json");

            FlowsExecutionHistoryDTO dto = GSON_INSTANCE.fromJson(getResponse(con), FlowsExecutionHistoryDTO.class);
            con.disconnect();
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public FlowNamesDTO getFlowDefinitionNames() {
        try {
            HttpURLConnection con = getConnection(FLOW_NAMES, "GET", "application/json");

            FlowNamesDTO flowNames = GSON_INSTANCE.fromJson(getResponse(con), FlowNamesDTO.class);
            return flowNames;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createRoles(List<Role> newRole) {
        try {
            HttpURLConnection con = getConnection(ROLES, "POST", "application/json");
            OutputStream os = con.getOutputStream();
            os.write(GSON_INSTANCE.toJson(new RolesDTO(newRole)).getBytes("UTF-8"));
            os.flush();
            os.close();
            con.getResponseMessage();
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Role> getRoles() {
        try {
            HttpURLConnection con = getConnection(ROLES, "GET", null);
            List<Role> roles = GSON_INSTANCE.fromJson(getResponse(con), RolesDTO.class).getRoles();
            con.disconnect();
            return roles;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public StartUpStatus isAdminOnline() {
        try {
            HttpURLConnection con = getConnection(ADMIN_STATUS, "GET", null);
            boolean result = GSON_INSTANCE.fromJson(getResponse(con), Boolean.class);
            con.disconnect();
            return result ? StartUpStatus.ALREADY_RUNNING : StartUpStatus.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StartUpStatus.FAILURE;
    }


    public void logoutAdmin() {
        try {
            HttpURLConnection con = getConnection(ADMIN_LOGOUT, "GET", null);
            getResponse(con);
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
