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

import static communication.Utils.GSON_INSTANCE;



public class AdminRequestsDispatcher extends StepperRequestsDispatcher{



    private static final String LOAD_XML = "/loadXML";
    private static final String STATISTICS = "/statistics";
    private static final String HISTORY = "/history";
    private static final String ADMIN = "/admin";
    private static final String USER = "/user";
    private static final String ADMIN_STATUS = ADMIN + "/status";
    private static final String ADMIN_LOGOUT = ADMIN + "/logout";
    private static final String FLOWS_NAMES = "/flow/names";
    private static final String USERS_INFO_ALL = USER + "/info/all";
    private static final String ROLES = "/roles";
    private static final String ROLES_MAP = ROLES + "/map";

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

            LoadDataDTO dto = GSON_INSTANCE.fromJson(getBodyResponseFromConnectio(con), LoadDataDTO.class);
            con.disconnect();
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<Integer, Role> getRolesMap() {
        try {
            HttpURLConnection con = getConnection(ROLES, "GET", "application/json");
            Map<Integer, Role> roleMap = GSON_INSTANCE.fromJson(getBodyResponseFromConnectio(con), RolesMapDTO.class).getRolesMap();
            con.disconnect();
            return roleMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new LinkedHashMap<>();
    }

    public StatisticsDTO getStatisticsDTO(){
        try {
            HttpURLConnection con = getConnection(STATISTICS, "GET", "application/json");
            con.getOutputStream().flush();

            StatisticsDTO dto = GSON_INSTANCE.fromJson(getBodyResponseFromConnectio(con), StatisticsDTO.class);
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
            List<UserSystemInfo> users = GSON_INSTANCE.fromJson(getBodyResponseFromConnectio(con), UsersSystemInfoDTO.class).getUsersSystemInfo();
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

            FlowsExecutionHistoryDTO dto = GSON_INSTANCE.fromJson(getBodyResponseFromConnectio(con), FlowsExecutionHistoryDTO.class);
            con.disconnect();
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    synchronized public FlowNamesDTO getFlowDefinitionNames() {
        try {
            HttpURLConnection con = getConnection(FLOWS_NAMES, "GET", "application/json");

            FlowNamesDTO flowNames = GSON_INSTANCE.fromJson(getBodyResponseFromConnectio(con), FlowNamesDTO.class);
            return flowNames;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<Integer, Role> postRoles(List<Role> newRole) {
        try {
            HttpURLConnection con = getConnection(ROLES, "POST", "application/json");
            OutputStream os = con.getOutputStream();
            os.write(GSON_INSTANCE.toJson(new RolesDTO(newRole)).getBytes("UTF-8"));
            RolesMapDTO rolesMapDTO = GSON_INSTANCE.fromJson(getBodyResponseFromConnectio(con), RolesMapDTO.class);
            return rolesMapDTO.getRolesMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new LinkedHashMap<>();
    }

    synchronized public List<Role> getRoles() {
        try {
            HttpURLConnection con = getConnection(ROLES, "GET", null);
            List<Role> roles = GSON_INSTANCE.fromJson(getBodyResponseFromConnectio(con), RolesDTO.class).getRoles();
            con.disconnect();
            return roles;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public StartUpStatus getIsAdminOnline() {
        try {
            HttpURLConnection con = getConnection(ADMIN_STATUS, "GET", null);
            boolean result = GSON_INSTANCE.fromJson(getBodyResponseFromConnectio(con), Boolean.class);
            con.disconnect();
            return result ? StartUpStatus.ALREADY_RUNNING : StartUpStatus.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StartUpStatus.FAILURE;
    }


    public void putLogoutAdmin() {
        try {
            HttpURLConnection con = getConnection(ADMIN_LOGOUT, "PUT", null);
            getBodyResponseFromConnectio(con);
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
