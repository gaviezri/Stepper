package GUI.header;


import communication.AdminRequestsDispatcher;
import GUI.app.AppController;

import GUI.utils.Utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import dto.flow.LoadDataDTO;

import java.io.*;


public class HeaderController  {
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

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
    @FXML
    void BrowseBtnMousePress(MouseEvent event) {
        File chosenFile = getFlowXMLFile();
        if (chosenFile != null) {
            String path = chosenFile.getAbsolutePath();
            StringBuilder sb = StringifyXMLContent(path);
            LoadDataDTO result = AdminRequestsDispatcher.getInstance().loadXML(sb.toString());
            updateGUIonXMLLoadingStatus(path, result);
        }
    }

    private File getFlowXMLFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Flow XML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML Files", "*.xml"));

        File chosenFile = fileChooser.showOpenDialog(this.mainController.getPrimaryStage());
        return chosenFile;
    }

    private static StringBuilder StringifyXMLContent(String path) {
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
        return sb;
    }

    private void updateGUIonXMLLoadingStatus(String path, LoadDataDTO result) {
        if (result != null) {
            if(result.getStatus()){
                loadedXMLPath.set(path);
                Utils.ShowInformation("Success","Flow(s) loaded successfully", result.getMessage());
                mainController.updateFlows();
            } else {
                Utils.ShowError("Error","Error while loading flow(s)", result.getErrorMessage());
            }
        }
    }

}
