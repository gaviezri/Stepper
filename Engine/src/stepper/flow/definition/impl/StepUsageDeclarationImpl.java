package stepper.flow.definition.impl;

import stepper.dd.api.DataDefinition;
import stepper.flow.definition.api.StepUsageDeclaration;
import stepper.step.StepDefinitionRegistry;
import stepper.step.api.DataDefinitionDeclaration;
import stepper.step.api.StepDefinition;
import stepper.step.api.enums.DataNecessity;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class StepUsageDeclarationImpl implements StepUsageDeclaration, Serializable {
    private Map<String,String> inputs2finalNames = new LinkedHashMap<>();
    private Map<String,String> outputs2finalNames = new LinkedHashMap<>();
    private final StepDefinition stepDefinition;
    private boolean skipIfFail;
    private String stepName;

    @Override
    public Boolean isInputMandatory(String inputFinalName){
        String orgName = getInputOrgNameByFinalName(inputFinalName);
        DataDefinitionDeclaration dataDefDec= stepDefinition.getInputDataDefDecByInputOrgName(orgName);
        return dataDefDec.necessity().equals(DataNecessity.MANDATORY);
    }

    @Override
    public Collection<String> getAllInputsFinalNames(){ return inputs2finalNames.values();}
    @Override
    public Collection<String> getAllOutputsFinalNames(){ return outputs2finalNames.values();}
    @Override
    public String getFinalInputNameByOrg(String org){
        return inputs2finalNames.get(org);
    }

    public StepUsageDeclarationImpl(StepDefinition stepDefinition, boolean skipIfFail, String stepName) {
        this.stepDefinition = stepDefinition;
        this.skipIfFail = skipIfFail;
        this.stepName = stepName;
        stepDefinition.inputs().forEach(datadef -> inputs2finalNames.put(datadef.getName(), datadef.getName()));
        stepDefinition.outputs().forEach(datadef -> outputs2finalNames.put(datadef.getName(), datadef.getName()));
    }

    @Override
    public String getInputOrgNameByFinalName(String finalName){
        String orgName = null;
        for (String key:inputs2finalNames.keySet()) {
            if(inputs2finalNames.get(key).equals(finalName)){
                orgName = key;
            }
        }
        return orgName;
    }

    @Override
    public String getFinalStepName() {
        return stepName;
    }
    @Override
    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }
    @Override
    public boolean skipIfFail() {
        return skipIfFail;
    }

    @Override
    public StepDefinitionRegistry getStepDefinitionRegistry() {
        return StepDefinitionRegistry.valueOf(stepDefinition.getStepName());
    }

    @Override
    public String getInput2FinalName(String inputName) {
        return inputs2finalNames.get(inputName);
    }

    @Override
    public String getOutput2FinalName(String outputName) {
        return outputs2finalNames.get(outputName);
    }

    @Override
    public void putResource2FinalName(String resourceName, String finalName) {
        for (DataDefinitionDeclaration datadefdecl : stepDefinition.inputs()) {
            if (datadefdecl.getName().equals(resourceName)) {
                inputs2finalNames.put(resourceName, finalName);
                return;
            }
        }
        for (DataDefinitionDeclaration datadefdecl : stepDefinition.outputs()) {
            if (datadefdecl.getName().equals(resourceName)) {
                outputs2finalNames.put(resourceName, finalName);
                return;
            }
        }
    }
    @Override
    public String getResourceFinalName(String dataName) {

        for (DataDefinitionDeclaration datadefdecl : stepDefinition.inputs()){
            String finalName = inputs2finalNames.get(dataName);
            finalName = finalName == null ? dataName : finalName;
            if (datadefdecl.getName().equals(dataName)){
                String finalDataName = inputs2finalNames.get(dataName);
                return finalDataName == null ? dataName : finalDataName;
            }
        }
        for (DataDefinitionDeclaration datadefdecl : stepDefinition.outputs()){
            String finalName = outputs2finalNames.get(dataName);
            finalName = finalName == null ? dataName : finalName;
            if (datadefdecl.getName().equals(dataName)){
                String finalDataName = outputs2finalNames.get(dataName);
                return finalDataName == null ? dataName : finalDataName;
            }
        }
        return dataName;
    }
    @Override
    public Boolean containsResource(String dataName){

        for (DataDefinitionDeclaration datadefdecl : stepDefinition.inputs()){

            if (dataName.equals(inputs2finalNames.get(datadefdecl.getName())) || dataName.equals(datadefdecl.getName())){
                return true;
            }
        }
        for (DataDefinitionDeclaration datadefdecl : stepDefinition.outputs()){
            if (dataName.equals(outputs2finalNames.get(datadefdecl.getName())) || dataName.equals(datadefdecl.getName())){
                return true;
            }
        }
        return false;
    }

    @Override
    public DataDefinition getDataDefByName(String orgInputName) {
        DataDefinition resDD = null;
        for (DataDefinitionDeclaration dd:stepDefinition.inputs()){
            if (dd.getName().equals(orgInputName)){
                resDD = dd.dataDefinition();
            }
        }
        return resDD;
    }
}
