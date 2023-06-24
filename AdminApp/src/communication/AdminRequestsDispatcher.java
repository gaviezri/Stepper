package communication;

import dto.execution.history.FlowsExecutionHistoryDTO;
import dto.flow.LoadDataDTO;
import dto.statistics.StatisticsDTO;
import java.net.HttpURLConnection;
import static communication.Utils.GSON_INSTANCE;



public class AdminRequestsDispatcher extends StepperRequestsDispatcher{
    private static final String LOAD_XML = "/loadXML";
    private static final String STATISTICS = "/statistics";
    private static final String HISTORY = "/history";
    private static final String ADMIN_STATUS = "/admin/status";
    private static final String ADMIN_LOGOUT = "/admin/logout";

    private static AdminRequestsDispatcher instance = new AdminRequestsDispatcher();
    public static AdminRequestsDispatcher getInstance() {
        return instance;
    }
    private AdminRequestsDispatcher() {}



    public LoadDataDTO loadXML(String xmlContent){
        try {
            HttpURLConnection con = getConnection(LOAD_XML, "POST", "application/xml");
            con.getOutputStream().write(xmlContent.getBytes("UTF-8"));
            con.getOutputStream().flush();

            LoadDataDTO dto = GSON_INSTANCE.fromJson(getResponse(con), LoadDataDTO.class);
            con.disconnect();
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public StatisticsDTO getStatisticsDTO(){
        try {
            HttpURLConnection con = getConnection(STATISTICS, "GET", "application/json");
            con.getOutputStream().flush();

            StatisticsDTO dto = GSON_INSTANCE.fromJson(getResponse(con), StatisticsDTO.class);
            con.disconnect();
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public FlowsExecutionHistoryDTO getHistoryDTO(){
        try {
            HttpURLConnection con = getConnection(HISTORY, "GET", "application/json");
            con.getOutputStream().flush();

            FlowsExecutionHistoryDTO dto = GSON_INSTANCE.fromJson(getResponse(con), FlowsExecutionHistoryDTO.class);
            con.disconnect();
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean isAdminOnline() {
        try {
            HttpURLConnection con = getConnection(ADMIN_STATUS, "GET", "plain/text");
            con.getOutputStream().flush();
            boolean result = GSON_INSTANCE.fromJson(getResponse(con), Boolean.class);
            con.disconnect();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public void logoutAdmin() {
        try {
            HttpURLConnection con = getConnection(ADMIN_LOGOUT, "GET", "plain/text");
            con.getOutputStream().flush();
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
