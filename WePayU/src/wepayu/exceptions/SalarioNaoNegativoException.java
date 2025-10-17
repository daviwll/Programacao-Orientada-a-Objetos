package wepayu.exceptions;

public class SalarioNaoNegativoException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public SalarioNaoNegativoException() {
        super("Salario deve ser nao-negativo.");
    }

    public SalarioNaoNegativoException(String detail) {
        super(detail);
    }
}