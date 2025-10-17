package wepayu.exceptions;

public class DataFinalInvalidaException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public DataFinalInvalidaException() {
        super("Data final invalida.");
    }

    public DataFinalInvalidaException(String detail) {
        super(detail);
    }
}