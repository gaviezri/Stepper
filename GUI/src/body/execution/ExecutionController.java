package body.execution;

import body.BodyControllerComponent;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class ExecutionController extends BodyControllerComponent {

    @FXML private AnchorPane flowExecutionMainAnchorPane;
    @FXML private AnchorPane realExecutionAnchorPane;
    // fake section
    @FXML private VBox fakeLoadingVBox = new VBox();
    @FXML private ProgressBar fakeProgressBar = new ProgressBar();
    @FXML private Label fakeLoadingLabel = new Label("One moment please...");
    @FXML private ListView executedStepsStatusListView;
    @FXML private Tooltip executedStepsStatusListViewToolTip;
    //

    public void initialize() {
        flowExecutionMainAnchorPane.setDisable(true);
        initializeStepsStatusToolTip();

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

    public void bindTabToStartButton() {
    }

    public void bindFakeSectionToExecutionEnablement(TabPane mainTabPane) {
        // REFACTOR/////////////////////
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
