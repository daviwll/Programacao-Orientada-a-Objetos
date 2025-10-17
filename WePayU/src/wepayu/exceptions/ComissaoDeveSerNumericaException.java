package wepayu.exceptions;

public class ComissaoDeveSerNumericaException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public ComissaoDeveSerNumericaException() {
        super("Comissao deve ser numerica.");
    }

    public ComissaoDeveSerNumericaException(String detail) {
        super(detail);
    }
}