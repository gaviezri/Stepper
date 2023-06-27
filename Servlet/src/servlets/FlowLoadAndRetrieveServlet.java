package servlets;

import communication.Role;
import communication.UserSystemInfo;
import dto.flow.FlowDefinitionDTO;
import dto.flow.LoadDataDTO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javafx.util.Pair;
import stepper.controller.EngineController;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static communication.Utils.*;

@WebServlet(name = "LoadXMLServlet", urlPatterns = {LOAD_XML_ENDPOINT, FLOW_NAMES_ENDPOINT, FLOW_DEFINITIONS_ENDPOINT})
public class FlowLoadAndRetrieveServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//      load XML via Input Stream received from request body
        /** each servlet is a singleton and this is the ONLY servlet that make changes to the flow library data structure and therefore there is no need for synchronized */
        LoadDataDTO loadDataDTO = EngineController.getInstance().readXML(req.getInputStream());
//      set response message
        resp.setContentType(JSON_CONTENT_TYPE);
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
                resp.setContentType(JSON_CONTENT_TYPE);
                resp.getWriter().println(GSON_INSTANCE.toJson(EngineController.getInstance().getFlowDefinitionsNames()));
                break;
            case FLOW_DEFINITIONS_ENDPOINT:
                System.out.println("a call to 'flows/definitions' endpoint was made...");
                resp.setContentType(JSON_CONTENT_TYPE);

                ServletContext context = getServletContext();

                Function<Pair<HttpServletRequest,String>,String> cookieBaker =  (Function<Pair<HttpServletRequest,String>,String>)context.getAttribute(COOKIE_BAKER);
                Integer userCookie = Integer.valueOf(cookieBaker.apply(new Pair(req,"ID")));

                UserSystemInfo userInfo = ((Map<Integer,UserSystemInfo>) context.getAttribute(COOKIE_2_USER)).get(userCookie);

                List<Role> userRoles = userInfo.getRoles();

                Set<String> userAccessibleFlows = new HashSet<String>();

                for (Role role: userRoles) {
                    userAccessibleFlows.addAll(role.getFlows());
                }


                break;

        }

        System.out.println("response sent");
    }
}

