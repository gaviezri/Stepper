package dto.flow;

import dto.AbstractDTO;

import java.util.List;

public class ManyFlowDefinitionsDTO extends AbstractDTO {
    private final List<FlowDefinitionDTO> flowDefinitions;

    public ManyFlowDefinitionsDTO(List<FlowDefinitionDTO> flowDefinitions) {
        this.flowDefinitions = flowDefinitions;
    }

    public List<FlowDefinitionDTO> getFlowDefinitions() {
        return flowDefinitions;
    }
}
