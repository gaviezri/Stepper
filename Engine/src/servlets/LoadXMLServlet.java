package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stepper.controller.EngineController;
import  dto.flow.LoadDataDTO;

import java.io.IOException;



@WebServlet(name = "LoadXMLServlet", urlPatterns = "/loadXML")
public class LoadXMLServlet extends HttpServlet {
    private EngineController engineController = EngineController.getInstance();

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//      load XML via Input Stream received from request body and
        LoadDataDTO dto = engineController.readXML(req.getInputStream());
//      set response message
        resp.setContentType("application/json");
//        this.getServletContext().getAttribute(Utils.GSON);
//      create json from DTO
        String jsonDTO = Utils.GSON_INSTANCE.toJson(dto);
        resp.getWriter().println(jsonDTO);
        System.out.println(engineController.getFlowDefinitionsNames().getFlowNames());
    }
}

