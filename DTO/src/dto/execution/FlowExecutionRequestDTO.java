package dto.execution;

import javafx.util.Pair;
import java.util.Map;

public class FlowExecutionRequestDTO {
    private final Pair<Map,Map> valName2ValType;
    private final String flowName;

    public FlowExecutionRequestDTO(Pair<Map,Map> valName2ValType, String flowName) {
        this.valName2ValType = valName2ValType;
        this.flowName = flowName;
    }

    public Pair<Map,Map> getValName2ValType() {
        return valName2ValType;
    }

    public String getFlowName() {
        return flowName;
    }

}
