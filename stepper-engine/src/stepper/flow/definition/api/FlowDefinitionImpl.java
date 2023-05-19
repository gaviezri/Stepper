package stepper.flow.definition.api;

import javafx.util.Pair;
import stepper.dd.api.DataDefinition;
import stepper.flow.definition.aliasing.manager.DataAliasingManager;
import stepper.flow.definition.mapping.MappingGraph;
import stepper.step.StepDefinitionRegistry;
import stepper.step.api.DataDefinitionDeclaration;
import stepper.step.api.StepDefinition;
import stepper.step.api.enums.DataNecessity;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class FlowDefinitionImpl implements FlowDefinition, Serializable {
    private  String name;
    private  String description;
    private List<Pair<String,Class>> allMandatoryInputs = new ArrayList<>();
    private List<DataDefinitionDeclaration> unsatisfiedMandatoryInputs = new ArrayList<>();
    private List<DataDefinitionDeclaration> flowInputs = new ArrayList<>();
    private List<String> freeInputNames = new ArrayList<>();
    private List<String> freeInputFinalNames = new ArrayList<>();
    private List<Pair<DataDefinitionDeclaration,List<StepUsageDeclaration>>> flowFreeInputs2StepsThatUseThem = new ArrayList<>();
    private  List<String> flowFormalOutputNames = new ArrayList<>();
    private List<String> stepsFinalNames = new ArrayList<>();
    private DataAliasingManager dataAliasingManager = new DataAliasingManager();
    private  List<StepUsageDeclaration> stepsUsageDecl = new ArrayList<>();
    private  List<Pair<String,String>> rawSourceStepData2TargetStepDataMapping = new ArrayList<>();
    private MappingGraph mappingGraph;
    private boolean isReadOnly;

    private static final Map<String,String> stepInput2UserString = new HashMap<>();
    private static final Map<String,String> stepOutput2UserString = new HashMap<>();

    /** EX2 additional data members */
    private Map<String,Object> initialInputName2Value = new Hashtable<>();
    private Map<String,DataDefinition> initialInputName2DataDef = new Hashtable<>();

    static {
        ///////////////////////////////// INPUTS ///////////////////////////////////////////
        stepInput2UserString.put("SPEND_SOME_TIME:TIME_TO_SPEND","Total sleeping time (sec)");
        stepInput2UserString.put("COLLECT_FILES_IN_FOLDER:FOLDER_NAME","Folder name to scan");
        stepInput2UserString.put("COLLECT_FILES_IN_FOLDER:FILTER","Filter only these files");
        stepInput2UserString.put("FILES_DELETER:FILE_LIST","Files to delete");
        stepInput2UserString.put("FILES_RENAMER:FILES_TO_RENAME","Files to rename");
        stepInput2UserString.put("FILES_RENAMER:PREFIX","Add this prefix");
        stepInput2UserString.put("FILES_RENAMER:SUFFIX","Append this suffix");
        stepInput2UserString.put("FILES_CONTENT_EXTRACTOR:FILES_LIST","Files to extract");
        stepInput2UserString.put("FILES_RENAMER:LINE","Line number to extract");
        stepInput2UserString.put("CSV_EXPORTER:SOURCE","Source data");
        stepInput2UserString.put("PROPERTIES_EXPORTER:SOURCE","Source data");
        stepInput2UserString.put("FILE_DUMPER:CONTENT","Content");
        stepInput2UserString.put("FILE_DUMPER:FILE_NAME","Target file path");
        //////////////////////////////// OUTPUTS ///////////////////////////////////////////
        stepOutput2UserString.put("COLLECT_FILES_IN_FOLDER:FILE_LIST","Files list");
        stepOutput2UserString.put("COLLECT_FILES_IN_FOLDER:TOTAL_FOUND","Total files found");
        stepOutput2UserString.put("FILES_DELETER:DELETED_LIST","Files failed to be deleted");
        stepOutput2UserString.put("FILES_DELETER:DELETION_STATS","Deletion summary results");
        stepOutput2UserString.put("FILES_RENAMER:RENAME_RESULT","Rename operation summary");
        stepOutput2UserString.put("FILES_RENAMER:DATA","Data extraction");
        stepOutput2UserString.put("CSV_EXPORTER:RESULT","CSV export result");
        stepOutput2UserString.put("PROPERTIES_EXPORTER:RESULT","Properties export result");
        stepOutput2UserString.put("FILE_DUMPER:RESULT","File creation result");
    }

    @Override
    public List<String> getAllInputsFinalNames(){
        List<String> allInputs = new ArrayList<>();
        for (StepUsageDeclaration stp : stepsUsageDecl) {
            allInputs.addAll(stp.getAllInputsFinalNames());
        }
        return allInputs;
    }

    @Override
    public void addFlowsInitialInputValues(String inputName, Object initialValue, DataDefinition curDD){
        initialInputName2Value.put(inputName,initialValue);
        initialInputName2DataDef.put(inputName,curDD);
    }

    public FlowDefinitionImpl(String name) {
        this.name = name;
    }

    public static Map<String,String> getStepInput2UserString() {
        return stepInput2UserString;
    }

    @Override
    public void setFlowFormalOutputs(List<String> flowFormalOutputs) {
        flowFormalOutputNames = flowFormalOutputs;
    }

    @Override
    public void addFinalizedStep(String name, String finalName, boolean skipIfFail) {
        StepDefinition stepDef = StepDefinitionRegistry.convertFromUserFriendlyToInternal(name).getStepDefinition();
        stepsUsageDecl.add( new StepUsageDeclarationImpl(stepDef, skipIfFail, finalName));
        stepsFinalNames.add(finalName);
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
        for ( Pair<String,Class> curInput: allMandatoryInputs){
            List<Pair<String,Class>> restOfDataDefDecl = allMandatoryInputs.stream()
                    .filter(otherInput -> ! otherInput.equals(curInput))
                    .collect(Collectors.toList());
            for ( Pair<String,Class> curInput2: restOfDataDefDecl){
                if (curInput.getKey().equals(curInput2.getKey()) && !curInput.getValue().equals(curInput2.getValue())){
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
                        throw new RuntimeException("missing mandatory input \"" + unsatisfiedMandatoryInput.getName() + "\" is not accessible by user.");
                }
            }
        }
    }

    @Override
    public void setMandatoryInputs() {
        Pair<String,Class> curNameAndClass;
        for (StepUsageDeclaration stepUsageDeclaration : stepsUsageDecl) {
            for (DataDefinitionDeclaration dataDefinitionDeclaration : stepUsageDeclaration.getStepDefinition().inputs()) {
                if (dataDefinitionDeclaration.necessity() == DataNecessity.MANDATORY){
                    curNameAndClass = new Pair<>(stepUsageDeclaration.getFinalInputNameByOrg(dataDefinitionDeclaration.getName()),dataDefinitionDeclaration.getType());
                    allMandatoryInputs.add(curNameAndClass);
                    if (! mappingGraph.isSatisfied(dataDefinitionDeclaration.getName())){
                        unsatisfiedMandatoryInputs.add(dataDefinitionDeclaration);
//                        TODO: check if satisfied through INITIAL INPUT VALUES!
                    }
                }
                flowInputs.add(dataDefinitionDeclaration);
            }
        }
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
        for (StepUsageDeclaration stepusgdecl : stepsUsageDecl){
            if (stepusgdecl.getFinalStepName().equals(stepFinalName)){
                stepusgdecl.putResource2FinalName(sourceDataName, sourceDataAlias);
                return;
            }
        }
    }

    @Override
    public String getStepOriginalName(String stepFinalName) {
        for (StepUsageDeclaration stepusgdecl : stepsUsageDecl){
            if (stepusgdecl.getFinalStepName().equals(stepFinalName)){
                return stepusgdecl.getStepDefinition().getStepName();
            }
        }
        return null;
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

        for (StepUsageDeclaration stepusgdecl : stepsUsageDecl){
            if (stepusgdecl.getFinalStepName().equals(stepFinalName)){
                return stepusgdecl.getResourceFinalName(dataName);
            }
        }
        return dataName;
    }

    @Override
    public void createMapping(){

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
    public DataDefinition getResourceDataDefinition(String stepName, String dataName) {
        return stepsUsageDecl.stream()
                .filter(x -> x.getFinalStepName().equals(stepName))
                .findFirst()
                .get()
                .getStepDefinition()
                .getResourceDataDefinition(dataName);
    }
    @Override
    public List<String> getStepInputsOriginalNames(String stepName) {
        return stepsUsageDecl.stream()
                .filter(x-> x.getFinalStepName().equals(stepName))
                .findFirst()
                .get()
                .getStepDefinition()
                .inputs()
                .stream()
                .map(x-> x.getName())
                .collect(Collectors.toList());

    }
    @Override
    public List<String> getStepOutputsOriginalNames(String stepName) {
        return stepsUsageDecl.stream()
                .filter(x-> x.getFinalStepName().equals(stepName))
                .findFirst()
                .get()
                .getStepDefinition()
                .outputs()
                .stream()
                .map(x-> x.getName())
                .collect(Collectors.toList());
    }
    @Override
    public DataNecessity getResourceDataNecessity(String stepName, String dataName) {
        return stepsUsageDecl.stream()
                .filter(x-> x.getFinalStepName().equals(stepName))
                .findFirst()
                .get()
                .getStepDefinition()
                .getResourceNecessity(dataName);
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
        for (StepUsageDeclaration stepUsgDecl : stepsUsageDecl){
            for (DataDefinitionDeclaration dataDefDecl : stepUsgDecl.getStepDefinition().inputs()){
                String finalName = stepUsgDecl.getResourceFinalName(dataDefDecl.getName());
                if (!mappingGraph.isSatisfied(finalName)){
                    freeInputFinalNames.add(finalName);
                    freeInputNames.add(dataDefDecl.getName());
                    List<StepUsageDeclaration> StepsNames = getStepsThatUsesInput(finalName, stepsUsageDecl);
                    flowFreeInputs2StepsThatUseThem.add(new Pair<>(dataDefDecl, StepsNames));
                }
            }
       }
    }

    private List<StepUsageDeclaration> getStepsThatUsesInput(String inputName, List<StepUsageDeclaration> stepsUsageDecl) {
        List<StepUsageDeclaration> stepsThatUsesInput = new ArrayList<>();
        for (StepUsageDeclaration stepUsageDeclaration : stepsUsageDecl){
            if(stepUsageDeclaration.containsResource(inputName)){
                stepsThatUsesInput.add(stepUsageDeclaration);
            }
        }
        return stepsThatUsesInput;
    }

    @Override
    public List<String> getFreeInputsFinalNames() {
        return freeInputFinalNames;
    }

    @Override
    public List<String> getFreeInputsUserString() {
        List<String> freeInputsUserString = new ArrayList<>();
        List<String> freeInputsFinalNames = getFreeInputsFinalNames();
        for (StepUsageDeclaration stepusgdecl : stepsUsageDecl){
            for(int i = 0; i < freeInputsFinalNames.size(); i++) {
                if (stepusgdecl.containsResource(freeInputsFinalNames.get(i))) {
                    freeInputsUserString.add(stepusgdecl.getStepDefinition().getResourceUserString(freeInputNames.get(i)));
                }
            }
        }
        return freeInputsUserString;
    }

    private List<String> getFreeInputsNames() {
       return freeInputNames;
    }

    @Override
    public List<String> getFreeInputsTypes() {
        return flowFreeInputs2StepsThatUseThem.stream()
                .map(x-> x.getKey().getType().getSimpleName())
                .collect(Collectors.toList());
    }
    @Override
    public List<Pair<String, List<String>>> getFreeInputs2StepsThatUseThem(){
        List<Pair<String, List<String>>> convertedToStrings = new ArrayList<>();
        for (Pair<DataDefinitionDeclaration,List<StepUsageDeclaration>> input2steps : flowFreeInputs2StepsThatUseThem){
            convertedToStrings.add(new Pair<>(input2steps.getKey().getName(), input2steps.getValue().stream().map(x-> x.getFinalStepName()).collect(Collectors.toList())));
        }
        return convertedToStrings;
    }
    @Override
    public List<String> getFreeInputsNecessity(){
        return flowFreeInputs2StepsThatUseThem.stream()
                .map(x-> x.getKey().necessity())
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
                allOutputsNames.add(output.getType().getSimpleName());
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

    @Override
    public String getStepFinalName(String stepFinalName){
        try {
            return stepsUsageDecl.stream()
                    .filter(x -> x.getFinalStepName().equals(stepFinalName))
                    .findFirst().get().getFinalStepName();
        }catch (Exception e){
            return stepFinalName;
        }
    }
    @Override
    public List<String> getStepsNamesWithAlias(){
        return stepsUsageDecl.stream()
                .map(x-> {
                    String finalName = x.getFinalStepName();
                    String originalName = x.getStepDefinition().getStepName();
                    return finalName.equals(originalName) ? finalName : finalName + " (" + originalName + ")";
                })
                .collect(Collectors.toList());
    }

    @Override
    public String getDataDefOriginalName(String inputName) {
        String orgName = null;

        for (StepUsageDeclaration stepusgdecl : stepsUsageDecl){
            orgName = stepusgdecl.getInputOrgNameByFinalName(inputName);
            if (orgName != null){break;}
        }
        return orgName;
    }
}
