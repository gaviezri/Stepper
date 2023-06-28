package servlets;

import communication.Role;
import communication.UserSystemInfo;
import communication.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import javafx.util.Pair;
import stepper.controller.EngineController;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static communication.Utils.ENGINE_CONTROLLER;

public class Servlet {
    private ServletContext contextRef;
    private Servlet() {
    }
    private static Servlet instance;

    public static void initialize(ServletContext context) {
        instance = new Servlet();
        instance.contextRef = context;
    }

    public static EngineController getEngineController() {
        return (EngineController)instance.contextRef.getAttribute(ENGINE_CONTROLLER);
    }

    public static Map<String, UserSystemInfo> getUserName2Info() {
        return (Map) instance.contextRef.getAttribute(Utils.USERS_IN_SYSTEM);
    }

    public static Boolean getIsAdminLoggedIn() {
        return (Boolean) instance.contextRef.getAttribute(Utils.ADMIN_LOGGED_IN);
    }

    public static Map<Integer,String> getCookie2User() {
        return (Map) instance.contextRef.getAttribute(Utils.COOKIE_2_USER);
    }

    public static Integer getNextFreeId() {
        return (Integer) instance.contextRef.getAttribute(Utils.NEXT_FREE_ID);
    }

    public static List<Role> getRoles() {
        return (List) instance.contextRef.getAttribute(Utils.ROLES);
    }

    public static Boolean getRolesChanged() {
        return (Boolean) instance.contextRef.getAttribute(Utils.ROLES_CHANGED);
    }

    public static Boolean getFetchStartupDataAdmin() {
        return (Boolean) instance.contextRef.getAttribute(Utils.FETCH_STARTUP_DATA_ADMIN);
    }

    public static Role.RoleManager getRoleManager() {
        return (Role.RoleManager) instance.contextRef.getAttribute(Utils.ROLES_MANAGER);
    }

    public static Map<Integer, List<java.util.UUID>> getCookie2FlowExecId() {
        return (Map) instance.contextRef.getAttribute(Utils.COOKIE_2_FLOW_EXEC_ID);
    }

    public static Function<Pair<HttpServletRequest, String>, Integer> getCookieBaker() {
        return (Function<Pair<HttpServletRequest, String>, Integer>)
                instance.contextRef.getAttribute(Utils.COOKIE_BAKER);
    }

}
