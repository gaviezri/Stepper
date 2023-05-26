package stepper.flow.definition.api;

import stepper.dd.api.DataDefinition;
import stepper.step.StepDefinitionRegistry;
import stepper.step.api.StepDefinition;

import java.util.Collection;

public interface StepUsageDeclaration {
    Boolean isInputMandatory(String inputFinalName);

    Collection<String> getAllInputsFinalNames();

    String getFinalStepName();
    StepDefinition getStepDefinition();
    boolean skipIfFail();

    StepDefinitionRegistry getStepDefinitionRegistry();

    String getInput2FinalName(String inputName);
    String getOutput2FinalName(String outputName);

    void putResource2FinalName(String resourceName, String finalName);

    String getResourceFinalName(String dataName);

    Boolean containsResource(String dataName);

    DataDefinition getDataDefByName(String orgInputName);

    Collection<String> getAllOutputsFinalNames();

    String getFinalInputNameByOrg(String org);

    public String getInputOrgNameByFinalName(String finalName);
}
