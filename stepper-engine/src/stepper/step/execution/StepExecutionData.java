package stepper.step.execution;

import stepper.dd.api.AbstractDataDefinition;
import stepper.step.api.StepDefinition;
import stepper.step.api.enums.StepResult;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class StepExecutionData {
    private StepResult stepResult;
    private Instant start;
    private Duration totalDuration;
//    private Map<String, Object> data;

    public Duration getDuration() {
        return totalDuration;
    }
    public void start() {
        start = Instant.now();
    }
    public void stop() {
        totalDuration = java.time.Duration.between(start, Instant.now());
    }
    public StepResult getStepResult() {
        return stepResult;
    }
    public void setStepResult(StepResult stepResult) {
        this.stepResult = stepResult;
    }
}
