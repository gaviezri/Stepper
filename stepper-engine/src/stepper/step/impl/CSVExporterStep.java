package stepper.step.impl;

import stepper.dd.api.DataDefinition;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.dd.impl.relation.RelationData;
import stepper.dd.impl.relation.RelationDataDefinition;
import stepper.dd.impl.string.StringData;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

import javax.xml.crypto.Data;

public class CSVExporterStep extends AbstractStepDefinition {
    public CSVExporterStep() {
        super("CSV Exporter", true);

        //inputs
        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source data", DataDefinitionRegistry.RELATION));

        //outputs
        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "CSV export result", DataDefinitionRegistry.STRING));

    }
    @Override
    public DataDefinition getResourceDataDefinition(String dataOriginalName) {
        switch (dataOriginalName) {
            case "SOURCE":
                return DataDefinitionRegistry.RELATION;
            case "RESULT":
                return DataDefinitionRegistry.STRING;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }
    @Override
    public DataNecessity getResourceNecessity(String dataOriginalName) {
        switch (dataOriginalName) {
            case "SOURCE":
                return DataNecessity.MANDATORY;
            case "RESULT":
                return DataNecessity.NA;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }
    @Override
    public StepResult validateInputs(StepExecutionContext context) {
        return StepResult.SUCCESS;
    }

    @Override
    public StepResult invoke(StepExecutionContext context, String finalName) {

        context.tick(this.getStepName());
        AbstractLogger logger = context.getStepLogger(this);
        RelationData relation;
        StepResult result;
        try {
            relation = (RelationData) context.getDataValue("SOURCE", RelationDataDefinition.class);

            StringBuilder CSV = new StringBuilder();


            if (relation == null || relation.getRowSize() == 0) {
                logger.addLogLine("Relation is Empty or null");
                result = StepResult.WARNING;
            } else {
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
                context.storeDataValue(this.outputs().get(0).getName(), CSV.toString(), DataDefinitionRegistry.STRING);
                logger.addSummaryLine("relation exported to CSV successfully");
                result = StepResult.SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.addLogLine("Error while exporting relation to CSV");
            logger.addLogLine(e.getMessage());
            return StepResult.FAILURE;
        }
        context.tock(this.getStepName());
        return result;
    }
}
