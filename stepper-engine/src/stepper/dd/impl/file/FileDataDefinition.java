package stepper.dd.impl.file;

import stepper.dd.api.AbstractDataDefinition;

public abstract class FileDataDefinition extends AbstractDataDefinition {
    public FileDataDefinition() {
            super("File", false, FileData.class);
        }

    public abstract String presentToUser();
}
