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

public class CSVExporterStep extends AbstractStepDefinition {
    public CSVExporterStep() {
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
        StringBuilder CSV = new StringBuilder();
        StepResult result;

        if (relation == null || relation.getRowSize() == 0) {
            logger.addLogLine("Relation is Empty or null");
            result = StepResult.WARNING;
        }
        else{
            int relationSize = relation.getRowSize();
            int i = 0;
            logger.addLogLine("About to process " + relation.getRowSize() + " lines of data");
            for (; i < relationSize - 1; i++) {
                    CSV.append(relation.getDataFromRow(i))
                            .append(',');
                }
            // last line no comma
            CSV.append(relation.getDataFromRow(i));
            //TODO: Get output-name from context if alias is used
            context.storeDataValue(this.outputs().get(0).getName(), CSV.toString());
            logger.addSummaryLine("relation exported to CSV successfully");
            result = StepResult.SUCCESS;
        }
        return result;
    }
}
