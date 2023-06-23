import Communication.StepperRequestsDispatcher;
import GUI.utils.Utils;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Map;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        StringBuilder sb = new StringBuilder();
        Map result = StepperRequestsDispatcher.getInstance().loadXML(sb.toString());
        if (result != null) {
            if(result.get("valid").equals(true)){

            } else {
                Utils.ShowError("Error","Error while loading flow(s)", (String)result.get("error"));
            }
        }
    }
}