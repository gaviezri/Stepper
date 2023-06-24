package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@WebServlet(name="UsersPresenceServlet", urlPatterns = {"/admin/status","/admin/logout","/user/status"})
public class UsersPresenceServlet extends HttpServlet {
    /**
     * This servlet is responsible for:
     * 1. /admin/status -> returns the current admin login status, if no admin is logged it updates it to true
     * 2. /admin/logout -> sets the admin login status to false
     * 3.
     * */
    final protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        String res = "false";
        if (path.equals("/admin/status")) {
            res = handleAdminStatus();
        } else if(path.equals("/admin/logout")) {
            this.getServletContext().setAttribute(Utils.ADMIN_LOGGED_IN,false);
        } else if(path.equals("/user/status")){
            // fetch (query) parameter from the request
            String name = req.getParameter("name");
            Object userInfo= this.getServletContext().getAttribute(name);
            if(userInfo == null){

            }
        }
        resp.getWriter().println(res);
    }

    private String handleAdminStatus() {
        String res;
        Boolean isAdminLoggedIn = (Boolean) this.getServletContext().getAttribute(Utils.ADMIN_LOGGED_IN);
        res = isAdminLoggedIn.toString();

        if(!isAdminLoggedIn){
            this.getServletContext().setAttribute(Utils.ADMIN_LOGGED_IN,true);
        }
        return res;
    }
    private void addUserToSystem(String name){
        HashMap<String, ArrayList<String>> user = new HashMap<>();
        ArrayList<String> userInfo = new ArrayList<>();
//        userInfo.

        this.getServletContext().setAttribute(Utils.USERS_IN_SYSTEM,name);
    }
}


//public class UserSystemInfo{
//
//}