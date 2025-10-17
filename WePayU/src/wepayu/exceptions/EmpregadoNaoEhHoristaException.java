package wepayu.exceptions;

public class EmpregadoNaoEhHoristaException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoEhHoristaException() {
        super("Empregado nao eh horista.");
    }

    public EmpregadoNaoEhHoristaException(String detail) {
        super(detail);
    }
}