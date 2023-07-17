package servlets;

import com.sun.org.apache.regexp.internal.RE;
import communication.Role;
import communication.UserSystemInfo;
import dto.flow.FlowDefinitionDTO;
import dto.flow.LoadDataDTO;
import dto.flow.ManyFlowDefinitionsDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stepper.controller.EngineController;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static communication.Utils.*;

@WebServlet(name = "LoadXMLServlet", urlPatterns = {LOAD_XML_ENDPOINT, FLOW_NAMES_ENDPOINT, FLOW_DEFINITIONS_ENDPOINT})
public class FlowLoadAndRetrieveServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//      load XML via Input Stream received from request body
        Servlet.userCheckIn(req);
        EngineController engineController = Servlet.getEngineController();
        Role.RoleManager roleManager = Servlet.getRoleManager();

        LoadDataDTO loadDataDTO = engineController.readXML(req.getInputStream());

        roleManager.AssignFlowsToRole(ALL_FLOWS, engineController.getFlowDefinitionsNames(false).getFlowNames());
        roleManager.AssignFlowsToRole(READ_ONLY_FLOWS, engineController.getFlowDefinitionsNames(true).getFlowNames());
        roleManager.addRolesToContext(Servlet.getRoles());
//      Servlet.setRolesChanged(true);
        resp.setContentType(JSON_CONTENT_TYPE);

        String jsonDTO = GSON_INSTANCE.toJson(loadDataDTO);
        resp.getWriter().println(jsonDTO);
        System.out.println("XML loaded successfully");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Servlet.userCheckIn(req);
        switch(req.getServletPath()){
            case FLOW_NAMES_ENDPOINT:
                resp.setContentType(JSON_CONTENT_TYPE);
                resp.getWriter().println(GSON_INSTANCE.toJson(Servlet.getEngineController()
                                                                .getFlowDefinitionsNames(false)));
                break;
            case FLOW_DEFINITIONS_ENDPOINT:
                resp.setContentType(JSON_CONTENT_TYPE);
                resp.getWriter().println(GSON_INSTANCE.toJson(getUserSpecificFilteredFlowDefinitionDTOS(req)));
                break;

        }
    }

    private ManyFlowDefinitionsDTO getUserSpecificFilteredFlowDefinitionDTOS(HttpServletRequest req) {
        UserSystemInfo userInfo = Servlet.getUserSystemInfo(req.getCookies());
        // get flows definitions according to accessible flows of user (if user is manager bring all flows.)
        List<FlowDefinitionDTO> flowDefinitionDTOS = filterFlowDefinitionsByUsersAccessLevel(
                userInfo,
                Servlet.getUserAccessibleFlowNames(userInfo));
        return new ManyFlowDefinitionsDTO(flowDefinitionDTOS);
    }

    private static List<FlowDefinitionDTO> filterFlowDefinitionsByUsersAccessLevel(UserSystemInfo userInfo, Set<String> userAccessibleFlows) {
        List<FlowDefinitionDTO> flowDefinitionDTOS = userInfo.isManager() ? 
                Servlet.getEngineController().getAllFlowDefinitionsData() :
                filterFlowAndContinuationsByRolesAssigned(userAccessibleFlows);
        return flowDefinitionDTOS;
    }

    private static List<FlowDefinitionDTO> filterFlowAndContinuationsByRolesAssigned(Set<String> userAccessibleFlows) {
        List<FlowDefinitionDTO> filteredFlows = filterFlows(userAccessibleFlows);
        return filterContinuations(userAccessibleFlows, filteredFlows);
    }

    private static List<FlowDefinitionDTO> filterContinuations(Set<String> userAccessibleFlows, List<FlowDefinitionDTO> filteredFlows) {
        for ( FlowDefinitionDTO flowDefinitionDTO : filteredFlows) {
            List<String> continuations = flowDefinitionDTO.getContinuationFlowNames();
            for (String continuation : continuations) {
                if (!userAccessibleFlows.contains(continuation)) {
                    flowDefinitionDTO.deleteContinuation(continuation);
                }
            }
       }
        return filteredFlows;
    }

    private static List<FlowDefinitionDTO> filterFlows(Set<String> userAccessibleFlows) {
        return Servlet.getEngineController().getAllFlowDefinitionsData().stream().
                filter(x -> userAccessibleFlows.contains(x.getFlowName())).map(FlowDefinitionDTO::clone).collect(Collectors.toList());
    }
}

