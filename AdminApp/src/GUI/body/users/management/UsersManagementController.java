package GUI.body.users.management;

import GUI.body.BodyControllerComponent;
import communication.Role;
import communication.UserSystemInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

public class UsersManagementController extends BodyControllerComponent {

    @FXML
    private ListView<UserSystemInfo> onlineUsersListView;

    @FXML
    private CheckBox managerCheckbox;

    @FXML
    private Label selectedUserLabel;

    @FXML
    private ListView<String> rolesListView;

    @FXML
    private Button saveChangesButton;

    private boolean changesMade;
    public void initialize() {
        changesMade = false;
        initializeIsManagerCheckBox();
        initializeOnlineUsersListView();

    }

    private void initializeOnlineUsersListView() {
        onlineUsersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedUserLabel.setText(newValue.getName());
                managerCheckbox.setSelected(newValue.isManager());
                List<Role> rolesList = bodyController.getRoleManager()
                        .getRolesListFromCumulativeRoleValue(newValue.getRoles());

                rolesListView.setItems(rolesList
                        .stream()
                        .map(x->x.getName())
                        .collect(java.util.stream.Collectors.toCollection(FXCollections::observableArrayList)));
            }
        });
    }

    private void initializeIsManagerCheckBox() {

    }

    public void updateOnlineUsers(List<UserSystemInfo> userSystemInfos) {
        ObservableList<UserSystemInfo> usiInListView =  onlineUsersListView.getItems();
        UserSystemInfo selectedUsi = onlineUsersListView.getSelectionModel().getSelectedItem();

        for (UserSystemInfo usi : userSystemInfos) {
            if (!usiInListView.contains(usi)) {
                usiInListView.add(usi);
            }
        }

        if (selectedUsi != null) {
            try { onlineUsersListView.getSelectionModel().select(selectedUsi);}
            catch (Exception e) { onlineUsersListView.getSelectionModel().select(0);
            }
        }

    }
}
