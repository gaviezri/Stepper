package servlets;

import communication.Role;
import communication.UserSystemInfo;
import dto.roles.map.RolesMapDTO;
import dto.user.roles.RolesDTO;
import dto.user.system.info.UsersSystemInfoDTO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


import static communication.Utils.*;

@WebServlet(name = "RolesManagementServlet", urlPatterns = {ROLES_ENDPOINT, ROLES_USER_ENDPOINT, ROLES_MAP_ENDPOINT})
public class RolesManagementServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case ROLES_ENDPOINT:
                handleRoleGet(resp);
                break;
            case ROLES_USER_ENDPOINT:
                handleRolesUserGet(req, resp);
                break;
            case ROLES_MAP_ENDPOINT:
                handleRolesMapGet(resp);
                break;
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case ROLES_ENDPOINT:
                handleRolesPost(req, resp);
                break;
        }
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case ROLES_USER_ENDPOINT:
                handleRolesUserPut(req, resp);
                break;
        }
    }

    @Override
    protected  void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case ROLES_ENDPOINT:
                handleRolesDelete(req, resp);
                break;
        }
    }

    private void handleRolesDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletContext context = getServletContext();
        synchronized (context) {
            context.setAttribute(ROLES_CHANGED, true);
        }
        Role.RoleManager theManager = Servlet.getRoleManager();
        List<Role> allRoles = Servlet.getRoles();
        List<Role> rolesToDelete = GSON_INSTANCE.fromJson(req.getReader(), RolesDTO.class).getRoles();
        removeDeletedRolesFromManagerAndContext(theManager, allRoles, rolesToDelete);
        removeDeletedRolesFromUsersSystemInfo(context, rolesToDelete);
    }

    private static void removeDeletedRolesFromManagerAndContext(Role.RoleManager theManager, List<Role> allRoles, List<Role> rolesToDelete) {
        allRoles.sort(Comparator.comparing(Role::getName));
        for (Role roleToDel : rolesToDelete) {
            if (allRoles.contains(roleToDel)) {
                allRoles.remove(roleToDel);
                theManager.deleteRole(roleToDel);
            }
        }
    }

    private static void removeDeletedRolesFromUsersSystemInfo(ServletContext context, List<Role> rolesToDelete) {
        Map<String, UserSystemInfo> name2info = Servlet.getUserName2Info();
        synchronized (context) {
            for (UserSystemInfo userSystemInfo : name2info.values()) {
                userSystemInfo.getRoles().removeAll(rolesToDelete);
            }
        }
    }

    private void handleRolesUserPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        ServletContext context = getServletContext();
        Map<String, UserSystemInfo> name2info = Servlet.getUserName2Info();
        UsersSystemInfoDTO usersSystemInfoDTO = GSON_INSTANCE.fromJson(req.getReader(),UsersSystemInfoDTO.class);
        synchronized (context) {
            for (UserSystemInfo userSystemInfo : usersSystemInfoDTO.getUsersSystemInfo()) {
                name2info.put(userSystemInfo.getName(), userSystemInfo);
            }
        }
    }

    private void handleRolesPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletContext context = getServletContext();
        synchronized (context) {
            context.setAttribute(ROLES_CHANGED, true);
        }
        Role.RoleManager theManager = Servlet.getRoleManager();
        List<Role> allRoles = Servlet.getRoles();
        List<Role> newRoles = GSON_INSTANCE.fromJson(req.getReader(), RolesDTO.class).getRoles();

        for (Role role : newRoles) {
            if (allRoles.contains(role)) {
                allRoles.set(allRoles.indexOf(role),role);
            } else {
                allRoles.add(role);
            }
            theManager.addRole(role);
        }
        allRoles.sort(Comparator.comparing(Role::getName));
        Map<String, UserSystemInfo> users = Servlet.getUserName2Info();
        for (UserSystemInfo user : users.values()) {
            user.updateRoles(allRoles);
        }
        synchronized (context) {
            context.setAttribute(ROLES, allRoles);
        }
        resp.getWriter().println(GSON_INSTANCE.toJson(new RolesMapDTO(theManager.getRolesMap())));
        System.out.println("Roles updated");
    }

    private void handleRolesMapGet(HttpServletResponse resp) throws IOException {
        ServletContext context = getServletContext();
        Role.RoleManager theManager = Servlet.getRoleManager();
        String results = GSON_INSTANCE.toJson(new RolesMapDTO(theManager.getRolesMap()));
        resp.getWriter().println(results);
    }

    private void handleRolesUserGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String res = "";
        Cookie[] cookie = req.getCookies();
        ServletContext context = getServletContext();
        Map<Integer, String> cookie2User = Servlet.getCookie2User();
         Map<String, UserSystemInfo> user2Info = Servlet.getUserName2Info();
        if (cookie[0] != null){
            String user = cookie2User.get(Integer.parseInt(cookie[0].getValue()));
            UserSystemInfo userInfo = user2Info.get(user);
            List<Role> rolesList = userInfo.getRoles();
            res = GSON_INSTANCE.toJson(new RolesDTO(rolesList));
        }
        resp.getWriter().println(res);
    }

    private void handleRoleGet(HttpServletResponse resp) throws IOException {
        ServletContext context = getServletContext();
        String results = GSON_INSTANCE.toJson(new RolesDTO());

            if(context.getAttribute(ROLES_CHANGED).equals(true) ||
                    context.getAttribute(FETCH_STARTUP_DATA_ADMIN).equals(true)){
                synchronized (context) {
                context.setAttribute(ROLES_CHANGED, false);
                context.setAttribute(FETCH_STARTUP_DATA_ADMIN, false);
                List<Role> roles = Servlet.getRoles();
                results = GSON_INSTANCE.toJson(new RolesDTO(roles));
            }
        }
        resp.getWriter().println(results);
    }
}
