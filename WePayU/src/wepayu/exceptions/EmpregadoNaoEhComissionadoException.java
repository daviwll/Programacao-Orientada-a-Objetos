package wepayu.exceptions;

public class EmpregadoNaoEhComissionadoException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoEhComissionadoException() {
        super("Empregado nao eh comissionado.");
    }

    public EmpregadoNaoEhComissionadoException(String detail) {
        super(detail);
    }
}