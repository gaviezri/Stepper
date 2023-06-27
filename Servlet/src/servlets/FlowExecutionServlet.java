package servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import static communication.Utils.*;
@WebServlet(name="FlowExecutionServlet",urlPatterns = {FLOW_EXECUTION_ENDPOINT})
public class FlowExecutionServlet extends HttpServlet {

}
