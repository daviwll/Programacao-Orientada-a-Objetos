package wepayu.exceptions;

public class TaxaSindicalDeveSerNumericaException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public TaxaSindicalDeveSerNumericaException() {
        super("Taxa sindical deve ser numerica.");
    }

    public TaxaSindicalDeveSerNumericaException(String detail) {
        super(detail);
    }
}