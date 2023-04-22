package stepper.step.impl;

import stepper.dd.impl.DataDefinitionRegistry;
import stepper.dd.impl.file.FileData;
import stepper.dd.impl.list.ListData;
import stepper.dd.impl.number.NumberData;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class CollectFilesInFolderStep extends AbstractStepDefinition {

    public CollectFilesInFolderStep() {
        super("Collect Files In Folder", true);

        //inputs
        addInput(new DataDefinitionDeclarationImpl("FOLDER_NAME", DataNecessity.MANDATORY, "Folder name to scan", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("FILTER", DataNecessity.OPTIONAL, "Filter only these files", DataDefinitionRegistry.STRING));

        //outputs
        addOutput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.NA, "Files list", DataDefinitionRegistry.LIST));
        addOutput(new DataDefinitionDeclarationImpl("TOTAL_FOUND", DataNecessity.NA, "Total files found", DataDefinitionRegistry.NUMBER));

    }

    @Override
    public StepResult validateInputs(StepExecutionContext context) {

        AbstractLogger logger = context.getStepLogger(this);
        String folderName = context.getDataValue("FOLDER_NAME", String.class);
        String filter = context.getDataValue("FILTER", String.class);
        StepResult result;
        if (FolderNotExist(folderName)) {
            logger.addLogLine("Folder name is invalid");
            logger.addSummaryLine("Folder " + folderName + " NOT scanned! it does not exist");
            result = StepResult.FAILURE;
        } else if (NotAFolder(folderName)) {
            logger.addLogLine("Folder name represents a non-folder entity");
            logger.addSummaryLine("Folder " + folderName + " NOT scanned! it is not a folder");
            result = StepResult.FAILURE;

        } else if (FolderIsEmpty(folderName)) {
            logger.addLogLine("Folder name represents an empty folder");
            result = StepResult.WARNING;
        } else{
            logger.addLogLine("Reading folder " + folderName + " content with filter: " + (filter != null ? filter : "none"));
            result = StepResult.SUCCESS;
        }
        return result;
    }

    private boolean FolderIsEmpty(String folderName) {
        Path path = Paths.get(folderName);
        try {
            return Files.list(path).count() == 0;
        } catch (Exception e) {
            return true;
        }

    }

    private boolean NotAFolder(String folderName) {
        Path path = Paths.get(folderName);
        return !Files.isDirectory(path);
    }

    private boolean FolderNotExist(String folderName) {
        Path path = Paths.get(folderName);
        return !Files.exists(path);
    }

    @Override
    public StepResult invoke(StepExecutionContext context, String finalName) {
//        context.tick(this.getStepName());
        AbstractLogger logger = context.getStepLogger(this);
        StepResult result = validateInputs(context);
        switch (result) {
            case SUCCESS:
                result = handleSUCCESS(context);
                break;
            case WARNING:
                logger.addSummaryLine("Folder " + context.getDataValue("FOLDER_NAME", String.class) + " scanned successfully but it is empty");
                context.storeDataValue(this.outputs().get(0).getName(), null);
                context.storeDataValue(this.outputs().get(1).getName(), new NumberData(0));
                break;
            case FAILURE:
                break;
        }
        return result;
    }

    public StepResult handleSUCCESS(StepExecutionContext context) {

        AbstractLogger logger = context.getStepLogger(this);
        String folderName = context.getDataValue("FOLDER_NAME", String.class);
        String filter = context.getDataValue("FILTER", String.class);
        List<FileData> filesList = null;
        try {
            // get all files in folder
            logger.addLogLine("Reading folder " + folderName + " content" + (filter != null ? " with filter: " + filter : ""));
            filesList = Files.list(Paths.get(folderName))
                    .map(FileData::new)
                    .collect(Collectors.toList());
            logger.addLogLine("Found " + filesList.size() + " files in folder" + (filter != null ? " matching filter: " + filter : ""));
        } catch (IOException e) {
            logger.addLogLine("Error while reading folder content");
            logger.addLogLine(e.getMessage());
            return StepResult.FAILURE;
        }

        try {
            // filter files by regex
            if (filter != null) {
                filter = filter.replace("*", ".*");
                Pattern regexPattern = Pattern.compile(filter);
                filesList = filesList.stream()
                        .filter(f -> regexPattern.matcher(f.getName()).matches())
                        .collect(Collectors.toList());
            }

        } catch (Exception e) {
            logger.addLogLine("Error while reading folder content");
            logger.addLogLine(e.getMessage());
            return StepResult.FAILURE;
        }
        if (filesList != null) {
            context.storeDataValue(this.outputs().get(0).getName(), new ListData(filesList));
            context.storeDataValue(this.outputs().get(1).getName(), new NumberData(filesList.size()));
            logger.addSummaryLine("Folder " + folderName + " scanned successfully");
            return StepResult.SUCCESS;
        }
        logger.addSummaryLine("Folder " + folderName + " NOT scanned!\n" + "Unknown error");
        return StepResult.FAILURE;
    }
}
