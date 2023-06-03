package stepper.step.impl;

import stepper.dd.impl.DataDefinitionRegistry;
import stepper.dd.impl.file.FileData;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

import java.util.*;
import java.util.stream.Collectors;

public class FilesDeleterStep extends AbstractStepDefinition {
  // TODO: delete this
    public FilesDeleterStep() {
        super("Files Deleter", false);

        //inputs
        addInput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.MANDATORY, "Files to delete", DataDefinitionRegistry.LIST));

        //outputs
        addOutput(new DataDefinitionDeclarationImpl("DELETED_LIST", DataNecessity.NA, "Files failed to be deleted", DataDefinitionRegistry.LIST));
        addOutput(new DataDefinitionDeclarationImpl("DELETION_STATS", DataNecessity.NA, "Deletion summary results", DataDefinitionRegistry.MAPPING));
    }
    @Override
    public DataNecessity getResourceNecessity(String dataOriginalName) {
        switch (dataOriginalName) {
            case "FILES_LIST":
                return DataNecessity.MANDATORY;
            case "DELETED_LIST":
            case "DELETION_STATS":
                return DataNecessity.NA;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }
    @Override
    public stepper.dd.api.DataDefinition getResourceDataDefinition(String dataOriginalName) {
        switch (dataOriginalName) {
            case "DELETED_LIST":
            case "FILES_LIST":
                return DataDefinitionRegistry.LIST;
            case "DELETION_STATS":
                return DataDefinitionRegistry.MAPPING;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }
    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.tick();
        List<FileData> FILES_LIST = new ArrayList<>();
        List<String> DELETED_LIST = new ArrayList<>();
        StepResult res;
        AbstractLogger logger = context.getStepLogger();
        try {
            FILES_LIST = context.getDataValue("FILES_LIST",List.class);
        }catch (Exception e){
            logger.log("Failed to get files list");
            res = StepResult.FAILURE;
        }
        boolean filesListIsEmpty =  FILES_LIST.isEmpty();
        int numberOfFilesToDelete = FILES_LIST.size();

        if(!filesListIsEmpty) {
            logger.log("About to start delete " + numberOfFilesToDelete + " files");

            DELETED_LIST = FILES_LIST.stream()
                    .filter(FileData::exists)
                    .map(file -> {
                        String fname = file.getName();
                        boolean deletedSuccessfully = file.delete();
                        if (!deletedSuccessfully) {
                            logger.log("Failed to delete file " + file.getName());
                            fname = null;
                        }
                        return fname;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        Map<String, String> DELETION_STATS =  new HashMap<>();
        DELETION_STATS.put("DELETED", String.valueOf(numberOfFilesToDelete - DELETED_LIST.size()));
        DELETION_STATS.put("FAILED", String.valueOf(DELETED_LIST.size()));
        try {
            context.storeDataValue("DELETED_LIST", DELETED_LIST, DataDefinitionRegistry.LIST);
            context.storeDataValue("DELETION_STATS", DELETION_STATS, DataDefinitionRegistry.MAPPING);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store data value", e);
        }
        if(DELETED_LIST.size() == 0){
            res = StepResult.SUCCESS;
            logger.addSummaryLine(filesListIsEmpty ? "No files? No problem - no files were given so no deletion failed" : "All files deleted successfully!");
        }
        else if(DELETED_LIST.size() != numberOfFilesToDelete){
            res = StepResult.WARNING;
            logger.log("WARNING: Only " + DELETION_STATS.get("DELETED") + "/" + numberOfFilesToDelete + " files where deleted!");
        }
        else{
            res = StepResult.FAILURE;
            logger.log("FAILURE: No files where deleted! check yourself before you wreck yourself");
        }
        context.tock();
        context.getCurrentStepManager().setStepResult(res);
        return res;
    }

    @Override
    public StepResult validateInputs(StepExecutionContext context) {
        return null;
    }
}
