package stepper.dto.execution.history;

import stepper.flow.execution.FlowExecutionResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SingleFlowExecutionDTO {
    private String flowName;
    private String startTime; // need to be in format: {dd-mm-yyyy hh:mm:ss}
    private FlowExecutionResult flowExecutionResult;

    public SingleFlowExecutionDTO(String flowName, String startTimeInstant, FlowExecutionResult flowExecutionResult) {
        this.flowName = flowName;
        this.startTime = startTimeInstant;
        this.flowExecutionResult = flowExecutionResult;
    }

    public String getFlowName() {
        return flowName;
    }

    public String getStartTime() {
        return startTime;
    }

    public FlowExecutionResult getFlowExecutionResult() {
        return flowExecutionResult;
    }

    public List<Object> getFlowData(){
        List<Object> res = new ArrayList<>();

        res.add(flowName);
        res.add(startTime);
        res.add(flowExecutionResult);

        return res;
    }
}
