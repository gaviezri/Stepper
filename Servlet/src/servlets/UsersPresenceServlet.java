package servlets;

import communication.UserSystemInfo;
import communication.Utils;
import dto.user.system.info.UsersSystemInfoDTO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@WebServlet(name="UsersPresenceServlet", urlPatterns = {"/admin/status","/admin/logout","/user/status","/user/login","/user/logout", "/user/info/all"})
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
        switch (path) {
            case "/admin/status":
                res = handleAdminStatus();
                break;
            case "/admin/logout":
                res = handleAdminLogout();
                break;
            case "/user/status":
                res = handleUserStatus(req);
                break;
            case "/user/info/all":
                res = handleUserInfo(req);
                break;
        }
        resp.getWriter().println(res);
    }

    private String handleUserInfo(HttpServletRequest req) {

        ServletContext context = getServletContext();
        Map<String, UserSystemInfo> name2info = (Map) context.getAttribute(Utils.USERS_IN_SYSTEM);
        List<UserSystemInfo> usersInfo = Arrays.asList(name2info.values().toArray(new UserSystemInfo[0]));
        UsersSystemInfoDTO dto = new UsersSystemInfoDTO(usersInfo);
        return Utils.GSON_INSTANCE.toJson(dto);
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
                        Map<Integer,String> cookies2User = (Map) this.getServletContext().getAttribute(Utils.COOKIE_2_USER); // get users in system ID map
                        cookies2User.put(id, name); // add user
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
                Integer id = Integer.parseInt(Arrays.stream(cookies).filter(x -> x.getName().equals("ID")).findFirst().get().getValue());
                Map<String, UserSystemInfo> usersInfoMap = (Map) this.getServletContext().getAttribute(Utils.USERS_IN_SYSTEM);  // get current logged in users
                Map<Integer, String> usersCookies = (Map) this.getServletContext().getAttribute(Utils.COOKIE_2_USER); // get users in system ID map
                String userName = usersCookies.get(id);
                if (userName != null) {
                   usersInfoMap.remove(userName);
                   usersCookies.remove(id);
                    resp.getWriter().println(String.format("{0} with id {1} was logged out (deleted)", userName, id));
                    System.out.println(String.format("{0} with id {1} was logged out (deleted)", userName, id));
                    return;
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

