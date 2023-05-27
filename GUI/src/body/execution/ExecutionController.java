package body.execution;

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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import stepper.flow.execution.last.executed.data.center.LastExecutedDataCenter;
import stepper.step.api.enums.StepResult;

import java.util.List;
import java.util.Map;
import java.util.Observable;
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
    @FXML private FlowPane selectedStepDetailsFlowPane;
    @FXML private Label stepInProgressLabel;
    @FXML private Label flowProgressPercentageLabel;
    @FXML private ProgressBar flowProgressBar;
    // continuation section
    // summary section
    // step details section
    private static final int POLLING_INTERVAL = 200;
    ScheduledExecutorService poller = Executors.newScheduledThreadPool(1);



    public void initialize() {
        flowExecutionMainAnchorPane.setDisable(true);
        flowProgressPercentageLabel.setWrapText(true);
        initializeStepsStatusToolTip();
        bindExecutionTabComponents();


    }

    public void bindExecutionTabComponents() {
        poller.scheduleAtFixedRate(() -> {
            try {
                Thread.sleep(POLLING_INTERVAL* 5);
            } catch (InterruptedException ignored) {
            }
            if (realExecutionAnchorPane.isVisible() && LastExecutedDataCenter.isFlowExecutionInProgress()) {
                Platform.runLater(() -> {
                float flowProgressPercentage =  (LastExecutedDataCenter.getCurrentStepIdx() + 1.f) / LastExecutedDataCenter.getStepsCount();
                updateFlowProgressPercentageLabel(flowProgressPercentage);
                updateFlowProgressBar(flowProgressPercentage);
                updateStepInProgressLabel(LastExecutedDataCenter.getCurrentStepName());
                updateExecutedStepsStatusListView(LastExecutedDataCenter.getExecutedStepsStatus());
            });
            }
        }, 0, POLLING_INTERVAL, java.util.concurrent.TimeUnit.MILLISECONDS);

        flowProgressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.floatValue() == 1.0f) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                }
                Platform.runLater(() -> {
                    utils.Utils.ShowInformation("Heads up!", "Flow execution completed successfully", "");
//                    flowProgressBar.setVisible(false);
//                    flowProgressPercentageLabel.setVisible(false);
//                    stepInProgressLabel.setVisible(false);
                    //continuationPane.setVisible(true);
                    //executionSummaryLabel.setVisible(true);
                    // bind continuation pane and execution summary label progressbar
                });


            }
        });

    }

    private void updateExecutedStepsStatusListView(Map<String, StepResult> executedStepsStatus) {

        executedStepsStatus.forEach((stepName, stepResult) -> {
            executedStepsStatusListView.getItems().forEach(item -> {
                Label curItem = (Label) item;
                if (curItem.getText().equals(stepName)) {
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
        executedStepsStatusListViewToolTip.setGraphic(textFlow);
        executedStepsStatusListViewToolTip.wrapTextProperty().setValue(true);
        executedStepsStatusListViewToolTip.setText("");

        executedStepsStatusListView.setOnMouseEntered(event -> {
            executedStepsStatusListViewToolTip.show(executedStepsStatusListView, event.getScreenX(), event.getScreenY());
        });

        executedStepsStatusListView.setOnMouseExited(event -> {
            executedStepsStatusListViewToolTip.hide();
        });

    }
    /**
     * CHANGE THIS , LISTENING TO THE WRONG FOLK!
     * */

    public void bindFlowExecutionElementsToSelectButton(DefinitionController definitionController, InputController inputController) {

        definitionController.getFlowDefAvailableFlowsList().getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            flowProgressBar.setVisible(true);
            flowProgressPercentageLabel.setVisible(true);
            stepInProgressLabel.setVisible(true);
            // continuationPane.setVisible(false);
            // executionSummaryLabel.setVisible(false);
            executedStepsStatusListView.getItems().clear();
            VBox stepsNames = (VBox) definitionController.getStepsTitledPane().getContent();
            for(int i = 0; i < stepsNames.getChildren().size(); i++) {
                Label stepName = new Label(((TitledPane) stepsNames.getChildren().get(i)).getText());
                stepName.setTextFill(javafx.scene.paint.Color.GREY);
                executedStepsStatusListView.getItems().add(stepName);
            }
        }));
    }
    public void bindFakeSectionToExecutionEnablement(TabPane mainTabPane) {
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == mainTabPane.getTabs().get(1)) {
                realExecutionAnchorPane.setVisible(false);
                fakeLoadingVBox.setVisible(true);
                // animate fake progress bar for 2 seconds/
                // then show real execution pane and hide fake loading pane
                KeyFrame begin =  new KeyFrame(Duration.ZERO, new KeyValue(fakeProgressBar.progressProperty(), 0));
                KeyFrame end = new KeyFrame(Duration.seconds(1), new KeyValue(fakeProgressBar.progressProperty(), 1));
                Timeline timeline = new Timeline(begin, end);
                timeline.setCycleCount(1);
                timeline.setOnFinished(event -> {
                    realExecutionAnchorPane.setVisible(true);
                    fakeLoadingVBox.setVisible(false);
                });
                timeline.play();
            }
        });
    }

}
