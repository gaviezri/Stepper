package servlets;

import communication.Role;
import communication.UserSystemInfo;
import communication.Utils;
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


import static communication.Utils.GSON_INSTANCE;
import static servlets.RolesManagementServlet.*;
@WebServlet(name = "RolesManagementServlet", urlPatterns = {ROLES_ENDPOINT, ROLES_USER_ENDPOINT})
public class RolesManagementServlet extends HttpServlet {
    static final String ROLES_ENDPOINT = "/roles";
    static final String ROLES_USER_ENDPOINT = "/roles/user";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRolesPost(req);
    }

    private void handleRolesPost(HttpServletRequest req) throws IOException {
        ServletContext context = getServletContext();
        synchronized (context) {
            context.setAttribute(Utils.ROLES_CHANGED, true);
        }
        List<Role> allRoles = (List) context.getAttribute(Utils.ROLES);
        List<Role> newRoles = Utils.GSON_INSTANCE.fromJson(req.getReader(), RolesDTO.class).getRoles();

        for (Role role : newRoles) {
            if (allRoles.contains(role)) {
                allRoles.set(allRoles.indexOf(role),role);
            } else {
                allRoles.add(role);
            }
        }
        allRoles.sort(Comparator.comparing(Role::getName));

        synchronized (context) {
            context.setAttribute(Utils.ROLES, allRoles);
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case "/roles":
                handleRoleGet(resp);
                break;
            case "/roles/user":
                handleRolesUserGet(req, resp);
                break;
        }
    }

    private void handleRolesUserGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String res = "[]";
        Cookie[] cookie = req.getCookies();
        ServletContext context = getServletContext();
        Map<Integer, String> cookie2User = (Map) context.getAttribute(Utils.COOKIE_2_USER);
        Map<String, UserSystemInfo> user2Info = (Map) context.getAttribute(Utils.USERS_IN_SYSTEM);
        Role.RoleManager rolesManager = (Role.RoleManager) context.getAttribute(Utils.ROLES_MANAGER);
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
            if(context.getAttribute(Utils.ROLES_CHANGED).equals(true) ||
                    context.getAttribute(Utils.FETCH_STARTUP_DATA_ADMIN).equals(true)){
                context.setAttribute(Utils.ROLES_CHANGED, false);
                context.setAttribute(Utils.FETCH_STARTUP_DATA_ADMIN, false);
                List<Role> roles = (List) context.getAttribute(Utils.ROLES);
                results = GSON_INSTANCE.toJson(new RolesDTO(roles));
            }
            // send the updated roles to admin
            resp.getWriter().println(results);
        }
    }
}
