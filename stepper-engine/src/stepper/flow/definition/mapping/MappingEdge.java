package stepper.flow.definition.mapping;

import stepper.dd.api.DataDefinition;
import stepper.dd.impl.DataDefinitionRegistry;

public class MappingEdge {
    private final String sourceStepFinalName;
    private final String sourceDataFinalName;
    private final String targetStepFinalName;
    private final String targetDataFinalName;
    private final DataDefinition dataDefinition;

    public MappingEdge(String sourceStepFinalName, String sourceDataFinalName, String targetStepFinalName, String targetDataFinalName, DataDefinition datadef) {
        this.sourceStepFinalName = sourceStepFinalName;
        this.sourceDataFinalName = sourceDataFinalName;
        this.targetStepFinalName = targetStepFinalName;
        this.targetDataFinalName = targetDataFinalName;
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
    public DataDefinition getDataDefinition() {
        return dataDefinition;
    }

}
