package wepayu.exceptions;

public class TaxaSindicalNulaException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public TaxaSindicalNulaException() {
        super("Taxa sindical nao pode ser nula.");
    }

    public TaxaSindicalNulaException(String detail) {
        super(detail);
    }
}