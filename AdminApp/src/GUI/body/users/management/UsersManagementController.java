package GUI.body.users.management;

import GUI.body.BodyControllerComponent;
import GUI.body.roles.RolesController;
import communication.Role;
import communication.UserSystemInfo;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javax.management.relation.RoleList;
import java.util.List;

public class UsersManagementController extends BodyControllerComponent {
    private class RoleListItem {
        private final StringProperty name = new SimpleStringProperty();
        private final BooleanProperty assigned = new SimpleBooleanProperty();

        public RoleListItem(String name, boolean assigned) {
            this.name.set(name);
            this.assigned.set(assigned);
        }

        public RoleListItem(Role role){
            this.name.set(role.getName());
            this.assigned.set(false);
        }

        public String getName() { return name.get(); };

        public BooleanProperty assignedProperty() {return assigned;}

        @Override
        public String toString() {return name.get();}
    }

    @FXML
    private ListView<UserSystemInfo> onlineUsersListView;

    @FXML
    private CheckBox managerCheckbox;

    @FXML
    private Label selectedUserLabel;

    @FXML
    private ListView<RoleListItem> rolesListView;

    @FXML
    private Button saveChangesButton;

    private boolean changesMade;
    public void initialize() {
        changesMade = false;
        initializeIsManagerCheckBox();
        initializeOnlineUsersListView();

    }

    public void initializeRolesListView(RolesController rolesController) {
        ListView<Role> rolesListView = rolesController.getRolesListView();
        rolesListView.itemsProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Platform.runLater(()-> {
                    //TODO: implement binding between roles in Role Controller and roles in Users Management Controller
//                    this.rolesListView.setItems(
//                                                newValue
//                                                .stream()
//                                                .map(x-> new RoleListItem(x))
//                                                .collect(java.util.stream.Collectors
//                                                        .toCollection(FXCollections::observableArrayList))
//                                                );
                });
            }
        });
    }
    private void initializeOnlineUsersListView() {
        onlineUsersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedUserLabel.setText(newValue.getName());
                managerCheckbox.setSelected(newValue.isManager());
                List<Role> rolesList = bodyController.getRoleManager()
                        .getRolesListFromCumulativeRoleValue(newValue.getRoles());

                Platform.runLater(()-> {
                    // TODO: check the checkboxes that correspond to the roles of the selected user
//                    rolesListView.setItems(rolesList
//                            .stream()
//                            .map(x-> createNewRoleListItem(x))
//                            .collect(java.util.stream.Collectors.toCollection(FXCollections::observableArrayList)));
                });

            }
        });
    }

    private RoleListItem createNewRoleListItem(Role x) {
        RoleListItem item = new RoleListItem(x.getName(), false);
        //TODO: implement logic for checking or unchecking the checkbox
        return item;
    }

    private void initializeIsManagerCheckBox() {

    }

    public void updateOnlineUsers(List<UserSystemInfo> userSystemInfos) {
        ObservableList<UserSystemInfo> usiInListView =  onlineUsersListView.getItems();
        UserSystemInfo selectedUsi = onlineUsersListView.getSelectionModel().getSelectedItem();
        Platform.runLater(()-> {
            for (UserSystemInfo usi : userSystemInfos) {
                if (!usiInListView.contains(usi)) {
                    usiInListView.add(usi);
                }
            }
        });

        if (selectedUsi != null) {
            try { onlineUsersListView.getSelectionModel().select(selectedUsi);}
            catch (Exception e) { onlineUsersListView.getSelectionModel().select(0);
            }
        }

    }
}
