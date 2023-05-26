package body.execution;

import body.BodyControllerComponent;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ExecutionController extends BodyControllerComponent {

    @FXML private AnchorPane flowExecutionMainAnchorPane;
    @FXML private AnchorPane realExecutionAnchorPane;
    // fake section
    @FXML private VBox fakeLoadingVBox = new VBox();
    @FXML private ProgressBar fakeProgressBar = new ProgressBar();
    @FXML private Label fakeLoadingLabel = new Label("One moment please...");
    //

    public void initialize() {
        flowExecutionMainAnchorPane.setDisable(true);

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
