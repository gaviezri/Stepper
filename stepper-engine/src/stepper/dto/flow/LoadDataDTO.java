package stepper.dto.flow;

public class LoadDataDTO implements stepper.dto.DTO {
    String path;
    String content;
    String errorMessage;
    boolean success;
    Class<?> Type = this.getClass();

    public LoadDataDTO(String path, String content, boolean success, String errorMessage) {
        this.path = path;
        this.content = content;
        this.success = success;
        this.errorMessage = "";
    }
    public LoadDataDTO(String path, String content) {
        this.path = path;
        this.content = content;
        this.success = true;
        this.errorMessage = "";
    }

    public String getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean getStatus() {
        return success;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }


    @Override
    public Class<?> getType() {
        return this.getClass();
    }
}
