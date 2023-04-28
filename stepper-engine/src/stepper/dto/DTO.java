package stepper.dto;

public interface DTO {
    boolean getStatus();
    String getErrorMessage();
    String getSuccessMessage();
    Class<?> getType();

}
