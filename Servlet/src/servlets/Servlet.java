package servlets;

import communication.Role;
import communication.UserSystemInfo;
import communication.Utils;
import dto.flow.FlowDefinitionDTO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import stepper.controller.EngineController;

import java.util.*;

import static communication.Utils.ENGINE_CONTROLLER;

public class Servlet {
    private ServletContext contextRef;
    private Servlet() {
    }


    public static void userCheckOut(Integer cookie){
        // redirect to user logout procedure servlet...
        // if user last timestamp exceeds threshold
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
    public static Map<UUID, Integer> getUuid2Cookie() {
        return (Map) instance.contextRef.getAttribute(Utils.UUID_2_COOKIE);
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

    public static Stack<UUID> getFlowExecIdStack(Integer cookie) {
        return ((Map<Integer,Stack<UUID>>) instance.contextRef.getAttribute(Utils.COOKIE_2_FLOW_EXEC_ID)).get(cookie);
    }

    public static void createNewFlowExecStack(Integer cookie) {
        ((Map<Integer,Stack<UUID>>) instance.contextRef.getAttribute(Utils.COOKIE_2_FLOW_EXEC_ID)).put(cookie, new Stack<>());
    }
    public static Integer idCookieBaker(Cookie[] cookies){
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("ID")) {
                return Integer.parseInt(cookie.getValue());
            }
        }
        return -1;
    }

    public static Set<String> getUserAccessibleFlowNames(UserSystemInfo userInfo){
        // get current users assigned roles
        List<Role> userRoles = userInfo.getRoles();
        // create current users accessible flow names set
        Set<String> userAccessibleFlows = new HashSet<>();
        for (Role role: userRoles) {
            userAccessibleFlows.addAll(role.getAccessibleFlowsNames());
        }
        return userAccessibleFlows;
    }

    public static UserSystemInfo getUserSystemInfo(Cookie[] cookies) {
        // get users cookie
        Integer userCookie = Servlet.idCookieBaker(cookies);
        // get current user info by cookie id
        String userName = Servlet.getCookie2User().get(userCookie);
        UserSystemInfo userInfo = Servlet.getUserName2Info().get(userName);
        return userInfo;
    }

    public static Boolean isAdmin(Cookie[] cookies){
        // get users cookie
        Integer userCookie = Servlet.idCookieBaker(cookies);
        return userCookie.equals(0);
    }
}
