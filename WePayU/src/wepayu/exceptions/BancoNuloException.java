package wepayu.exceptions;

public class BancoNuloException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public BancoNuloException() {
        super("Banco nao pode ser nulo.");
    }

    public BancoNuloException(String detail) {
        super(detail);
    }
}