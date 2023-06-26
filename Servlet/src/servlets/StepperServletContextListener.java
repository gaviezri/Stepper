package servlets;

import communication.Role;
import communication.UserSystemInfo;
import communication.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.HashMap;
import java.util.LinkedList;

@WebListener
public class StepperServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // Called when the ServletContext is initialized
        ServletContext servletContext = servletContextEvent.getServletContext();
        synchronized (servletContext) {
        servletContext.setAttribute(Utils.ADMIN_LOGGED_IN,false);
        servletContext.setAttribute(Utils.USERS_IN_SYSTEM, new HashMap<String, UserSystemInfo>());
        servletContext.setAttribute(Utils.COOKIE_2_USER, new HashMap<Integer, String>());
        servletContext.setAttribute(Utils.NEXT_FREE_ID,1);
        servletContext.setAttribute(Utils.ROLES, new LinkedList<Role>());
        servletContext.setAttribute(Utils.ROLES_CHANGED, false);
        servletContext.setAttribute(Utils.FETCH_STARTUP_DATA_ADMIN, true);
        servletContext.setAttribute(Utils.ROLES_MANAGER, new Role.RoleManager());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // Called when the ServletContext is about to be destroyed
        // Perform cleanup tasks here
        // ...
    }
}

