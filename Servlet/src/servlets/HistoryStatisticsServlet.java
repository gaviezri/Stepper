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
        switch (req.getServletPath()){
            case STATISTICS_ENDPOINT:
                System.out.println("a call to 'statistics' endpoint was made...");
                //  handling statistics request
                resp.getWriter().println(handleStatisticsRequest());
                break;
            case HISTORY_ENDPOINT:
                System.out.println("a call to 'history' endpoint was made...");
                // handling history request
                resp.getWriter().println(handleHistoryRequest());
                break;
        }
    }

    private String handleStatisticsRequest(){
        return GSON_INSTANCE.toJson(
                ((EngineController)getServletContext().getAttribute(ENGINE_CONTROLLER))
                .getCurrentLoadedFlowsStatisticsDetails()
        );
    }
    private String handleHistoryRequest(){
        return GSON_INSTANCE.toJson(((EngineController)getServletContext().getAttribute(ENGINE_CONTROLLER))
                .getExecutedFlowsHistoryDetails()
        );
    }
}
