package wepayu.exceptions;

public class IdentificacaoMembroNulaException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public IdentificacaoMembroNulaException() {
        super("Identificacao do membro nao pode ser nula.");
    }

    public IdentificacaoMembroNulaException(String detail) {
        super(detail);
    }
}