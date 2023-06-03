package stepper.dto.execution.history;

import stepper.flow.execution.FlowExecution;

import java.util.*;
import java.util.stream.Collectors;

public class FlowsExecutionHistoryDTO {
    public enum SortFilter {
        NAME,
        TIME,

        RESULT,

    }

    // each DTO contains a list with 3 elements: name, time and result.
    final List<SingleFlowExecutionDTO> flowExecutionDTOs = new ArrayList<>();

    public FlowsExecutionHistoryDTO(Stack<FlowExecution> flowExecutionStack) {
        for(FlowExecution flow:flowExecutionStack){
            SingleFlowExecutionDTO flowExDTO = new SingleFlowExecutionDTO(flow.getName(),flow.getFormattedStartTime(),
                    flow.getFlowExecutionResult());
            this.flowExecutionDTOs.add(flowExDTO);
        }
    }

    public void sortedFlowExecutionDTOsBy(SortFilter sortFilter){
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

    public List<List<Object>> getFlowExecutionHistoryDetailsRows() {
        return flowExecutionDTOs.stream().map(x->x.getFlowData()).collect(Collectors.toList());
    }


    //    private class ComparatorByName implements Comparator<SingleFlowExecutionDTO> {
//        public int compare(SingleFlowExecutionDTO x, SingleFlowExecutionDTO y) {
//            // TODO: Handle possible null values
//            return x.getFlowName().compareTo(y.getFlowName());
//        }
//    }
//    private class ComparatorByTime implements Comparator<SingleFlowExecutionDTO> {
//        public int compare(SingleFlowExecutionDTO x, SingleFlowExecutionDTO y) {
//            // TODO: Handle possible null values
//            return x.getStartTime().compareTo(y.getStartTime());
//        }
//    }
//    private class ComparatorByRes implements Comparator<SingleFlowExecutionDTO> {
//        public int compare(SingleFlowExecutionDTO x, SingleFlowExecutionDTO y) {
//            // TODO: Handle possible null values
//            return x.getFlowExecutionResult().compareTo(y.getFlowExecutionResult());
//        }
//    }
}
