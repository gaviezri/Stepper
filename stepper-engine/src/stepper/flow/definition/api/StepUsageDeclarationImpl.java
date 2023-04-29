package stepper.flow.definition.api;

import stepper.step.StepDefinitionRegistry;
import stepper.step.api.DataDefinitionDeclaration;
import stepper.step.api.StepDefinition;

import java.util.LinkedHashMap;
import java.util.Map;

public class StepUsageDeclarationImpl implements StepUsageDeclaration {
    private Map<String,String> inputs2finalNames = new LinkedHashMap<>();
    private Map<String,String> outputs2finalNames = new LinkedHashMap<>();
    private final StepDefinition stepDefinition;
    private boolean skipIfFail;
    private String stepName;

    public StepUsageDeclarationImpl(StepDefinition stepDefinition, boolean skipIfFail, String stepName) {
        this.stepDefinition = stepDefinition;
        this.skipIfFail = skipIfFail;
        this.stepName = stepName;
        stepDefinition.inputs().forEach(datadef -> inputs2finalNames.put(datadef.getName(), datadef.getName()));
        stepDefinition.outputs().forEach(datadef -> outputs2finalNames.put(datadef.getName(), datadef.getName()));
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
            if (datadefdecl.getName().equals(dataName)){
                String finalDataName = inputs2finalNames.get(dataName);
                return finalDataName == null ? dataName : finalDataName;
            }
        }
        for (DataDefinitionDeclaration datadefdecl : stepDefinition.outputs()){
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
            if (datadefdecl.getName().equals(dataName)){
                return true;
            }
        }
        for (DataDefinitionDeclaration datadefdecl : stepDefinition.outputs()){
            if (datadefdecl.getName().equals(dataName)){
                return true;
            }
        }
        return false;
    }
}
