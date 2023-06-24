package dto.flow;

import javafx.util.Pair;
import dto.AbstractDTO;
import stepper.flow.execution.FlowExecution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExecutedFlowDetailsDTO extends AbstractDTO {
    /* 1 */ final private String flowExecutionId;
    /* 2 */ final private String flowName;
    /* 3 */ final private String flowExecutionResult;
    /* 4 */ final private String executionTimeInMillis;
    /* 5.1 */ final List<String> freeInputsFinalNames;
    /* 5.2 */ final List<String> freeInputsTypes;
    /* 5.3 */ final List<String> freeInputsContent ;
    /* 5.4 */ final List<String> freeInputsNecessity;
    /* 6.1 */ final List<String> outputsFinalNames;
    /* 6.2 */ final List<String> outputsTypes;
    /* 6.3 */ final List<Object> outputsContent = new ArrayList<>();
    /* 7.1 */ final List<String> stepsNamesWithAlias;
    /* 7.2 */ final List<String> stepsDurationInMillis;
    /* 7.3 */ final List<String> stepsResult;
    /* 7.4 */ final List<String> stepsSummaryLine;
    /* 7.5 */ final List<List<Pair<String,String>>> stepsLogs2TimeStamp;

    public ExecutedFlowDetailsDTO(FlowExecution flowExecution) {
        freeInputsContent = new ArrayList<>();
        Map<String,String> headers = flowExecution.getFlowHeader();

        flowExecutionId = headers.get("ID");
        flowName = headers.get("Name");
        executionTimeInMillis = flowExecution.getDurationInMillis().toString();
        flowExecutionResult = String.valueOf(flowExecution.getFlowExecutionResult());
        freeInputsFinalNames = flowExecution.getFlowDefinition().getFreeInputsFinalNames();
        freeInputsTypes = flowExecution.getFlowDefinition().getFreeInputsTypes();
        for (String freeInputName : freeInputsFinalNames){
            try {
                freeInputsContent.add(flowExecution.getFreeInputContent(freeInputName));
            } catch (Exception e) {
                freeInputsContent.add("N/A");
            }
        }

        freeInputsNecessity = flowExecution.getFlowDefinition().getFreeInputsNecessity();
        outputsFinalNames = flowExecution.getFlowDefinition().getAllOutputsNames();
        outputsTypes = flowExecution.getFlowDefinition().getAllOutputsTypes();
        // TODO: The root function " FlowExecution.getAllOutputsContent() " was changed so that the return value will
        //  be Objects and not there string representatives
        outputsContent.addAll(flowExecution.getAllOutputsContent());

        stepsNamesWithAlias = flowExecution.getStepsNamesWithAlias();
        stepsDurationInMillis = flowExecution.getStepsDurationInMillis();
        stepsResult = flowExecution.getStepsResult();
        stepsSummaryLine = flowExecution.getStepsSummaryLine();
        stepsLogs2TimeStamp = flowExecution.getStepsLogs2TimeStamp();

    }

    public String getFlowName() {
        return flowName;
    }

    public String getFlowExecutionResult() {
        return flowExecutionResult;
    }

    public String getExecutionTimeInMillis() {
        return executionTimeInMillis;
    }

    public List<String> getFreeInputsFinalNames() {
        return freeInputsFinalNames;
    }

    public List<String> getFreeInputsTypes() {
        return freeInputsTypes;
    }

    public List<String> getFreeInputsContent() {
        return freeInputsContent;
    }

    public List<String> getFreeInputsNecessity() {
        return freeInputsNecessity;
    }

    public List<String> getOutputsFinalNames() {
        return outputsFinalNames;
    }

    public List<String> getOutputsTypes() {
        return outputsTypes;
    }

    public List<Object> getOutputsContent() {
        return outputsContent;
    }

    public List<String> getStepsNamesWithAlias() {
        return stepsNamesWithAlias;
    }

    public List<String> getStepsDurationInMillis() {
        return stepsDurationInMillis;
    }

    public List<String> getStepsResult() {
        return stepsResult;
    }

    public List<String> getStepsSummaryLine() {
        return stepsSummaryLine;
    }

    public List<List<Pair<String, String>>> getStepsLogs2TimeStamp() {
        return stepsLogs2TimeStamp;
    }

    public String getFlowExecutionId() {
        return flowExecutionId;
    }
}
