package stepper.step.impl;

import com.sun.org.apache.xerces.internal.impl.xpath.XPath;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.StepExecutionLogger;
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

            if (validateFileName(fileName) == false) {
                return StepResult.FAILURE;
            }

            if (validateContent(content) == false) {
                return StepResult.WARNING;
            }
            return StepResult.SUCCESS;
        }

        @Override
        public StepResult invoke(StepExecutionContext context) {
            StepResult result = StepResult.NULL;
            String content = context.getDataValue("CONTENT", String.class);
            String fileName = context.getDataValue("FILENAME", String.class);
            StepExecutionLogger logger = context.getLogger();
            result = validateInputs(context);

            switch (result){
                case SUCCESS:
                    logger.addLogLine("File name is valid",this);
                    logger.addLogLine("About to create file named " + fileName,this);
                case WARNING:
                    logger.addLogLine("Content is empty",this);
                    try {
                        createFile(fileName, content);
                    }
                    catch (IOException e) {
                        logger.addLogLine(e.getMessage(),this);
                    }
                    break;
                case FAILURE:
                    logger.addLogLine("File name is invalid",this);
                default:
                    logger.addLogLine("Something went wrong",this);
                    break;
            }

            context.storeDataValue("RESULT", result);
            logger.addSummaryLine("File " + fileName + " created successfully !", this);
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
