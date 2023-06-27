package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javafx.util.Pair;

import java.io.IOException;
import java.util.function.Function;

import static communication.Utils.*;

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

    private void handleFlowExecutionPost(HttpServletRequest req, HttpServletResponse resp) {

        Function<Pair,String> baker = (Function) getServletContext().getAttribute(COOKIE_BAKER);
        String cookie = baker.apply(new Pair<>(req,"ID"));


    }

}
