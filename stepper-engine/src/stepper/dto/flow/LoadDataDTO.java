package stepper.dto.flow;

public class LoadDataDTO implements stepper.dto.DTO {
    // files full path
    String path;
    // message describing result of operation (positive mostly)
    String message;
    // error message received from the exception
    String errorMessage;
    // if the file was loaded successfully- success will be true -> else false.
    boolean success;
    Class<?> Type = this.getClass();

    public LoadDataDTO(String path, String message, boolean success, String errorMessage) {
        this.path = path;
        this.message = message;
        this.success = success;
        this.errorMessage = errorMessage;
    }
    public LoadDataDTO(String path, String message) {
        this.path = path;
        this.message = message;
        this.success = true;
        this.errorMessage = "";
    }

    public String getPath() {
        return path;
    }

    public String getMessage() {
        return message;
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
