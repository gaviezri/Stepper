package dto.execution.history;

import stepper.flow.execution.FlowExecution;
import stepper.flow.execution.FlowExecutionResult;
import stepper.step.api.enums.StepResult;

import java.util.*;
import java.util.stream.Collectors;

public class FlowsExecutionHistoryDTO {
    public enum SortFilter {
        NAME,
        TIME,
        RESULT,
    }

    private final List<SingleFlowExecutionDTO> flowExecutionDTOs = new ArrayList<>();
    private List<SingleFlowExecutionDTO> curFilteredExecutions = flowExecutionDTOs;

    public FlowsExecutionHistoryDTO(Stack<FlowExecution> flowExecutionStack) {
        for(FlowExecution flowExecution:flowExecutionStack){
            SingleFlowExecutionDTO flowExDTO = new SingleFlowExecutionDTO(flowExecution);
            this.flowExecutionDTOs.add(flowExDTO);
        }
    }

    public FlowsExecutionHistoryDTO(List<SingleFlowExecutionDTO> flowExecutionsList, Map<UUID,Integer> uuid2Cookie, Map<Integer,String> cookie2Name){
        this.flowExecutionDTOs.addAll(flowExecutionsList);
        this.flowExecutionDTOs.stream()
                .forEach(x-> x.setExecutingUserName(cookie2Name.
                                get(uuid2Cookie.get(x.getUniqueId()))));
    }
    public FlowsExecutionHistoryDTO(List<SingleFlowExecutionDTO> flowExecutionsList){
        this.flowExecutionDTOs.addAll(flowExecutionsList);
    }

    public List<SingleFlowExecutionDTO> filterFlowExecutionDTOsBy(FlowExecutionResult filter){
        curFilteredExecutions =  filter.equals(FlowExecutionResult.NONE) ? flowExecutionDTOs : flowExecutionDTOs.stream().filter(x->x.getFlowExecutionResult().equals(filter)).collect(Collectors.toList());
        return curFilteredExecutions;
    }
    public void sortFlowExecutionDTOsBy(SortFilter sortFilter){

        switch (sortFilter){
            case NAME:
                Collections.sort(curFilteredExecutions,new Comparator<SingleFlowExecutionDTO>() {
                        public int compare(SingleFlowExecutionDTO x, SingleFlowExecutionDTO y) {
                            return x.getFlowName().compareTo(y.getFlowName());
                        }
                    });
                break;
            case TIME:
                Collections.sort(curFilteredExecutions,new Comparator<SingleFlowExecutionDTO>() {
                    public int compare(SingleFlowExecutionDTO x, SingleFlowExecutionDTO y) {
                        return x.getStartTime().compareTo(y.getStartTime());
                    }
                });
                break;
            case RESULT:
                Collections.sort(curFilteredExecutions,new Comparator<SingleFlowExecutionDTO>() {
                    public int compare(SingleFlowExecutionDTO x, SingleFlowExecutionDTO y) {
                        return x.getFlowExecutionResult().compareTo(y.getFlowExecutionResult());
                    }
                });
                break;
            default:
                throw new IllegalArgumentException("No such filter to sort by.");
        }
    }

    public List<SingleFlowExecutionDTO> getFlowExecutionDTOs() {
        return curFilteredExecutions;
    }

    public SingleFlowExecutionDTO getFlowExecutionDTOByIndex(int index){
        return flowExecutionDTOs.get(index);
    }

}
