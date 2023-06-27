package GUI.body.users.management;

import GUI.body.BodyControllerComponent;
import GUI.body.roles.RolesController;
import communication.Role;
import communication.UserSystemInfo;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    private List<UserSystemInfo> modifiedUsers;
    private BooleanProperty changesMade;

    public void initialize() {
        changesMade = new SimpleBooleanProperty(false);
        modifiedUsers = FXCollections.observableArrayList();
        initializeRolesListView();
        initializeIsManagerCheckBox();
        initializeSaveChangesButton();
        initializeOnlineUsersListView();

    }

    public void initializeSaveChangesButton(){
        saveChangesButton.disableProperty().bind(Bindings.not(changesMade));
        saveChangesButton.setOnAction(event -> {
            bodyController.updateUsersOnServer(modifiedUsers);
            changesMade.setValue(false);
        });
    }

    public void initializeRolesListView(RolesController rolesController) {

        ListView<Role> rolesListView = rolesController.getRolesListView();
        rolesListView.getItems().addListener((ListChangeListener<? super Role>) (observabe)-> {
            List<Role> newValue = rolesListView.getItems();
            if (newValue != null) {
                Platform.runLater(()-> this.rolesListView.setItems(
                                                            newValue.stream()
                                                                    .map(x-> createNewRoleListItem(x))
                                                                    .collect(java.util.stream.Collectors
                                                                    .toCollection(FXCollections::observableArrayList))
                        )
                );
            }
        });
    }
    private void initializeOnlineUsersListView() {
        onlineUsersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                rolesListView.setDisable(false);
                selectedUserLabel.setText(newValue.getName());
                managerCheckbox.setSelected(newValue.isManager());
                Set<String> rolesNamesSet = newValue.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

                Platform.runLater(()-> rolesListView.getItems()
                        .forEach(x->x.assignedProperty()
                        .set(rolesNamesSet.contains(x.getName()))));
            }
        });
    }
    private RoleListItem createNewRoleListItem(Role x) {
        RoleListItem item = new RoleListItem(x.getName(), false);
        item.assignedProperty().addListener((observable, oldValue, newValue) -> {
            changesMade.setValue(true);
            UserSystemInfo selectedUser = onlineUsersListView.getSelectionModel().getSelectedItem();
            if (newValue != null) {
                if (newValue) {
                    selectedUser.assignNewRole(x);
                } else {
                    selectedUser.unAssignNewRole(x);
                }
                if(!modifiedUsers.contains(selectedUser)) {
                    modifiedUsers.add(selectedUser);
                } else {
                    modifiedUsers.set(modifiedUsers.indexOf(selectedUser), selectedUser);
                }
            }
        });
        return item;
    }

    private void initializeIsManagerCheckBox() {
        managerCheckbox.setDisable(true);
        managerCheckbox.disableProperty().bind(onlineUsersListView.getSelectionModel().selectedItemProperty().isNull());
        managerCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                onlineUsersListView.getSelectionModel().getSelectedItem().setManager(newValue);
                changesMade.setValue(true);
            }
        });
    }

    private void initializeRolesListView() {
       rolesListView.setDisable(true);
       rolesListView.setCellFactory(CheckBoxListCell.forListView(RoleListItem::assignedProperty));
    }

    public void updateOnlineUsers(List<UserSystemInfo> userSystemInfos) {
;
        UserSystemInfo selectedUsi = onlineUsersListView.getSelectionModel().getSelectedItem();
        if (selectedUsi != null) {
            try { onlineUsersListView.getSelectionModel().select(selectedUsi);}
            catch (Exception e) { onlineUsersListView.getSelectionModel().clearSelection();
            }

        }

        Platform.runLater(()-> {
            ObservableList<UserSystemInfo> items = onlineUsersListView.getItems();
            if (userSystemInfos.size()>0){
                for (UserSystemInfo userSystemInfo : userSystemInfos) {
                    if (!items.contains(userSystemInfo)) {
                        items.add(userSystemInfo);
                    } else {
                        items.set(items.indexOf(userSystemInfo), userSystemInfo);
                    }
                }
            } else {
                items.clear();
            }
        });


    }

    public void OnSelection() {
        Platform.runLater(()-> {
            onlineUsersListView.getSelectionModel().clearSelection();
            rolesListView.getItems().forEach(x->x.assignedProperty().set(false));
            rolesListView.setDisable(true);
            managerCheckbox.setSelected(false);
        });
    }
}
