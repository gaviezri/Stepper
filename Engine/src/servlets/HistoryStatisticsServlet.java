package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name="HistoryStatisticsServlet",urlPatterns = {"/statistics","/history"})
public class HistoryStatisticsServlet extends HttpServlet {
    @Override
    final protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
            if (path.equals("/statistics")) {
                System.out.println("a call to 'statistics' endpoint was made...");
                //  handling statistics request
                resp.getWriter().println(handleStatisticsRequest());
        } else if (path.equals("/history")) {
                System.out.println("a call to 'history' endpoint was made...");
            // handling history request
                resp.getWriter().println(handleHistoryRequest());
        } else {
        // Handle unknown endpoints
            resp.getWriter().println("Invalid endpoint");
        }
    }

    private String handleStatisticsRequest(){
//        get statistics DTO and turn it to Json
        return Utils.GSON_INSTANCE.toJson(Utils.ENGINE_CONTROLLER_INSTANCE.getCurrentLoadedFlowsStatisticsDetails());
    }
    private String handleHistoryRequest(){
        return Utils.GSON_INSTANCE.toJson(Utils.ENGINE_CONTROLLER_INSTANCE.getExecutedFlowsHistoryDetails());
    }
}
