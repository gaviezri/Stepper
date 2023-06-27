package servlets;

import communication.Role;
import communication.UserSystemInfo;
import dto.roles.map.RolesMapDTO;
import dto.user.roles.RolesDTO;
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
        handleRolesPost(req,resp);
    }

    private void handleRolesPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletContext context = getServletContext();
        synchronized (context) {
            context.setAttribute(ROLES_CHANGED, true);
        }
        Role.RoleManager theManager = (Role.RoleManager) context.getAttribute(ROLES_MANAGER);
        List<Role> allRoles = (List) context.getAttribute(ROLES);
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

        synchronized (context) {
            context.setAttribute(ROLES, allRoles);
        }
        resp.getWriter().println(GSON_INSTANCE.toJson(new RolesMapDTO(theManager.getRolesMap())));
    }

    private void handleRolesMapGet(HttpServletResponse resp) throws IOException {
        ServletContext context = getServletContext();
        Role.RoleManager theManager = (Role.RoleManager) context.getAttribute(ROLES_MANAGER);
        String results = GSON_INSTANCE.toJson(new RolesMapDTO(theManager.getRolesMap()));
        resp.getWriter().println(results);
    }

    private void handleRolesUserGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String res = "[]";
        Cookie[] cookie = req.getCookies();
        ServletContext context = getServletContext();
        Map<Integer, String> cookie2User = (Map) context.getAttribute(COOKIE_2_USER);
        Map<String, UserSystemInfo> user2Info = (Map) context.getAttribute(USERS_IN_SYSTEM);
        Role.RoleManager rolesManager = (Role.RoleManager) context.getAttribute(ROLES_MANAGER);
        if (cookie[0] != null){
            String user = cookie2User.get(Integer.parseInt(cookie[0].getValue()));
            UserSystemInfo userInfo = user2Info.get(user);
            List<Role> rolesList = rolesManager.getRolesListFromCumulativeRoleValue(userInfo.getRoles());
            res = GSON_INSTANCE.toJson(new RolesDTO(rolesList));
        }
        resp.getWriter().println(res);
    }

    private void handleRoleGet(HttpServletResponse resp) throws IOException {
        ServletContext context = getServletContext();
        String results = GSON_INSTANCE.toJson(new RolesDTO());
        synchronized (context) {
            if(context.getAttribute(ROLES_CHANGED).equals(true) ||
                    context.getAttribute(FETCH_STARTUP_DATA_ADMIN).equals(true)){
                context.setAttribute(ROLES_CHANGED, false);
                context.setAttribute(FETCH_STARTUP_DATA_ADMIN, false);
                List<Role> roles = (List) context.getAttribute(ROLES);
                results = GSON_INSTANCE.toJson(new RolesDTO(roles));
            }
            // send the updated roles to admin
            resp.getWriter().println(results);
        }
    }
}
