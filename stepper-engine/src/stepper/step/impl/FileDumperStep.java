package stepper.step.impl;

import stepper.dd.impl.DataDefinitionRegistry;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.DataNecessity;
import stepper.step.api.StepResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileDumperStep extends AbstractStepDefinition {

        public FileDumperStep() {
            super("FileDumper", true);

            //inputs
            addInput(new DataDefinitionDeclarationImpl("CONTENT", DataNecessity.MANDATORY, "Content", DataDefinitionRegistry.STRING));
            addInput(new DataDefinitionDeclarationImpl("FILENAME", DataNecessity.MANDATORY, "Target file path", DataDefinitionRegistry.STRING));

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
        public StepResult validateInputs(StepExecutionContext context) {

            String content = context.getDataValue("CONTENT", String.class);
            String fileName = context.getDataValue("FILENAME", String.class);
            StepResult result;
            if (! validateFileName(fileName)) {
                result = StepResult.FAILURE;
            }
            else if (! validateContent(content)) {
                result = StepResult.WARNING;
            }
            else{
                result =  StepResult.SUCCESS;
            }
            return result;
        }

        @Override
        public StepResult invoke(StepExecutionContext context) {

            StepResult result = StepResult.NULL;
            String content = context.getDataValue("CONTENT", String.class);
            String fileName = context.getDataValue("FILENAME", String.class);
            String Cause = "";
            AbstractLogger logger = context.getStepLogger(this);
            result = validateInputs(context);

            switch (result){
                case SUCCESS:
                    logger.addLogLine("File name is valid");
                    logger.addLogLine("About to create file named " + fileName);
                    logger.addSummaryLine("File " + fileName + " created successfully !");
                case WARNING:
                    Cause = "Content is empty";
                    logger.addLogLine(Cause);
                    try {
                        createFile(fileName, content);
                    }
                    catch (IOException e) {
                        logger.addLogLine(e.getMessage());
                    }
                    break;
                case FAILURE:
                    Cause = "File name is invalid";
                    logger.addLogLine(Cause);

                default:
                    logger.addLogLine("Something went wrong");
                    logger.addSummaryLine("File " + fileName + " NOT created!");
                    break;
            }

            context.storeDataValue("RESULT",  result +
                    (result != StepResult.SUCCESS ?  ": " + Cause : ""));

            return result;
        }

    private void createFile(String filename, String content) throws IOException {

        File file = new File(filename);
        if (file.createNewFile())
        {
            FileWriter Writer = new FileWriter(filename);
            Writer.write(content);

        }
    }
}
