package servlets;

import dto.execution.FlowExecutionRequestDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javafx.util.Pair;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Function;

import static communication.Utils.*;

@WebServlet(name="FlowExecutionServlet",urlPatterns = {FLOW_EXECUTION_ENDPOINT, FLOW_EXECUTION_PROGRESS_ENDPOINT})
public class FlowExecutionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        switch (req.getServletPath()) {
            case FLOW_EXECUTION_ENDPOINT:
                handleFlowExecutionPost(req, resp);
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case FLOW_EXECUTION_PROGRESS_ENDPOINT:
                handleFlowExecutionProgressGet(req, resp);
                break;
        }
    }

    private void handleFlowExecutionProgressGet(HttpServletRequest req, HttpServletResponse resp) {
        // get user cookie
        // by cookie get the top UUID in the stack
        // get the progress of the flow execution by UUID from engine
        // return the progress to the user using dto
    }

    private void handleFlowExecutionPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Integer cookie = Servlet.idCookieBaker(req.getCookies());
        FlowExecutionRequestDTO flowExecutionRequestDTO = GSON_INSTANCE.fromJson(req.getReader(), FlowExecutionRequestDTO.class);
        UUID flowUUID = Servlet.getEngineController().executeFlow(flowExecutionRequestDTO.getFlowInd(), flowExecutionRequestDTO.getValName2ValType());
        synchronized (getServletContext()){
            Servlet.getFlowExecIdStack(cookie).push(flowUUID);
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }

}
