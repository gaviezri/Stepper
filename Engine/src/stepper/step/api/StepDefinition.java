package stepper.step.api;

import stepper.dd.api.DataDefinition;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

import java.util.List;

public abstract class StepDefinition {
    public abstract String getStepName();
    public abstract boolean isReadonly();
    public abstract List<DataDefinitionDeclaration> inputs();
    public abstract List<DataDefinitionDeclaration> outputs();
    public abstract StepResult invoke(StepExecutionContext context);
    public abstract StepResult validateInputs(StepExecutionContext context);
    public abstract DataDefinition getResourceDataDefinition(String dataOriginalName);
    public abstract DataNecessity getResourceNecessity(String dataOriginalName);
    public abstract String getResourceUserString(String resource);
    public DataDefinitionDeclaration getInputDataDefDecByInputOrgName(String dataName){
        DataDefinitionDeclaration res = null;
        for(DataDefinitionDeclaration dataDefDec : inputs()){
            if(dataDefDec.getName().equals(dataName)){
                res = dataDefDec;
            }
        }
        return res;
    }

    public DataDefinitionDeclaration getOutputDataDefDecByOutputOrgName(String dataName){
        DataDefinitionDeclaration res = null;
        for(DataDefinitionDeclaration dataDefDec : outputs()){
            if(dataDefDec.getName().equals(dataName)){
                res = dataDefDec;
            }
        }
        return res;
    }
}
