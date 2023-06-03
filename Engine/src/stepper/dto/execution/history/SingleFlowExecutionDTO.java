package stepper.dto.execution.history;

import stepper.flow.execution.FlowExecutionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SingleFlowExecutionDTO {
    private String flowName;
    private String startTime; // need to be in format: {dd-mm-yyyy hh:mm:ss}
    private FlowExecutionResult flowExecutionResult;
    private  UUID uniqueId;
    private Map<String, Object> dataName2value;

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Map<String, Object> getDataName2value() {
        return dataName2value;
    }

    public SingleFlowExecutionDTO(String flowName, String startTimeInstant, FlowExecutionResult flowExecutionResult, UUID uniqueId, Map<String, Object> dataName2value) {
        this.flowName = flowName;
        this.startTime = startTimeInstant;
        this.flowExecutionResult = flowExecutionResult;
        this.uniqueId = uniqueId;
        this.dataName2value = dataName2value;
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
