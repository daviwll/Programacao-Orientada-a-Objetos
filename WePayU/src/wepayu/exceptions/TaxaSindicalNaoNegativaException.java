package wepayu.exceptions;

public class TaxaSindicalNaoNegativaException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public TaxaSindicalNaoNegativaException() {
        super("Taxa sindical deve ser nao-negativa.");
    }

    public TaxaSindicalNaoNegativaException(String detail) {
        super(detail);
    }
}