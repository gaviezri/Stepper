package stepper.flow.execution.last.executed.data.center;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import stepper.step.api.enums.StepResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LastExecutedDataCenter {
    private String lastExecutedFlowName;
    private UUID lastExecutedFlowUUID;
    private String currentStepName;
    private Integer currentStepIdx;
    private Integer stepsCount;
    private Map<String, Object> executionDataValues;
    private Map<String, StepResult> stepResults;
    private Boolean isFlowInProgress = false;

    private static LastExecutedDataCenter instance = new LastExecutedDataCenter();
    private LastExecutedDataCenter() {
        // Exists only to defeat instantiation.
    }
    public static String getLastExecutedFlowName() {
        return instance.lastExecutedFlowName;
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
    }

    public static String getCurrentStepName() {
        return instance.currentStepName;
    }

    public static synchronized void setCurrentStepName(String currentStepName, UUID lastExecutedFlowUUID) {
        if (instance.lastExecutedFlowUUID.equals(lastExecutedFlowUUID)) {
            instance.currentStepName = currentStepName;
        }
    }

    public static  Integer getCurrentStepIdx() {
        return instance.currentStepIdx;
    }

    public static synchronized void setCurrentStepIdx(Integer currentStepIdx, UUID lastExecutedFlowUUID) {
        if (instance.lastExecutedFlowUUID.equals(lastExecutedFlowUUID)) {
            instance.currentStepIdx = currentStepIdx;
        }
    }

    public static Integer getStepsCount() {
        return instance.stepsCount;
    }

    public static synchronized void setStepsCount(Integer stepsCount, UUID lastExecutedFlowUUID) {
        if (instance.lastExecutedFlowUUID.equals(lastExecutedFlowUUID)) {
            instance.stepsCount = stepsCount;
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

    public static Map<String,StepResult> getExecutedStepsStatus() {
        return instance.stepResults;
    }

    public static boolean isFlowExecutionInProgress() {
        return instance.lastExecutedFlowUUID != null;
    }

    public static void startFlow(UUID lastExecutedFlowUUID) {
        if (instance.lastExecutedFlowUUID.equals(lastExecutedFlowUUID)) {
            instance.isFlowInProgress = true;
        }
    }

    public static void endFlow(UUID lastExecutedFlowUUID) {
        if (instance.lastExecutedFlowUUID.equals(lastExecutedFlowUUID)) {
            instance.isFlowInProgress = false;
        }
    }
}
