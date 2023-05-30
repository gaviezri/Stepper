package body.execution;

import body.BodyController;
import body.BodyControllerComponent;
import body.library.definition.DefinitionController;
import body.library.input.InputController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import stepper.flow.execution.FlowExecutionResult;
import stepper.flow.execution.last.executed.data.center.LastExecutedDataCenter;
import stepper.step.api.enums.StepResult;
import sun.plugin.services.WPlatformService;

import javax.tools.Tool;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExecutionController extends BodyControllerComponent {
    // fake section
    @FXML private VBox fakeLoadingVBox = new VBox();
    @FXML private ProgressBar fakeProgressBar = new ProgressBar();
    @FXML private Label fakeLoadingLabel;
    // real section
    @FXML private AnchorPane flowExecutionMainAnchorPane;
    @FXML private AnchorPane realExecutionAnchorPane;
    @FXML private ListView executedStepsStatusListView;
    @FXML private Tooltip executedStepsStatusListViewToolTip;

    @FXML private Label stepInProgressLabel;
    @FXML private Label flowProgressPercentageLabel;
    @FXML private ProgressBar flowProgressBar;

    @FXML private FlowPane selectedStepDetailsFlowPane;
    @FXML private TableView stepSinglularDetailsTableView;
    @FXML private TableColumn stepNameCol;
    @FXML private TableColumn stepResultCol;
    @FXML private TableColumn stepDurationCol;

    @FXML private TableView logsTableView;
    @FXML private TableColumn stepLogsCol;

    @FXML private TableView outputsTableView;
    @FXML private TableColumn stepOutputsCol;

    @FXML private Label executionEndLabel;

    @FXML private HBox continuationOptionsHbox;
    // continuation section
    // summary section
    // step details section
    private static final int POLLING_INTERVAL = 200;
    private ScheduledExecutorService poller = Executors.newScheduledThreadPool(1);
    private boolean notified = false;
    private boolean fooled = false;



    public void initialize() {
        flowExecutionMainAnchorPane.setDisable(true);
        flowProgressPercentageLabel.setWrapText(true);
        initializeStepsStatusToolTip();
        bindExecutionTabComponents();
        executionEndLabel.setWrapText(true);
    }
    @Override
    protected void finalize() {
        poller.shutdown();
    }

    public void bindExecutionTabComponents() {
        poller.scheduleAtFixedRate(() -> {
            if (bodyController != null) {
                if (bodyController.getMainController() != null) {
                    int numOfFlows = bodyController.getMainController().numOfFlowsExecutedProperty().get();
                    if (realExecutionAnchorPane.isVisible() && numOfFlows > 0) {
                        try {
                            float flowProgressPercentage = (LastExecutedDataCenter.getCurrentStepIdx() + 1.f) / LastExecutedDataCenter.getStepsCount();
                            if (flowProgressPercentage != flowProgressBar.getProgress()) {
                                Platform.runLater(() -> {
                                    updateFlowProgressPercentageLabel(flowProgressPercentage);
                                    updateFlowProgressBar(flowProgressPercentage);
                                    updateStepInProgressLabel(LastExecutedDataCenter.getCurrentStepName());
                                    updateExecutedStepsStatusListView(LastExecutedDataCenter.getExecutedStepsStatus());
                                });

                            }
                        } catch (NullPointerException ignored) {
                        }
                    }


                    if (!LastExecutedDataCenter.isFlowExecutionInProgress()
                            && !notified
                            && numOfFlows > 0) {
                        notified = true;
                        FlowExecutionResult flowResult = LastExecutedDataCenter.getFlowExecutionResult();
                        StringBuilder message = new StringBuilder("Flow execution ended with ");
                        switch (flowResult) {
                            case SUCCESS:
                                message.append("success");
                                break;
                            case FAILURE:
                                message.append("failure");
                                break;
                            default:
                                message.append("warnings");
                                break;
                        }

                        Platform.runLater(() -> {
                            executionEndLabel.setText("\"" + LastExecutedDataCenter.getLastExecutedFlowName() + "\" " + message.toString() + "!");
                            utils.Utils.ShowInformation("Heads up!", message.toString(), "");
                            flowProgressBar.setProgress(0);
                            doneExecutionPaneSwitch();
                        });
                    }
                }
            }

        }, 0, POLLING_INTERVAL, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    private void doneExecutionPaneSwitch() {
        ExecutionPaneSwitch(false);
    }

    private void startExecutionPaneSwitch(){
        ExecutionPaneSwitch(true);
    }

    private void ExecutionPaneSwitch(boolean value){
        Platform.runLater(() -> {
            flowProgressBar.setVisible(value);
            flowProgressPercentageLabel.setVisible(value);
            stepInProgressLabel.setVisible(value);

            executionEndLabel.setVisible(!value);
            continuationOptionsHbox.setVisible(!value);
            // continuation pane.setvisible
            // bind continuation pane and execution summary label progressbar
        });
    }


    private void updateExecutedStepsStatusListView(Map<String, StepResult> executedStepsStatus) {

        executedStepsStatus.forEach((stepName, stepResult) -> {
            executedStepsStatusListView.getItems().forEach(item -> {
                Label curItem = (Label) item;
                // remove (readonly) from step name
                String rawItemName = curItem.getText();
                String finalItemName =  (rawItemName.contains("(read-only)")) ?
                        rawItemName.substring(0,rawItemName.indexOf("(")-1)
                        :
                        curItem.getText();

                if (finalItemName.equals(stepName)) {
//
                    if (curItem.getTextFill().equals(javafx.scene.paint.Color.GREY)) {

                        switch (stepResult) {
                            case SUCCESS:
                                ((Label) item).setTextFill(javafx.scene.paint.Color.GREEN);
                                break;
                            case FAILURE:
                                ((Label) item).setTextFill(javafx.scene.paint.Color.RED);
                                break;
                            case WARNING:
                                ((Label) item).setTextFill(javafx.scene.paint.Color.ORANGE);
                                break;
                            default:
                                break;
                        }

                    }
                }
            });
        });
    }

    private void updateStepInProgressLabel(String currentStepName) {
        stepInProgressLabel.setText("Step in progress: " + currentStepName);
    }

    private void updateFlowProgressPercentageLabel(float flowProgressPercentage) {
        flowProgressPercentageLabel.setText(String.format("%.0f", flowProgressPercentage * 100) + "%");
    }
    private void updateFlowProgressBar(float flowProgressPercentage) {
        double initialValue = flowProgressBar.getProgress();
        double animationDuration = 300; // Duration of the animation in milliseconds

        // Create a Timeline animation
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(flowProgressBar.progressProperty(), initialValue)),
                new KeyFrame(Duration.millis(animationDuration), new KeyValue(flowProgressBar.progressProperty(), flowProgressPercentage))
        );
        timeline.play();
        flowProgressBar.setProgress(flowProgressPercentage);
    }


    private void initializeStepsStatusToolTip() {
        TextFlow textFlow = new TextFlow();
        Text header = new Text("Steps status:\n");
        header.setStyle("-fx-font-weight: bold;");
        header.setStyle("-fx-fill: white;");
        Text green = new Text("Step executed successfully - Green\n");
        green.setStyle("-fx-fill: green;");
        Text red = new Text("Step failed during execution - Red\n");
        red.setStyle("-fx-fill: red;");
        Text orange = new Text("Step executed with warnings - Orange\n");
        orange.setStyle("-fx-fill: orange;");
        Text grey = new Text("Step not yet executed - Grey\n");
        grey.setStyle("-fx-fill: grey;");
        textFlow.getChildren().addAll(header,green, red, orange, grey);
        executedStepsStatusListViewToolTip.setShowDelay(Duration.ZERO);
        executedStepsStatusListViewToolTip.setGraphic(textFlow);
        executedStepsStatusListViewToolTip.wrapTextProperty().setValue(true);
        executedStepsStatusListViewToolTip.setText("");
        executedStepsStatusListViewToolTip.setMaxHeight(200);
        Tooltip.install(executedStepsStatusListView, executedStepsStatusListViewToolTip);

    }
    public void bindFlowExecutionElementsToSelectButton(DefinitionController definitionController) {

            bodyController.getMainController().numOfFlowsExecutedProperty().addListener(((observable, oldValue, newValue) -> {
                startExecutionPaneSwitch();
                Platform.runLater(() -> {
                    notified = false;
                    executedStepsStatusListView.getItems().clear();
                    VBox stepsNames = (VBox) definitionController.getStepsTitledPane().getContent();
                    for (int i = 0; i < stepsNames.getChildren().size(); i++) {
                        Label stepName = new Label(((TitledPane) stepsNames.getChildren().get(i)).getText());
                        stepsNames.setStyle("-fx-font-weight: bold;");
                        stepName.setTextFill(javafx.scene.paint.Color.GREY);
                        executedStepsStatusListView.getItems().add(stepName);
                    }
                });
        }));
    }
    public void bindFakeSectionToExecutionEnablement(TabPane mainTabPane) {
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == mainTabPane.getTabs().get(1)) {

                // animate fake progress bar for 2 seconds/
                // then show real execution pane and hide fake loading pane
                if (!fooled) {
                    realExecutionAnchorPane.setVisible(false);
                    fakeLoadingVBox.setVisible(true);
                    KeyFrame begin = new KeyFrame(Duration.ZERO, new KeyValue(fakeProgressBar.progressProperty(), 0));
                    KeyFrame end = new KeyFrame(Duration.seconds(1.5), new KeyValue(fakeProgressBar.progressProperty(), 1));
                    Timeline timeline = new Timeline(begin, end);
                    timeline.setCycleCount(1);
                    timeline.setOnFinished(event -> {
                        realExecutionAnchorPane.setVisible(true);
                        fakeLoadingVBox.setVisible(false);
                    });
                    timeline.play();
                    fooled = true;
                }
            }
        });
    }

}
