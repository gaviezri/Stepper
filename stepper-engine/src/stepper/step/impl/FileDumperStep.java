package stepper.step.impl;

import stepper.dd.api.DataDefinition;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileDumperStep extends AbstractStepDefinition {

        public FileDumperStep() {
            super("File Dumper", true);

            //inputs
            addInput(new DataDefinitionDeclarationImpl("CONTENT", DataNecessity.MANDATORY, "Content", DataDefinitionRegistry.STRING));
            addInput(new DataDefinitionDeclarationImpl("FILE_NAME", DataNecessity.MANDATORY, "Target file path", DataDefinitionRegistry.STRING));

            //outputs
            addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "File Creation Result", DataDefinitionRegistry.STRING));
        }

        private boolean validateFileName(String fileName) {
            int extenstionIdx = fileName.lastIndexOf('.');
            int lastSlashIdx = fileName.lastIndexOf('\\');
            extenstionIdx = extenstionIdx == -1 ? fileName.length() : extenstionIdx;
            return extenstionIdx > lastSlashIdx || new File(fileName).exists();
        }

        private boolean validateContent(String content) {
            return content != null && !content.isEmpty();
        }

        @Override
        public DataDefinition getResourceDataDefinition(String dataOriginalName) {
            switch (dataOriginalName) {
                case "CONTENT":
                case "FILE_NAME":
                case "RESULT":
                    return DataDefinitionRegistry.STRING;
                default:
                    throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
            }
        }
        @Override
        public DataNecessity getResourceNecessity(String dataOriginalName) {
            switch (dataOriginalName) {
                case "CONTENT":
                case "FILE_NAME":
                    return DataNecessity.MANDATORY;
                case "RESULT":
                    return DataNecessity.NA;
                default:
                    throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
            }
        }
        @Override
        public StepResult validateInputs(StepExecutionContext context) {
            AbstractLogger logger = context.getStepLogger();
            StepResult result;
            try{
                String content = context.getDataValue("CONTENT", String.class);
                String fileName = context.getDataValue("FILE_NAME", String.class);

                if (! validateFileName(fileName)) {
                    logger.log("File name is invalid");
                    logger.addSummaryLine("File " + fileName + " NOT created!");
                    result = StepResult.FAILURE;
                }
                else if (! validateContent(content)) {
                    logger.log("Content is empty");
                    result = StepResult.WARNING;
                }
                else{
                    logger.log("File name is valid");
                    logger.log("About to create file named " + fileName);
                    result =  StepResult.SUCCESS;
                }
            }
            catch (Exception e){
                logger.log("Exception: " + e.getMessage());
                result = StepResult.FAILURE;
            }
            return result;
        }

        @Override
        public StepResult invoke(StepExecutionContext context) {

            context.tick();
            StepResult result = validateInputs(context);
            AbstractLogger logger = context.getStepLogger();
            String content;
            String fileName = "UNKNOWN";
            String cause = "";
            try {
                switch (result) {
                    case SUCCESS:
                    case WARNING:
                        try {
                            content = context.getDataValue("CONTENT", String.class);
                            fileName = context.getDataValue("FILE_NAME", String.class);
                            createFile(fileName, content);
                            logger.addSummaryLine("File " + fileName + " created successfully !");
                        } catch (IOException e) {
                            logger.log(e.getMessage());
                            logger.addSummaryLine("File " + fileName + " NOT created!");
                            result = StepResult.FAILURE;
                        }
                        break;
                    case FAILURE:
                    default:
                        cause = "file name is invalid";
                        logger.log("Something went wrong");
                        logger.addSummaryLine("File " + fileName + " NOT created!");
                        break;
                }

                context.storeDataValue("RESULT",
                        (result == StepResult.FAILURE ? "FAILURE: " + cause : "SUCCESS"), DataDefinitionRegistry.STRING);
            } catch (Exception e) {
                logger.log(e.getMessage());
                logger.addSummaryLine("File " + fileName + " NOT created!");
                result = StepResult.FAILURE;
            }
            context.tock();
            return result;
        }

    private void createFile(String filename, String content) throws IOException {

        File file = new File(filename);
        if (file.createNewFile())
        {
            FileWriter Writer = new FileWriter(filename);
            Writer.write(content);
            Writer.close();

        }
        else{
            throw new IOException("File <" + filename + ">  already exists");
        }
    }
}
