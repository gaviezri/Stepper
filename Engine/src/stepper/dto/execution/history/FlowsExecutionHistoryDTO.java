package stepper.dto.execution.history;

import stepper.flow.execution.FlowExecution;

import java.util.*;

public class FlowsExecutionHistoryDTO {
    public enum SortFilter {
        NAME,
        TIME,

        RESULT,

    }


    final List<SingleFlowExecutionDTO> flowExecutionDTOs = new ArrayList<>();

    public FlowsExecutionHistoryDTO(Stack<FlowExecution> flowExecutionStack) {
        for(FlowExecution flowExecution:flowExecutionStack){
            SingleFlowExecutionDTO flowExDTO = new SingleFlowExecutionDTO(flowExecution);
            this.flowExecutionDTOs.add(flowExDTO);
        }
    }

    public void sortFlowExecutionDTOsBy(SortFilter sortFilter){
        List<SingleFlowExecutionDTO> res;

        switch (sortFilter){
            case NAME:
                Collections.sort(flowExecutionDTOs,new Comparator<SingleFlowExecutionDTO>() {
                        public int compare(SingleFlowExecutionDTO x, SingleFlowExecutionDTO y) {
                            // TODO: Handle possible null values
                            return x.getFlowName().compareTo(y.getFlowName());
                        }
                    });
                break;
            case TIME:
                Collections.sort(flowExecutionDTOs,new Comparator<SingleFlowExecutionDTO>() {
                    public int compare(SingleFlowExecutionDTO x, SingleFlowExecutionDTO y) {
                        // TODO: Handle possible null values
                        return x.getStartTime().compareTo(y.getStartTime());
                    }
                });
                break;
            case RESULT:
                Collections.sort(flowExecutionDTOs,new Comparator<SingleFlowExecutionDTO>() {
                    public int compare(SingleFlowExecutionDTO x, SingleFlowExecutionDTO y) {
                        // TODO: Handle possible null values
                        return x.getFlowExecutionResult().compareTo(y.getFlowExecutionResult());
                    }
                });
                break;
            default:
                throw new IllegalArgumentException("No such filter to sort by.");

        }
    }

    public List<SingleFlowExecutionDTO> getFlowExecutionDTOs() {
        return flowExecutionDTOs;
    }

    public SingleFlowExecutionDTO getFlowExecutionDTOByIndex(int index){
        return flowExecutionDTOs.get(index);
    }

}
