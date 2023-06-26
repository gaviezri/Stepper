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


@WebServlet(name = "LoadXMLServlet", urlPatterns = {"/loadXML", "/flows/names", "/flow/definitions"})
public class FlowLoadAndRetrieveServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("a call to 'loadXML' endpoint was made...");
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
            case "flows/names":
                System.out.println("a call to 'flows/names' endpoint was made...");
                resp.setContentType("application/json");
                resp.getWriter().println(GSON_INSTANCE.toJson(EngineController.getInstance().getFlowDefinitionsNames()));
                break;
            case "/flow/definitions":
                System.out.println("a call to 'flows/definitions' endpoint was made...");
                resp.setContentType("application/json");
                List<FlowDefinitionDTO> allFlowDefinitionsData = EngineController.getInstance().getAllFlowDefinitionsData();
                // need to filter FDs by users role before sending back
                break;

        }
        System.out.println("response sent");
    }
}

