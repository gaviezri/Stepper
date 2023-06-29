package dto.execution.progress;

public class ExecutionProgressDTO {
    ExecutedFlowDetailsDTO executedFlowDetailsDTO;
    boolean isExecutionInProgress;

    public ExecutionProgressDTO(ExecutedFlowDetailsDTO executedFlowDetailsDTO, boolean isExecutionInProgress) {
        this.executedFlowDetailsDTO = executedFlowDetailsDTO;
        this.isExecutionInProgress = isExecutionInProgress;
    }

    public ExecutedFlowDetailsDTO getExecutedFlowDetailsDTO() {
        return executedFlowDetailsDTO;
    }

    public boolean isExecutionInProgress() {
        return isExecutionInProgress;
    }
}
