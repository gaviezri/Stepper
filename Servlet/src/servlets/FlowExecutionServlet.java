package servlets;

import dto.flow.FlowExecutionRequestDTO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javafx.util.Pair;
import stepper.controller.EngineController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static communication.Utils.*;
import static javax.swing.text.html.CSS.getAttribute;

@WebServlet(name="FlowExecutionServlet",urlPatterns = {FLOW_EXECUTION_ENDPOINT})
public class FlowExecutionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        switch (req.getServletPath()) {
            case FLOW_EXECUTION_ENDPOINT:
                handleFlowExecutionPost(req, resp);
                break;
        }
    }

    private void handleFlowExecutionPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        ServletContext context = getServletContext();
        // get user cookie
        Function<Pair,Integer> baker = (Function) context.getAttribute(COOKIE_BAKER);
        Integer cookie = baker.apply(new Pair<>(req,"ID"));

        // execute flow and get UUID from engineController
        EngineController engineController = (EngineController)context.getAttribute(ENGINE_CONTROLLER);
        FlowExecutionRequestDTO flowExecutionRequestDTO = GSON_INSTANCE.fromJson(req.getReader(),FlowExecutionRequestDTO.class);
        UUID fUUID = engineController.executeFlow(flowExecutionRequestDTO.getFlowInd(),flowExecutionRequestDTO.getValName2ValType());

        //  store UUID in users' UUID map
        synchronized (context) {
            Map<Integer, List<UUID>> cookie2uuid = (Map<Integer, List<UUID>>) context.getAttribute(COOKIE_2_FLOW_EXEC_ID);
            cookie2uuid.get(cookie).add(fUUID);
        }
    }

}
