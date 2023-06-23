package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import stepper.controller.EngineController;

public class Utils {
    // Create a single instance of Gson object
    final static public Gson gson = new GsonBuilder().create();
    final static public EngineController engineController = EngineController.getInstance();
    final static public String adminLoggedInStatus = "isAdminLoggedIn";

}
