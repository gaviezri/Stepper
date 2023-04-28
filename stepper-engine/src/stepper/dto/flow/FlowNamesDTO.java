package stepper.dto.flow;

import stepper.dto.DTO;

import java.util.List;

public class FlowNamesDTO implements DTO {
    List<String> flowNames;
    String errorMessage;
    boolean success;

    public FlowNamesDTO(List<String> flowNames, boolean success, String errorMessage) {
        this.flowNames = flowNames;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public FlowNamesDTO(List<String> flowNames) {
        this(flowNames, true, "");
    }

    public List<String> getFlowNames() {
        return flowNames;
    }

    @Override
    public boolean getStatus() {
        return success;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public Class<?> getType() {
        return this.getClass();
    }
}
