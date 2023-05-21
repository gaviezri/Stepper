package header;

import app.AppController;
import body.definition.DefinitionController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import stepper.controller.EngineController;
import stepper.dto.flow.LoadDataDTO;

import java.io.*;
import java.nio.file.Paths;

import static utils.Utils.*;


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

    @FXML
    private Menu systemMenu;

    @FXML
    private MenuItem saveSnapshot;

    @FXML
    private MenuItem loadSnapshot;

    @FXML
    private Menu themeMenu;

    @FXML
    private Menu changeThemeMenu;

    @FXML
    private MenuItem themeItem1;

    @FXML
    private MenuItem themeItem2;

    @FXML
    void BrowseBtnMouseEnter(MouseEvent event) {

    }

    @FXML
    void BrowseBtnMouseLeave(MouseEvent event) {

    }

    @FXML
    void BrowseBtnMousePress(MouseEvent event) {
        EngineController  engine = mainController.getEngineController();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Flow XML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML Files", "*.xml"));

        java.io.File chosenFile = fileChooser.showOpenDialog(this.mainController.getPrimaryStage());
        if (chosenFile != null) {
            String path = chosenFile.getAbsolutePath();
            LoadDataDTO ldDTO =  mainController.readXML(path);
            if(ldDTO.getStatus()){
                renderFlowDefinitionData(path);
            } else {
                showLoadErrorModal(ldDTO);
            }
        }
    }

    private void renderFlowDefinitionData(String path) {
        pathTextField.setText(path);
        DefinitionController flowDefController = mainController.getBodyController().getFlowDefinitionController();
        flowDefController.showFlowNames(mainController.getFlowNames());
        // some more logic...
    }

    private static void showLoadErrorModal(LoadDataDTO ldDTO) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error while loading flow(s)");
        alert.setContentText(ldDTO.getErrorMessage());
        alert.showAndWait();
    }

    public void setMainController(AppController appController) {
        this.mainController = appController;
    }


    public void SaveSystemSnapshotPressed(ActionEvent actionEvent) {
        EngineController engineController =  mainController.getEngineController();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Snapshot path");
        String serPath = directoryChooser.showDialog(this.mainController.getPrimaryStage()).getAbsolutePath();
        directoryChooser = null;
        serPath = serPath + "\\stepper.ser";
        FileOutputStream FileOutStream  = null;
        ObjectOutputStream ObjectOutStream = null;
        try {
            FileOutStream = new FileOutputStream(serPath);
            ObjectOutStream = new ObjectOutputStream(FileOutStream);
            engineController.prepareForSerialization();
            ObjectOutStream.writeObject(engineController);
            ObjectOutStream.close();
            FileOutStream.close();
            ShowInformation("Success", "System state saved successfully", "");
        }catch (Exception e) {
            try {
                System.out.println(e.getMessage());
                System.out.println(e.getStackTrace());
                ObjectOutStream.close();
                FileOutStream.close();
                java.nio.file.Files.delete(Paths.get(serPath));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            ShowError("Error", "System state could not be saved!", e.getMessage());
        }
    }

    public void LoadSystemSnapshotPressed(ActionEvent actionEvent) {
        loadEngineController();
        //presentUI
    }

    private void loadEngineController() {
        EngineController engineController =  mainController.getEngineController();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Snapshot");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("SER Files", "*.ser"));
        String serFullPath = fileChooser.showOpenDialog(this.mainController.getPrimaryStage()).getAbsolutePath();

        try {
            FileInputStream FileInStream = new FileInputStream(serFullPath);
            ObjectInputStream ObjectInStream = new ObjectInputStream(FileInStream);
            engineController = (EngineController) ObjectInStream.readObject();
            engineController.wakeUp();
            ObjectInStream.close();
            FileInStream.close();
            ShowInformation("Success", "System state loaded successfully", "");
        }catch (Exception e){
            ShowError("Error", "System state could not be loaded!", e.getMessage());
        }
    }
}
