package wepayu.exceptions;

public class DataInvalidaException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public DataInvalidaException() {
        super("Data invalida.");
    }

    public DataInvalidaException(String detail) {
        super(detail);
    }
}