package dto.step;

import stepper.flow.definition.api.StepUsageDeclaration;
import stepper.flow.definition.mapping.MappingGraph;

import java.util.ArrayList;
import java.util.List;


public class StepsDTO {
    final List<SingleStepDTO> steps = new ArrayList<>();

    public StepsDTO(List<StepUsageDeclaration> stepsUsageDecl, MappingGraph mappingGraph)
    {
        for(StepUsageDeclaration step:stepsUsageDecl){

            steps.add(new SingleStepDTO(step,
                    mappingGraph.getAllTargetStepsAndTargetInputFinalNamesBySourceStepFinalName(step.getFinalStepName()),
                    mappingGraph.getSourceStepAndSourceOutputFinalNamesByTargetStepFinalName(step.getFinalStepName())));
        }
    }
    public StepsDTO(StepsDTO other){
        for(SingleStepDTO step:other.steps){
            steps.add(new SingleStepDTO(step));
        }
    }

//
//    public Boolean doesThisStepExists(String stepName){
//        return steps.stream().
//                map(SingleStepDTO::getStepName).
//                anyMatch(x->x.equals(stepName));
//    }
//
//    public SingleStepDTO getStepDTOByName(String stepName){
//        for(SingleStepDTO stepDTO:steps){
//            if(stepDTO.getStepName().equals(stepName)){
//                return stepDTO;
//            }
//        }
//        throw new IllegalArgumentException("Step \"" + stepName + "\" does not exist.");
//    }
//
//    public SingleStepDTO getStepByInd(int stepInd){ //only if list of DTOs with same order as stepUsageDeclaration in flow definition
//        return steps.get(stepInd);
//    }
//
//    public int getStepsCount(){
//        return steps.size();
//    }

    public List<SingleStepDTO> getSteps() {
        return steps;
    }

}
