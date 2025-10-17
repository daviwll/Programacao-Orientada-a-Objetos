package wepayu.exceptions;

public class EmpregadoNaoRecebeEmBancoException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoRecebeEmBancoException() {
        super("Empregado nao recebe em banco.");
    }

    public EmpregadoNaoRecebeEmBancoException(String detail) {
        super(detail);
    }
}