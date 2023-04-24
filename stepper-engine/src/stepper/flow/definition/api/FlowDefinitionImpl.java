package stepper.flow.definition.api;

import javafx.util.Pair;
import stepper.flow.definition.aliasing.manager.DataAliasingManager;
import stepper.flow.definition.mapping.MappingGraph;
import stepper.step.StepDefinitionRegistry;
import stepper.step.api.DataDefinitionDeclaration;
import stepper.step.api.StepDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlowDefinitionImpl implements FlowDefinition {

    private  String name;
    private  String description;
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

    }
    private void createAutomaticMapping(){
        mappingGraph.createAutomaticMapping();

    }



}
