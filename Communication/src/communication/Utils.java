package communication;

import com.google.gson.*;


public class Utils {



    // Create a single instance of Gson object
    final static public Gson GSON_INSTANCE = GsonCreator.createGson();
    final static public String ENGINE_CONTROLLER = "engineController";
    final static public String COOKIE_2_FLOW_EXEC_ID = "cookie2FlowExecId";
    final static public String UUID_2_COOKIE = "uuid2Cookie";
    final static public String ADMIN_LOGGED_IN = "isAdminLoggedIn";
    final static public String USERS_IN_SYSTEM = "usersLoggedInMap";
    final static public String COOKIE_2_USER = "userName2Id";
    final static public String NEXT_FREE_ID = "nextAvailableUserId";
    public static final String ROLES = "roles";
    public static final String ROLES_CHANGED = "rolesChanged";
    public static final String FETCH_STARTUP_DATA_ADMIN = "fetchStartupDataAdmin";
    public static final String ROLES_MANAGER = "rolesManager";
    public static final String JSON_CONTENT_TYPE = "application/json";
    public static final String PLAIN_TEXT_CONTENT_TYPE = "text/plain";
    public static final String XML_CONTENT_TYPE = "application/xml";
    public static final String HISTORY_FILTER = "filter";
    public static final String ALL_FLOWS = "All Flows";
    public static final String READ_ONLY_FLOWS = "Read Only Flows";

    /*#################### endpoints ####################*/
    public static final String USER_LOGIN = "/user/login?name=";
    public static final  String LOAD_XML_ENDPOINT = "/loadXML";
    public static final String FLOW_NAMES_ENDPOINT = "/flow/names";
    public static final String FLOW_EXECUTION_ENDPOINT = "/flow/execution";
    public static final String FLOW_DEFINITIONS_ENDPOINT = "/flow/definitions";
    public static final String STATISTICS_ENDPOINT = "/statistics";
    public static final String HISTORY_ENDPOINT = "/history";
    public static final String ROLES_ENDPOINT = "/roles";
    public static final String ROLES_USER_ENDPOINT = "/roles/user";
    public static final String ROLES_MAP_ENDPOINT = "/roles/map";
    public static final String ADMIN_STATUS_ENDPOINT = "/admin/status";
    public static final String ADMIN_LOGOUT_ENDPOINT = "/admin/logout";
    public static final String USER_STATUS_ENDPOINT = "/user/status";
    public static final String USER_LOGIN_ENDPOINT = "/user/login";
    public static final String USER_LOGOUT_ENDPOINT = "/user/logout";
    public static final String USER_INFO_ALL_ENDPOINT = "/user/info/all";
    public static final String SINGLE_USER_INFO_ENDPOINT = "/user/info";
    public static final String FLOW_EXECUTION_PROGRESS_ENDPOINT = "/flow/execution/progress";
    public static final String FILTER_HISTORY_FILTER_ENDPOINT = "/history/filter";
    public static final String COOKIE_2_LAST_ACCESS = "cookie2LastAccess";
    public static final long ONE_MINUTE = 60000;
}
