package wepayu.exceptions;

public class ValorDeveSerPositivoException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public ValorDeveSerPositivoException() {
        super("Valor deve ser positivo.");
    }

    public ValorDeveSerPositivoException(String detail) {
        super(detail);
    }
}