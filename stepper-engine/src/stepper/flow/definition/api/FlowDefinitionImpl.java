package stepper.flow.definition.api;

import javafx.util.Pair;
import stepper.step.StepDefinitionRegistry;
import stepper.step.api.DataDefinitionDeclaration;
import stepper.step.api.StepDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlowDefinitionImpl implements FlowDefinition {

    private  String name;
    private  String description;
    private  List<String> flowOutputs = new ArrayList<>();
    private List<String> stepsName = new ArrayList<>();
    private List<String> stepsAliases = new ArrayList<>();
    private List<Pair<String,String>> stepAliasColonDataName2AliasDataName = new ArrayList<>();
    private  List<StepUsageDeclaration> stepsUsageDecl = new ArrayList<>();
    private  List<String> stepAliasThatCanSkipIfFail = new ArrayList<>();
    private  List<Pair<String,String>> SourceStepData2TargetStepDataCustomMapping = new ArrayList<>();


    public FlowDefinitionImpl(String name) {
        this.name = name;
    }


    @Override
    public void setFlowFormalOutputs(List<String> flowFormalOutputs) {
        flowOutputs = flowFormalOutputs;
    }

    @Override
    public void addStepAlias(String finalname) {
        stepsAliases.add(finalname);
    }
    @Override
    public void validateFlowStructure() {

    }
    @Override
    public void addStep(String stepName) {
        stepsName.add(stepName);
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<StepUsageDeclaration> getFlowSteps() {
        return stepsUsageDecl;
    }

    @Override
    public List<String> getFlowFormalOutputs() {
        return flowOutputs;
    }

    @Override
    public void setDescription(String s) {
        description = s;
    }

    @Override
    public void addFlowLevelAlias(String stepFinalName, String sourceDataName, String sourceDataAlias) {
        stepAliasColonDataName2AliasDataName.add(new Pair<>(stepFinalName + ":" + sourceDataName, sourceDataAlias));
    }

    @Override
    public String getStepFinalName(String stepName, boolean fromAlias) {
        if (fromAlias){
            return stepsAliases.get(stepsAliases.indexOf(stepName));
        }
        return stepsAliases.get(stepsName.indexOf(stepName));
    }

    @Override
    public String getStepOriginalName(String aliasName) {
        return stepsName.get(stepsAliases.indexOf(aliasName));
    }

    public List<StepDefinitionRegistry> getStepDefinitionRegistries() {
       return this.getFlowSteps().stream()
               .map(StepUsageDeclaration::getStepDefinitionRegistry)
               .collect(Collectors.toList());
    }

    @Override
    public void addCustomMapping(String stepName, String dataName, String targetStepName, String targetDataName) {
        SourceStepData2TargetStepDataCustomMapping.add(new Pair<>(stepName + ":" + dataName, targetStepName + ":" + targetDataName));
    }

    @Override
    public String getResourceFinalName(String stepFinalName, String dataName) {

        if (stepAliasColonDataName2AliasDataName.stream()
                .anyMatch(pair -> pair.getKey()
                        .split(":")[0]
                        .equals(stepFinalName) && pair.getValue().equals(dataName))) {

            return stepAliasColonDataName2AliasDataName.stream()
                    .filter(pair -> pair.getKey()
                            .split(":")[0]
                            .equals(stepFinalName) && pair.getValue().equals(dataName))
                    .findFirst()
                    .get()
                    .getValue();
        }

        String originalNameInRegistry = StepDefinitionRegistry.convertFromUserFriendlyToInternal(getStepOriginalName(stepFinalName));
        StepDefinition theStep = StepDefinitionRegistry.valueOf(originalNameInRegistry).getStepDefinition();
        for (DataDefinitionDeclaration dataDef : theStep.inputs()) {
            if (dataDef.getName().equals(dataName)) {
                return dataName;
            }
        }

        for (DataDefinitionDeclaration dataDef : theStep.outputs()) {
            if (dataDef.getName().equals(dataName)) {
                return dataName;
            }
        }
        return null;
    }

    @Override
    public void addStepAliasThatCanSkipIfFail(String stepName) {
        stepAliasThatCanSkipIfFail.add(stepName);
    }
}
