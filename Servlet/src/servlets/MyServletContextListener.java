package servlets;

import communication.UserSystemInfo;
import communication.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.HashMap;

@WebListener
public class MyServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // Called when the ServletContext is initialized
        ServletContext servletContext = servletContextEvent.getServletContext();

        servletContext.setAttribute(Utils.ADMIN_LOGGED_IN,false);
        servletContext.setAttribute(Utils.USERS_IN_SYSTEM, new HashMap<String, UserSystemInfo>());
        servletContext.setAttribute(Utils.USER_2_COOKIE, new HashMap<String, Integer>());
        servletContext.setAttribute(Utils.NEXT_FREE_ID,0);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // Called when the ServletContext is about to be destroyed
        // Perform cleanup tasks here
        // ...
    }
}

