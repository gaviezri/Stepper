package stepper.step.impl;

import stepper.dd.impl.DataDefinitionRegistry;
import stepper.exception.GivenValueTypeDontMatchException;
import stepper.exception.NoMatchingKeyWasFoundException;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipperStep extends AbstractStepDefinition {
    public enum OperationType {
        ZIP,
        UNZIP
    }
    public ZipperStep() {
        super("Zipper", false);

        // inputs
        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("OPERATION", DataNecessity.MANDATORY, "Operation type", DataDefinitionRegistry.ENUMERATION));

        // outputs
        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "Zip operation result", DataDefinitionRegistry.STRING));

    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.tick();
        AbstractLogger logger = context.getStepLogger();
        String source = null;
        String operationOutcome = null;
        OperationType operation = null;
        StepResult result = StepResult.SUCCESS;
        try {
            source = context.getDataValue("SOURCE", String.class);
            operation = context.getDataValue("OPERATION", OperationType.class);
        } catch (NoMatchingKeyWasFoundException e) {
            logger.log("Error while getting data from context: " + e.getMessage());
            logger.addSummaryLine(operationOutcome = "Operation failed due to missing data");
            result = StepResult.FAILURE;
        } catch (GivenValueTypeDontMatchException e) {
            logger.log("Error while getting data from context" + e.getMessage());
            logger.addSummaryLine(operationOutcome = "Operation failed due to corrupted data in context");
            result = StepResult.FAILURE;
        } catch (Exception e) {
            logger.log("Error while getting data from context" + e.getMessage());
            logger.addSummaryLine(operationOutcome = "Operation failed due to unexpected error");
            result = StepResult.FAILURE;
        }
        if (!result.equals(StepResult.FAILURE)) {

            switch (operation) {
                case ZIP:
                    result = handleZip(logger, source);
                    break;
                case UNZIP:
                    result = handleUnzip(logger, source);
                    break;
                default:
                    logger.log("Error: Unknown operation type");
                    logger.addSummaryLine(operationOutcome = "Operation failed due to unknown operation type: " + operation + ". expected ZIP or UNZIP");
                    result = StepResult.FAILURE;
                    break;
            }
        }
        context.storeDataValue(
                "RESULT",
                operationOutcome == null ? "SUCCESS" : "FAILURE: " + operationOutcome,
                DataDefinitionRegistry.STRING
        );

        context.tock();
        return result;
    }

    private void zip(String source) throws Exception {
        File zippedFile = new File(source + ".zip");
        File sourceFile = new File(source);
        try (
                java.io.FileOutputStream fos = new java.io.FileOutputStream(zippedFile);
                java.util.zip.ZipOutputStream zipOut = new java.util.zip.ZipOutputStream(fos);
                java.io.FileInputStream fis = new java.io.FileInputStream(sourceFile);
                )
        {
            zipOut.putNextEntry(new ZipEntry(sourceFile.getName()));
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                zipOut.write(buffer, 0, bytesRead);
            }
            zipOut.closeEntry();
        }
    }

    private void unzip(String source) throws Exception {
        File zipFile = new File(source);
        File targetDirectory = new File(source.substring(0, source.lastIndexOf('.')));
        try (
                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        ) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                File file = new File(targetDirectory, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    file.createNewFile();
                    try (
                            OutputStream outputStream = new FileOutputStream(file);
                    ) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }
        }
    }
    private StepResult handleZip(AbstractLogger logger, String source) {
        StepResult result;
        logger.log("Zip operation being invoked");
        try {
            zip(source);
            logger.addSummaryLine("Zip operation performed successfully");
            result = StepResult.SUCCESS;
        } catch (Exception e) {
            result = handleZipError(logger, e);
        }
        return result;
    }

    private StepResult handleUnzip(AbstractLogger logger, String source) {
        StepResult result;
        logger.log("Unzip operation being invoked");
        try {
            unzip(source);
            logger.addSummaryLine("Unzip operation performed successfully");
            result = StepResult.SUCCESS;
        } catch (Exception e) {
            result = handleZipError(logger, e);
        }
        return result;
    }

    private static StepResult handleZipError(AbstractLogger logger, Exception e) {
        StepResult result;
        logger.log("Error while performing operation: " + e.getMessage());
        logger.addSummaryLine("Operation failed due to external error. ");
        result = StepResult.FAILURE;
        return result;
    }

    @Override
    public StepResult validateInputs(StepExecutionContext context) {
        return null;
    }

    @Override
    public stepper.dd.api.DataDefinition getResourceDataDefinition(String dataOriginalName) {
        switch (dataOriginalName) {
            case "SOURCE":
            case "RESULT":
                return DataDefinitionRegistry.STRING;
            case "OPERATION":
                return DataDefinitionRegistry.ENUMERATION;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }

    @Override
    public DataNecessity getResourceNecessity(String dataOriginalName) {
        switch (dataOriginalName) {
            case "SOURCE":
            case "OPERATION":
                return DataNecessity.MANDATORY;
            case "RESULT":
                return DataNecessity.NA;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }
}
