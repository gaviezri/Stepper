package stepper.flow.execution.runner;

import stepper.flow.execution.FlowExecutionResult;

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
public class FlowExecutorsManager {
    public enum FlowExecutionStatus {
        RUNNING, FINISHED, FAILED, NOT_FOUND
    }
    //nullify when GUI is ready
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    // future will hold the result of the flow execution
    Map<UUID, Future<FlowExecutionResult>> UUID2Execution = new HashMap<UUID, Future<FlowExecutionResult>>();

    public void setWorkersCount(int workersCount) {
        if (executorService != null){
            executorService.shutdown();
        }
        executorService = Executors.newFixedThreadPool(workersCount);
    }

    public void executeFlow(FlowExecutor flowExecutor) {
            UUID2Execution.put(flowExecutor.getFlowUUID(), executorService.submit(flowExecutor));
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