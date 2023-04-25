package stepper.flow.definition.api;

import javafx.util.Pair;
import stepper.dd.api.DataDefinition;
import stepper.flow.definition.aliasing.manager.DataAliasingManager;
import stepper.flow.definition.mapping.MappingGraph;
import stepper.step.StepDefinitionRegistry;
import stepper.step.api.DataDefinitionDeclaration;
import stepper.step.api.StepDefinition;
import stepper.step.api.enums.DataNecessity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlowDefinitionImpl implements FlowDefinition {

    private  String name;
    private  String description;
    private List<DataDefinitionDeclaration> allMandatoryInputs = new ArrayList<>();
    private List<DataDefinitionDeclaration> unsatisfiedMandatoryInputs = new ArrayList<>();
    private List<DataDefinitionDeclaration> flowInputs = new ArrayList<>();
    private  List<String> flowFormalOutputNames = new ArrayList<>();
    private List<String> stepsName = new ArrayList<>();
    private List<String> stepsAliases = new ArrayList<>();
    //private List<Pair<String,String>> stepAliasXDataName2AliasDataName = new ArrayList<>();
    private DataAliasingManager dataAliasingManager = new DataAliasingManager();
    private  List<StepUsageDeclaration> stepsUsageDecl = new ArrayList<>();
    private  List<String> stepAliasThatCanSkipIfFail = new ArrayList<>();
    private  List<Pair<String,String>> rawSourceStepData2TargetStepDataMapping = new ArrayList<>();
    private MappingGraph mappingGraph;


    public FlowDefinitionImpl(String name) {
        this.name = name;
    }


    @Override
    public void setFlowFormalOutputs(List<String> flowFormalOutputs) {
        flowFormalOutputNames = flowFormalOutputs;
    }

    @Override
    public void addStepAlias(String finalname) {
        stepsAliases.add(finalname);
    }
    @Override
    public void validateFlowStructure() {
        validateMissingMandatoryInputsAreUserFriendly();
    }

    private void validateMissingMandatoryInputsAreUserFriendly() {
            if (! unsatisfiedMandatoryInputs.isEmpty()){
                for (DataDefinitionDeclaration unsatisfiedMandatoryInput : unsatisfiedMandatoryInputs) {
                    if (! unsatisfiedMandatoryInput.isUserFriendly()){
                        throw new RuntimeException("missing mandatory input \"" + unsatisfiedMandatoryInput.getName() + "\" is not user friendly");
                }
            }
        }
    }

    @Override
    public void setMandatoryInputs() {
        for (StepUsageDeclaration stepUsageDeclaration : stepsUsageDecl) {
            for (DataDefinitionDeclaration dataDefinitionDeclaration : stepUsageDeclaration.getStepDefinition().inputs()) {
                if (dataDefinitionDeclaration.necessity() == DataNecessity.MANDATORY){
                    allMandatoryInputs.add(dataDefinitionDeclaration);
                    if (! mappingGraph.isSatisfied(dataDefinitionDeclaration.getName())){
                        unsatisfiedMandatoryInputs.add(dataDefinitionDeclaration);
                    }
                }
            }
        }
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
        return flowFormalOutputNames;
    }

    @Override
    public void setDescription(String s) {
        description = s;
    }

    @Override
    public void addFlowLevelAlias(String stepFinalName, String sourceDataName, String sourceDataAlias) {
        //stepAliasXDataName2AliasDataName.add(new Pair<>(stepFinalName + ":" + sourceDataName, sourceDataAlias));
        dataAliasingManager.putAliasDataName(stepFinalName, sourceDataName, sourceDataAlias);
    }

    @Override
    public String getStepFinalName(String stepName, boolean fromAlias) {
        try {
            if (fromAlias) {
                return stepsAliases.get(stepsAliases.indexOf(stepName));
            }
            return stepsAliases.get(stepsName.indexOf(stepName));
        } catch (Exception e) {
            throw new RuntimeException("Step " + stepName + " not found in flow " + name);
        }
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
        rawSourceStepData2TargetStepDataMapping.add(new Pair<>(stepName + ":" + dataName, targetStepName + ":" + targetDataName));
    }

    @Override
    public String getResourceFinalName(String stepFinalName, String dataName) {

        if (dataAliasingManager.isAnAlias(stepFinalName, dataName)) {
            return dataName;
        }

        StepDefinition theStep = StepDefinitionRegistry.convertFromUserFriendlyToInternal(getStepOriginalName(stepFinalName))
                .getStepDefinition();

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

    @Override
    public void createMapping(){
        for (int stepidx = 0; stepidx < stepsName.size(); ++stepidx ) {

            StepDefinition stepDef = StepDefinitionRegistry.convertFromUserFriendlyToInternal(stepsName.get(stepidx)).getStepDefinition();
            String alias = stepsAliases.get(stepidx);
            boolean canSkipIfFail = stepAliasThatCanSkipIfFail.contains(alias);
            stepsUsageDecl.add(new StepUsageDeclarationImpl(stepDef,canSkipIfFail, alias));

        }
        mappingGraph = new MappingGraph(stepsUsageDecl, rawSourceStepData2TargetStepDataMapping, dataAliasingManager);
        createAutomaticMapping();
        setMandatoryInputs();

    }
    private void createAutomaticMapping(){
        mappingGraph.createAutomaticMapping(this);

    }
    @Override
    public List<String> getStepOutputsFinalNames(String stepFinalName) {

        List<String> outputFinalNames = new ArrayList<>();
        StepDefinition stepDef = StepDefinitionRegistry.convertFromUserFriendlyToInternal(getStepOriginalName(stepFinalName)).getStepDefinition();

        for (DataDefinitionDeclaration dataDef : stepDef.outputs()) {
           String alias = dataAliasingManager.getAliasDataName(stepFinalName, dataDef.getName());
           String finalName = alias == null ? dataDef.getName() : alias;
            outputFinalNames.add(finalName);
        }
        return outputFinalNames;
    }

    @Override
    public List<String> getStepInputsFinalNames(String stepFinalName){

        List<String> inputFinalNames = new ArrayList<>();
        StepDefinition stepDef = StepDefinitionRegistry.convertFromUserFriendlyToInternal(getStepOriginalName(stepFinalName)).getStepDefinition();

        for (DataDefinitionDeclaration dataDef : stepDef.inputs()) {
            String alias = dataAliasingManager.getAliasDataName(stepFinalName, dataDef.getName());
            String finalName = alias == null ? dataDef.getName() : alias;
            inputFinalNames.add(finalName);
        }
        return inputFinalNames;
    }
    @Override
    public StepUsageDeclaration getStepUsageDeclaration(String stepName) {
        return stepsUsageDecl.get(stepsAliases.indexOf(stepName));
    }

    @Override
    public DataDefinition getResourceDataDefinition(String stepName, String dataName) {
        return stepsUsageDecl.get(stepsAliases.indexOf(stepName)).getStepDefinition().getResourceDataDefinition(dataName);
    }
    @Override
    public List<String> getStepInputsOriginalNames(String stepName) {
        return stepsUsageDecl.get(stepsAliases.indexOf(stepName)).getStepDefinition().inputs().stream()
                .map(DataDefinitionDeclaration::getName)
                .collect(Collectors.toList());
    }
    @Override
    public List<String> getStepOutputsOriginalNames(String stepName) {
        return stepsUsageDecl.get(stepsAliases.indexOf(stepName)).getStepDefinition().outputs().stream()
                .map(DataDefinitionDeclaration::getName)
                .collect(Collectors.toList());
    }
    @Override
    public DataNecessity getResourceDataNecessity(String stepName, String dataName) {
        return stepsUsageDecl.get(stepsAliases.indexOf(stepName)).getStepDefinition().getResourceNecessity(dataName);
    }

}
