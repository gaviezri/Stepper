package stepper.dto.flow;

import stepper.dto.AbstractDTO;

import java.util.List;

public class FlowNamesDTO extends AbstractDTO {
    List<String> flowNames;

    public FlowNamesDTO(List<String> flowNames) {
        this.flowNames = flowNames;
    }
    public FlowNamesDTO(String errorMessage) {
        super(errorMessage);
    }


    public List<String> getFlowNames() {
        return flowNames;
    }


}
