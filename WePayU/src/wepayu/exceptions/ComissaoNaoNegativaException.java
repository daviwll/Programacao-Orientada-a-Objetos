package wepayu.exceptions;

public class ComissaoNaoNegativaException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public ComissaoNaoNegativaException() {
        super("Comissao deve ser nao-negativa.");
    }

    public ComissaoNaoNegativaException(String detail) {
        super(detail);
    }
}