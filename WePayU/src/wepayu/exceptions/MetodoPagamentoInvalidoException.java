package wepayu.exceptions;

public class MetodoPagamentoInvalidoException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public MetodoPagamentoInvalidoException() {
        super("Metodo de pagamento invalido.");
    }

    public MetodoPagamentoInvalidoException(String detail) {
        super(detail);
    }
}