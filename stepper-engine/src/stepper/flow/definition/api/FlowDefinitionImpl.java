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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlowDefinitionImpl implements FlowDefinition {
    private  String name;
    private  String description;
    private List<DataDefinitionDeclaration> allMandatoryInputs = new ArrayList<>();
    private List<DataDefinitionDeclaration> unsatisfiedMandatoryInputs = new ArrayList<>();
    private List<DataDefinitionDeclaration> flowInputs = new ArrayList<>();
    private List<DataDefinitionDeclaration> flowFreeInputs = new ArrayList<>();
    private List<DataDefinitionDeclaration> flowOutputs = new ArrayList<>();

    private  List<String> flowFormalOutputNames = new ArrayList<>();
    private List<String> stepsName = new ArrayList<>();
    private List<String> stepsAliases = new ArrayList<>();
    private DataAliasingManager dataAliasingManager = new DataAliasingManager();
    private  List<StepUsageDeclaration> stepsUsageDecl = new ArrayList<>();
    private  List<String> stepAliasThatCanSkipIfFail = new ArrayList<>();
    private  List<Pair<String,String>> rawSourceStepData2TargetStepDataMapping = new ArrayList<>();

    private MappingGraph mappingGraph;
    private boolean isReadOnly;


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
        // flow output contains a data that doesn't exist in the flow's scope - V
        validateFlowOutputsIsInScope();
        // numerous mandatory inputs with the same name from different types! - V
        validateMandatoryInputsSameNameSameType();
    }

    private void validateMandatoryInputsSameNameSameType() {
        for ( DataDefinitionDeclaration DataDefDecl1: allMandatoryInputs){
            List<DataDefinitionDeclaration> restOfDataDefDecl = allMandatoryInputs.stream()
                    .filter(datadefdecl -> ! datadefdecl.equals(DataDefDecl1))
                    .collect(Collectors.toList());
            for ( DataDefinitionDeclaration DataDefDecl2: restOfDataDefDecl){
                if (DataDefDecl1.getName().equals(DataDefDecl2.getName()) && DataDefDecl1.getType().equals(DataDefDecl2.getType())){
                    throw new RuntimeException("numerous mandatory inputs with the same name from different types!");
                }
            }
        }
    }

    private void validateFlowOutputsIsInScope() {
        boolean missing;
        List<String> formalOutputs = getFlowFormalOutputs();
        for (String formaloutput : formalOutputs) {
           missing = true;
            for (String stepFinalName : mappingGraph.getStepNodes()){
                // by step's final name get to step final outputs names
                // by step's final outputs names get to step's final outputs names
                // and check if formal output is in step's final outputs names
                List<String> stepFinalOutputsNames = getStepOutputsFinalNames(stepFinalName);
                missing = ! stepFinalOutputsNames.contains(formaloutput);
                if (! missing) {
                    break;
                }
            }
           if (missing){
               throw new RuntimeException("flow output \"" + formaloutput + "\" is not in flow's scope");
           }
        }

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
                flowInputs.add(dataDefinitionDeclaration);
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

    @Override
    public  List<DataDefinitionDeclaration> getUnsatisfiedMandatoryInputs(){
        return unsatisfiedMandatoryInputs;
    }

    @Override
    public void setAccessibility(){
        isReadOnly = stepsUsageDecl.stream().allMatch(step -> step.getStepDefinition().isReadonly());
    }
    @Override
    public Boolean getAccessibility(){
        return isReadOnly;
    }
    @Override
    public void setFreeInputs(){
       for (DataDefinitionDeclaration input : flowInputs){
          if (!mappingGraph.isSatisfied(input.getName())) {
              flowFreeInputs.add(input);
          }
       }
    }
    @Override
    public List<String> getFreeInputsNames() {
        return flowFreeInputs.stream()
                .map(x -> dataAliasingManager.getAliasDataName(x.getName()))
                .collect(Collectors.toList());
    }
    @Override
    public List<String> getFreeInputsTypes() {
        return flowFreeInputs.stream()
                .map(DataDefinitionDeclaration::getType)
                //.map(Class::toString)
                .map(Class::getSimpleName)
                .collect(Collectors.toList());
    }
    @Override
    public Map<String, List<String>> getFreeInputs2StepsThatUseThem(){
        Map<String,List<String>> freeInputs2StepsThatUseThem = new LinkedHashMap<>();
        for (DataDefinitionDeclaration input : flowFreeInputs){
            List<String> stepsThatUseIt = getStepsThatUseInput(input.getName());
            freeInputs2StepsThatUseThem.put(input.getName(),stepsThatUseIt);
        }
        return freeInputs2StepsThatUseThem;
    }
    private List<String> getStepsThatUseInput(String inputName){
        List<String> stepsThatUseIt = new ArrayList<>();
        for (StepUsageDeclaration step : stepsUsageDecl){
            if (step.getStepDefinition().inputs().stream().anyMatch(input -> dataAliasingManager.getOriginalDataName(step.getFinalStepName(),input.getName()).equals(inputName))){
                stepsThatUseIt.add(step.getFinalStepName());
            }
        }
        return stepsThatUseIt;
    }
    @Override
    public List<String> getFreeInputsNecessity(){
        return flowFreeInputs.stream()
                .map(DataDefinitionDeclaration::necessity)
                .map(DataNecessity::toString)
                .collect(Collectors.toList());
    }
    @Override
    public List<String> getAllOutputsNames(){
        List<String> allOutputsNames = new ArrayList<>();
        for (StepUsageDeclaration step : stepsUsageDecl){
            allOutputsNames.addAll(getStepOutputsFinalNames(step.getFinalStepName()));
        }
        return allOutputsNames;
    }
    @Override
    public List<String> getAllOutputsTypes(){
        List<String> allOutputsNames = new ArrayList<>();
        for (StepUsageDeclaration step : stepsUsageDecl){
            for (DataDefinitionDeclaration output : step.getStepDefinition().outputs()){
                allOutputsNames.add(output.getType().toString());
            }
        }
        return allOutputsNames;
    }

    @Override
    public List<String> getFinalStepNamesThatProducedTheOutputs() {
        List<String> allOutputsNames = new ArrayList<>();
        for (StepUsageDeclaration step : stepsUsageDecl){
            for (DataDefinitionDeclaration output : step.getStepDefinition().outputs()){
                allOutputsNames.add(step.getFinalStepName());
            }
        }
        return allOutputsNames;
    }

    @Override
    public DataAliasingManager getDataAliasingManager(){
        return dataAliasingManager;
    }

    @Override
    public MappingGraph getMappingGraph(){
        return mappingGraph;
    }
}
