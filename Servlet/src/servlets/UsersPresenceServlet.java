package servlets;

import communication.UserSystemInfo;
import communication.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@WebServlet(name="UsersPresenceServlet", urlPatterns = {"/admin/status","/admin/logout","/user/status","/user/login","/user/logout"})
public class UsersPresenceServlet extends HttpServlet {
    /**
     * This servlet is responsible for:
     * 1. /admin/status -> returns the current admin login status, if no admin is logged it updates it to true
     * 2. /admin/logout -> sets the admin login status to false
     * 3. /user/status  ->
     * */
    final protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        String res = null;
        if (path.equals("/admin/status")) {
            res = handleAdminStatus();
        } else if(path.equals("/admin/logout")) {
            res = handleAdminLogout();
        } else if(path.equals("/user/status")){ //check if user logged in
            // fetch (query) parameter from the request
            res = handleUserStatus(req.getParameter("name"));
        }
        resp.getWriter().println(res);
    }

    private String handleAdminLogout() {
        String res;
        ServletContext context = getServletContext();
        synchronized (context) {
            context.setAttribute(Utils.FETCH_STARTUP_DATA_ADMIN, true);
            context.setAttribute(Utils.ADMIN_LOGGED_IN, false);
        }
        res = "false";
        return res;
    }

    final protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if(path.equals("/user/login")) {
            String name = req.getParameter("name");
            Integer id = -1;
            if (name != null) {
                Map<String,UserSystemInfo> usersInfoMap =  (Map) this.getServletContext().getAttribute(Utils.USERS_IN_SYSTEM);  // get current logged in users
                if(!usersInfoMap.containsKey(name)) {  // if given name is not already logged in
                    UserSystemInfo userSystemInfo = new UserSystemInfo(name);  // create new user with given name and default values
                    synchronized (this.getServletContext()) {
                        usersInfoMap.put(name, userSystemInfo); // add new user
                        id = (Integer) this.getServletContext().getAttribute(Utils.NEXT_FREE_ID); // get the next available ID
                        Map<String,Integer> usersCookies = (Map) this.getServletContext().getAttribute(Utils.USER_2_COOKIE); // get users in system ID map
                        usersCookies.put(name, id); // add user
                        this.getServletContext().setAttribute(Utils.NEXT_FREE_ID, id + 1); // update next available ID
                    }
                    System.out.println(String.format("New user with \"{0}\" with id \"{1}\" was logged in...",name,id));
                }
                else {
                    System.out.println(String.format("Something went wrong probably user with name {0} already logged in",name));
                }
            }
            else{
                System.out.println("Something went wrong... no name was given");
            }
            resp.getWriter().println(id);
        }
    }

    final protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if(path.equals("/user/logout")) {
            // Retrieve cookies from the request
            Cookie[] cookies = req.getCookies();
            if(cookies != null) {
                String nameToDelete;
                Integer id = Integer.parseInt(Arrays.stream(cookies).filter(x -> x.getName().equals("ID")).findFirst().get().getValue());
                Map<String, UserSystemInfo> usersInfoMap = (Map) this.getServletContext().getAttribute(Utils.USERS_IN_SYSTEM);  // get current logged in users
                Map<String, Integer> usersCookies = (Map) this.getServletContext().getAttribute(Utils.USER_2_COOKIE); // get users in system ID map
                for (Map.Entry<String, Integer> entry : usersCookies.entrySet()) {  // for all users in
                    if (entry.getValue().equals(id)) {
                        nameToDelete = entry.getKey();
                        usersCookies.remove(nameToDelete);
                        usersInfoMap.remove(nameToDelete);
                        resp.getWriter().println(String.format("{0} with id {1} was logged out (deleted)", nameToDelete, id));
                        System.out.println(String.format("{0} with id {1} was logged out (deleted)", nameToDelete, id));
                        return;
                    }
                }
                resp.getWriter().println(String.format("user with id {0} was not found in system", id));
                System.out.println(String.format("user with id {0} was not found in system", id));
            }
            resp.getWriter().println(String.format("no cookie found... please login first"));
            System.out.println(String.format("no cookie found... please login first"));
        }
    }

    private String handleUserStatus(String name) {
        if(name != null) {
            Map usersInSystemInfo = (Map) this.getServletContext().getAttribute(Utils.USERS_IN_SYSTEM);
            return Utils.GSON_INSTANCE.toJson(usersInSystemInfo.containsKey(name));
        }
        return null; //=name
    }

    private String handleAdminStatus() {
        String res;
        Boolean isAdminLoggedIn = (Boolean) this.getServletContext().getAttribute(Utils.ADMIN_LOGGED_IN);
        res = isAdminLoggedIn.toString();

        if(!isAdminLoggedIn){
            ServletContext context = getServletContext();
            synchronized (context) {
                context.setAttribute(Utils.ADMIN_LOGGED_IN, true);
                context.setAttribute(Utils.FETCH_STARTUP_DATA_ADMIN, true);
            }
        }
        return res;
    }
}

