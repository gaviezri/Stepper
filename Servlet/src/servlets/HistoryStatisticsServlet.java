package servlets;


import communication.UserSystemInfo;
import dto.execution.history.SingleFlowExecutionDTO;
import dto.statistics.StatisticsDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import static communication.Utils.*;

import java.io.IOException;
import java.util.List;

import java.util.Set;
import java.util.stream.Collectors;

import static dto.execution.history.FlowsExecutionHistoryDTO.SortFilter;


@WebServlet(name="HistoryStatisticsServlet",urlPatterns = {STATISTICS_ENDPOINT,HISTORY_ENDPOINT, FILTER_HISTORY_FILTER_ENDPOINT})
public class HistoryStatisticsServlet extends HttpServlet {

    @Override
    final protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()){
            case STATISTICS_ENDPOINT:
                //  handling statistics request
                resp.getWriter().println(handleStatisticsRequest());
                break;
            case HISTORY_ENDPOINT:
                // handling history request
                resp.getWriter().println(handleHistoryRequest(req.getCookies()));
                break;
            case FILTER_HISTORY_FILTER_ENDPOINT:
                filterHistoryByParam(SortFilter.valueOf(req.getParameter(HISTORY_FILTER)));
        }
    }

    private String handleStatisticsRequest(){
        StatisticsDTO allCurrentLoadedFlowsStatisticsDetails = Servlet.getEngineController().getCurrentLoadedFlowsStatisticsDetails();
        return GSON_INSTANCE.toJson(allCurrentLoadedFlowsStatisticsDetails);
    }
    private String handleHistoryRequest(Cookie[] cookies){
        List<SingleFlowExecutionDTO> accessibleFlowsHistoryDetails = Servlet.getEngineController().getExecutedFlowsHistoryDetails().getFlowExecutionDTOs();;
        UserSystemInfo userInfo = Servlet.getUserSystemInfo(cookies);

        if(!(Servlet.isAdmin(cookies) || userInfo.isManager())){
            accessibleFlowsHistoryDetails = accessibleFlowsHistoryDetails.stream().
                    filter(x->Servlet.getFlowExecIdStack(Servlet.idCookieBaker(cookies)).
                            contains(x.getUniqueId())).collect(Collectors.toList());
        }

        return GSON_INSTANCE.toJson(accessibleFlowsHistoryDetails);
    }

    private void filterHistoryByParam(SortFilter filter){
        Servlet.getEngineController().getExecutedFlowsHistoryDetails().sortFlowExecutionDTOsBy(filter);
    }
}
