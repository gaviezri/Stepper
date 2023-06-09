package dto.step;

import javafx.util.Pair;
import stepper.flow.definition.api.StepUsageDeclaration;

import java.util.*;

public class SingleStepDTO {
    String stepName;

    // INPUTS INFO
    List<String> inputsFinalNames = new ArrayList<>();
    List<Boolean> isMandatoryInput = new ArrayList<>();    //for input in index i, value in index i in list will be TRUE if input i is mandatory else FALSE
    List<Boolean> isConnectedInput = new ArrayList<>();    //for input in index i, value in index i in list will be TRUE if input i is connected to an output of another flow else FALSE
    Map<String,Pair<String,String>> inputPostAliasName2SourceStepNameAndSourceOutputName = new LinkedHashMap<>();

    // OUTPUTS INFO
    List<String> outputsNamesPostAliasing = new ArrayList<>();
    Map<String,List<Pair<String,String>>> outputPostAliasName2AllTargetStepNameAndTargetInputName = new LinkedHashMap<>();

    public SingleStepDTO(StepUsageDeclaration stepDec,
                         Map<String,List<Pair<String,String>>> stepOutputs2AllTargetStepAndTargetInputs,
                         Map<String,Pair<String,String>> stepInputs2SourceStepAndSourceOutput)
    {
        this.stepName = stepDec.getFinalStepName();

        this.inputsFinalNames.addAll(stepDec.getAllInputsFinalNames());
        setIsMandatoryInput(stepDec, this.inputsFinalNames);
        setIsConnectedInput(stepInputs2SourceStepAndSourceOutput,this.inputsFinalNames);
        this.inputPostAliasName2SourceStepNameAndSourceOutputName = stepInputs2SourceStepAndSourceOutput;

        this.outputsNamesPostAliasing.addAll(stepDec.getAllOutputsFinalNames());
        this.outputPostAliasName2AllTargetStepNameAndTargetInputName = stepOutputs2AllTargetStepAndTargetInputs;
    }

    public SingleStepDTO(SingleStepDTO other) {
        this.stepName = other.stepName;

        this.inputsFinalNames.addAll(other.inputsFinalNames);
        this.isMandatoryInput.addAll(other.isMandatoryInput);
        this.isConnectedInput.addAll(other.isConnectedInput);
        this.inputPostAliasName2SourceStepNameAndSourceOutputName.putAll(other.inputPostAliasName2SourceStepNameAndSourceOutputName);

        this.outputsNamesPostAliasing.addAll(other.outputsNamesPostAliasing);
        this.outputPostAliasName2AllTargetStepNameAndTargetInputName.putAll(other.outputPostAliasName2AllTargetStepNameAndTargetInputName);
    }

    private void setIsMandatoryInput(StepUsageDeclaration stepDec, List<String> inputsFinalNames){
        for(String inputName:inputsFinalNames){
            isMandatoryInput.add(stepDec.isInputMandatory(inputName));
        }
    }

    private void setIsConnectedInput(Map<String,Pair<String,String>> stepInputs2AllSourceStepAndSourceOutputs, List<String> inputsFinalNames){
        for(String inputName:inputsFinalNames){
            isConnectedInput.add(stepInputs2AllSourceStepAndSourceOutputs.containsKey(inputName));
        }
    }

    public String getStepName() {
        return stepName;
    }

    public List<String> getInputsFinalNames() {
        return inputsFinalNames;
    }

    public List<Boolean> getIsMandatoryInput() {
        return isMandatoryInput;
    }

    public List<Boolean> getIsConnectedInput() {
        return isConnectedInput;
    }

    public Map<String, Pair<String, String>> getInputPostAliasName2SourceStepNameAndSourceOutputName() {
        return inputPostAliasName2SourceStepNameAndSourceOutputName;
    }

    public List<String> getOutputsNamesPostAliasing() {
        return outputsNamesPostAliasing;
    }

    public Map<String, List<Pair<String, String>>> getOutputPostAliasName2AllTargetStepNameAndTargetInputName() {
        return outputPostAliasName2AllTargetStepNameAndTargetInputName;
    }
}
