package stepper.step.impl;

import stepper.dd.impl.DataDefinitionRegistry;
import stepper.dd.impl.relation.RelationDataDefinition;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.DataNecessity;
import stepper.step.api.StepResult;

public class CSVExporter extends AbstractStepDefinition {
    public CSVExporter() {
        super("CSVExporter", true);

        //inputs
        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source data", DataDefinitionRegistry.RELATION));

        //outputs
        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "CSV export result", DataDefinitionRegistry.STRING));

    }

    @Override
    public StepResult validateInputs(StepExecutionContext context) {
        return StepResult.SUCCESS;
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        RelationDataDefinition relation = context.getDataValue("SOURCE", RelationDataDefinition.class);
        AbstractLogger logger = context.getStepLogger(this);
        StringBuilder result;
        //TODO: implement


        return StepResult.NULL;
    }
}
