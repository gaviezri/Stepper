package stepper.step.impl;

import stepper.dd.impl.DataDefinitionRegistry;
import stepper.dd.impl.relation.RelationData;
import stepper.dd.impl.relation.RelationDataDefinition;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.DataNecessity;
import stepper.step.api.StepResult;

import java.util.List;

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
        RelationData relation = (RelationData) context.getDataValue("SOURCE", RelationDataDefinition.class);
        AbstractLogger logger = context.getStepLogger(this);
        StringBuilder result;
        //TODO: SOME LOGGINGS AND FAILSAFE
        for (int i = 0; i < relation.getRowSize() ; i++) {
            result = new StringBuilder();
            for (int j = 0; j < relation.getColSize(); j++) {
                result.append(relation.getDataFromRow(i))
                        .append(",");
            }
            //logger.info(result.toString());
        }


        return StepResult.NULL;
    }
}
