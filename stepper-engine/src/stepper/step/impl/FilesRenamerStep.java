package stepper.step.impl;

import stepper.dd.api.DataDefinition;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.dd.impl.file.FileData;
import stepper.dd.impl.relation.RelationData;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FilesRenamerStep extends AbstractStepDefinition {


    public FilesRenamerStep(){
        super("Files Renamer",false);

        //inputs
        addInput(new DataDefinitionDeclarationImpl("FILES_TO_RENAME", DataNecessity.MANDATORY, "Files to rename", DataDefinitionRegistry.LIST));
        addInput(new DataDefinitionDeclarationImpl("PREFIX", DataNecessity.OPTIONAL, "Add this prefix", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("SUFFIX", DataNecessity.OPTIONAL, "Append this suffix", DataDefinitionRegistry.STRING));

        //outputs
        addOutput(new DataDefinitionDeclarationImpl("RENAME_RESULT", DataNecessity.NA, "Rename operation summary", DataDefinitionRegistry.RELATION));
    }

    @Override
    public DataNecessity getResourceNecessity(String dataOriginalName) {
        switch (dataOriginalName) {
            case "FILES_TO_RENAME":
                return DataNecessity.MANDATORY;
            case "PREFIX":
            case "SUFFIX":
                return DataNecessity.OPTIONAL;
            case "RENAME_RESULT":
                return DataNecessity.NA;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }
    @Override
    public DataDefinition getResourceDataDefinition(String dataOriginalName) {
        switch (dataOriginalName) {
            case "FILES_TO_RENAME":
                return DataDefinitionRegistry.LIST;
            case "PREFIX":
            case "SUFFIX":
                return DataDefinitionRegistry.STRING;
            case "RENAME_RESULT":
                return DataDefinitionRegistry.RELATION;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }
    @Override
    public StepResult validateInputs(StepExecutionContext context) {
        return StepResult.NULL;
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        String finalName = context.getCurrentStepName();
        context.tick();
        RelationData renameResult = null;
        AbstractLogger logger = context.getStepLogger();
        StepResult result = StepResult.SUCCESS;
        StepResult subResult;
        List<FileData> filesToRename;
        String prefix;
        String suffix;
        try {
            List<String> columnsName = new ArrayList<>();
            columnsName.add("Ordinal Number");
            columnsName.add("Old File Name");
            columnsName.add("New File Name");
            renameResult = new RelationData(columnsName);
            filesToRename = context.getDataValue("FILES_TO_RENAME", List.class);
            try {
                prefix = context.getDataValue("PREFIX", String.class);
            } catch (Exception e) {
                prefix = "";
            }
            try {
                suffix = context.getDataValue("SUFFIX", String.class);
            } catch (Exception e) {
                suffix = "";
            }
            int filesToRenameSize = filesToRename.size();

            logger.addLogLine("About to start rename " + filesToRenameSize + " files." +
                    ( !prefix.equals("") ? (" Adding Prefix: " + prefix + ";") : "") +
                    ( !suffix.equals("") ? (" Adding Suffix: " + suffix + ";") : ""));

            for (int i = 0; i < filesToRenameSize; i++) {
                String oldName = filesToRename.get(i).getName();
                Path oldFileFullPath = Paths.get(filesToRename.get(i).getPath());
                String filenameNoExtenstion = oldName.substring(0, oldName.lastIndexOf('.'));
                String extension = oldName.substring(oldName.lastIndexOf('.'));
                Path newFileFullPath = oldFileFullPath.resolveSibling(prefix + filenameNoExtenstion + suffix + extension);

                subResult = renameFile(oldFileFullPath, newFileFullPath, logger, renameResult);
                if (subResult == StepResult.WARNING) {
                    result = StepResult.WARNING;
                }
            }

            if (filesToRenameSize == 0) {
                logger.addSummaryLine("No files to rename hence all files renamed successfully");
            } else {
                switch (result) {
                case SUCCESS:
                    logger.addSummaryLine("All files renamed successfully");
                    break;
                case WARNING:
                    List<String> filesthatFailed = new ArrayList<>();
                    for (int i = 0; i < renameResult.getRowSize(); i++) {
                        // if old file name equals new file name then rename failed
                        if (renameResult.getDataFromRow(i).get(2).equals(renameResult.getDataFromRow(i).get(1))) {
                            filesthatFailed.add((String) renameResult.getDataFromRow(i).get(1));
                        }
                    }
                    logger.addSummaryLine("The following files were not renamed successfully:\n" +
                            filesthatFailed.stream()
                                    .collect(Collectors.joining("\n")));
                    break;
                }
            }
        } catch (Exception e) {
            logger.addLogLine("Exception occurred: " + e.getMessage());
            result = StepResult.FAILURE;
        }
        context.storeDataValue("RENAME_RESULT", renameResult ,DataDefinitionRegistry.RELATION);
        context.tock();
        return result;
    }

    private StepResult renameFile(Path oldPath, Path newPath, AbstractLogger logger, RelationData renameResult) {

        List<String> data = new ArrayList<>();
        StepResult result = StepResult.SUCCESS;
        data.add(Integer.toString(renameResult.getRowSize() + 1));
        data.add(oldPath.getName(oldPath.getNameCount() - 1).toString());

        try {
            Files.move(oldPath, newPath);
            data.add(newPath.getName(newPath.getNameCount() - 1).toString());
        }catch (Exception e) {
            data.add(oldPath.getName(oldPath.getNameCount() - 1).toString());
            logger.addLogLine("Problem renaming file: " + oldPath.getName(oldPath.getNameCount() - 1));
            result = StepResult.WARNING;
        }
        renameResult.addRow(data);
        return result;
    }
}
