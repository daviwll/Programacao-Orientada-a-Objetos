package wepayu.exceptions;

public class EmpregadoNaoEhSindicalizadoException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoEhSindicalizadoException() {
        super("Empregado nao eh sindicalizado.");
    }

    public EmpregadoNaoEhSindicalizadoException(String detail) {
        super(detail);
    }
}