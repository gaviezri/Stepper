package stepper.flow.execution.runner;

import stepper.flow.execution.FlowExecutionResult;
import stepper.flow.execution.last.executed.data.center.LastExecutedDataCenter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * FlowExecutorsManager is responsible for managing the execution of the flows.
 * It is responsible for:
 * 1. Creating the thread pool for the flows execution.
 * 2. Managing the execution of the flows.
 * 3. Managing the status query of the flows.
 * */
public class FlowExecutorsManager implements Serializable {

    private int workersCount;
    private ExecutorService executorService = null;
    public void prepareForSerialization() {
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }

    public void wakeUp() {
        if (workersCount>0){
            executorService = Executors.newFixedThreadPool(workersCount);
        }
    }

    public enum FlowExecutionStatus {
        RUNNING, FINISHED, FAILED, NOT_FOUND
    }

    // future will hold the result of the flow execution
    Map<UUID, Future<FlowExecutionResult>> UUID2Execution = new HashMap<UUID, Future<FlowExecutionResult>>();

    public void setWorkersCount(int workersCount) {
        if (executorService != null){
            executorService.shutdown();
        }
        this.workersCount = workersCount;
        executorService = Executors.newFixedThreadPool(this.workersCount);
    }

    public void executeFlow(FlowExecutor flowExecutor) {
        UUID latestUUID = flowExecutor.getFlowUUID();
        LastExecutedDataCenter.setLastExecutedFlowUUID(latestUUID);
        UUID2Execution.put(latestUUID, executorService.submit(flowExecutor));
    }

    public FlowExecutionStatus getFlowExecutionStatus(UUID flowUUID) {
        if (UUID2Execution.containsKey(flowUUID)) {
            Future<FlowExecutionResult> future = UUID2Execution.get(flowUUID);
            if (future.isDone()) {
                try {
                    future.get();
                    return FlowExecutionStatus.FINISHED;
                } catch (Exception e) {
                    return FlowExecutionStatus.FAILED;
                }
            } else {
                return FlowExecutionStatus.RUNNING;
            }
        } else {
            return FlowExecutionStatus.NOT_FOUND;
        }
    }
}