package GUI.body.execution;

import GUI.app.AppController;
import GUI.body.BodyControllerComponent;
import GUI.body.library.definition.DefinitionController;
import dto.execution.progress.ExecutedFlowDetailsDTO;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import dto.flow.FlowDefinitionDTO;
import stepper.flow.execution.FlowExecutionResult;
import stepper.step.api.enums.StepResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    private boolean updatedAfterExecutionEnd = true;

    private Map<String , SingleStepExecutionTableData> currentFlowStepsExecutionTableDataMap = new LinkedHashMap<>();
    private Map<String, List<Pair<String,String>>> continuationDataMap;

    private static final int POLLING_INTERVAL = 200;
    private ScheduledExecutorService poller = Executors.newScheduledThreadPool(1);
    private boolean fooled = false;
    private BooleanProperty gotContinuations = new SimpleBooleanProperty(false);



    public void initialize() {
        flowExecutionMainAnchorPane.setDisable(true);
        flowProgressPercentageLabel.setWrapText(true);
        executionEndLabel.setWrapText(true);

        initializeStepsStatusToolTip();
        bindExecutionTabComponents();
        bindSelectionOfStepInListViewToStepDetails();
        bindSelectionOfOutputInListViewToOutputDetails();
        initializeContinuationSection();

        stepSummaryLineLabel.setTooltip(new Tooltip());
        stepSummaryLineLabel.getTooltip().setOnShowing(event -> {
            Tooltip tooltip = (Tooltip) event.getSource();
            tooltip.setText(stepSummaryLineLabel.getText());
        });
    }

    private void bindSelectionOfOutputInListViewToOutputDetails() {
        outputsListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if(newValue != null) {

                    Stage outputModal = new Stage();
                    outputModal.setTitle("Outputs");
                    outputModal.initModality(Modality.APPLICATION_MODAL);
                    outputModal.initOwner(this.getBodyController().getMainController().getPrimaryStage());
                    int outputidx = outputsListView.getSelectionModel().getSelectedIndex();
                    String outputName = getStepNameWithoutReadonly(((Label) executedStepsStatusListView.getSelectionModel().getSelectedItem()).getText());
                    VBox currentOutputExecutionDataRoot = currentFlowStepsExecutionTableDataMap.get(outputName).getOutputNode(outputidx);
                    Scene outputScene = new Scene(currentOutputExecutionDataRoot, 200, 100);
                    outputModal.setScene(outputScene);
                    outputModal.sizeToScene();
                    outputModal.showAndWait();
                    outputsListView.getSelectionModel().clearSelection();
                }
            });

        }));
    }

    public void reset(){
        updatedAfterExecutionEnd = true;
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


    public void clearStepDetails() {
        stepDetailsNameLabel.setText("select a step from the list");
        stepDetailsDurationLabel.setText("to get");
        stepDetailsResultLabel.setText("further details");
        stepDetailsResultLabel.setTextFill(Color.BLACK);
        outputsListView.getItems().clear();
        logsListView.getItems().clear();
    }
    private void bindSelectionOfStepInListViewToStepDetails() {
        executedStepsStatusListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String stepPresentationName = getStepNameWithoutReadonly(((Label)newValue).getText());
                SingleStepExecutionTableData singleStepExecutionTableData = currentFlowStepsExecutionTableDataMap.get(getStepNameWithoutReadonly(stepPresentationName));
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
                SingleStepExecutionTableData singleStepExecutionTableData = currentFlowStepsExecutionTableDataMap.get(getStepNameWithoutReadonly(stepPresentationName));
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
        Platform.runLater(()-> {
            stepDetailsNameLabel.setText(singleStepExecutionTableData.getName());
            stepDetailsDurationLabel.setText("Duration: " + singleStepExecutionTableData.getDuration().toString());
            setResultLabelToNotExecuted(singleStepExecutionTableData.getResult().toString(), singleStepExecutionTableData.getResult());
            updateStepLogs(singleStepExecutionTableData.getLogs());
            updateStepOutputs(singleStepExecutionTableData.getOutputsName());
            stepSummaryLineLabel.setText(singleStepExecutionTableData.getSummaryLine());
        });

    }

    private void updateStepOutputs(List<String> outputs){
        Platform.runLater(()->{
            outputsListView.visibleProperty().set(true);
            outputsListView.getItems().clear();
            outputsListView.getItems().addAll(outputs);
        });
    }

    private void updateStepLogs(List<String> logs) {
        Platform.runLater(()->{
            logsListView.visibleProperty().set(true);
            logsListView.getItems().clear();
            logsListView.getItems().addAll(logs);
        });
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
//                        try {
//                            float flowProgressPercentage = (appController.getCurrentStepIdx() + 1.f) / appController.getStepsCount();
//                            if (flowProgressPercentage != flowProgressBar.getProgress()) {
//                                Platform.runLater(() -> {
//                                    synchronized (this) {
//                                        updateFlowProgressPercentageLabel(flowProgressPercentage);
//                                        updateFlowProgressBar(flowProgressPercentage);
//                                        updateStepInProgressLabel(appController.getCurrentStepName());
//                                        updateExecutedStepsStatusListView(appController.getExecutedStepsStatus());
//
//                                        // update step details section
//
//                                        updateSingleStepExecutionData(appController.getAllStepsListOfLogs(),
//                                                appController.getOutputsForAllSteps(),
//                                                appController.getExecutedStepsStatus(),
//                                                appController.getAllStepsDuration(),
//                                                appController.getAllSummaryLines());
//                                        // check it out ^^^^
//                                    }
//                                });
//                            }
//                        } catch (NullPointerException ignored) {
//                        }
                    }
//                    if (!appController.isFlowExecutionInProgress()
//                            && !notified
//                            && numOfFlowExecuted > 0) {
//                        notified = true;
//                        FlowExecutionResult flowResult = appController.getFlowExecutionResult();
//                        StringBuilder message = new StringBuilder("Flow execution ended with ");
//                        switch (flowResult) {
//                            case SUCCESS:
//                                message.append("success");
//                                break;
//                            case FAILURE:
//                                message.append("failure");
//                                break;
//                            default:
//                                message.append("warnings");
//                                break;
//                        }
//
//                        Platform.runLater(() -> {
//                            executionEndLabel.setText("\"" + appController.getLastExecutedFlowName() + "\" " + message.toString() + "!");
//                            Utils.ShowInformation("Heads up!", message.toString(), "");
//                            flowProgressBar.setProgress(0);
//                            doneExecutionPaneSwitch();
//                        });
//                    }
                }
            }

        }, 0, POLLING_INTERVAL, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
    private void updateSingleStepExecutionData(Map<String, List<String>> allStepsListOfLogs, Map<String,
                                               Map<String, Pair<String, Object>>> outputsForAllSteps,
                                               Map<String,StepResult> executedStepsStatus,
                                               Map<String, java.time.Duration> allStepsDuration,
                                               Map<String, String> allSummaryLines) {


        Map<String, Duration> allStepsJavaFXDuration = new LinkedHashMap<>();
        for (String stepName : allStepsDuration.keySet()){
            allStepsJavaFXDuration.put(stepName, Duration.millis(allStepsDuration.get(stepName)==null ? 0.0 : allStepsDuration.get(stepName).toMillis()));
        }
        // update step details section
        for ( String stepName : executedStepsStatus.keySet()){
            if (currentFlowStepsExecutionTableDataMap.containsKey(stepName)){
                // update it
                if(currentFlowStepsExecutionTableDataMap.get(stepName).getResult().equals(StepResult.NOT_EXECUTED)){
                    continue;
                }
                else{
                    currentFlowStepsExecutionTableDataMap.get(stepName).updateData(executedStepsStatus.get(stepName),
                            allStepsJavaFXDuration.get(stepName),
                            allStepsListOfLogs.get(stepName),
                            allSummaryLines.get(stepName),
                            outputsForAllSteps.get(stepName), true);
                };
            }
            else{
                currentFlowStepsExecutionTableDataMap.put(stepName,
                        new SingleStepExecutionTableData(stepName,
                                executedStepsStatus.get(stepName),
                                allStepsJavaFXDuration.get(stepName),
                                allStepsListOfLogs.get(stepName),
                                allSummaryLines.get(stepName),
                                outputsForAllSteps.get(stepName),true));
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
                    if (curItem.getTextFill().equals(Color.GREY)) {
                        ((Label) item).setTextFill(getColorBasedOnResult(stepResult));

                    }
                }
            });
        });
    }
    private static Paint getColorBasedOnResult(StepResult stepResult) {
        Paint color = Color.GREY;
        switch (stepResult) {
            case SUCCESS:
                color = Color.GREEN;
                break;
            case FAILURE:
                color = Color.RED;
                break;
            case WARNING:
                color = Color.ORANGE;
                break;
            case NOT_EXECUTED:
                color = Color.DEEPPINK;
                break;
            default:
                break;
        }
        return color;
    }
    public static String getStepNameWithoutReadonly(String Name) {
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
        //executedStepsStatusListViewToolTip.setShowDelay(Duration.ZERO);
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
                    executedStepsStatusListView.getItems().clear();
                    VBox stepsNames = (VBox) definitionController.getStepsTitledPane().getContent();
                    for (int i = 0; i < stepsNames.getChildren().size(); i++) {
                        Label stepName = new Label(((TitledPane) stepsNames.getChildren().get(i)).getText());
                        stepName.setTextFill(Color.GREY);
                        executedStepsStatusListView.getItems().add(stepName);
                    }

                    stepDetailsNameLabel.setText("select a step from the list");
                    stepDetailsDurationLabel.setText("to get");
                    stepDetailsResultLabel.setText("further details");
                    currentFlowStepsExecutionTableDataMap.clear();
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

    public void stop() {
        if (poller != null) {
            poller.shutdown();
        }
    }

    public void updateExecutionProgess(ExecutedFlowDetailsDTO executionProgressDTO) {
        try {
            if (executionProgressDTO.getFlowExecutionProgress() != flowProgressBar.getProgress()) {
                Platform.runLater(() -> {
                    synchronized (this) {
                        updateFlowProgressIndicators(executionProgressDTO);
                        updateStepsDetailsSection(executionProgressDTO);
                    }
                });
            }
        } catch (NullPointerException ignored) {}
        if (executionProgressDTO.isExecutionInProgress() || updatedAfterExecutionEnd) {
            updatedAfterExecutionEnd = executionProgressDTO.isExecutionInProgress();
            String message = "execution ended with " + executionProgressDTO.getFlowExecutionResult() + "!";
            Platform.runLater(() -> {
                executionEndLabel.setText("\"" + executionProgressDTO.getFlowName() + "\" " + message + "!");
                GUI.utils.Utils.ShowInformation("Heads up!", message, "");
                flowProgressBar.setProgress(0);
                doneExecutionPaneSwitch();
            });
        }
    }

    private void updateStepsDetailsSection(ExecutedFlowDetailsDTO executionProgressDTO) {
        updateStepInProgressLabel(executionProgressDTO.getCurrentStepName());
        updateExecutedStepsStatusListView(executionProgressDTO.getStepsResult());
        updateSingleStepExecutionData(executionProgressDTO.getAllStepsListOfLogs(),
                executionProgressDTO.getOutputsForAllSteps(),
                executionProgressDTO.getStepsResult(),
                executionProgressDTO.getSteps2DurationInMillis(),
                executionProgressDTO.getStepsSummaryLine());
    }

    private void updateFlowProgressIndicators(ExecutedFlowDetailsDTO executionProgressDTO) {
        float flowProgressPercentage = executionProgressDTO.getFlowExecutionProgress();
        updateFlowProgressPercentageLabel(flowProgressPercentage);
        updateFlowProgressBar(flowProgressPercentage);
    }
}
