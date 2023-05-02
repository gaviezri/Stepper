package stepper.flow.definition.mapping;

import stepper.dd.api.DataDefinition;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.step.api.enums.DataNecessity;

import java.io.Serializable;

public class MappingEdge implements Serializable {
    private final String sourceStepFinalName;
    private final String sourceDataFinalName;
    private final String targetStepFinalName;
    private final String targetDataFinalName;
    private final DataDefinition dataDefinition;
    private final DataNecessity dataNecessityAtTargetStep;

    public MappingEdge(String sourceStepFinalName, String sourceDataFinalName, String targetStepFinalName, String targetDataFinalName, DataDefinition datadef, DataNecessity dataNecessityAtTargetStep) {
        this.sourceStepFinalName = sourceStepFinalName;
        this.sourceDataFinalName = sourceDataFinalName;
        this.targetStepFinalName = targetStepFinalName;
        this.targetDataFinalName = targetDataFinalName;
        this.dataNecessityAtTargetStep = dataNecessityAtTargetStep;
        this.dataDefinition = datadef;
    }

    public String getSourceStepName() {
        return sourceStepFinalName;
    }
    public String getSourceDataName() {
        return sourceDataFinalName;
    }
    public String getTargetStepName() {
        return targetStepFinalName;
    }
    public String getTargetDataName() {
        return targetDataFinalName;
    }
    public DataNecessity getDataNecessityAtTargetStep() {
        return dataNecessityAtTargetStep;
    }
    public DataDefinition getDataDefinition() {
        return dataDefinition;
    }

}
