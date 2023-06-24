package communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Utils {
    // Create a single instance of Gson object
    final static public Gson GSON_INSTANCE = new GsonBuilder().create();
    final static public String ADMIN_LOGGED_IN = "isAdminLoggedIn";
    final static public String USERS_IN_SYSTEM = "usersLoggedInMap";
    final static public String USER_2_COOKIE = "userName2Id";
    final static public String NEXT_FREE_ID = "nextAvailableUserId";
}
