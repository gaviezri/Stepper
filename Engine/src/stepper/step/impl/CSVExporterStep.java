package stepper.step.impl;

import stepper.dd.impl.DataDefinitionRegistry;
import stepper.dd.impl.relation.RelationData;
import stepper.dd.impl.relation.RelationDataDefinition;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

public class CSVExporterStep extends AbstractStepDefinition {
    public CSVExporterStep() {
        super("CSV Exporter", true);

        //inputs
        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source data", DataDefinitionRegistry.RELATION));

        //outputs
        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "CSV export result", DataDefinitionRegistry.STRING));

    }
    @Override
    public stepper.dd.api.DataDefinition getResourceDataDefinition(String dataOriginalName) {
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
    public StepResult invoke(StepExecutionContext context) {

        context.tick();
        AbstractLogger logger = context.getStepLogger();
        RelationData relation;
        StepResult result;
        try {
            relation = (RelationData) context.getDataValue("SOURCE", RelationDataDefinition.class);

            StringBuilder CSV = new StringBuilder();


            if (relation == null || relation.getRowSize() == 0) {
                logger.log("Relation is Empty or null");
                logger.addSummaryLine("relation did not export to CSV because it is empty or null. ");
                result = StepResult.WARNING;
            } else {
                int i = 0;
                // insert column names
                for (String columnName : relation.getColumnNames()) {
                    CSV.append(columnName).append(',');
                }
                // delete last comma and add new line
                CSV.delete(CSV.length() - 1, CSV.length()).append('\n');

                logger.log("About to process " + relation.getRowSize() + " lines of data");
                for (; i < relation.getRowSize(); i++) {
                    int j = 0;
                    for (; j < relation.getColSize()-1 ; j++){
                        CSV.append(relation.getDataFromCell(i, j)).append(',');
                    }
                    CSV.append(relation.getDataFromCell(i,j)).append('\n');
                }
                context.storeDataValue(this.outputs().get(0).getName(), CSV.toString(), DataDefinitionRegistry.STRING);
                logger.addSummaryLine("relation exported to CSV successfully");
                result = StepResult.SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.log("Error while exporting relation to CSV");
            logger.log(e.getMessage());
            logger.addSummaryLine("relation did not export to CSV.");
            return StepResult.FAILURE;
        }
        context.tock();
        context.getCurrentStepManager().setStepResult(result);
        return result;
    }
}
