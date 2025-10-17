package wepayu.exceptions;

public class DataInicialInvalidaException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public DataInicialInvalidaException() {
        super("Data inicial invalida.");
    }

    public DataInicialInvalidaException(String detail) {
        super(detail);
    }
}