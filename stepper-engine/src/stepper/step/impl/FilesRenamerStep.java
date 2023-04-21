package stepper.step.impl;

import stepper.dd.impl.DataDefinitionRegistry;
import stepper.dd.impl.list.ListData;
import stepper.dd.impl.relation.RelationData;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FilesRenamerStep extends AbstractStepDefinition {


    public FilesRenamerStep(){
        super("FilesRenamer",false);

        //inputs
        addInput(new DataDefinitionDeclarationImpl("FILES_TO_RENAME", DataNecessity.MANDATORY, "Files to rename", DataDefinitionRegistry.LIST));
        addInput(new DataDefinitionDeclarationImpl("PREFIX", DataNecessity.OPTIONAL, "Add this prefix", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("SUFFIX", DataNecessity.OPTIONAL, "Append this suffix", DataDefinitionRegistry.STRING));

        //outputs
        addOutput(new DataDefinitionDeclarationImpl("RENAME_RESULT", DataNecessity.NA, "Rename operation summary", DataDefinitionRegistry.RELATION));
    }

    @Override
    public StepResult validateInputs(StepExecutionContext context) {
        return StepResult.NULL;
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {

        AbstractLogger logger = context.getStepLogger(this);
        StepResult result = StepResult.SUCCESS;
        StepResult subResult;
        ListData filesToRename = (ListData) context.getDataValue("FILES_TO_RENAME", ListData.class);

        List<String> columnsName = new ArrayList<>();
        columnsName.add("Ordinal Number");
        columnsName.add("Old File Name");
        columnsName.add("New File Name");
        RelationData renameResult = new RelationData(columnsName);

        String prefix = context.getDataValue("PREFIX", String.class);
        prefix = prefix != null ? prefix : "";

        String suffix = context.getDataValue("SUFFIX", String.class);
        suffix = suffix != null ? suffix : "";
        int filesToRenameSize = filesToRename.size();

        logger.addLogLine("About to start rename " + filesToRenameSize + " files." +
                ( !prefix.equals("") ? (" Adding Prefix: " + prefix + ";") : "") +
                ( !suffix.equals("") ? (" Adding Suffix: " + suffix + ";") : ""));

        for (int i = 0; i < filesToRenameSize; i++) {
            String fileName = (String) filesToRename.get(i);
            String newFileName = prefix + fileName + suffix;
            subResult = renameFile(fileName, newFileName, logger, renameResult);
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

        return result;
    }

    private StepResult renameFile(String fileName, String newFileName, AbstractLogger logger, RelationData renameResult) {
        File fileToRename = new File(fileName);
        File newName = new File(newFileName);
        List<String> data = new ArrayList<>();

        data.add(Integer.toString(renameResult.getRowSize() + 1));
        data.add(fileName);

        if (fileToRename.renameTo(newName)) {
            data.add(newFileName);
            renameResult.addRow(data);
            return StepResult.SUCCESS;
        } else {
            data.add(fileName);
            renameResult.addRow(data);
            logger.addLogLine("Problem renaming file: " + fileName);
            return StepResult.WARNING;
        }
    }
}
