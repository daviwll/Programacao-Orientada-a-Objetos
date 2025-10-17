package wepayu.exceptions;

public class ComissaoNulaException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public ComissaoNulaException() {
        super("Comissao nao pode ser nula.");
    }

    public ComissaoNulaException(String detail) {
        super(detail);
    }
}