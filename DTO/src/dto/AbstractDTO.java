package dto;

public abstract class AbstractDTO {
    protected String errorMessage;

    protected boolean valid;

    protected AbstractDTO(String errorMessage) {
        this.errorMessage = errorMessage;
        this.valid = false;
    }
    protected AbstractDTO() {
        this.errorMessage = "";
        this.valid = true;
    }

    public boolean getStatus() {
        return valid;
    }

    public String getErrorMessage(){
        return errorMessage;
    }

}
