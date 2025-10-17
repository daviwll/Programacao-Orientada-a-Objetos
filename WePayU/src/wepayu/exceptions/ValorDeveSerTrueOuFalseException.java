package wepayu.exceptions;

public class ValorDeveSerTrueOuFalseException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public ValorDeveSerTrueOuFalseException() {
        super("Valor deve ser true ou false.");
    }

    public ValorDeveSerTrueOuFalseException(String detail) {
        super(detail);
    }
}