package wepayu.exceptions;

public class AgenciaNulaException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public AgenciaNulaException() {
        super("Agencia nao pode ser nulo.");
    }

    public AgenciaNulaException(String detail) {
        super(detail);
    }
}