package stepper.step.impl;

import stepper.dd.api.DataDefinition;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.dd.impl.file.FileData;
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
import java.util.ArrayList;
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
    public DataDefinition getResourceDataDefinition(String dataOriginalName) {
        switch (dataOriginalName) {
            case "FILES_LIST":
                return DataDefinitionRegistry.LIST;
            case "TOTAL_FOUND":
                return DataDefinitionRegistry.NUMBER;
            case "FOLDER_NAME":
            case "FILTER":
                return DataDefinitionRegistry.STRING;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }

    @Override
    public DataNecessity getResourceNecessity(String dataOriginalName) {
        switch (dataOriginalName) {
            case "FILES_LIST":
            case "TOTAL_FOUND":
                return DataNecessity.NA;
            case "FOLDER_NAME":
                return DataNecessity.MANDATORY;
            case "FILTER":
                return DataNecessity.OPTIONAL;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }

    @Override
    public StepResult validateInputs(StepExecutionContext context) {

        AbstractLogger logger = context.getStepLogger();
        String folderName;
        String filter;
        StepResult result;
        try {

            // and check if there is a filter
            folderName = context.getDataValue("FOLDER_NAME", String.class);
            try {
                filter = context.getDataValue("FILTER", String.class);
            } catch (Exception e) {
                System.out.println("No filter provided... continuing");
                filter = null;
            }
            if (FolderNotExist(folderName)) {
                // make sure folder exists
                logger.log("Folder name is invalid");
                logger.addSummaryLine("Folder " + folderName + " NOT scanned! it does not exist");
                result = StepResult.FAILURE;
            } else if (NotAFolder(folderName)) {
                // make sure exists and also a directory
                logger.log("Folder name represents a non-folder entity");
                logger.addSummaryLine("Folder " + folderName + " NOT scanned! it is not a folder");
                result = StepResult.FAILURE;

            } else if (FolderIsEmpty(folderName)) {
                logger.log("Folder name represents an empty folder");
                result = StepResult.WARNING;
            } else{
                logger.log("Reading folder " + folderName + " content with filter: " + (filter != null ? filter : "no-filter"));
                result = StepResult.SUCCESS;
            }
        } catch (Exception e) {
            logger.log("Error while reading inputs: " + e.getMessage());
            result = StepResult.FAILURE;
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
    public StepResult invoke(StepExecutionContext context) {
        context.tick();
        String finalName = context.getCurrentStepName();
        AbstractLogger logger = context.getStepLogger();
        StepResult result = validateInputs(context);
        switch (result) {
            case SUCCESS:
                result = handleSUCCESS(context);
                break;
            case WARNING:
                try {
                    logger.addSummaryLine("Folder " + context.getDataValue("FOLDER_NAME", String.class) + " scanned successfully but it is empty");
                    context.storeDataValue(this.outputs().get(0).getName(), new ArrayList<>(), DataDefinitionRegistry.LIST);
                    context.storeDataValue(this.outputs().get(1).getName(), 0, DataDefinitionRegistry.NUMBER);
                } catch (Exception e) {
                    logger.log("Error while storing data");
                    logger.log(e.getMessage());
                    result = StepResult.FAILURE;
                }
                break;
            case FAILURE:
                break;
        }
        context.tock();
        return result;
    }

    public StepResult handleSUCCESS(StepExecutionContext context) {

        AbstractLogger logger = context.getStepLogger();
        String folderName;
        String filter;
        try {
             folderName = context.getDataValue("FOLDER_NAME", String.class);
        } catch (Exception e) {
            logger.log("Error while reading folder name");
            logger.log(e.getMessage());
            return StepResult.FAILURE;
        }
        try {
            filter = context.getDataValue("FILTER", String.class);
        }catch (Exception e){
            filter = null;
        }
        List<FileData> filesList = null;
        try {
            // get all files in folder that are not directories
            logger.log("Reading folder " + folderName + " content" + (filter != null ? " with filter: " + filter : ""));
            filesList = Files.list(Paths.get(folderName))
                    .filter(f-> !Files.isDirectory(f))
                    .map(FileData::new)
                    .collect(Collectors.toList());

            logger.log("Found " + filesList.size() + " files in folder" + (filter != null ? " matching filter: " + filter : ""));
        } catch (IOException e) {
            logger.log("Error while reading folder content");
            logger.log(e.getMessage());
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
            logger.log("Error while reading folder content");
            logger.log(e.getMessage());
            return StepResult.FAILURE;
        }
        if (filesList != null) {
            try {
                context.storeDataValue(this.outputs().get(0).getName(), filesList, DataDefinitionRegistry.LIST);
                context.storeDataValue(this.outputs().get(1).getName(), filesList.size(), DataDefinitionRegistry.NUMBER);
                logger.addSummaryLine("Folder " + folderName + " scanned successfully");
                return StepResult.SUCCESS;
            }
            catch (Exception e) {
                logger.log("Error while storing data");
                logger.log(e.getMessage());
                return StepResult.FAILURE;
            }

        }
        logger.addSummaryLine("Folder " + folderName + " NOT scanned!\n" + "Unknown error");
        return StepResult.FAILURE;
    }
}
