package stepper.step.impl;

import javafx.util.Pair;
import stepper.communication.EngineRequestsDispatcher;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.StepLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

import static communication.Utils.JSON_CONTENT_TYPE;

public class HTTPCallStep extends AbstractStepDefinition {
    public enum MethodType {
        GET,
        POST,
        PUT,
        DELETE
    }
    public enum ProtocolType {
        HTTP,
        HTTPS
    }

    public HTTPCallStep() {
        super("HTTP Call", false);

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
    
    private Pair<String,Number> preProcessStepInputsAndGetResponse(StepExecutionContext context, StepLogger logger) throws Exception {
        String resource;
        String address;
        String protocol;
        String method = MethodType.GET.toString();
        String body = null;

        try {
            resource = context.getDataValue("RESOURCE", String.class);
            address = context.getDataValue("ADDRESS", String.class);
            protocol = context.getDataValue("PROTOCOL", ProtocolType.class).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try{
            method = context.getDataValue("METHOD", MethodType.class).toString();
        }catch (Exception e){}

        try{
            body = context.getDataValue("BODY", String.class);
        }catch (Exception e){}

        logger.log(protocol + "://" + address + resource);
         return EngineRequestsDispatcher.getInstance().createHTTPRequestAndReturnOutput(protocol, resource,
                address, method, JSON_CONTENT_TYPE, body);
    }
    public StepResult invoke(StepExecutionContext context) {
        context.tick();
        StepLogger logger = context.getStepLogger();
        StepResult result = StepResult.SUCCESS;
        Pair<String,Number> res = new Pair<>("",500);

        try {
            res = preProcessStepInputsAndGetResponse(context, logger);
        }
        catch (Exception e) {
            logger.log("Exception occurred: " + e.getMessage());
            result = StepResult.FAILURE;
        }

        logger.log("Received Response. Status code: " + res.getValue().toString());
        context.storeDataValue("CODE", res.getValue(),DataDefinitionRegistry.NUMBER);
        context.storeDataValue("RESPONSE_BODY", res.getKey(),DataDefinitionRegistry.STRING);
        context.tock();
        context.getCurrentStepManager().setStepResult(result);
        return result;
    }

    @Override
    public StepResult validateInputs(StepExecutionContext context) {
        return null;
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
