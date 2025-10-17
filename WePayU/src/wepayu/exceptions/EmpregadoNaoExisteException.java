package wepayu.exceptions;

public class EmpregadoNaoExisteException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoExisteException() {
        super("Empregado nao existe.");
    }

    public EmpregadoNaoExisteException(String detail) {
        super(detail);
    }
}