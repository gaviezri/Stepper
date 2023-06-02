package stepper.flow.execution.last.executed.data.center;
import com.sun.applet2.AppletParameters;
import javafx.util.Pair;
import stepper.dd.api.DataDefinition;
import stepper.flow.execution.FlowExecutionResult;
import stepper.step.api.enums.StepResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.Duration;

public class LastExecutedDataCenter {
    private String lastExecutedFlowName;
    private UUID lastExecutedFlowUUID;
    private String currentStepName;
    private Integer currentStepIdx;
    private Integer stepsCount;
    private Map<String, Object> executionDataValues;
    private Map<String, StepResult> stepResults;
    private Map<String, List<String>> step2ListOfLogs;
    private Map<String ,Map<String,Pair<DataDefinition,Object>>> step2MapOfOutputsNames2DataDefAndValue;
    private Map<String, Duration> step2Duration = new HashMap<>();
    private Map<String,String> step2SummaryLine = new HashMap<>();
    private FlowExecutionResult flowExecutionResult;
    private Boolean isFlowInProgress = false;

    private static LastExecutedDataCenter instance = new LastExecutedDataCenter();


    private LastExecutedDataCenter() {
        // Exists only to defeat instantiation.
    }

    public static void setStepLogs(String finalStepName, List<String> stepLogs, UUID flowUUID) {
        if (instance.lastExecutedFlowUUID.equals(flowUUID)) {
            instance.step2ListOfLogs.put(finalStepName, stepLogs);
        }
    }

    public static void setStepSummaryLine(String finalStepName, String stepSummaryLine, UUID flowUUID) {
        if (instance.lastExecutedFlowUUID.equals(flowUUID)) {
            instance.step2SummaryLine.put(finalStepName, stepSummaryLine);
        }
    }
    public static void setStepOutputs(String finalStepName, Map<String, Pair<DataDefinition, Object>> stepOutputs, UUID flowUUID) {
        if (instance.lastExecutedFlowUUID.equals(flowUUID)) {
            instance.step2MapOfOutputsNames2DataDefAndValue.put(finalStepName, stepOutputs);
        }
    }

    public String getLastExecutedFlowName() {
        return lastExecutedFlowName;
    }

    public static synchronized String setLastExecutedFlowName(String lastExecutedFlowName, UUID lastExecutedFlowUUID) {
        if (instance.lastExecutedFlowUUID.equals(lastExecutedFlowUUID)) {
            instance.lastExecutedFlowName = lastExecutedFlowName;
        }
        return  lastExecutedFlowName;
    }

    public static UUID getLastExecutedFlowUUID() {
        return instance.lastExecutedFlowUUID;
    }

    public static synchronized void setLastExecutedFlowUUID(UUID lastExecutedFlowUUID) {
        instance.lastExecutedFlowUUID = lastExecutedFlowUUID;
        instance.executionDataValues = new HashMap<>();
        instance.stepResults = new HashMap<>();
        instance.step2ListOfLogs = new HashMap<>();
        instance.step2MapOfOutputsNames2DataDefAndValue = new HashMap<>();
        instance.step2Duration = new HashMap<>();
        instance.flowExecutionResult = null;

    }

    public static synchronized void setCurrentStepName(String currentStepName, UUID lastExecutedFlowUUID) {
        if (instance.lastExecutedFlowUUID.equals(lastExecutedFlowUUID)) {
            instance.currentStepName = currentStepName;
        }
    }

    public static synchronized void setCurrentStepIdx(Integer currentStepIdx, UUID lastExecutedFlowUUID) {
        if (instance.lastExecutedFlowUUID.equals(lastExecutedFlowUUID)) {
            instance.currentStepIdx = currentStepIdx;
        }
    }

    public static synchronized void setStepsCount(Integer stepsCount, UUID lastExecutedFlowUUID) {
        if (instance.lastExecutedFlowUUID.equals(lastExecutedFlowUUID)) {
            instance.stepsCount = stepsCount;
        }
    }

    public static synchronized  void setStepsDuration(String stepName,Duration duration, UUID lastExecutedFlowUUID) {
        if (instance.lastExecutedFlowUUID.equals(lastExecutedFlowUUID)) {
            instance.step2Duration.put(stepName, duration);
        }
    }

    public static synchronized void setExecutionOutputs(Map<String, Object> executionData, UUID lastExecutedFlowUUID) {
        if (instance.lastExecutedFlowUUID.equals(lastExecutedFlowUUID)) {
            instance.executionDataValues = executionData;
        }
    }

    public static synchronized void setStepResult(String finalStepName, StepResult stepResult, UUID lastExecutedFlowUUID) {
        if (instance.lastExecutedFlowUUID.equals(lastExecutedFlowUUID)) {
            instance.stepResults.put(finalStepName, stepResult);
        }
    }

    public  Map<String,StepResult> getExecutedStepsStatus() {
        return stepResults;
    }

    public Map<String,List<String>> getAllStepsListOfLogs() {
        return step2ListOfLogs;
    }

    public Map<String,Map<String,Pair<DataDefinition,Object>>> getOutputsForAllSteps() {
        return step2MapOfOutputsNames2DataDefAndValue;
    }

    public String getCurrentStepName() {
        return currentStepName;
    }

    public  Integer getCurrentStepIdx() {
        return currentStepIdx;
    }

    public FlowExecutionResult getFlowExecutionResult() {
        return flowExecutionResult;
    }

    public static LastExecutedDataCenter getInstance() {
        return instance;
    }

    public static Map<String,Duration> getAllStepsDuration() {
        return instance.step2Duration;
    }

    public Integer getStepsCount() {
        return stepsCount;
    }

    public static void startFlow(UUID lastExecutedFlowUUID) {
        if (instance.lastExecutedFlowUUID.equals(lastExecutedFlowUUID)) {
            instance.isFlowInProgress = true;
        }
    }

    public static void endFlow(UUID lastExecutedFlowUUID, FlowExecutionResult flowExecutionResult) {
        if (instance.lastExecutedFlowUUID.equals(lastExecutedFlowUUID)) {
            instance.isFlowInProgress = false;
            instance.flowExecutionResult = flowExecutionResult;
        }
    }

    public static void reset() {
        instance = new LastExecutedDataCenter();
    }

    public boolean isFlowExecutionInProgress()
    {
        return isFlowInProgress;
    }

    public Map<String, String> getAllSummaryLines() {
        return step2SummaryLine;
    }
}
