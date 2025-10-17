package wepayu.exceptions;

public class IdentificacaoEmpregadoNulaException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public IdentificacaoEmpregadoNulaException() {
        super("Identificacao do empregado nao pode ser nula.");
    }

    public IdentificacaoEmpregadoNulaException(String detail) {
        super(detail);
    }
}