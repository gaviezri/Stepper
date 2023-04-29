package stepper.flow.execution;

import stepper.flow.definition.api.FlowDefinition;
import stepper.flow.definition.api.StepUsageDeclaration;

import java.sql.Time;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class FlowExecution {

    private final UUID uniqueId;
    private final FlowDefinition flowDefinition;
    private FlowExecutionResult flowExecutionResult = FlowExecutionResult.FAILURE;
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

    public FlowDefinition getFlowDefinition() {
        return flowDefinition;
    }
    public List<StepUsageDeclaration> getFlowSteps() {
        return flowDefinition.getFlowSteps();
    }

    public FlowExecutionResult getFlowExecutionResult() {
        return flowExecutionResult;
    }
}
