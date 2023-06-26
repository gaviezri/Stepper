package servlets;

import communication.Role;
import communication.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static communication.Role.createRoleListFromJson;
import static communication.Utils.GSON_INSTANCE;

@WebServlet(name = "RolesManagementServlet", urlPatterns = {"/roles"})
public class RolesManagementServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = getServletContext();
        synchronized (context) {
            context.setAttribute(Utils.ROLES_CHANGED, true);
        }
        List<Role> allRoles = (List) context.getAttribute(Utils.ROLES);
        List<Role> newRoles = createRoleListFromJson(Utils.GSON_INSTANCE.fromJson(req.getReader(), List.class));

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
        ServletContext context = getServletContext();
        String results = "[]";
        synchronized (context) {
            if(context.getAttribute(Utils.ROLES_CHANGED).equals(true) ||
                    context.getAttribute(Utils.FETCH_STARTUP_DATA_ADMIN).equals(true)){
                context.setAttribute(Utils.ROLES_CHANGED, false);
                context.setAttribute(Utils.FETCH_STARTUP_DATA_ADMIN, false);
                List<Role> roles = (List) context.getAttribute(Utils.ROLES);
                results = GSON_INSTANCE.toJson(roles);
            }
            // send the updated roles to admin
            resp.getWriter().println(results);
        }
    }
}
