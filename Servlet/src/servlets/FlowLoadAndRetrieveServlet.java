package servlets;

import communication.Role;
import communication.UserSystemInfo;
import dto.flow.FlowDefinitionDTO;
import dto.flow.LoadDataDTO;
import dto.flow.ManyFlowDefinitionsDTO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javafx.util.Pair;
import stepper.controller.EngineController;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static communication.Utils.*;

@WebServlet(name = "LoadXMLServlet", urlPatterns = {LOAD_XML_ENDPOINT, FLOW_NAMES_ENDPOINT, FLOW_DEFINITIONS_ENDPOINT})
public class FlowLoadAndRetrieveServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//      load XML via Input Stream received from request body
        /** each servlet is a singleton and this is the ONLY servlet that make changes to the flow library data structure and therefore there is no need for synchronized */
        LoadDataDTO loadDataDTO = Servlet.getEngineController().readXML(req.getInputStream());
//      set response message
        resp.setContentType(JSON_CONTENT_TYPE);
//        this.getServletContext().getAttribute(Utils.GSON);
//      create json from DTO
        String jsonDTO = GSON_INSTANCE.toJson(loadDataDTO);
        resp.getWriter().println(jsonDTO);
        System.out.println("XML loaded successfully");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch(req.getServletPath()){
            case FLOW_NAMES_ENDPOINT:
                resp.setContentType(JSON_CONTENT_TYPE);
                resp.getWriter().println(GSON_INSTANCE.toJson(Servlet.getEngineController()
                                                                .getFlowDefinitionsNames()));
                break;
            case FLOW_DEFINITIONS_ENDPOINT:
                resp.setContentType(JSON_CONTENT_TYPE);
                resp.getWriter().println(GSON_INSTANCE.toJson(getUserSpecificFilteredFlowDefinitionDTOS(req)));
                break;

        }
    }

    private ManyFlowDefinitionsDTO getUserSpecificFilteredFlowDefinitionDTOS(HttpServletRequest req) {
        ServletContext context = getServletContext();
        // get cookie getter function from Context
        Function<Pair<HttpServletRequest,String>,Integer> cookieBaker = Servlet.getCookieBaker();
        Integer userCookie = cookieBaker.apply(new Pair(req,"ID"));
        // get current user info by cookie id
        String userName = Servlet.getCookie2User().get(userCookie);
        UserSystemInfo userInfo = Servlet.getUserName2Info().get(userName);
        // get current users assigned roles
        List<Role> userRoles = userInfo.getRoles();
        // create current users accessible flow names set
        Set<String> userAccessibleFlows = new HashSet<>();
        for (Role role: userRoles) {
            userAccessibleFlows.addAll(role.getFlows());
        }
        // get flows definitions according to accessible flows of user (if user is manager bring all flows.)
        EngineController engineInstance = Servlet.getEngineController();
        List<FlowDefinitionDTO> flowDefinitionDTOS = filterFlowDefinitionsByUsersAccessLevel(userInfo, userAccessibleFlows, engineInstance);
        return new ManyFlowDefinitionsDTO(flowDefinitionDTOS);
    }

    private static List<FlowDefinitionDTO> filterFlowDefinitionsByUsersAccessLevel(UserSystemInfo userInfo, Set<String> userAccessibleFlows, EngineController engineInstance) {
        List<FlowDefinitionDTO> flowDefinitionDTOS = userInfo.isManager() ?
                engineInstance.getAllFlowDefinitionsData() :
                engineInstance.getAllFlowDefinitionsData().stream().
                        filter(x -> userAccessibleFlows.contains(x.getFlowName())).collect(Collectors.toList());
        return flowDefinitionDTOS;
    }
}

