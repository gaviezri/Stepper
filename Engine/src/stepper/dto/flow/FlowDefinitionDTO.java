package stepper.dto.flow;

import javafx.util.Pair;
import stepper.dto.AbstractDTO;
import stepper.dto.step.StepsDTO;
import stepper.flow.definition.api.FlowDefinition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlowDefinitionDTO extends AbstractDTO {
    /* see command definition in word document pg 19 */
    /* 1 */ String flowName;
    /* 2 */ String description;
    /* 3 */ List<String> formalOutputs;
    /* 4 */ Boolean isFlowReadonly;

    public Integer getContinuationsCount() {
        return continuationsCount;
    }

    /* 5 */ Integer continuationsCount;

    // ---STEPS--- //

    /* 5.1 */ List<Pair<String,String>> step2alias;
    /* 5.2 */ List<Boolean> isStepReadonly;
    /* 5.3 */ StepsDTO stepsDTO;

    // ---INPUTS--- //

    /* 6.1 */ List<String> freeInputsFinalNames;
    /* 6.2 */ List<String> freeInputTypes;
    /* 6.3 */ List<Pair<String,List<String>>> freeInputs2StepsThatUseThem;
    List<String> freeInputUserString;
    /* 6.4 */ List<String> freeInputNecessity;

    // ---OUTPUTS--- //
    /* 7.1 */ List<String> outputsFinalNames;
    /* 7.2 */ List<String> outputTypes;
    /* 7.3 */ List<String> finalStepNameThatProducedTheOutput;

    // ---CONTINUATION--- //
    /* 8 */ Map<String, List<Pair<String,String>>> targetFlowName2DataMappings;

    public List<String> freeInputUserString() {
        return freeInputUserString;
    }


    public FlowDefinitionDTO(FlowDefinition flowDef){
        flowName = flowDef.getName();
        description = flowDef.getDescription();
        formalOutputs = flowDef.getFlowFormalOutputs();
        isFlowReadonly = flowDef.getAccessibility();
        step2alias = flowDef.getFlowSteps().stream()
                                .map( stepusgdecl -> new Pair<>(stepusgdecl.getStepDefinition().getStepName(), stepusgdecl.getFinalStepName()))
                                .collect(Collectors.toList());

        isStepReadonly = flowDef.getFlowSteps()
                                .stream()
                                .map(stepusgdecl -> stepusgdecl.getStepDefinition().isReadonly())
                                .collect(Collectors.toList());
        freeInputsFinalNames = flowDef.getFreeInputsFinalNames();
        freeInputTypes = flowDef.getFreeInputsTypes();
        freeInputUserString = flowDef.getFreeInputsUserString();
        freeInputs2StepsThatUseThem = flowDef.getFreeInputs2StepsThatUseThem();
        freeInputNecessity = flowDef.getFreeInputsNecessity();
        outputsFinalNames = flowDef.getAllOutputsNames();
        outputTypes =  flowDef.getAllOutputsTypes();
        finalStepNameThatProducedTheOutput = flowDef.getFinalStepNamesThatProducedTheOutputs();
        continuationsCount = flowDef.getContinuationsCount();
        stepsDTO = new StepsDTO(flowDef.getFlowSteps(), flowDef.getMappingGraph());
        targetFlowName2DataMappings = flowDef.getContinuation().
                getAllContinuationDataFromCurFlow();
    }
    public Map<String, List<Pair<String,String>>> getContinuationDataMap(){
        return targetFlowName2DataMappings;
    }
    public FlowDefinitionDTO(String errmessage) {
        super(errmessage);
    }

    public List<String> getOutputsFinalNames() {
        return outputsFinalNames;
    }
    public String getDescription() {
        return description;
    }

    public List<String> getFormalOutputs() {
        return formalOutputs;
    }

    public Boolean getFlowReadonly() {
        return isFlowReadonly;
    }

    public List<Pair<String, String>> getStep2alias() {
        return step2alias;
    }

    public List<Boolean> getIsStepReadonly() {
        return isStepReadonly;
    }

    public List<String> getFreeInputsFinalNames() {
        return freeInputsFinalNames;
    }

    public List<String> getFreeInputTypes() {
        return freeInputTypes;
    }

    public List<Pair<String,List<String>>> getFreeInputs2StepsThatUseThem() {
        return freeInputs2StepsThatUseThem;
    }

    public List<String> getFreeInputUserString() {
        return freeInputUserString;
    }

    public List<String> getFreeInputNecessity() {
        return freeInputNecessity;
    }

    public List<String> outputsFinalNames() {
        return outputsFinalNames;
    }

    public List<String> getOutputTypes() {
        return outputTypes;
    }

    public List<String> getFinalStepNameThatProducedTheOutput() {
        return finalStepNameThatProducedTheOutput;
    }

    public String getFlowName() {
        return flowName;
    }
    public StepsDTO getStepsDTO() {
        return stepsDTO;
    }

}
