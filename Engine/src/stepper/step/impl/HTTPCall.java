package stepper.step.impl;

import stepper.dd.impl.DataDefinitionRegistry;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.StepLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

public class HTTPCall extends AbstractStepDefinition {
    public HTTPCall(String stepName, boolean readonly) {
        super("HTTP call", false);

        //inputs
        addInput(new DataDefinitionDeclarationImpl("RESOURCE", DataNecessity.MANDATORY,"Resource Name (include query parameters)", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("ADDRESS", DataNecessity.MANDATORY, "Domain:Port", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("PROTOCOL", DataNecessity.MANDATORY,"protocol", DataDefinitionRegistry.ENUMERATION));
        addInput(new DataDefinitionDeclarationImpl("METHOD", DataNecessity.OPTIONAL, "Method", DataDefinitionRegistry.ENUMERATION));//if no method selected = GET
        addInput(new DataDefinitionDeclarationImpl("BODY", DataNecessity.OPTIONAL, "Request Body", DataDefinitionRegistry.JSON));

        //outputs
        addOutput(new DataDefinitionDeclarationImpl("CODE", DataNecessity.NA, "Response code", DataDefinitionRegistry.NUMBER));
        addOutput(new DataDefinitionDeclarationImpl("RESPONSE_BODY", DataNecessity.NA, "Response body", DataDefinitionRegistry.STRING));
    }

    public StepResult invoke(StepExecutionContext context) {
        context.tick();
        StepLogger logger = context.getStepLogger();
        StepResult result = StepResult.SUCCESS;

        try {
            /** stepper request dospatcher */
        }
        catch (Exception e) {
            logger.log("Exception occurred: " + e.getMessage());
            result = StepResult.FAILURE;
        }

//        context.storeDataValue("RENAME_RESULT",  , DataDefinitionRegistry.RELATION);
        context.tock();
        context.getCurrentStepManager().setStepResult(result);
        return result;
    }
        @Override
    public stepper.dd.api.DataDefinition getResourceDataDefinition(String dataOriginalName) {
        switch (dataOriginalName) {
            case "RESOURCE":
            case "ADDRESS":
                return DataDefinitionRegistry.STRING;
            case "PROTOCOL":
            case "METHOD":
                return DataDefinitionRegistry.ENUMERATION;
            case "BODY":
                return DataDefinitionRegistry.JSON;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }
    @Override
    public DataNecessity getResourceNecessity(String dataOriginalName) {
        switch (dataOriginalName) {
            case "RESOURCE":
            case "ADDRESS":
            case "PROTOCOL":
                return DataNecessity.MANDATORY;
            case "METHOD":
            case "BODY":
                return DataNecessity.OPTIONAL;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }

}
