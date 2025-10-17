package wepayu.exceptions;

public class IdentificacaoSindicatoNulaException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public IdentificacaoSindicatoNulaException() {
        super("Identificacao do sindicato nao pode ser nula.");
    }

    public IdentificacaoSindicatoNulaException(String detail) {
        super(detail);
    }
}