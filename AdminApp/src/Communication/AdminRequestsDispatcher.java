package Communication;

import stepper.dto.execution.history.FlowsExecutionHistoryDTO;
import stepper.dto.flow.ExecutedFlowDetailsDTO;
import stepper.dto.flow.LoadDataDTO;
import stepper.dto.statistics.StatisticsDTO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static servlets.Utils.gson;

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

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            LoadDataDTO dto = gson.fromJson(sb.toString(), LoadDataDTO.class);
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

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            StatisticsDTO dto = gson.fromJson(sb.toString(), StatisticsDTO.class);
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

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            FlowsExecutionHistoryDTO dto = gson.fromJson(sb.toString(), FlowsExecutionHistoryDTO.class);
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

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            boolean result = gson.fromJson(sb.toString(), Boolean.class);
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
