package stepper.step.impl;

import stepper.dd.api.AbstractDataDefinition;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.dd.impl.relation.RelationData;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

import java.util.List;
import java.util.logging.Logger;

public class PropertiesExporterStep extends AbstractStepDefinition {
    public PropertiesExporterStep() {
        super("PropertiesExporter", true);

        //inputs
        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source data", DataDefinitionRegistry.RELATION));

        //outputs
        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "Properties export result", DataDefinitionRegistry.STRING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {

        AbstractLogger logger = context.getStepLogger(this);
        StepResult stepResult = validateInputs(context);

        if (stepResult == StepResult.SUCCESS) {

            RelationData source = context.getDataValue("SOURCE", RelationData.class);
            List<String> properties = source.getColumnsNames();
            List row;
            StringBuilder result = new StringBuilder();


            logger.addLogLine("About to process " + source.getRowSize() + " lines of data");

            for (int i = 0; i < source.getRowSize(); i++) {

                row = source.getDataFromRow(i);

                for (int j = 0; j < row.size(); j++) {
                    result.append("row-")
                            .append(i + 1)
                            .append(".")
                            .append(properties.get(j))
                            .append("=")
                            .append(row.get(j))
                            .append("\r\n");
                }
            }
            logger.addLogLine("Extracted total of " + source.getTotalSize() + " properties");
            context.storeDataValue("RESULT", result.toString());
        } else {
            logger.addSummaryLine("Source data is empty\n result is empty");
        }
        logger.addSummaryLine("Properties export completed successfully");
        return stepResult;
    }

    @Override
    public StepResult validateInputs(StepExecutionContext context) {
        return context.getDataValue("SOURCE", RelationData.class).getRowSize() != 0 ?
                StepResult.SUCCESS : StepResult.WARNING;
    }
}
