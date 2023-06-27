package dto.flow;

import java.util.List;

public class ManyFlowDefinitionsDTO {
    private List<FlowDefinitionDTO> flowDefinitions;

    public ManyFlowDefinitionsDTO(List<FlowDefinitionDTO> flowDefinitions) {
        this.flowDefinitions = flowDefinitions;
    }

    public List<FlowDefinitionDTO> getFlowDefinitions() {
        return flowDefinitions;
    }
}
