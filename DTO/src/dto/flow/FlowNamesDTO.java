package dto.flow;

import dto.AbstractDTO;

import java.util.List;

public class FlowNamesDTO extends AbstractDTO {
    List<String> flowNames;

    public FlowNamesDTO(List<String> flowNames) {
        this.flowNames = flowNames;
    }
    public FlowNamesDTO(String errorMessage) {
        super(errorMessage);
    }

    public int size() {
        return flowNames == null ? 0 : flowNames.size();
    }
    public List<String> getFlowNames() {
        return flowNames;
    }


}
