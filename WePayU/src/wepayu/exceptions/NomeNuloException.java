package wepayu.exceptions;

public class NomeNuloException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public NomeNuloException() {
        super("Nome nao pode ser nulo.");
    }

    public NomeNuloException(String detail) {
        super(detail);
    }
}