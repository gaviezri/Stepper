package stepper.step.execution;

import stepper.dd.api.AbstractDataDefinition;
import stepper.step.api.StepDefinition;
import stepper.step.api.enums.StepResult;

import java.util.Map;

public class StepExecutionData {
    private StepResult stepResult;
    private Number Duration;

    public Number getDuration() {
        return Duration;
    }
    public void setDuration(Number duration) {
        Duration = duration;
    }
    public StepResult getStepResult() {
        return stepResult;
    }
    public void setStepResult(StepResult stepResult) {
        this.stepResult = stepResult;
    }
}
