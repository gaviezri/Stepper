package stepper.step.impl;

import stepper.dd.impl.DataDefinitionRegistry;
import stepper.dd.impl.relation.RelationData;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

import java.util.List;

public class PropertiesExporterStep extends AbstractStepDefinition {
    public PropertiesExporterStep() {
        super("Properties Exporter", true);

        //inputs
        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source data", DataDefinitionRegistry.RELATION));

        //outputs
        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "Properties export result", DataDefinitionRegistry.STRING));
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
    public stepper.dd.api.DataDefinition getResourceDataDefinition(String dataOriginalName) {
        switch (dataOriginalName) {
            case "RESULT":
                return DataDefinitionRegistry.STRING;
            case "SOURCE":
                return DataDefinitionRegistry.RELATION;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }
    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.tick();

        AbstractLogger logger = context.getStepLogger();
        StepResult stepResult = validateInputs(context);
        RelationData source;
        try{
            if (stepResult == StepResult.SUCCESS) {

                source = context.getDataValue("SOURCE", RelationData.class);
                List<String> properties = source.getColumnsNames();
                List row;
                StringBuilder result = new StringBuilder();


                logger.log("About to process " + source.getRowSize() + " lines of data");

                for (int i = 0; i < source.getRowSize(); i++) {

                    row = source.getDataFromRow(i);

                    for (int j = 0; j < row.size(); j++) {
                        result.append("row-")
                                .append(i + 1)
                                .append(".")
                                .append(properties.get(j))
                                .append("=")
                                .append(row.get(j))
                                .append("\n");
                    }
                }
                logger.log("Extracted total of " + source.getTotalSize() + " properties");
                context.storeDataValue("RESULT", result.toString(), DataDefinitionRegistry.STRING);
            } else {
                logger.addSummaryLine("Source data is empty\n result is empty");
            }
        }catch (Exception e){
            logger.addSummaryLine("Error occurred during properties export");
            logger.addSummaryLine(e.getMessage());
            stepResult = StepResult.FAILURE;
        }
        logger.addSummaryLine("Properties export completed successfully");
        context.tock();
        return stepResult;
    }

    @Override
    public StepResult validateInputs(StepExecutionContext context) {
        try {
            return  context.getDataValue("SOURCE", RelationData.class).getRowSize() != 0 ?
                    StepResult.SUCCESS : StepResult.WARNING;
        } catch (Exception e) {
            return StepResult.FAILURE;
        }
    }
}
