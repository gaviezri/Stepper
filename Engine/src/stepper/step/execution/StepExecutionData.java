package stepper.step.execution;

import javafx.util.Pair;
import stepper.dd.api.AbstractDataDefinition;
import stepper.dd.api.DataDefinition;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.step.api.StepDefinition;
import stepper.step.api.enums.StepResult;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class StepExecutionData implements Serializable {
    private StepResult stepResult;
    private Instant start;
    private Duration totalDuration;
    private Map<String, Pair<DataDefinition,Object>> dataName2DefinitionAndValue = new HashMap<>();

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

    public void addStepOutput(String finalDataName, Object value, DataDefinition dataDefinition) {
        dataName2DefinitionAndValue.put(finalDataName, new Pair<>(dataDefinition, value));
    }

    public Map<String, Pair<DataDefinition, Object>> getStepOutputs() {
        return dataName2DefinitionAndValue;
    }
}
