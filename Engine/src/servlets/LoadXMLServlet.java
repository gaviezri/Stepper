package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stepper.controller.EngineController;
import  dto.flow.LoadDataDTO;


import java.io.IOException;

import static servlets.Utils.ENGINE_CONTROLLER_INSTANCE;


@WebServlet(name = "LoadXMLServlet", urlPatterns = "/loadXML")
public class LoadXMLServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("a call to 'loadXML' endpoint was made...");
//      load XML via Input Stream received from request body
        /** each servlet is a singleton and this is the ONLY servlet that make changes to the flow library data structure and therefore there is no need for synchronized */
        LoadDataDTO loadDataDTO = ENGINE_CONTROLLER_INSTANCE.readXML(req.getInputStream());
//      set response message
        resp.setContentType("application/json");
//        this.getServletContext().getAttribute(Utils.GSON);
//      create json from DTO
        String jsonDTO = Utils.GSON_INSTANCE.toJson(loadDataDTO);
        resp.getWriter().println(jsonDTO);
    }
}

