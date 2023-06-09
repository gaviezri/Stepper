package servlets;

import com.sun.org.apache.xpath.internal.operations.Bool;
import communication.Role;
import communication.UserSystemInfo;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import javafx.util.Pair;
import stepper.controller.EngineController;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import java.util.UUID;
import java.util.function.Function;

import static communication.Utils.*;

@WebListener
public class StepperServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // Called when the ServletContext is initialized
        ServletContext servletContext = servletContextEvent.getServletContext();
        synchronized (servletContext) {
        servletContext.setAttribute(ADMIN_LOGGED_IN, Boolean.FALSE);
        servletContext.setAttribute(ENGINE_CONTROLLER, EngineController.getInstance());
        servletContext.setAttribute(USERS_IN_SYSTEM, new HashMap<String, UserSystemInfo>());
        servletContext.setAttribute(COOKIE_2_USER, new HashMap<Integer, String>());
        servletContext.setAttribute(NEXT_FREE_ID,1);
        servletContext.setAttribute(ROLES, new LinkedList<Role>());
        servletContext.setAttribute(ROLES_CHANGED, Boolean.FALSE);
        servletContext.setAttribute(FETCH_STARTUP_DATA_ADMIN, Boolean.TRUE);
        servletContext.setAttribute(ROLES_MANAGER, new Role.RoleManager());
        servletContext.setAttribute(COOKIE_2_FLOW_EXEC_ID, new HashMap<Integer, Stack<UUID>>());
        Servlet.initialize(servletContext);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // Called when the ServletContext is about to be destroyed
        // Perform cleanup tasks here
        // ...
    }
}

