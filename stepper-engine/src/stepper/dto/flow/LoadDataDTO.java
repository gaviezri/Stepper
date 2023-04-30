package stepper.dto.flow;

import stepper.dto.AbstractDTO;

public class LoadDataDTO extends AbstractDTO {
    // files full path
    String path;
    // message describing result of operation (positive mostly)
    String message;
    // error message received from the exception

    public LoadDataDTO(String path, String message, boolean valid, String errorMessage) {
        this.path = path;
        this.message = message;
        this.valid = valid;
        this.errorMessage = errorMessage;
    }


    public String getMessage() {
        return message;
    }

    @Override
    public boolean getStatus() {
        return valid;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

}
