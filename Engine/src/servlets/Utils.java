package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import stepper.controller.EngineController;

public class Utils {
    // Create a single instance of Gson object
    final static public Gson GSON_INSTANCE = new GsonBuilder().create();
    final static public EngineController ENGINE_CONTROLLER_INSTANCE = EngineController.getInstance();
    final static public String ADMIN_LOGGED_IN = "isAdminLoggedIn";
    final static public String USERS_IN_SYSTEM = "usersLoggedInMap";
}
