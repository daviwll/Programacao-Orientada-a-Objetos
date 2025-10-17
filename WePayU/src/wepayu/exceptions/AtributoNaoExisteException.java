package wepayu.exceptions;

public class AtributoNaoExisteException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public AtributoNaoExisteException() {
        super("Atributo nao existe.");
    }

    public AtributoNaoExisteException(String detail) {
        super(detail);
    }
}