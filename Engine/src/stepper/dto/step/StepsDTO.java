package stepper.dto.step;

import stepper.flow.definition.api.StepUsageDeclaration;
import stepper.flow.definition.mapping.MappingGraph;

import java.util.ArrayList;
import java.util.List;


public class StepsDTO {
    List<SingleStepDTO> steps = new ArrayList<>();

    public StepsDTO(List<StepUsageDeclaration> stepsUsageDecl, MappingGraph mappingGraph)
 {
        for(StepUsageDeclaration step:stepsUsageDecl){

            steps.add(new SingleStepDTO(step,
                    mappingGraph.getAllTargetStepsAndTargetInputFinalNamesBySourceStepFinalName(step.getFinalStepName()),
                    mappingGraph.getSourceStepAndSourceOutputFinalNamesByTargetStepFinalName(step.getFinalStepName())));
        }
    }

//    public SingleStepDTO getStepByName(String stepName){
//
//    }
    public SingleStepDTO getStepByInd(int stepInd){ //only if list of DTOs with same order as stepUsageDeclaration in flow definition
        return steps.get(stepInd);
    }

}
