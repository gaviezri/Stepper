package GUI.body.users.management;

import GUI.body.BodyControllerComponent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class UsersManagementController extends BodyControllerComponent {

    @FXML
    private ListView<?> onlineUsersListView;

    @FXML
    private CheckBox managerCheckbox;

    @FXML
    private Label selectedUserLabel;

    @FXML
    private ListView<?> rolesListView;

    @FXML
    private Button saveChangesButton;

    public void initialize() {

    }

}
