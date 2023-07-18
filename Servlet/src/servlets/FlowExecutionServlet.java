package servlets;

import dto.execution.FlowExecutionRequestDTO;
import dto.execution.progress.ExecutedFlowDetailsDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

import static communication.Utils.*;

@WebServlet(name="FlowExecutionServlet",urlPatterns = {FLOW_EXECUTION_ENDPOINT, FLOW_EXECUTION_PROGRESS_ENDPOINT})
public class FlowExecutionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        Servlet.userCheckIn(req);
        if (req.getServletPath().equals(FLOW_EXECUTION_ENDPOINT)) {
            handleFlowExecutionPost(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
        Servlet.userCheckIn(req);
        if (req.getServletPath().equals(FLOW_EXECUTION_PROGRESS_ENDPOINT)) {
            handleFlowExecutionProgressGet(req, resp);
        }
    }

    private void handleFlowExecutionProgressGet(HttpServletRequest req, HttpServletResponse resp) {
        Integer cookie = Servlet.idCookieBaker(req.getCookies());
        String userName = Servlet.getCookie2User().get(cookie);
        UUID flowUUID = Servlet.getFlowExecIdStack(userName).peek();
        ExecutedFlowDetailsDTO executionProgressDTO = Servlet.getEngineController().getExecutedFlowDetailsByUUID(flowUUID);

        try {
            resp.getWriter().println(GSON_INSTANCE.toJson(executionProgressDTO));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        // by cookie get the top UUID in the stack
        // get the progress of the flow execution by UUID from engine
        // return the progress to the user using dto
    }

    private void handleFlowExecutionPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Integer cookie = Servlet.idCookieBaker(req.getCookies());
        String userName = Servlet.getCookie2User().get(cookie);
        FlowExecutionRequestDTO flowExecutionRequestDTO = GSON_INSTANCE.fromJson(req.getReader(), FlowExecutionRequestDTO.class);
        UUID flowUUID = Servlet.getEngineController().executeFlow(flowExecutionRequestDTO.getFlowName(), flowExecutionRequestDTO.getValName2ValType());
        synchronized (getServletContext()){
            if ( Servlet.getFlowExecIdStack(userName) == null){
                Servlet.createNewFlowExecStack(userName);
            }
            Servlet.getFlowExecIdStack(userName).push(flowUUID);
            Servlet.getUuid2User().put(flowUUID,userName);
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }

}
