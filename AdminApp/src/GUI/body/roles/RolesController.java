package GUI.body.roles;

import GUI.body.BodyControllerComponent;
import GUI.body.roles.create.role.NewRoleModalController;
import communication.Role;
import communication.UserSystemInfo;
import dto.flow.FlowNamesDTO;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.*;

public class RolesController extends BodyControllerComponent {

    @FXML private AnchorPane rolesAnchorPane;

    @FXML private ListView<Role> availableRolesListView;

    @FXML private Button newRoleButton;

    @FXML private Label chosenRoleLabel;

    @FXML private Button saveChangesButton;

    @FXML private Label chosenLabelDescriptionLabel;

    @FXML private ListView<FlowListItem> assignedFlowsListView;

    @FXML private ListView<String> assignedUsersListView;
    private ObservableList<UserSystemInfo> onlineUsers;
    private ObservableList<Role> newlyAddedOrModifiedRoles;
    private BooleanProperty changesMade;

    public void updateOnlineUsers(List<UserSystemInfo> userSystemInfos) {
        onlineUsers = FXCollections.observableArrayList(userSystemInfos);
    }

    public ListView getRolesListView() {
        return availableRolesListView;
    }

    public class FlowListItem {
        private final StringProperty name = new SimpleStringProperty();
        private final BooleanProperty assigned = new SimpleBooleanProperty();

        public FlowListItem(String name, boolean assigned) {
            this.name.set(name);
            this.assigned.set(assigned);
        }

        public String getName() {
            return name.get();
        }

        public BooleanProperty assignedProperty() {
            return assigned;
        }

        @Override
        public String toString() {
            return name.get();
        }
    }

    public void initialize() {

        newlyAddedOrModifiedRoles = FXCollections.observableArrayList();
        initializeChosenRoleLabels();
        initializeChangesMadeProperty();
        initializeAssignedFlowsListView();
        initializeAvailableRolesListView();
        initializeNewRoleButton();
        initializeSaveChangesButton();
    }

    private void initializeChosenRoleLabels() {
        chosenRoleLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            Role selectedRole = availableRolesListView.getSelectionModel().getSelectedItem();
            if (selectedRole != null) {
                return selectedRole.getName();
            }
            return "";
        }, availableRolesListView.getSelectionModel().selectedItemProperty()));

        chosenLabelDescriptionLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            Role selectedRole = availableRolesListView.getSelectionModel().getSelectedItem();
            if (selectedRole != null) {
                return selectedRole.getDescription();
            }
            return "";
        }, availableRolesListView.getSelectionModel().selectedItemProperty()));
    }

    private void initializeChangesMadeProperty() {
        changesMade = new SimpleBooleanProperty(false);

    }

    private void initializeSaveChangesButton() {
        saveChangesButton.setDisable(true);
        saveChangesButton.disableProperty().bind(Bindings.not(changesMade));
        defineSaveChangeButtonOnActionBehaviour();
    }

    private void defineSaveChangeButtonOnActionBehaviour() {
        saveChangesButton.setOnAction(event -> {
            bodyController.createRoleOnServer(newlyAddedOrModifiedRoles);
            availableRolesListView.getItems().forEach(role -> {
               if (newlyAddedOrModifiedRoles.contains(role)) {
                   role.setAssignedFlowNames(newlyAddedOrModifiedRoles.get(newlyAddedOrModifiedRoles.indexOf(role)).getAssignedFlowNames());
               }
            });
            newlyAddedOrModifiedRoles.clear();
            changesMade.setValue(false);
        });
    }

    private void initializeAvailableRolesListView() {
        setRolesListViewCellFactory();
        defineRolesListViewSelectionBehaviour();
    }

    private void setRolesListViewCellFactory() {
        availableRolesListView.setCellFactory(param -> new ListCell<Role>() {
            @Override
            protected void updateItem(Role item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
    }

    private void defineRolesListViewSelectionBehaviour() {

        availableRolesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            assignedFlowsListView.setDisable(false);
        });

        availableRolesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Get the selected role and its assigned flows
                Role selectedRole = newValue;
                List<String> assignedFlows = selectedRole.getAssignedFlowNames();

                // recheck the assigned flows based on the selected role
                for (int i = 0; i < assignedFlowsListView.getItems().size(); i++) {
                   FlowListItem listItem = assignedFlowsListView.getItems().get(i);
                   listItem.assignedProperty().setValue(assignedFlows.contains(listItem.getName()));
               }
            }
        });
    }


    private void initializeNewRoleButton() {
        newRoleButton.setOnAction(event -> {
            Role newRole = getNewRoleFromDialog();
            if (newRole != null) {
                if (availableRolesListView.getItems().contains(newRole)) {
                    availableRolesListView.getItems().remove(newRole);
                }
                newlyAddedOrModifiedRoles.add(newRole);
                availableRolesListView.getItems().add(newRole);
                try {
                    availableRolesListView.getItems().sort(Comparator.comparing(Role::getName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                changesMade.setValue(true);
            }
        });
    }

    private Role getNewRoleFromDialog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("create/role/newRoleModal.fxml"));
        try {
            return openDialogAndGetRole(loader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Role openDialogAndGetRole(FXMLLoader loader) throws IOException {
        Parent root = loader.load();
        NewRoleModalController newRoleModalController = loader.getController();
        Stage modalStage = new Stage();
        newRoleModalController.setStage(modalStage);
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setScene(new Scene(root));
        modalStage.setTitle("New Role");
        modalStage.showAndWait();
        return newRoleModalController.getNewRole();
    }

    private void initializeAssignedFlowsListView() {
        assignedFlowsListView.setDisable(true);

        assignedFlowsListView.setCellFactory(CheckBoxListCell.forListView(FlowListItem::assignedProperty));
    }

    public void updateFlowNames(FlowNamesDTO flowNames) {
            ObservableList<String> fetchedFlowNames = FXCollections.observableArrayList(flowNames.getFlowNames());
            Platform.runLater(() -> {
            for (String flowName : fetchedFlowNames) {
                if (assignedFlowsListView.getItems().stream().noneMatch(item -> item.getName().equals(flowName))) {
                    assignedFlowsListView.getItems().add(createNewFlowListItem(flowName));
                }
            }});
    }

    private FlowListItem createNewFlowListItem(String flowName) {
        FlowListItem item = new FlowListItem(flowName, false);
        item.assignedProperty().addListener((observable, oldValue, newValue) -> {
            changesMade.setValue(true);
            Role selectedRole = availableRolesListView.getSelectionModel().getSelectedItem();
            if (selectedRole != null) {
                if (newValue) {
                    selectedRole.assignNewFlow(flowName);
                } else {
                    selectedRole.unassignOldFlow(flowName);
                }
                if (!newlyAddedOrModifiedRoles.contains(selectedRole)) {
                    newlyAddedOrModifiedRoles.add(selectedRole);
                } else {
                    newlyAddedOrModifiedRoles.set(newlyAddedOrModifiedRoles.indexOf(selectedRole), selectedRole);
                }
            }
        });
        return item;
    }


    public void updateRoles(List<Role> roles) {
        Platform.runLater(() -> {
            availableRolesListView.getItems().clear();
            availableRolesListView.getItems().addAll(roles);
            availableRolesListView.getItems().sort(Comparator.comparing(Role::getName));
        });
    }
}
