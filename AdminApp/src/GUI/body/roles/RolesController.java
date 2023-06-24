package GUI.body.roles;

import GUI.body.BodyControllerComponent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

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
    private ListView<?> assignedFlowsListView;

    @FXML
    private ListView<?> assignedUsersListView;

}
