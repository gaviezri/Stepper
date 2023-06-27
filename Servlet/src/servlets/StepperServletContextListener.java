package servlets;

import communication.Role;
import communication.UserSystemInfo;
import communication.Utils;
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
        servletContext.setAttribute(ADMIN_LOGGED_IN,false);
        servletContext.setAttribute(ENGINE_CONTROLLER, EngineController.getInstance());
        servletContext.setAttribute(USERS_IN_SYSTEM, new HashMap<String, UserSystemInfo>());
        servletContext.setAttribute(COOKIE_2_USER, new HashMap<Integer, String>());
        servletContext.setAttribute(NEXT_FREE_ID,1);
        servletContext.setAttribute(ROLES, new LinkedList<Role>());
        servletContext.setAttribute(ROLES_CHANGED, false);
        servletContext.setAttribute(FETCH_STARTUP_DATA_ADMIN, true);
        servletContext.setAttribute(ROLES_MANAGER, new Role.RoleManager());
        servletContext.setAttribute(COOKIE_2_FLOW_EXEC_ID, new HashMap<Integer, LinkedList<UUID>>());

        Function<Pair<HttpServletRequest,String>,Integer> cookieBaker = (pair) -> {
            Cookie[] cookies = pair.getKey().getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(pair.getValue())) {
                    return Integer.parseInt(cookie.getValue());
                }
            }
            return null;
        };

        servletContext.setAttribute(COOKIE_BAKER, cookieBaker);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // Called when the ServletContext is about to be destroyed
        // Perform cleanup tasks here
        // ...
    }
}

