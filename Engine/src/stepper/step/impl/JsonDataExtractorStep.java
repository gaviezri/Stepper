package stepper.step.impl;

import com.jayway.jsonpath.JsonPath;
import communication.Utils;
import stepper.dd.api.DataDefinition;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.StepLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

import java.util.Arrays;
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

    private List<String> extractJsonPathList(String fullJsonPath){
        return Arrays.asList(fullJsonPath.split(Pattern.quote("|")));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.tick();
        StepLogger logger = context.getStepLogger();
        StepResult result = StepResult.SUCCESS;
        /** this function handles both single and multiply json path commands.
         * it will extract the data from the json by each command and append it to the result string */
        try {
            String json = context.getDataValue("JSON", String.class);
            json = Utils.GSON_INSTANCE.fromJson(json,String.class);

            String jsonPath = context.getDataValue("JSON_PATH", String.class);
            List<String> jsonPathList = extractJsonPathList(jsonPath);
            StringBuilder extractedValue = new StringBuilder();
            String curExtractedValue = null;
            int counter = 0;

            if (jsonPathList.isEmpty()) {
                logger.log("No value found for json path " + jsonPath);
            }
            else {
                while (counter < jsonPathList.size()) {
                    try {
                    curExtractedValue = JsonPath.read(json, jsonPathList.get(counter));
                    }
                    catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    if (counter == 0) {
                        extractedValue.append(curExtractedValue);
                    }
                    else{
                        extractedValue.append(", ").append(curExtractedValue);
                    }
                    counter += 1;
                }
            }

            logger.log("Extracting data " + jsonPath + ". Value: " + extractedValue.toString());
            context.storeDataValue("VALUE", extractedValue.toString() , DataDefinitionRegistry.STRING);
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
