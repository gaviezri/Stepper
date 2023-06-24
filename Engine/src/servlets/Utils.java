package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utils {
    // Create a single instance of Gson object
    final static public Gson GSON_INSTANCE = new GsonBuilder().create();
}
