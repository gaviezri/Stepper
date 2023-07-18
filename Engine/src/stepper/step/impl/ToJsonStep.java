package stepper.step.impl;

import javafx.util.Pair;
import stepper.dd.api.DataDefinition;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.StepLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

import static communication.Utils.GSON_INSTANCE;

public class ToJsonStep extends AbstractStepDefinition {

    public ToJsonStep() {
        super("To Json", true);

        //inputs
        addInput(new DataDefinitionDeclarationImpl("CONTENT", DataNecessity.MANDATORY,"Content", DataDefinitionRegistry.STRING));

        //outputs
        addOutput(new DataDefinitionDeclarationImpl("JSON", DataNecessity.NA, "Json representation", DataDefinitionRegistry.JSON));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.tick();
        StepLogger logger = context.getStepLogger();
        StepResult result = StepResult.SUCCESS;
        String content;
        String jsonRes;

        try {
           content = context.getDataValue("CONTENT", String.class);
           logger.log("Content is JSON string. Converting it to json...");
           jsonRes = GSON_INSTANCE.toJson(content);
           context.storeDataValue("JSON", jsonRes ,DataDefinitionRegistry.JSON);
        }
        catch (Exception e) {
            logger.log("Content is not a valid JSON representation.");
            logger.log("Exception occurred: " + e.getMessage());
            result = StepResult.FAILURE;
        }

        context.tock();
        context.getCurrentStepManager().setStepResult(result);
        return result;
    }

    @Override
    public DataDefinition getResourceDataDefinition(String dataOriginalName) {
        switch (dataOriginalName) {
            case "CONTENT":
                return DataDefinitionRegistry.STRING;
            case "JSON":
                return DataDefinitionRegistry.JSON;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }

    @Override
    public DataNecessity getResourceNecessity(String dataOriginalName) {
        if (dataOriginalName.equals("CONTENT")) {
            return DataNecessity.MANDATORY;
        }
        throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
    }

    @Override
    public StepResult validateInputs(StepExecutionContext context) {
        return null;
    }
}
