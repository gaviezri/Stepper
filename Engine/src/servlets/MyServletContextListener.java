package servlets;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class MyServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // Called when the ServletContext is initialized
        ServletContext servletContext = servletContextEvent.getServletContext();

        servletContext.setAttribute(Utils.adminLoggedInStatus,false);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // Called when the ServletContext is about to be destroyed
        // Perform cleanup tasks here
        // ...
    }
}

