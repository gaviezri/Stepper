package servlets;

import dto.execution.FlowExecutionRequestDTO;
import dto.execution.progress.ExecutedFlowDetailsDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Stack;
import java.util.UUID;
import java.util.Vector;

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
        switch (req.getServletPath()) {
            case FLOW_EXECUTION_PROGRESS_ENDPOINT:
                handleFlowExecutionProgressGet(req, resp);
                break;
        }
    }

    private void handleFlowExecutionProgressGet(HttpServletRequest req, HttpServletResponse resp) {
        Integer cookie = Servlet.idCookieBaker(req.getCookies());
        UUID flowUUID = Servlet.getFlowExecIdStack(cookie).peek();
        ExecutedFlowDetailsDTO executionProgressDTO = Servlet.getEngineController().getExecutedFlowDetailsByUUID(flowUUID);
        // by cookie get the top UUID in the stack
        // get the progress of the flow execution by UUID from engine
        // return the progress to the user using dto
        try {
            resp.getWriter().println(GSON_INSTANCE.toJson(executionProgressDTO));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleFlowExecutionPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Integer cookie = Servlet.idCookieBaker(req.getCookies());
        FlowExecutionRequestDTO flowExecutionRequestDTO = GSON_INSTANCE.fromJson(req.getReader(), FlowExecutionRequestDTO.class);
        UUID flowUUID = Servlet.getEngineController().executeFlow(flowExecutionRequestDTO.getFlowName(), flowExecutionRequestDTO.getValName2ValType());
        synchronized (getServletContext()){
            if ( Servlet.getFlowExecIdStack(cookie) == null){
                Servlet.createNewFlowExecStack(cookie);
            }
            Servlet.getFlowExecIdStack(cookie).push(flowUUID);
            Servlet.getUuid2Cookie().put(flowUUID,cookie);
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }

}
