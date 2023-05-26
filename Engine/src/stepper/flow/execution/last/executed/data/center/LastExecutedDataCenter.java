package stepper.flow.execution.last.executed.data.center;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

import java.util.UUID;

public class LastExecutedDataCenter {
    private String lastExecutedFlowName;
    private UUID lastExecutedFlowUUID;
    private String currentStepName;
    private Integer currentStepIdx;
    private Integer stepsCount;

    private static LastExecutedDataCenter instance = null;
    private LastExecutedDataCenter() {
        // Exists only to defeat instantiation.
    }
    public static String getLastExecutedFlowName() {
        return instance.lastExecutedFlowName;
    }

    public static synchronized String setLastExecutedFlowName(String lastExecutedFlowName, UUID lastExecutedFlowUUID) {
        if (instance.lastExecutedFlowUUID == lastExecutedFlowUUID) {
            instance.lastExecutedFlowName = lastExecutedFlowName;
        }
        return  lastExecutedFlowName;
    }

    public static UUID getLastExecutedFlowUUID() {
        return instance.lastExecutedFlowUUID;
    }

    public static synchronized void setLastExecutedFlowUUID(UUID lastExecutedFlowUUID) {
        instance.lastExecutedFlowUUID = lastExecutedFlowUUID;
    }

    public static String getCurrentStepName() {
        return instance.currentStepName;
    }

    public static synchronized void setCurrentStepName(String currentStepName, UUID lastExecutedFlowUUID) {
        if (instance.lastExecutedFlowUUID == lastExecutedFlowUUID) {
            instance.currentStepName = currentStepName;
        }
    }

    public static  Integer getCurrentStepIdx() {
        return instance.currentStepIdx;
    }

    public static synchronized void setCurrentStepIdx(Integer currentStepIdx, UUID lastExecutedFlowUUID) {
        if (instance.lastExecutedFlowUUID == lastExecutedFlowUUID) {
            instance.currentStepIdx = currentStepIdx;
        }
    }

    public static Integer getStepsCount() {
        return instance.stepsCount;
    }

    public static synchronized void setStepsCount(Integer stepsCount, UUID lastExecutedFlowUUID) {
        if (instance.lastExecutedFlowUUID == lastExecutedFlowUUID) {
            instance.stepsCount = stepsCount;
        }
    }
}
