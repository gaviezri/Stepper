package servlets;

import communication.Role;
import communication.UserSystemInfo;
import communication.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import stepper.controller.EngineController;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static communication.Utils.ENGINE_CONTROLLER;

public class Servlet {
    private ServletContext contextRef;
    private static ScheduledExecutorService userLogoutExecutorService;
    private Servlet() {

    }


    public static void userCheckIn(HttpServletRequest req){
        Integer cookie = idCookieBaker(req.getCookies());
        if (!cookie.equals(-1)) {
            synchronized (instance.contextRef){
                Map<Integer,Long> cookie2LastAccessMap = (Map) instance.contextRef.getAttribute(Utils.COOKIE_2_LAST_ACCESS);
                cookie2LastAccessMap.put(cookie, System.currentTimeMillis());
            }
        }
    }
    private static Servlet instance;

    public static void initialize(ServletContext context) {
        instance = new Servlet();
        instance.contextRef = context;
        userLogoutExecutorService = Executors.newSingleThreadScheduledExecutor();
        userLogoutExecutorService.scheduleAtFixedRate(()->{
            Collection<Integer> cookiesToRemove = new LinkedList<>();
            Map<Integer,Long> cookie2LastAccessMap = getCookie2LastAccessMap();
            for (Map.Entry<Integer,Long> entry : cookie2LastAccessMap.entrySet()){
                if (System.currentTimeMillis() - entry.getValue() > Utils.ONE_MINUTE){
                    cookiesToRemove.add(entry.getKey());
                }
            }

            synchronized (instance.contextRef) {
                for (Integer cookie : cookiesToRemove) {
                    cookie2LastAccessMap.remove(cookie);
                    String userName = getCookie2User().get(cookie);
                    getUserName2Info().remove(userName);
                    getCookie2User().remove(cookie);
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    public static Map<Integer, Long> getCookie2LastAccessMap() {
        return (Map) instance.contextRef.getAttribute(Utils.COOKIE_2_LAST_ACCESS);
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

    public static void setRolesChanged(Boolean rolesChanged) {
        synchronized (instance.contextRef){
            instance.contextRef.setAttribute(Utils.ROLES_CHANGED, rolesChanged);
        }
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
       try {
              return Integer.parseInt(Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("ID")).findFirst().get().getValue());
       } catch (Exception e) {
           return -1;
       }
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

    public static void shutdown() {
        userLogoutExecutorService.shutdown();
    }
}
