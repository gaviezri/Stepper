package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name="UsersPresenceServlet", urlPatterns = {"/admin/status","/admin/logout","/user"})
public class UsersPresenceServlet extends HttpServlet {
    final protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        String res = null;
        if (path.equals("/admin/status")) {
            Boolean isAdminLoggedIn = (Boolean) this.getServletContext().getAttribute(Utils.adminLoggedInStatus);
            res = isAdminLoggedIn.toString();

            if(!isAdminLoggedIn){
                this.getServletContext().setAttribute(Utils.adminLoggedInStatus,true);
            }
        } else if(path.equals("/admin/logout")) {
            this.getServletContext().setAttribute(Utils.adminLoggedInStatus,false);
        } else if(path.equals("/user")){

        }
        resp.getWriter().println(res);
    }
}
