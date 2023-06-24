package GUI.body.roles;

import GUI.body.BodyControllerComponent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.util.List;

public class RolesController extends BodyControllerComponent {

    @FXML
    private AnchorPane rolesAnchorPane;

    @FXML
    private ListView<?> availableRolesListView;

    @FXML
    private Button newRoleButton;

    @FXML
    private Button saveChangesButton;

    @FXML
    private Label chosenRoleLabel;

    @FXML
    private Label chosenLabelDescriptionLabel;

    @FXML
    private ListView<String> assignedFlowsListView;

    @FXML
    private ListView<String> assignedUsersListView;

    public void initialize() {


    }
    public void updateFlowNames(List<String> flowNames) {
        ObservableList obFlowNames = FXCollections.observableArrayList(flowNames);
        assignedFlowsListView.setItems(obFlowNames);
        assignedFlowsListView.setCellFactory(CheckBoxListCell.forListView(item -> {
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                // Handle checkbox selection/deselection here
                System.out.println("Selected: " + item + ", Checked: " + newValue);
            });
            return checkBox.selectedProperty();
        }));


    }
}
