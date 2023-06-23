package GUI.header;


import GUI.app.AppController;

import GUI.utils.Utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import Communication.StepperRequestsDispatcher;
import java.io.*;
import java.nio.file.Paths;
import java.util.Map;


public class HeaderController {
    private AppController mainController;

    @FXML
    private GridPane topBanner;

    @FXML
    private FlowPane fileSelectionPane;

    @FXML
    private TextField pathTextField;

    @FXML
    private Button browseBtn;



    private final StringProperty loadedXMLPath = new SimpleStringProperty();


    public void initialize() {
        pathTextField.textProperty().bind(loadedXMLPath);
    }


    @FXML
    void BrowseBtnMousePress(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Flow XML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML Files", "*.xml"));

        File chosenFile = fileChooser.showOpenDialog(this.mainController.getPrimaryStage());
        if (chosenFile != null) {
            String path = chosenFile.getAbsolutePath();
            File selectedFile = new File(path);
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))){
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }catch (Exception e){
                Utils.ShowError("Error","Error opening xml", e.getMessage());
            }


            Map result = StepperRequestsDispatcher.getInstance().loadXML(sb.toString());
            if (result != null) {
                if(result.get("valid").equals(true)){
                    loadedXMLPath.set(path);
                } else {
                    Utils.ShowError("Error","Error while loading flow(s)", (String)result.get("error"));
                }
            }
        }
    }

}
