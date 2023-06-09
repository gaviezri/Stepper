package dto.flow;

import dto.AbstractDTO;

public class LoadDataDTO extends AbstractDTO {

    // message describing result of operation (positive mostly)
    String message;
    // error message received from the exception

    public LoadDataDTO(String message, boolean valid, String errorMessage) {
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
