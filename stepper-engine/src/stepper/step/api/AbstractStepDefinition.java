package stepper.step.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStepDefinition implements StepDefinition, Serializable {

    private final String stepName;
    private final boolean readonly;
    private final List<DataDefinitionDeclaration> inputs = new ArrayList<>();
    private final List<DataDefinitionDeclaration> outputs = new ArrayList<>();

    public AbstractStepDefinition(String stepName, boolean readonly) {
        this.stepName = stepName;
        this.readonly = readonly;
    }

    protected void addInput(DataDefinitionDeclaration dataDefinitionDeclaration) {
        inputs.add(dataDefinitionDeclaration);
    }

    protected void addOutput(DataDefinitionDeclaration dataDefinitionDeclaration) {
        outputs.add(dataDefinitionDeclaration);
    }

    @Override
    public String getStepName() {
        return stepName;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public List<DataDefinitionDeclaration> inputs() {
        return inputs;
    }

    @Override
    public List<DataDefinitionDeclaration> outputs() {
        return outputs;
    }

    @Override
    public String getResourceUserString(String resource) {
        for (DataDefinitionDeclaration datadef : inputs) {
            if (datadef.getName().equals(resource)) {
                return datadef.userString();
            }
        }
        for (DataDefinitionDeclaration datadef : outputs) {
            if (datadef.getName().equals(resource)) {
                return datadef.userString();
            }
        }
        return resource;
    }



}
