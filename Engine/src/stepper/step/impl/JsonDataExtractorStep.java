package stepper.step.impl;

import com.jayway.jsonpath.JsonPath;
import stepper.dd.api.DataDefinition;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.StepLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;
import java.util.List;
import java.util.regex.Pattern;

public class JsonDataExtractorStep extends AbstractStepDefinition {

    public JsonDataExtractorStep() {
        super("Json Data Extractor", true);

        //inputs
        addInput(new DataDefinitionDeclarationImpl("JSON", DataNecessity.MANDATORY,"Json source", DataDefinitionRegistry.JSON));
        addInput(new DataDefinitionDeclarationImpl("JSON_PATH", DataNecessity.MANDATORY,"data", DataDefinitionRegistry.STRING));

        //outputs
        addOutput(new DataDefinitionDeclarationImpl("VALUE", DataNecessity.NA, "Dats value", DataDefinitionRegistry.STRING));
    }

    private String[] extractJsonPathList(String fullJsonPath){
        String[] parts = new String[]{};

        if(fullJsonPath.contains(Pattern.quote("|"))) {
            parts = fullJsonPath.split(Pattern.quote("|"));
        }
        else{
            parts[0] = fullJsonPath;
        }

        return parts;
    }
    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.tick();
        StepLogger logger = context.getStepLogger();
        StepResult result = StepResult.SUCCESS;
        String jsonPath;
        String json;
        String extractedValue = null;

        try {
            jsonPath = context.getDataValue("JSON_PATH", String.class);
            json = context.getDataValue("JSON", String.class);
            extractJsonPathList(jsonPath);

            List<String> extractedValueList = JsonPath.read(json, jsonPath);

            if(extractedValueList.isEmpty()) {
                logger.log("No value found for json path " + jsonPath);
            } else{
                extractedValue = String.join(",", extractedValueList);
            }

            logger.log("Extracting data " + jsonPath + ". Value: " + extractedValue);
            context.storeDataValue("VALUE", extractedValue , DataDefinitionRegistry.STRING);
        }
        catch (Exception e) {
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
            case "JSON_PATH":
            case "VALUE":
                return DataDefinitionRegistry.STRING;
            case "JSON":
                return DataDefinitionRegistry.JSON;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }

    @Override
    public DataNecessity getResourceNecessity(String dataOriginalName) {
        switch (dataOriginalName) {
            case "JSON_PATH":
            case "JSON":
                return DataNecessity.MANDATORY;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }

    @Override
    public StepResult validateInputs(StepExecutionContext context) {
        return null;
    }
}
