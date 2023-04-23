package stepper.exception;

public class GivenValueTypeDontMatchException extends Exception{
    public GivenValueTypeDontMatchException(String errorMessage) {
        super(errorMessage);
    }
}
