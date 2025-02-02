package blue_walnut.TrSever.exception;

import java.util.List;

public class ParamValidateException extends RuntimeException {
    private List<String> errors;

    public ParamValidateException(List<String> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getCode() {
        return "PARAM_ERR";
    }
    public String getMessage() {
        return errors.toString();
    }

}