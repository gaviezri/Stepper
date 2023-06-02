package body.execution;

import app.AppController;
import body.BodyControllerComponent;
import body.library.definition.DefinitionController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import javafx.util.Pair;
import stepper.dd.api.DataDefinition;
import stepper.dto.flow.FlowDefinitionDTO;
import stepper.flow.execution.FlowExecutionResult;
import stepper.step.api.enums.StepResult;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

    @FXML private VBox stepDetailsVBox;
    @FXML private Label stepDetailsNameLabel;
    @FXML private Label stepDetailsDurationLabel;
    @FXML private Label stepDetailsResultLabel;
    @FXML private Label stepSummaryLineLabel;

    @FXML private TitledPane outputsTitledPane;
    @FXML private AnchorPane outputsDefaultAnchorPane;
    @FXML private ListView<String> outputsListView;

    @FXML private TitledPane logsTitledPane;
    @FXML private AnchorPane logsDefaultAnchorPane;
    @FXML private ListView<String> logsListView;

    @FXML private Label executionEndLabel;

    @FXML private AnchorPane continuationAnchorPane;
    @FXML private Button continueButton;
    @FXML private ListView<String> continuationListView;
    @FXML private Label continuationLabel;

    private Map<String ,SingleStepExecutionTableData> currentFlowStepsExecutionTableDataList  = new LinkedHashMap<>();
    private Map<String, List<Pair<String,String>>> continuationDataMap;

    private static final int POLLING_INTERVAL = 200;
    private ScheduledExecutorService poller = Executors.newScheduledThreadPool(1);
    private boolean notified = false;
    private boolean fooled = false;
    private BooleanProperty gotContinuations = new SimpleBooleanProperty(false);



    public void initialize() {
        flowExecutionMainAnchorPane.setDisable(true);
        flowProgressPercentageLabel.setWrapText(true);
        executionEndLabel.setWrapText(true);

        initializeStepsStatusToolTip();
        bindExecutionTabComponents();
        bindSelectionOfStepInListViewToStepDetails();
        initializeContinuationSection();

        stepSummaryLineLabel.setTooltip(new Tooltip());
        stepSummaryLineLabel.getTooltip().setOnShowing(event -> {
            Tooltip tooltip = (Tooltip) event.getSource();
            tooltip.setText(stepSummaryLineLabel.getText());
        });
    }

    private void initializeContinuationSection(){
        continueButton.setDisable(true);

        continuationListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                continueButton.setDisable(false);
            }
        });
        continueButton.setOnMouseClicked( event -> {
            continueButton.setDisable(true);
            Platform.runLater(()->{
                bodyController.setInputSectionToContinuation(continuationListView.getSelectionModel().getSelectedItem(), continuationDataMap.get(continuationListView.getSelectionModel().getSelectedItem()));
                continuationListView.getSelectionModel().clearSelection();
            });
        });

        continuationListView.onMouseClickedProperty().addListener((observable, oldValue, newValue) -> {
            //TODO: fix when clicking on the same item allows the continue button to be pressed again
            // happens when clicking continue on continuation and then not executing it going back to execution and clicking on the same continuation
            continueButton.setDisable(false);
        });
    }
    private void bindSelectionOfStepInListViewToStepDetails() {
        executedStepsStatusListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String stepPresentationName = getStepNameWithoutReadonly(((Label)newValue).getText());
                SingleStepExecutionTableData singleStepExecutionTableData = currentFlowStepsExecutionTableDataList.get(getStepNameWithoutReadonly(stepPresentationName));
                if (singleStepExecutionTableData != null) {
                    updateStepDetails(singleStepExecutionTableData);
                }
                else {
                    updateStepDetails(stepPresentationName);
                }
            }
        });
        executedStepsStatusListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String stepPresentationName = getStepNameWithoutReadonly(((Label)newValue).getText());
                SingleStepExecutionTableData singleStepExecutionTableData = currentFlowStepsExecutionTableDataList.get(getStepNameWithoutReadonly(stepPresentationName));
                if (singleStepExecutionTableData != null) {
                    updateStepDetails(singleStepExecutionTableData);
                }
                else {
                    updateStepDetails(stepPresentationName);
                }
            }
        });
    }
    private void updateStepDetails(SingleStepExecutionTableData singleStepExecutionTableData) {
        stepDetailsNameLabel.setText(singleStepExecutionTableData.getName());
        stepDetailsDurationLabel.setText("Duration: " + singleStepExecutionTableData.getDuration().toString());
        setResultLabelToNotExecuted(singleStepExecutionTableData.getResult().toString(), singleStepExecutionTableData.getResult());
        updateStepLogs(singleStepExecutionTableData.getLogs());
        updateStepOutputs(singleStepExecutionTableData.getOutputsName());

        stepSummaryLineLabel.setText(singleStepExecutionTableData.getSummaryLine());
    }

    private void updateStepOutputs(List<String> outputs){
        outputsListView.visibleProperty().set(true);
        outputsListView.getItems().clear();
        outputsListView.getItems().addAll(outputs);
    }

    private void updateStepLogs(List<String> logs) {
        logsListView.visibleProperty().set(true);
        logsListView.getItems().clear();
        logsListView.getItems().addAll(logs);

    }

    private void updateStepDetails(String stepPresentationName) {
        stepDetailsNameLabel.setText(stepPresentationName);
        stepDetailsDurationLabel.setText("Duration: N/A");
        setResultLabelToNotExecuted("Not Executed", StepResult.NOT_EXECUTED);
        logsListView.visibleProperty().set(false);
        outputsListView.visibleProperty().set(false);
        stepSummaryLineLabel.setText("");
    }
    private void setResultLabelToNotExecuted(String resultText, StepResult stepResult) {
        stepDetailsResultLabel.setText(resultText);
        stepDetailsResultLabel.setTextFill(getColorBasedOnResult(stepResult));
    }
    @Override
    protected void finalize() {
        poller.shutdown();
    }

    public void bindExecutionTabComponents() {
        poller.scheduleAtFixedRate(() -> {
            if (bodyController != null) {
                AppController appController = bodyController.getMainController();
                if (bodyController.getMainController() != null) {
                    int numOfFlowExecuted = bodyController.getMainController().numOfFlowsExecutedProperty().get();
                    if (realExecutionAnchorPane.isVisible() && numOfFlowExecuted > 0) {
                        try {
                            float flowProgressPercentage = (appController.getCurrentStepIdx() + 1.f) / appController.getStepsCount();
                            if (flowProgressPercentage != flowProgressBar.getProgress()) {
                                Platform.runLater(() -> {
                                    updateFlowProgressPercentageLabel(flowProgressPercentage);
                                    updateFlowProgressBar(flowProgressPercentage);
                                    updateStepInProgressLabel(appController.getCurrentStepName());
                                    updateExecutedStepsStatusListView(appController.getExecutedStepsStatus());

                                    // update step details section

                                    updateSingleStepExecutionData(appController.getAllStepsListOfLogs(),
                                                                    appController.getOutputsForAllSteps(),
                                                                     appController.getExecutedStepsStatus(),
                                                                      appController.getAllStepsDuration(),
                                                                        appController.getAllSummaryLines());
                                    // check it out ^^^^
                                });
                            }
                        } catch (NullPointerException ignored) {
                        }
                    }
                    if (!appController.isFlowExecutionInProgress()
                            && !notified
                            && numOfFlowExecuted > 0) {
                        notified = true;
                        FlowExecutionResult flowResult = appController.getFlowExecutionResult();
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
                            executionEndLabel.setText("\"" + appController.getLastExecutedFlowName() + "\" " + message.toString() + "!");
                            utils.Utils.ShowInformation("Heads up!", message.toString(), "");
                            flowProgressBar.setProgress(0);
                            doneExecutionPaneSwitch();
                        });
                    }
                }
            }

        }, 0, POLLING_INTERVAL, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
    private void updateSingleStepExecutionData(Map<String, List<String>> allStepsListOfLogs, Map<String,
                                               Map<String, Pair<DataDefinition, Object>>> outputsForAllSteps,
                                               Map<String,StepResult> executedStepsStatus,
                                               Map<String, java.time.Duration> allStepsDuration,
                                               Map<String, String> allSummaryLines) {


        Map<String,javafx.util.Duration> allStepsJavaFXDuration = new LinkedHashMap<>();
        for (String stepName : allStepsDuration.keySet()){
            allStepsJavaFXDuration.put(stepName, javafx.util.Duration.millis(allStepsDuration.get(stepName).toMillis()));
        }
        // update step details section
        for ( String stepName : executedStepsStatus.keySet()){
            if (currentFlowStepsExecutionTableDataList.containsKey(stepName)){
                // update it
                if(currentFlowStepsExecutionTableDataList.get(stepName).getResult().equals(StepResult.NOT_EXECUTED)){
                    continue;
                }
                else{
                    currentFlowStepsExecutionTableDataList.get(stepName).updateData(executedStepsStatus.get(stepName),
                            allStepsJavaFXDuration.get(stepName),
                            allStepsListOfLogs.get(stepName),
                            allSummaryLines.get(stepName),
                            outputsForAllSteps.get(stepName));
                };
            }
            else{
                currentFlowStepsExecutionTableDataList.put(stepName,
                        new SingleStepExecutionTableData(stepName,
                                executedStepsStatus.get(stepName),
                                allStepsJavaFXDuration.get(stepName),
                                allStepsListOfLogs.get(stepName),
                                allSummaryLines.get(stepName),
                                outputsForAllSteps.get(stepName)));
            }
        }
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
            continuationAnchorPane.setVisible(!value && gotContinuations.get());

            // continuation pane.setvisible
            // bind continuation pane and execution summary label progressbar
        });
    }
    private void updateExecutedStepsStatusListView(Map<String, StepResult> executedStepsStatus) {

        executedStepsStatus.forEach((stepName, stepResult) -> {
            executedStepsStatusListView.getItems().forEach(item -> {
                Label curItem = (Label) item;
                // remove (readonly) from step name
                String finalItemName = getStepNameWithoutReadonly(curItem.getText());

                if (finalItemName.equals(stepName)) {
//
                    if (curItem.getTextFill().equals(javafx.scene.paint.Color.GREY)) {
                        ((Label) item).setTextFill(getColorBasedOnResult(stepResult));

                    }
                }
            });
        });
    }
    private static Paint getColorBasedOnResult(StepResult stepResult) {
        javafx.scene.paint.Paint color = javafx.scene.paint.Color.GREY;
        switch (stepResult) {
            case SUCCESS:
                color = javafx.scene.paint.Color.GREEN;
                break;
            case FAILURE:
                color = javafx.scene.paint.Color.RED;
                break;
            case WARNING:
                color = javafx.scene.paint.Color.ORANGE;
                break;
            case NOT_EXECUTED:
                color = Color.DEEPPINK;
                break;
            default:
                break;
        }
        return color;
    }
    private static String getStepNameWithoutReadonly(String Name) {
        String finalItemName =  (Name.contains("(read-only)")) ?
                Name.substring(0,Name.indexOf("(")-1)
                :
                Name;
        return finalItemName;
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

                    stepDetailsNameLabel.setText("select a step from the list");
                    stepDetailsDurationLabel.setText("to get");
                    stepDetailsResultLabel.setText("further details");
                    currentFlowStepsExecutionTableDataList.clear();
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
    public void setContinuationProperty(FlowDefinitionDTO dto) {
        gotContinuations.set(dto.getContinuationsCount() > 0);
        continuationDataMap = dto.getContinuationDataMap();
        if (continuationDataMap.size() > 0) {
            continuationListView.getItems().clear();
            continuationDataMap.forEach((key, value) -> {
                continuationListView.getItems().add(key.toString());
            });
        }
    }
}
