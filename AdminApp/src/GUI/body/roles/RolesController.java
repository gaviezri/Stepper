package GUI.body.roles;

import GUI.body.BodyControllerComponent;
import GUI.body.roles.create.role.NewRoleModalController;
import com.google.gson.internal.LinkedTreeMap;
import communication.Role;
import dto.flow.FlowNamesDTO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
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

import java.io.IOException;
import java.util.*;

public class RolesController extends BodyControllerComponent {

    @FXML private AnchorPane rolesAnchorPane;

    @FXML private ListView<Role> availableRolesListView;

    @FXML private Button newRoleButton;

    @FXML private Label chosenRoleLabel;

    @FXML private Button saveChangesButton;

    @FXML private Label chosenLabelDescriptionLabel;

    @FXML private ListView<String> assignedFlowsListView;

    @FXML private ListView<String> assignedUsersListView;

    private ObservableList<Role> newlyAddedOrModifiedRoles;
    private BooleanProperty changesMade;


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

        saveChangesButton.setOnAction(event -> {
            bodyController.createRoleOnServer(newlyAddedOrModifiedRoles);
            newlyAddedOrModifiedRoles.clear();
            changesMade.setValue(false);
        });
    }

    private void initializeAvailableRolesListView() {
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

        availableRolesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Get the selected role and its assigned flows
                Role selectedRole = newValue;
                List<String> assignedFlows = selectedRole.getAssignedFlowNames();

                // recheck the assigned flows based on the selected role
                for (int i = 0; i < assignedFlowsListView.getItems().size(); i++) {
                   String flowName = assignedFlowsListView.getItems().get(i);
                   // set the flow
               }

            }
        });
    }


    private void initializeNewRoleButton() {
        newRoleButton.setOnAction(event -> {
            Role newRole = openNewRoleDialog();
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

    private Role openNewRoleDialog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("create/role/newRoleModal.fxml"));
        try {
            Parent root = loader.load();
            NewRoleModalController newRoleModalController = loader.getController();
            Stage modalStage = new Stage();
            newRoleModalController.setStage(modalStage);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setScene(new Scene(root));
            modalStage.setTitle("New Role");
            modalStage.showAndWait();
            return newRoleModalController.getNewRole();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initializeAssignedFlowsListView() {
        assignedFlowsListView.setDisable(true);

        availableRolesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            assignedFlowsListView.setDisable(false);
        });

        assignedFlowsListView.setCellFactory(CheckBoxListCell.forListView(item -> {
            CheckBox checkBox = new CheckBox();
            BooleanProperty selectedProperty = new SimpleBooleanProperty();


            checkBox.selectedProperty().bindBidirectional(selectedProperty);

            selectedProperty.addListener((observable, oldValue, newValue) -> {
                // get selected role and assign flow name to it
                Role selectedRole = availableRolesListView.getSelectionModel().getSelectedItem();
                selectedRole.assignNewFlow(item);
                changesMade.setValue(true);

            });
            return selectedProperty;
        }));
    }

public void updateFlowNames(FlowNamesDTO flowNames) {
        ObservableList fetchedFlowNames = FXCollections.observableArrayList(flowNames.getFlowNames());
        if (assignedFlowsListView.getItems().size()>0){
            extendListWithNewFlowNamesIfFound(fetchedFlowNames);
        } else {
            assignedFlowsListView.setItems(fetchedFlowNames);
        }
    }

    private void extendListWithNewFlowNamesIfFound(ObservableList fetchedFlowNames) {
        ObservableList alreadyInside = assignedFlowsListView.getItems();
        for (Object flowName : fetchedFlowNames) {
            if (!alreadyInside.contains(flowName)) {
                alreadyInside.add(flowName);
            }
        }
    }

    public void updateRoles(List<Map> roles) {
        ObservableList adequateRoles = FXCollections.observableArrayList(Role.createRoleListFromJson(roles));
        availableRolesListView.setItems(adequateRoles);
    }


}
