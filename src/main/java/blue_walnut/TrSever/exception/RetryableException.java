package blue_walnut.TrSever.exception;

public class RetryableException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorMessage;

    public RetryableException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
    }
    public String getCode() {
        return errorCode.getCode();
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}