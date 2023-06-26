package servlets;

import dto.flow.FlowDefinitionDTO;
import dto.flow.LoadDataDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stepper.controller.EngineController;


import java.io.IOException;
import java.util.List;


import static communication.Utils.GSON_INSTANCE;
import static servlets.FlowLoadAndRetrieveServlet.*;


@WebServlet(name = "LoadXMLServlet", urlPatterns = {FlowLoadAndRetrieveServlet.LOAD_XML_ENDPOINT, FLOW_NAMES_ENDPOINT, FLOW_DEFINITIONS_ENDPOINT})
public class FlowLoadAndRetrieveServlet extends HttpServlet {
    static final  String LOAD_XML_ENDPOINT = "/loadXML";
    static final String FLOW_NAMES_ENDPOINT = "/flow/names";
    static final String FLOW_DEFINITIONS_ENDPOINT = "/flow/definitions";
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//      load XML via Input Stream received from request body
        /** each servlet is a singleton and this is the ONLY servlet that make changes to the flow library data structure and therefore there is no need for synchronized */
        LoadDataDTO loadDataDTO = EngineController.getInstance().readXML(req.getInputStream());
//      set response message
        resp.setContentType("application/json");
//        this.getServletContext().getAttribute(Utils.GSON);
//      create json from DTO
        String jsonDTO = GSON_INSTANCE.toJson(loadDataDTO);
        resp.getWriter().println(jsonDTO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch(req.getServletPath()){
            case FLOW_NAMES_ENDPOINT:
                System.out.println("a call to 'flows/names' endpoint was made...");
                resp.setContentType("application/json");
                resp.getWriter().println(GSON_INSTANCE.toJson(EngineController.getInstance().getFlowDefinitionsNames()));
                break;
            case FLOW_DEFINITIONS_ENDPOINT:
                System.out.println("a call to 'flows/definitions' endpoint was made...");
                resp.setContentType("application/json");
                List<FlowDefinitionDTO> allFlowDefinitionsData = EngineController.getInstance().getAllFlowDefinitionsData();
                // need to filter FDs by users role before sending back
                break;

        }

        System.out.println("response sent");
    }
}

