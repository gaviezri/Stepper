package Communication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class UserRequestsDispatcher extends StepperRequestsDispatcher{
    private static final String USER_LOGOUT = "/user/logout";

    private static UserRequestsDispatcher instance = new UserRequestsDispatcher();
    public static UserRequestsDispatcher getInstance() {
        return instance;
    }
    private UserRequestsDispatcher() {}


    public boolean logout(){
        try {
            HttpURLConnection con = getConnection(USER_LOGOUT, "GET", "application/json");
            con.getOutputStream().flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            boolean status = Boolean.parseBoolean(sb.toString());
            con.disconnect();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
