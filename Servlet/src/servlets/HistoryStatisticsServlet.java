package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stepper.controller.EngineController;

import static communication.Utils.*;

import java.io.IOException;
import static servlets.HistoryStatisticsServlet.*;

@WebServlet(name="HistoryStatisticsServlet",urlPatterns = {STATISTICS_ENDPOINT,HISTORY_ENDPOINT})
public class HistoryStatisticsServlet extends HttpServlet {

    @Override
    final protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
            if (path.equals(STATISTICS_ENDPOINT)) {
                System.out.println("a call to 'statistics' endpoint was made...");
                //  handling statistics request
                resp.getWriter().println(handleStatisticsRequest());
        } else if (path.equals(HISTORY_ENDPOINT)) {
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
        return GSON_INSTANCE.toJson(EngineController.getInstance().getCurrentLoadedFlowsStatisticsDetails());
    }
    private String handleHistoryRequest(){
        return GSON_INSTANCE.toJson(EngineController.getInstance().getExecutedFlowsHistoryDetails());
    }
}
