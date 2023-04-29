package stepper.flow.execution;

import stepper.flow.definition.api.FlowDefinition;
import stepper.flow.definition.api.StepUsageDeclaration;
import stepper.step.api.enums.StepResult;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class FlowExecution {

    private final UUID uniqueId;
    private final FlowDefinition flowDefinition;
    private FlowExecutionResult flowExecutionResult = FlowExecutionResult.SUCCESS;
    private String formattedStartTime;
    private Instant startTimeInstant;
    private Instant endTimeInstant;
    private Duration duration;


    // lots more data that needed to be stored while flow is being executed...

    public FlowExecution(FlowDefinition flowDefinition) {
        this.uniqueId = UUID.randomUUID();
        this.flowDefinition = flowDefinition;
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        this.formattedStartTime = time.format(formatter);
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void tick(){
        startTimeInstant = Instant.now();
    }

    public FlowDefinition getFlowDefinition() {
        return flowDefinition;
    }
    public List<StepUsageDeclaration> getFlowSteps() {
        return flowDefinition.getFlowSteps();
    }

    public FlowExecutionResult getFlowExecutionResult() {
        return flowExecutionResult;
    }
    public void updateExecutionResult(StepResult stepExecResult, Boolean skipIfFail){
        // the flow initally is success, and will be changed to failure only if a step fails and skipIfFail is false
        // will change to warning if a step fails and skipIfFail is true or step is warning
        // once changed to warning, cannot be changed back to success
        // once changed to failure, cannot be changed back to warning or success
        if (stepExecResult == StepResult.FAILURE && !skipIfFail){
            flowExecutionResult = FlowExecutionResult.FAILURE;
        }
        else if (stepExecResult == StepResult.FAILURE && skipIfFail){
            flowExecutionResult = FlowExecutionResult.WARNING;
        }
        else if (stepExecResult == StepResult.WARNING && flowExecutionResult == FlowExecutionResult.SUCCESS){
            flowExecutionResult = FlowExecutionResult.WARNING;
        }
    }
    public void tock(){
        endTimeInstant = Instant.now();
        duration = Duration.between(startTimeInstant, endTimeInstant);
    }

    public Duration getDuration() {
        return duration;
    }

    public String getFormattedStartTime() {
        return formattedStartTime;
    }
}
